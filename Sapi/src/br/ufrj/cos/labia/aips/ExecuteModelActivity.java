package br.ufrj.cos.labia.aips;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import br.ufrj.cos.labia.aips.comm.BasicConnector;
import br.ufrj.cos.labia.aips.customviews.SimpleAndarView;
import br.ufrj.cos.labia.aips.customviews.SimpleAndarView.Point;
import br.ufrj.cos.labia.aips.dal.Andar;
import br.ufrj.cos.labia.aips.dal.AndarManager;
import br.ufrj.cos.labia.aips.dal.Bridge;
import br.ufrj.cos.labia.aips.dal.ModeloLocalizacao;
import br.ufrj.cos.labia.aips.dal.ModeloLocalizacaoManager;
import br.ufrj.cos.labia.aips.dal.Posicao;
import br.ufrj.cos.labia.aips.dal.PosicaoManager;
import br.ufrj.cos.labia.aips.dal.SharedPrefManager;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.InvalidModelException;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;
import br.ufrj.cos.labia.aips.ips.kde.KDE;
import br.ufrj.cos.labia.aips.ips.whips.WHIPS;

public class ExecuteModelActivity extends Activity {

	private ModeloLocalizacaoManager mMLManager;
	private PosicaoManager mPosicaoManager;
	private AndarManager mAndarManager;
	
	private List<Posicao> mPosicoes;
	private List<Andar> mAndares;
	private long mIdLocal;

	private Spinner mSpAndares;
	private SimpleAndarView mAndar;
	private BridgeReceiver mReceiver;
	
