package com.tenkel.sapi;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import br.ufrj.cos.labia.aips.customviews.SimpleAndarView;
import br.ufrj.cos.labia.aips.customviews.SimpleAndarView.OnPointSelectListener;
import br.ufrj.cos.labia.aips.customviews.SimpleAndarView.Point;
import br.ufrj.cos.labia.aips.fragments.dialogs.ConfirmDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.ConfirmDialog.ConfirmDialogListener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPoint;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Bridge;
import com.tenkel.sapi.dal.LeituraWiFi;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.ModeloLocalizacao;
import com.tenkel.sapi.dal.ModeloLocalizacaoManager;
import com.tenkel.sapi.dal.Observacao;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;
import com.tenkel.sapi.kde.KDE;

public class TrainModelActivity extends Activity implements OnPointSelectListener, Listener {

	public static final String ID_LOCAL = "idLocal";
	
	private long mIdLocal;
	private int mCurrentPosition;
	private List<Andar> mAndares;
	private List<Posicao> mPosicoes;
	private BridgeReceiver mReceiver;

	private AndarManager mAndarManager;
	private AccessPointManager mAPManager;
	private PosicaoManager mPosicaoManager;
	private ModeloLocalizacaoManager mMLManager;
	private ObservacaoManager mObservacaoManager;
	private LeituraWifiManager mLeituraWIFIManager;

