package br.ufrj.cos.labia.aips.dal;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import br.ufrj.cos.labia.aips.dto.DispositivoDTO;

public class Dispositivo {
	
	public static DispositivoDTO getInfoDispositivo(Context context) {
		DispositivoDTO dispositivo = new DispositivoDTO();
		dispositivo.marca = getMarca();
		dispositivo.imei = getImei(context);
		dispositivo.mac = getMac(context);
		dispositivo.modelo = getModel();
		return dispositivo;
	}
	
	private static String getModel() {
		return Build.MODEL;
	}

	private static String getMarca() {
		return Build.MANUFACTURER;
	}

	private static String getMac(Context context) {
		if ("google_sdk".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT))
			return "aa:aa:aa:aa:aa:aa";
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getMacAddress();
	}

	public static String getImei(Context context) {
		if ("google_sdk".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT))
			return "12345";
		
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
}
