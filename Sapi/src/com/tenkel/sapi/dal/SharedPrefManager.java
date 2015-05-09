package com.tenkel.sapi.dal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefManager {

	private static final String PREFERENCES_FILE = "aips_config_file";
	private static final String ID_DISPOSITIVO = "idDispositivo";
	private static final String IMEI = "imei";
	private static final String ANDROIDID = "Android_ID";
	
	private SharedPreferences mPref;
	private Editor mEditPref;

	public SharedPrefManager(Context context, boolean edit) {
		mPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		if (edit) mEditPref = mPref.edit();
	}
	
	public String getImei(Context context) {
		return mPref.getString(IMEI, Dispositivo.getImei(context));
	}
	
	public void setImei(String imei) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putString(IMEI, imei);
	}
	
	public long getIdDispositivo() {
		return mPref.getLong(ID_DISPOSITIVO, -1);
	}
	
	public void setIdDispositivo(long id) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putLong(ID_DISPOSITIVO, id);
	}
	
	public boolean save() {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		return mEditPref.commit();
	}
	
	public String getAndroidID(Context context) {
		
		return mPref.getString(ANDROIDID, Dispositivo.getAndroidID(context));
	} 
	
}
