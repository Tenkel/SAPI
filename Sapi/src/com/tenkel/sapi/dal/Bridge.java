package com.tenkel.sapi.dal;

import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Bridge extends Service implements Scans {

	public static final String BROADCAST_SENSOR_DATA = "com.tenkel.sapi.SENSOR_DATA";
	
	private static boolean mIsRunning;
	
	// WiFi Variables
	private LoopScanner receiver;

	private LeituraSensoresManager mLeituraSensoresManager;

	private LeituraWifiManager mLeituraWiFiManager;

	private AccessPointManager mAccessPointManager;

	private ObservacaoManager mObservacaoManager;

	private Map<String, AccessPoint> mAccessPoints;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("Bridge", "onCreate");
		
		mLeituraSensoresManager = new LeituraSensoresManager(getApplicationContext());
		mLeituraWiFiManager = new LeituraWifiManager(getApplicationContext());
		mAccessPointManager = new AccessPointManager(getApplicationContext());
		mObservacaoManager = new ObservacaoManager(getApplicationContext());
		
		mAccessPoints = mAccessPointManager.getAllAsBSSIDMap();
		
		Toast.makeText(getApplicationContext(), "Captura iniciada", Toast.LENGTH_SHORT).show();
		receiver = new LoopScanner(getApplicationContext(), this);
		setRunning(true);
		receiver.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("Bridge", "onDestroy");
		
		Toast.makeText(getApplicationContext(), "Captura encerrada", Toast.LENGTH_SHORT).show();
		setRunning(false);
		receiver.stop();
	}
	
	@Override
	public void processScans(List<ScanResult> results, float[] gyro,
			float millibars, float temp_celsius, float[] magn_uT,
			float proximity_cm, float[] gravity, float humidity,
			float[] acceleration, double lat, double lon) {
		Log.i("Bridge", "processScans");
		
		// Salva a observação
		Observacao obs = new Observacao();
		obs.settimestamp(System.currentTimeMillis());
		mObservacaoManager.save(obs);
		
		// Para cada leitura de wifi
		for (ScanResult s : results) {
			
			// Cria o AccessPoint se este não for conhecido 
			if (!mAccessPoints.containsKey(s.BSSID)) {
				AccessPoint ap = new AccessPoint();
				ap.setbssid(s.BSSID);
				ap.setessid(s.SSID);
				ap.setconfianca(1f);
				mAccessPointManager.save(ap);
				mAccessPoints.put(ap.getbssid(), ap);
			}
			
			// Salva a leitura
			LeituraWiFi leitura = new LeituraWiFi();
			leitura.setidAccessPoint(mAccessPoints.get(s.BSSID).getId());
			leitura.setvalor(s.level);
			leitura.setidObservacao(obs.getId());
			mLeituraWiFiManager.save(leitura);
		}

		// TODO Store sensor readings
		// LeituraSensores sens = new LeituraSensores();
		// ...
		// mLeituraSensoresManager.save(sens);
		
		// Notifica os ouvintes do evento
		Intent intent = new Intent(BROADCAST_SENSOR_DATA);
		intent.putExtra("idObservacao", obs.getId());
		intent.putExtra("wifi", results.toArray());
		sendBroadcast(intent);
	}

	@Override
	public void calibrateSensor() {
		Intent intent = new Intent("com.tenkel.sapi.NEEDS_CALIBRATION");
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static boolean isRunning() {
		return mIsRunning;
	}
	
	private void setRunning(boolean r) {
		mIsRunning = r;
	}

	public static void start(Context context) {
		context.startService(new Intent(context, Bridge.class));
	}

	public static void stop(Context context) {
		context.stopService(new Intent(context, Bridge.class));
	}
	
}
