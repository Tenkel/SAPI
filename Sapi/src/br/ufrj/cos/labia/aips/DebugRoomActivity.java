package br.ufrj.cos.labia.aips;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.ufrj.cos.labia.aips.comm.BasicConnector;
import br.ufrj.cos.labia.aips.customviews.AndarGridView;
import br.ufrj.cos.labia.aips.dal.AccessPoint;
import br.ufrj.cos.labia.aips.dal.AccessPointManager;
import br.ufrj.cos.labia.aips.dal.Andar;
import br.ufrj.cos.labia.aips.dal.AndarManager;
import br.ufrj.cos.labia.aips.dal.Bridge;
import br.ufrj.cos.labia.aips.dal.Dispositivo;
import br.ufrj.cos.labia.aips.dal.LeituraWiFi;
import br.ufrj.cos.labia.aips.dal.LeituraWifiManager;
import br.ufrj.cos.labia.aips.dal.Observacao;
import br.ufrj.cos.labia.aips.dal.ObservacaoManager;
import br.ufrj.cos.labia.aips.dal.Posicao;
import br.ufrj.cos.labia.aips.dal.PosicaoManager;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;
import br.ufrj.cos.labia.aips.ips.kde.KDE;
import br.ufrj.cos.labia.aips.ips.whips.WHIPS;


public class DebugRoomActivity extends FragmentActivity implements OnClickListener {

	private AndarManager mAndarManager;
	private PosicaoManager mPosicaoManager;
	private ObservacaoManager mObservacaoManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private AccessPointManager mAccessPointManager;
	private long mAndarId;
	private Andar mAndar;
	private AndarGridView mAndarView;
	private WifiReceiver2 mReceiver;
	private Button mBtCollect;
	private Button mBtTrain;
	private IPS mIPS;
	private int mState;
	private TreeMap<Long, Posicao> mPosicoes;
	private Map<Long, AccessPoint> mAccessPoints;
	private Long[][] mPosicaoIds;
	private int mWidth;
	private int mHeight;
	//private UpdateThread mUpdateThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);

		//mUpdateThread = new UpdateThread(Dispositivo.getImei(getApplicationContext()));
		//mUpdateThread.start();
		
		// Managers
		mAndarManager = new AndarManager(getApplicationContext());
		mPosicaoManager = new PosicaoManager(getApplicationContext());
		mObservacaoManager = new ObservacaoManager(getApplicationContext());
		mLeituraWIFIManager = new LeituraWifiManager(getApplicationContext());
		mAccessPointManager = new AccessPointManager(getApplicationContext());
		
		// Carrega o andar escolhido 
		mAndarId = getIntent().getExtras().getLong("andar_id");
		mAndar = mAndarManager.getFirstById(mAndarId);
		
		// Ouvinte para os eventos do Bridge
		mReceiver = new WifiReceiver2();
		
		// Botoes
		mBtTrain = (Button) findViewById(R.id.btTrain);
		mBtTrain.setOnClickListener(this);
		
		mBtCollect = (Button) findViewById(R.id.btCollect);
		mBtCollect.setOnClickListener(this);
		
		// Configura o AndarView
		String[] tmp = mAndar.getCamadas().split("_");
		mWidth = Integer.parseInt(tmp[0]);
		mHeight = Integer.parseInt(tmp[1]);
		mAndarView = (AndarGridView) findViewById(R.id.roomView);
		mPosicaoIds = new Long[mWidth][mHeight];
		mAndarView.setSize(mWidth, mHeight);

		carregarPosicoes(mAndarId);
		carregarAccessPoints();
		
		List<Observacao> samples = mObservacaoManager.getByIdAndar(mAndarId);
		for (Observacao sample : samples) {
			if (!mPosicoes.containsKey(sample.getIdPosicao()))
				mPosicoes.put(sample.getIdPosicao(), mPosicaoManager.getFirstById(sample.getIdPosicao()));
			
			Posicao p = mPosicoes.get(sample.getIdPosicao());
			int x = (int) Math.round(p.getX());
			int y = (int) Math.round(p.getY());
			mAndarView.addCollected(x, y);
		}
		
		Log.i("RoomActivity", "Samples in this room:" + samples.size());
		Log.i("RoomActivity", "Total Samples:" + mObservacaoManager.getCount());
		