	private SimpleAndarView mAndar;
	private Spinner mSpAndares;
	private Button mBtCollect;
	private Button mBtTreinar;
	private Button mBtLimpar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trainmodel);

		// Recebe o id passado por quem chamou a activity 
		mIdLocal = getIntent().getLongExtra("idLocal", -1);
		if (mIdLocal == -1) Log.e("LocalActivity", "Parameter missing!!!");
		
		// Managers para acessar os dados no banco
		mLeituraWIFIManager = new LeituraWifiManager(getApplicationContext());
		mObservacaoManager = new ObservacaoManager(getApplicationContext());
		mMLManager = new ModeloLocalizacaoManager(getApplicationContext());
		mPosicaoManager = new PosicaoManager(getApplicationContext());
		mAPManager = new AccessPointManager(getApplicationContext());
		mAndarManager = new AndarManager(getApplicationContext());
		
		// View com o andar
		mAndar = (SimpleAndarView) findViewById(0);
		mAndar.setOnPointSelectListener(this);
		mAndar.setSelectable(true);
		
		// Botão para treinar o modelo
		mBtTreinar = (Button) findViewById(R.id.btTrain);
		mBtTreinar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadingDialog dialog = new LoadingDialog();
				dialog.setWorker(new Worker() {
						@Override
						public void doHeavyWork(LoadingDialog dialog) {
							try {
								Thread.sleep(500);
								trainModel();
							} catch (InterruptedException e) {
								
							}
						}

						@Override
						public void Finished() {
							// TODO Auto-generated method stub
							
						}
					});
				dialog.setListener(TrainModelActivity.this);
				dialog.show(getFragmentManager(), "training");
			}
		});
		
		// Botão para coletar as amostras
		mBtCollect = (Button) findViewById(R.id.btCollect);
		mBtCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Bridge.isRunning()) {
					if (mAndar.isEnabled()) {
						mAndar.setEnabled(false);
						mBtLimpar.setEnabled(false);
						mBtTreinar.setEnabled(false);
						mBtCollect.setText("Interromper");
					} else {
						mAndar.setEnabled(true);
						mBtLimpar.setEnabled(true);
						mBtTreinar.setEnabled(true);
						mBtCollect.setText("Coletar");
					}
				} else {
					Toast.makeText(getApplicationContext(), 
							"O serviço de captura não está rodando", 
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		// Botão para remover as amostras em um ponto de coleta
		mBtLimpar = (Button) findViewById(R.id.btRemoveSamples);
		mBtLimpar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConfirmDialog dialog = new ConfirmDialog();
				dialog.setTitle("Confirmação");
				dialog.setMessage("Tem certeza que deseja remover todas as amostras coletadas neste ponto?");
				dialog.setConfirmText("Sim");
				dialog.setCancelText("Cancelar");
				dialog.setListener(new ConfirmDialogListener() {
					@Override
					public void onConfirm(ConfirmDialog dialog) {
						Long id = mPosicoes.get(mCurrentPosition).getId();
						mObservacaoManager.deleteByIdPosition(id);
						mAndar.setCounterAt(mCurrentPosition, 0);
					}
					@Override
					public void onCancel(ConfirmDialog dialog) { }
				});
				dialog.show(getFragmentManager(), "confirm_clean");
			}
		});
		
		// Spinner com a lista de andares
		mAndares = mAndarManager.getByIdLocal(mIdLocal);
		String[] names = new String[mAndares.size()];
		for (int i=0;i<mAndares.size();++i) 
			names[i] = i + " - " + mAndares.get(i).getNome();
		
		mSpAndares = (Spinner) findViewById(R.id.spAndares);
		mSpAndares.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));
		mSpAndares.setSelection(0);
		mSpAndares.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Andar andar = mAndares.get(position);
				mAndar.setImage(getBitmapFor(andar));
				mAndar.setPoints(getPointsFor(andar));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mAndar.setImage(null);
				mAndar.setPoints(null);
				mBtCollect.setEnabled(false);
				mBtLimpar.setEnabled(false);
			}
		});
		
		// Receiver para receber as notificações da Bridge
		mReceiver = new BridgeReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getWindow().getDecorView().setKeepScreenOn(true);
		registerReceiver(mReceiver, new IntentFilter(
				Bridge.BROADCAST_SENSOR_DATA));
		
		Bridge.start(getApplicationContext());
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onPointSelected(SimpleAndarView view, int position) {
		if (position == -1) {
			mBtCollect.setEnabled(false);
			mBtLimpar.setEnabled(false);
			Log.i("LocalActivity", "Selected nothing");
		} else {
			mBtCollect.setEnabled(true);
			mBtLimpar.setEnabled(true);
			Log.i("LocalActivity", "Selected " + mPosicoes.get(position).getId());
		}
		
		mCurrentPosition = position;
	}

	@Override
	public void onStart(Worker w) {
		
	}

	@Override
	public void onFinish(Worker w) {
		Log.i("TrainModelActivity", "Model trained successfully");
		Toast.makeText(getApplicationContext(), "Success", 
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onError(Worker w, Exception e) {
		e.printStackTrace();
		
		if (e.getMessage() == null)
			Toast.makeText(getApplicationContext(), "Failed", 
					Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(), e.getMessage(), 
					Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancel(Worker w) {
		Log.w("TrainModelActivity", "Cancelado pelo usuário");
	}

	private Bitmap getBitmapFor(Andar andar) {
		byte[] image = andar.getImage();
		ByteArrayInputStream is = new ByteArrayInputStream(image);
		return BitmapFactory.decodeStream(is);
	}
	
	private SimpleAndarView.Point[] getPointsFor(Andar andar) {
		mPosicoes = mPosicaoManager.getByidAndar(andar.getId());
		Point[] points = new SimpleAndarView.Point[mPosicoes.size()];
		
		for (int i=0;i<mPosicoes.size();++i) {
			points[i] = new SimpleAndarView.Point();
			points[i].x = mPosicoes.get(i).getX().floatValue();
			points[i].y = mPosicoes.get(i).getY().floatValue();
			points[i].counter = mObservacaoManager.getByidPosicao(
					mPosicoes.get(i).getId()).size();
		}
		
		return points;
	}

	private void trainModel() {
		List<Observacao> samples = mObservacaoManager.getByIdLocal(mIdLocal);
		Map<Long, AccessPoint> accessPoints = mAPManager.getAllAsIDMap();
		Map<Long, Posicao> posicoes = mPosicaoManager.getAllAsIDMap();
		
		List<WIFISignal> all = new ArrayList<WIFISignal>();
		List<List<WIFISignal>> internal = new ArrayList<List<WIFISignal>>();
		
		for (Observacao s : samples) {
			List<LeituraWiFi> signals = mLeituraWIFIManager.getByidObservacao(s.getId());
			List<WIFISignal> signals2 = new ArrayList<WIFISignal>();
			
			for (LeituraWiFi s2 : signals) {
				AccessPoint ap = accessPoints.get(s2.getIdAccessPoint());
				WIFISignal s3 = new WIFISignal(ap.getbssid(), s2.getValor());
				signals2.add(s3);
				all.add(s3);
			}
			
			internal.add(signals2);
		}
		
		if (all.size() == 0) {
			Log.e("TrainModelActivity", "No signals detected");
			return;
		}
		
		//WHIPS ips = new WHIPS(all);
		
		KDE ips = new KDE();
		
		int i = 0;
		for (Observacao s : samples) {
			Reading r = new Reading(internal.get(i++));
			Location l = new Location(posicoes.get(s.getIdPosicao()).getIdRemoto());
			ips.learn(r, l);
		}

		ModeloLocalizacao ml = mMLManager.getFirstByIdLocal(mIdLocal);
		
		if (ml == null) {
			ml = new ModeloLocalizacao();
		} else {
			getApplicationContext().deleteFile(ml.getFilename());
		}
		
		ml.setNomeVersao(ips.getNomeVersao());
		ml.setFilename(UUID.randomUUID().toString() + "-" + ml.getNomeVersao());
		ml.setIdLocal(mIdLocal);
		
		ips.save(getApplicationContext(), ml.getFilename());

		if (ml.getId() == null) mMLManager.save(ml);
		else mMLManager.update(ml);
		
		ips.close();
	}

	public class BridgeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("LocalActivity", "Processing event from Bridge");
			
			long idObservacao = intent.getLongExtra("idObservacao", -1);
			if (idObservacao == -1) {
				Log.e("DebugRoomActivity", "Bridge did not send idObservacao, as expected");
				return;
			}
			
			Object[] objects = (Object[]) intent.getSerializableExtra("wifi");
			ScanResult[] results = new ScanResult[objects.length];
			for (int i=0; i<objects.length; ++i) 
				results[i] = (ScanResult) objects[i];
			
			if (!mAndar.isEnabled()) {
				Observacao obs = mObservacaoManager.getById(idObservacao);
				obs.setidPosicao(mPosicoes.get(mCurrentPosition).getId());
				mObservacaoManager.update(obs);
				mAndar.incCounter(mCurrentPosition);
			}
		}
	}

}