	private IPS mIPS;
	private CheckBox mCbRastrear;
	private Map<Long,Posicao> mIdRemotoToPosicao;
	private int mPredictedAndar;
	private int mPredictedPoint;
	private HashMap<Long, List<Posicao>> mAndarToPosicoes;
	private UpdateThread mUpdateThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_executemodel);

		// Recebe o id passado por quem chamou a activity
		mIdLocal = getIntent().getLongExtra("idLocal", -1);
		if (mIdLocal == -1) Log.e("LocalActivity", "Parameter missing!!!");

		// Thread para enviar as posicoes calculadas
		SharedPrefManager spm = new SharedPrefManager(getApplicationContext(), false);
		long idDispositivo = spm.getIdDispositivo();
		mUpdateThread = new UpdateThread(idDispositivo);
		mUpdateThread.start();
		
		// Managers para acessar os dados no banco
		mMLManager = new ModeloLocalizacaoManager(getApplicationContext());
		mPosicaoManager = new PosicaoManager(getApplicationContext());
		mAndarManager = new AndarManager(getApplicationContext());

		// View com o andar
		mAndar = (SimpleAndarView) findViewById(R.id.andar);
		
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
				
				if (position == mPredictedAndar) {
					mAndar.setPredicted(mPredictedPoint);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mAndar.setImage(null);
				mAndar.setPoints(null);
			}
		});
		
		// Checkbox para acompanhar a posição prevista
		mCbRastrear = (CheckBox) findViewById(R.id.cbAcompanhar);
		mCbRastrear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				Log.i("Checkbox" , "Changed to " + isChecked);
				mSpAndares.setEnabled(!isChecked);
			}
		});
		mCbRastrear.setChecked(true);
		
		// Todas as posicoes
		mAndarToPosicoes = new HashMap<Long, List<Posicao>>();
		mIdRemotoToPosicao = new HashMap<Long, Posicao>();
		for (Andar andar : mAndares) {
			List<Posicao> posicoes = mPosicaoManager.getByidAndar(andar.getId());
			mAndarToPosicoes.put(andar.getId(), posicoes);
			for (Posicao p : posicoes)
				mIdRemotoToPosicao.put(p.getIdRemoto(), p);
		}
		
		// Receiver para receber as notificações da Bridge
		mReceiver = new BridgeReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mPredictedAndar = -1;
		mPredictedPoint = -1;
		
		Bridge.start(getApplicationContext());
		getWindow().getDecorView().setKeepScreenOn(true);
		registerReceiver(mReceiver, new IntentFilter(
				Bridge.BROADCAST_SENSOR_DATA));
		
		ModeloLocalizacao ml = mMLManager.getFirstByIdLocal(mIdLocal);
		if (ml != null) {
			try {
				Log.i("ExecuteModelActivity", "Importing model " + ml.getFilename() 
						+ ", version is " + ml.getNomeVersao());
				
//				if (WHIPS.canLoad(ml.getNomeVersao()))
//					mIPS = WHIPS.load(getApplicationContext(), ml.getNomeVersao(), ml.getFilename());
				
//				else 
				if (KDE.canLoad(ml.getNomeVersao()))
					mIPS = KDE.load(getApplicationContext(), ml.getNomeVersao(), ml.getFilename());
				
				else
					Log.w("TrainModelActivity", "No model can not handle this model format");
				
			} catch (InvalidModelException e) {
				Log.e("TrainModelActivity", "WHIPS failed to open model " + 
						ml.getFilename() + " with version " + ml.getNomeVersao());
			}

		} else {
			Log.w("TrainModelActivity", "No model associated with this local");
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUpdateThread.finish();
	}
	
	private SimpleAndarView.Point[] getPointsFor(Andar andar) {
		mPosicoes = mAndarToPosicoes.get(andar.getId());
		Point[] points = new SimpleAndarView.Point[mPosicoes.size()];
		
		for (int i=0;i<mPosicoes.size();++i) {
			points[i] = new SimpleAndarView.Point();
			points[i].x = mPosicoes.get(i).getX().floatValue();
			points[i].y = mPosicoes.get(i).getY().floatValue();
			points[i].counter = i;
		}
		
		return points;
	}

	private Bitmap getBitmapFor(Andar andar) {
		byte[] image = andar.getImage();
		ByteArrayInputStream is = new ByteArrayInputStream(image);
		Bitmap img = BitmapFactory.decodeStream(is);
		
		if (img != null)
			return img;
		
		return BitmapFactory.decodeResource(
				getResources(), R.drawable.nomap);
	}

	protected void predictPosition(ScanResult[] results) {
		if (mIPS == null)
			return;
		
		List<WIFISignal> signals = new ArrayList<WIFISignal>();
		for (ScanResult r : results)
			signals.add(new WIFISignal(r.BSSID, r.level));
		
		Location l = mIPS.predict(new Reading(signals));
		Posicao predicted = mIdRemotoToPosicao.get(l.getPointId());
		
		Log.i("RoomActivity", "Predicted: " + l.getPointId() 
				+ ", Confidence=" + mIPS.getConfidence());
		
		mPredictedAndar = -1;
		mPredictedPoint = -1;

		if (predicted == null) {
			Log.e("ExecuteModelActivity", "Posicao prevista nao foi encontrada");
			return;
		}
		
		// Seleciona o andar correspondente no spinner
		for (int j=0;j<mAndares.size();++j) {
			if (mAndares.get(j).getId().equals(predicted.getIdAndar())) {
				mPredictedAndar = j;
				break;
			}
		}
		
		// Seleciona a posicao prevista
		List<Posicao> posicoes = mAndarToPosicoes.get(predicted.getIdAndar());
		for (int i=0;i<posicoes.size();++i) {
			if (posicoes.get(i).getId().equals(predicted.getId())) {
				mPredictedPoint = i;
				break;
			}
		}
		
		Log.e("ExecuteModelActivity", "Predicted: Andar=" + mPredictedAndar 
				+ ", Posicao=" + mPredictedPoint);
		
		// Atualiza o spinner e a view com o andar
		if (mCbRastrear.isChecked()) {
			if (mSpAndares.getSelectedItemPosition() != mPredictedAndar) {
				mSpAndares.setSelection(mPredictedAndar);
			} else {
				mAndar.setPredicted(mPredictedPoint);
			}
		} 
		
		else {
			if (mSpAndares.getSelectedItemPosition() == mPredictedAndar) {
				mAndar.setPredicted(mPredictedPoint);
			} else {
				mAndar.setPredicted(-1);
			}
		}

		// Send position to server
		mUpdateThread.setPosition(predicted.getIdRemoto(), System.currentTimeMillis());

		
//		TODO Set confidences in mAndar and draw later, as before
		
//		float c;
//		String s = "";
//		for (int y=0;y<mHeight;++y) {
//			for (int x=0;x<mWidth;++x) {
//				c = mIPS.getConfidence(new Location(mPosicaoIds[x][y]));
//				//s += c + " ";
//				if (c < 0f) {
//					Log.e("RoomActivity", "Invalid location: x=" + x + ", y=" + y);
//					mAndarView.setConfidence(x, y, 0f);
//				} else {
//					//Log.e("RoomActivity", "Excitation: x=" + x + ", y=" + y + ", c=" + c);
//					mAndarView.setConfidence(x, y, c);
//				}
//			}
//		}

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
			
			predictPosition(results);
		}
	}

	public class UpdateThread extends Thread {
		
		private String mDate;
		private long mIdPosicao;
		private long mIdDispositivo;
		private boolean mContinue;
		private Boolean mHasChanged;
		private SimpleDateFormat mFormater;
		private long mLastUpdate;

		public UpdateThread(long idDispositivo) {
			mFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
			mIdDispositivo = idDispositivo;
			mIdPosicao = -1l;
			mContinue = true;
			mHasChanged = false;
		}
		
		public void setPosition(long idPosicao, long time) {
			synchronized (mHasChanged) {
				if (idPosicao != mIdPosicao) {
					mDate = mFormater.format(new Date(time));
					mIdPosicao = idPosicao;
					mHasChanged = true;
				}
			}
		}
		
		public void finish() {
			mContinue = false;
		}
		
		@Override
		public void run() {
			boolean hasChanged;
			long idPosicao;
			long current;
			String date;
			
			mLastUpdate = System.currentTimeMillis();
			
			try {
				while(mContinue) {
					synchronized (mHasChanged) {
						date = mDate;
						hasChanged = mHasChanged;
						idPosicao = mIdPosicao;
						mHasChanged = false;
					}
					
					current = System.currentTimeMillis();
					if (hasChanged || current - mLastUpdate > 10000) {
						Log.i("UpdateThread", "Sending position idDispositivo:" + mIdDispositivo 
								+ " idPosicao:" + idPosicao
								+ " date:" + date);
						
						try {
							BasicConnector.registrarPosicao(mIdDispositivo, idPosicao, date);
							Log.i("Update Thread", "Position sent");
							mLastUpdate = current;
						} catch(Exception e) {
							Log.e("UpdateThread", "Failed to send coordinate");
						}
					}
					
					sleep(1000);
				}
			} catch(InterruptedException e) {
				
			} 
			
			// TODO Upload da posicao do usuario - OK
			// TODO Reverter coordenada Y do mapa - OK
			// TODO Obfuscar codigo e disponibilizar apk para a equipe - OK
			// TODO Interface de cadastro de pontos de coleta
			// TODO Download das imagens dos andares usando o mapserver
			
			// TODO Capacidade de treinar em um dispositivo e usar em outro (Com outro modelo)
			
			Log.i("UpdateThread", "Stopping UpdateThread");
		}
		
	}
	
}
