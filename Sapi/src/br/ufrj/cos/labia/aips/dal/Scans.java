package br.ufrj.cos.labia.aips.dal;

import java.util.List;

import android.net.wifi.ScanResult;

public interface Scans {
	void processScans(List<ScanResult> results, float[] gyro, float millibars, float temp_celsius, float[] magn_uT, float proximity_cm, float[] gravity, float humidity, float[] acceleration, double lat, double lon);
	void calibrateSensor();

}
