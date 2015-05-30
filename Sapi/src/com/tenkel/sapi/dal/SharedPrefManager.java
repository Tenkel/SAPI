package com.tenkel.sapi.dal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefManager {

	private static final String PREFERENCES_FILE = "aips_config_file";
	private static final String ID_DISPOSITIVO = "idDispositivo";
	private static final String IMEI = "imei";
	private static final String ANDROIDID = "Android_ID";
	private static final String ID_PAIS = "idPais";
	private static final String ID_USER = "idUser";
	private static final String TOKEN = "Token";
	private static final String DATE_TIME  = "dateTime";
	private static final String CONFIANCA = "Confianca";
	
	private SharedPreferences mPref;
	private Editor mEditPref;

	public SharedPrefManager(Context context, boolean edit) {
		mPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		if (edit) mEditPref = mPref.edit();
	}
	
	public String getImei(Context context) {
		return mPref.getString(IMEI, Dispositivo.getImei(context));
	}
	
	public String getToken() {
		return mPref.getString(TOKEN, null);
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
	
	public void setPaisID(long id) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putLong(ID_PAIS, id);
	}
	
	public void setUserID(int id) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putInt(ID_USER, id);
	}
	
	public void setToken(String token) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putString(TOKEN, token);
	}
	
	public boolean save() {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		return mEditPref.commit();
	}
	
	public String getAndroidID(Context context) {
		
		return mPref.getString(ANDROIDID, Dispositivo.getAndroidID(context));
	}

	public int getUserID() {
		return mPref.getInt(ID_USER, -1);
	}

	public void setDT(String tempoinicio) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putString(DATE_TIME, tempoinicio);
	} 
	
	public void setConfianca(float confianca) {
		if (mEditPref == null) throw new IllegalStateException(
				"SharedPrefManager is open as read only");
		mEditPref.putFloat(CONFIANCA, confianca);
	}
	
	public float getConfianca(){
		return mPref.getFloat(CONFIANCA, 100);
	}
	
	
}