		// Coloca o texto de referencia
		TextView txtReference = (TextView) findViewById(R.id.txtReference);
		txtReference.setText(mAndar.getURIMapa());
	}

	private void carregarAccessPoints() {
		mAccessPoints = new TreeMap<Long, AccessPoint>();
		List<AccessPoint> aps = mAccessPointManager.getAll();
		for (AccessPoint ap : aps) mAccessPoints.put(ap.getId(), ap);
	}

	private void carregarPosicoes(long idAndar) {
		mPosicoes = new TreeMap<Long, Posicao>();
		List<Posicao> posicoes = mPosicaoManager.getByidAndar(idAndar);
		for (Posicao p : posicoes) {
			int x = (int) Math.round(p.getX());
			int y = (int) Math.round(p.getY());
			mPosicoes.put(p.getId(), p);
			mPosicaoIds[x][y] = p.getId();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		getWindow().getDecorView().setKeepScreenOn(true);
		registerReceiver(mReceiver, new IntentFilter(
				Bridge.BROADCAST_SENSOR_DATA));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mUpdateThread.finish();
		if (mIPS != null) {
			mIPS.close();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add("Export samples");
		menu.add("Show Confidence").setCheckable(true);
		menu.add("Test mode").setCheckable(true).setChecked(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item))
			return true;
		
		if (item.getTitle() == "Export samples") {
			new LoadingDialog()
				.setWorker(new Worker() {
					@Override
					public void doHeavyWork(LoadingDialog dialog) {
						try {
							exportSamples2("samples_" + System.currentTimeMillis());
						} catch (IOException e) {
							Toast.makeText(getApplicationContext(), 
									"Could not save dataset", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				})
				.show(getFragmentManager(), "exporting_samples");
			
			return true;
		} if (item.getTitle() == "Show Confidence") {
			if (item.isChecked()) {
				mAndarView.setShowConfidence(false);
				item.setChecked(false);
			} else {
				mAndarView.setShowConfidence(true);
				item.setChecked(true);
			}
			return true;
		} if (item.getTitle() == "Test mode") {
			if (item.isChecked()) {
				mBtCollect.setEnabled(true);
				mBtTrain.setEnabled(true);
				mAndarView.setShowSelected(true);
				mAndarView.setShowPredicted(false);
				item.setChecked(false);
			} else {
				mBtCollect.setEnabled(false);
				mBtTrain.setEnabled(false);
				mAndarView.setShowSelected(false);
				mAndarView.setShowPredicted(true);
				item.setChecked(true);
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void exportSamples(String filename) throws IOException {
		
		File sd = Environment.getExternalStorageDirectory();
		Log.i("Path", sd.getAbsolutePath());
		File exportFold = new File(sd, "SCDP/Exported/");
		exportFold.mkdirs();
		
		File data = new File(exportFold, filename + "_data.csv");
		File attr = new File(exportFold, filename + "_attr.csv");
		
		List<Observacao> samples = mObservacaoManager.getByIdAndar(mAndarId);
		
		BufferedWriter dataWriter = new BufferedWriter(new FileWriter(data), 1024);
		BufferedWriter attrWriter = new BufferedWriter(new FileWriter(attr), 1024);
		
		for (Observacao s : samples) {
			List<LeituraWiFi> signals = mLeituraWIFIManager.getByidObservacao(s.getId());
			
			int length = signals.size();
			attrWriter.write(String.format("%d\t%d\n", s.getIdPosicao(), length));
			for (LeituraWiFi si : signals) {
				String bssid = mAccessPoints.get(si.getIdAccessPoint()).getbssid();
				int h1 = Integer.parseInt(bssid.substring( 0, 2), 16);
				int h2 = Integer.parseInt(bssid.substring( 3, 5), 16);
				int h3 = Integer.parseInt(bssid.substring( 6, 8), 16);
				int h4 = Integer.parseInt(bssid.substring( 9, 11), 16);
				int h5 = Integer.parseInt(bssid.substring(12, 14), 16);
				int h6 = Integer.parseInt(bssid.substring(15, 17), 16);
				float level = si.getValor();
				
				dataWriter.write(String.format(
						"%d\t%d\t%d\t%d\t%d\t%d\t%f\n", 
						h1, h2, h3, h4, h5, h6, level));
			}
		}
		
		dataWriter.close();
		attrWriter.close();
	}

	private void exportSamples2(String filename) throws IOException {
		
		File sd = Environment.getExternalStorageDirectory();
		Log.i("Path", sd.getAbsolutePath());
		File exportFold = new File(sd, "SCDP/Exported/");
		exportFold.mkdirs();
		
		File path = new File(exportFold, filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(path), 1024);
		
		List<Observacao> samples = mObservacaoManager.getByIdAndar(mAndarId);
		
		for (Observacao s : samples) {
			List<LeituraWiFi> signals = mLeituraWIFIManager.getByidObservacao(s.getId());
			
			Posicao p = mPosicoes.get(s.getIdPosicao());
			int x = (int) Math.round(p.getX());
			int y = (int) Math.round(p.getY());
			int target = y * mWidth + x;
			writer.write(String.format("%d\t%d\n", target, signals.size()));
			
			for (LeituraWiFi si : signals) {
				AccessPoint ap = mAccessPoints.get(si.getIdAccessPoint());
				writer.write(String.format("%d\t%s\t%s\n", 
						si.getValor(), ap.getbssid(), ap.getessid()));
			}
			
			writer.write("\n");
		}
		
		writer.close();
	}

	@Override
	public void onClick(View bt) {
		if (bt == mBtCollect) {
			if (mAndarView.isEnabled()) { 
				mBtCollect.setText("Stop collect");
				mAndarView.clearConfidences();
				mAndarView.setEnabled(false);
				mState = 0;
			} else {
				mBtCollect.setText("Start collect");
				mAndarView.setEnabled(true);
			}
		} 
		
		else if (bt == mBtTrain) {
			new LoadingDialog()
				.setWorker(new Worker() {
					@Override
					public void doHeavyWork(LoadingDialog dialog) {
						trainModel2();
					}
				})
				.show(getFragmentManager(), "training_model");
		}
	}

//	private void trainModel() {
//		
//		// Create model
//		List<Reading> signals = mSignalManager.buildReadingsFromSignalsWithinRoomId(mAndarId);
//		if (mIPS != null) mIPS.close();
//		mIPS = new PeakIPS(new WHIPS(signals, mAndar.getWidth()), 10);
//		
//		// Train model
//		List<Sample> samples = mSampleManager.getByRoomId(mAndarId);
//		for (Sample sample : samples) {
//			Log.i("RoomActivity", "Training " + sample.getId());
//			List<Reading> signals2 = mSignalManager.buildReadingsFromSignalsWithinSampleId(sample.getId());
//			mIPS.learn(signals2, new Location(sample.getX(), sample.getY()));
//		}
//		
//	}
	
	private void trainModel2() {
		List<Observacao> samples = mObservacaoManager.getByIdAndar(mAndarId);
		
		List<WIFISignal> all = new ArrayList<WIFISignal>();
		List<List<WIFISignal>> internal = new ArrayList<List<WIFISignal>>();
		
		for (Observacao s : samples) {
			List<LeituraWiFi> signals = mLeituraWIFIManager.getByidObservacao(s.getId());
			List<WIFISignal> signals2 = new ArrayList<WIFISignal>();
			
			for (LeituraWiFi s2 : signals) {
				AccessPoint ap = mAccessPoints.get(s2.getIdAccessPoint());
				WIFISignal s3 = new WIFISignal(ap.getbssid(), s2.getValor());
				signals2.add(s3);
				all.add(s3);
			}
			
			internal.add(signals2);
		}
		
		if (all.size() == 0) {
			Toast.makeText(getApplicationContext(), "No signals detected", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mIPS != null) mIPS.close();
		//mIPS = new PeakIPS(new WHIPS(all, mRoom.getWidth()), 1);
		// mIPS = new WHIPS(all);
		IPS ips = new KDE();
		
		int i = 0;
		for (Observacao s : samples) {
			ips.learn(new Reading(internal.get(i++)), new Location(s.getIdPosicao()));
		}
		
		mIPS = ips;
	}
	
	protected void predictPosition(ScanResult[] results) {
		if (mIPS == null || !mAndarView.isEnabled()) 
			return;
		
		List<WIFISignal> signals = new ArrayList<WIFISignal>();
		for (ScanResult r : results) 
			signals.add(new WIFISignal(r.BSSID, r.level));
		
		Reading reading = new Reading(signals);
		Location l = mIPS.predict(reading);
		
		if (l == null || l.getPointId() == null)  {
			Log.w("DebugRoomActivity", "IPS returned null as prediction");
			return;
		}
		
		Posicao p = mPosicoes.get(l.getPointId());
		int xx = (int) Math.round(p.getX());
		int yy = (int) Math.round(p.getY());
		mAndarView.setPredicted(xx, yy);
		//mUpdateThread.setPosition(xx, yy);
		Log.i("RoomActivity", "Confidence=" + mIPS.getConfidence());
		
		float c;
		//String s = "";
		for (int y=0;y<mHeight;++y) {
			for (int x=0;x<mWidth;++x) {
				c = mIPS.getConfidence(new Location(mPosicaoIds[x][y]));
				//s += c + " ";
				if (c < 0f) {
					Log.e("RoomActivity", "Invalid location: x=" + x + ", y=" + y + ", c=" + c);
					mAndarView.setConfidence(x, y, 0.5f);
				} else {
					//Log.e("RoomActivity", "Excitation: x=" + x + ", y=" + y + ", c=" + c);
					mAndarView.setConfidence(x, y, c);
				}
			}
		}
		//s += " - " + ((WHIPS)mIPS).locToTarget(l);
		//Log.e("Room", s);

		for (WIFISignal signal : reading.getSignals()) {
			if (signal.getBSSID().equals("00:25:86:f7:5b:ce")) {
				Log.i("ClassicSignalEncoder", "Pred: Level:" + signal.getLevel() + " Predicted:" + p.getX() + "," + p.getY());
			}
		}
	}

	public class WifiReceiver2 extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("RoomActivity", "Processing event from Bridge");
			
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
			
			if (mState <= 0) {
				if (!mAndarView.isEnabled()) {
					Observacao obs = mObservacaoManager.getById(idObservacao);
					obs.setidPosicao(mPosicaoIds[mAndarView.getSelectedX()][mAndarView.getSelectedY()]);
					mObservacaoManager.update(obs);
					
					mAndarView.addCollected(mAndarView.getSelectedX(), mAndarView.getSelectedY());
				}
			} else {
				mState--;
			}
		}
	}
//
//	public class UpdateThread extends Thread {
//		
//		private int mLastX;
//		private int mLastY;
//		private String mImei;
//		private Boolean mRefresh;
//		private boolean mContinue;
//
//		public UpdateThread(String imei) {
//			mLastX = -1;
//			mLastY = -1;
//			mImei = imei;
//			mRefresh = false;
//			mContinue = true;
//		}
//		
//		public void setPosition(int x, int y) {
//			synchronized (mRefresh) {
//				mRefresh = true;
//				mLastX = x;
//				mLastY = y;
//			}
//		}
//		
//		public void finish() {
//			mContinue = false;
//		}
//		
//		@Override
//		public void run() {
//			boolean tmp;
//			int x, y;
//			
//			try {
//				while(mContinue) {
//					synchronized (mRefresh) {
//						tmp = mRefresh;
//						x = mLastX;
//						y = mLastY;
//						mRefresh = false;
//					}
//					
//					if (tmp) {
//						Log.i("UpdateThread", "Sending position x:" + x +" y:" + y);
//						try{
//							BasicConnector.registrarPosicao(mImei, x, y, mWidth, mHeight);
//							Log.i("Update Thread", "Coordinates sent");
//						} catch(Exception e) {
//							Log.e("UpdateThread", "Failed to send coordinate");
//						}
//					}
//					
//					sleep(3000);
//				}
//			} catch(InterruptedException e) {
//				
//			} 
//			
//			// TODO Upload da posicao do usuario
//			// TODO Interface de cadastro de pontos de coleta
//			// TODO Obfuscar codigo e disponibilizar apk para a equipe
//			// TODO Download das imagens dos andares usando o mapserver
//			
//			// TODO Capacidade de treinar em um dispositivo e usar em outro (Com outro modelo)
//			
//			Log.i("UpdateThread", "Stopping UpdateThread");
//		}
//		
//	}
//	
}
