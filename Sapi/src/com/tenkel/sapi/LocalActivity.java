package com.tenkel.sapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import br.ufrj.cos.labia.aips.dto.ModeloDTO;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;

import com.tenkel.sapi.dal.Local;
import com.tenkel.sapi.dal.LocalManager;
import com.tenkel.sapi.dal.ModeloLocalizacao;
import com.tenkel.sapi.dal.ModeloLocalizacaoManager;
import com.tenkel.sapi.dal.SharedPrefManager;
import com.tenkel.sapi.kde.KDE;

public class LocalActivity extends Activity implements Listener {
	
	private long mIdLocal;
	private long mIdDispositivo;
	private Local mLocal;
	private Button mBtEnviar;
	private Button mBtExecutar;
	private Button mBtCriar;
	private Button mBtBaixar;
	private ModeloLocalizacaoManager mMLManager;
	private Button mBtDestruir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local);
		
		mMLManager = new ModeloLocalizacaoManager(getApplicationContext());
		
		mIdDispositivo = new SharedPrefManager(getApplicationContext(), false).getIdDispositivo();
		mIdLocal = getIntent().getLongExtra("idLocal", -1);
		if (mIdLocal == -1) Log.e("LocalActivity", "Missing idLocal");
		mLocal = new LocalManager(getApplicationContext()).getFirstById(mIdLocal);
		
		// Criar modelo
		mBtCriar = (Button) findViewById(R.id.btCriarModelo);
		mBtCriar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create new Model
				Intent intent = new Intent(getApplicationContext(), TrainModelActivity.class);
				intent.putExtra("idLocal", mIdLocal);
				startActivity(intent);
			}
		});
		
		// Baixar modelo
		mBtBaixar = (Button) findViewById(R.id.btBaixarModelo);
		mBtBaixar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadingDialog dialog = new LoadingDialog();
				dialog.setListener(LocalActivity.this);
				dialog.setWorker(new Worker() {
						@Override
						public void doHeavyWork(LoadingDialog dialog) {
						//	baixarModelo(); MODELO NÂO SE BAIXA
						}
					});
				dialog.show(getFragmentManager(), "baixar");
			}
		});
		
		// Destruir o modelo
		mBtDestruir = (Button) findViewById(R.id.btDestruirModelo);
		mBtDestruir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				destruirModelo();
			}
		});
		
		
		// Enviar modelo
		mBtEnviar = (Button) findViewById(R.id.btEnviarModelo);
		mBtEnviar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadingDialog dialog = new LoadingDialog();
				dialog.setListener(LocalActivity.this);
				dialog.setWorker(new Worker() {
						@Override
						public void doHeavyWork(LoadingDialog dialog) {
						//	enviarModelo(); MODELO NÂO SE ENVIA
						}
					});
				dialog.show(getFragmentManager(), "enviar");
			}
		});
		
		// Executar
		mBtExecutar = (Button) findViewById(R.id.btExecutar);
		mBtExecutar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ExecuteModelActivity.class);
				intent.putExtra("idLocal", mIdLocal);
				startActivity(intent);
			}
		});
	}

	private void destruirModelo() {
		ModeloLocalizacao ml = mMLManager.getFirstByIdLocal(mIdLocal);
		
		if (ml == null)
			Toast.makeText(getApplicationContext(), 
					"Não há um modelo de localização para este local", 
					Toast.LENGTH_SHORT).show();
		
		File models = getApplicationContext().getDir("models", Context.MODE_PRIVATE);
		File zipFile = new File(models, ml.getFilename());
		
		zipFile.delete();
		mMLManager.delete(ml);
		
		Toast.makeText(getApplicationContext(), "Modelo descartado", 
				Toast.LENGTH_SHORT).show();
		
		checkModelPresence();
	}

//	private void enviarModelo() {
//		ModeloLocalizacao ml = mMLManager.getFirstByIdLocal(mIdLocal);
//		
//		if (ml == null)
//			throw new IllegalStateException("Não há um modelo de localização para este local");
//		
//		File models = getApplicationContext().getDir("models", Context.MODE_PRIVATE);
//		File zipFile = new File(models, ml.getFilename());
//		
//		BasicConnector.enviarModelo(mIdDispositivo, mLocal.getIdRemoto(), 
//				ml.getNomeVersao(), zipFile);
//		Log.e("LocalActivity", "Não se envia modelo!");
//	}
	
//	private void baixarModelo() {
//		
//		// Baixa a lista e escolher o versionName que for mais recente e compátivel
//		String versionName = null;
//		
//		List<ModeloDTO> lista = BasicConnector.requisitarModelos(mIdDispositivo, mLocal.getIdRemoto());
//		for (ModeloDTO dto : lista) {
//			// if (WHIPS.canLoad(dto.versaoModelo)) {
//			if (KDE.canLoad(dto.versaoModelo)) {
//				versionName = dto.versaoModelo;
//				break;
//			}
//		}
//		
//		if (versionName == null) {
//			if (lista.isEmpty()) {
//				String msg = "Nenhum modelo disponivel para este par (local, dispositivo)";
//				Log.e("LocalActivity", msg);
//				throw new CommunicationException(msg);
//			} else {
//				String msg = "Nenhum modelo compátivel para este par (local, dispositivo)";
//				Log.e("LocalActivity", msg);
//				throw new CommunicationException(msg);
//			}
//		}
//		
//		// Baixa o modelo escolhido e o salva a um ModeloLocalizacao
//		String oldFilename = null;
//		ModeloLocalizacao ml = mMLManager.getFirstByIdLocal(mIdLocal);
//		if (ml == null) {
//			ml = new ModeloLocalizacao();
//		} else {
//			oldFilename = ml.getFilename();
//		}
//		
//		ml.setFilename(UUID.randomUUID().toString() + "-" + versionName);
//		ml.setNomeVersao(versionName);
//		ml.setIdLocal(mIdLocal);
//		
//		File models = getApplicationContext().getDir("models", Context.MODE_PRIVATE);
//		File zipFile = new File(models, ml.getFilename());
//		
//		try {
//			BasicConnector.baixarModelo(mIdDispositivo, mLocal.getIdRemoto(), 
//					ml.getNomeVersao(), new FileOutputStream(zipFile));
//			Log.i("LocalActivity", "Arquivo baixado");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			throw new CommunicationException("Falha ao salvar o modelo baixado");
//		}
//		
//		if (oldFilename != null)
//			new File(models, oldFilename).delete();
//		
//		if (ml.getId() == null) mMLManager.save(ml);
//		else mMLManager.update(ml);
		
		
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkModelPresence();
	}

	private void checkModelPresence() {
		ModeloLocalizacaoManager m = new ModeloLocalizacaoManager(getApplicationContext());
		boolean hasModel = m.getFirstByIdLocal(mIdLocal) != null;
		
		mBtDestruir.setEnabled(hasModel);
		mBtExecutar.setEnabled(hasModel);
		mBtEnviar.setEnabled(hasModel);
	}

	@Override
	public void onStart(Worker w) {
		
	}

	@Override
	public void onFinish(Worker w) {
		Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
		checkModelPresence();
	}

	@Override
	public void onError(Worker w, Exception e) {
		e.printStackTrace();
		
		if (e.getMessage() == null)
			Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancel(Worker w) {
		
	}

}