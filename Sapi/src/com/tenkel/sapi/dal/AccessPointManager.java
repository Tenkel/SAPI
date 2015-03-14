package com.tenkel.sapi.dal;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccessPointManager extends DataManager {

	private final String[] AccessPointCol = new String[] {AccessPoint.ID, AccessPoint.BSSID, AccessPoint.ESSID,AccessPoint.CONFIANCA,AccessPoint.IDPOSICAO};
	private final String[] AccessPointbyPosition = new String[] {AccessPoint.ID, AccessPoint.BSSID, AccessPoint.ESSID,AccessPoint.CONFIANCA};
	
	public AccessPointManager(Context c) {
		super(c);
	}
	
	public void save(AccessPoint accesspoint) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put(AccessPoint.IDPOSICAO, accesspoint.getidPosicao());
			values.put(AccessPoint.BSSID, accesspoint.getbssid());
			values.put(AccessPoint.ESSID, accesspoint.getessid());
			values.put(AccessPoint.CONFIANCA, accesspoint.getconfianca());
			accesspoint.setId(db.insert(DataManager.AccessPointTable, null, values));
		} finally {
			db.close();
		}
	}

	public List<AccessPoint> getByidPosicao(long idPosicao) {
		List<AccessPoint> list = new ArrayList<AccessPoint>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = AccessPoint.IDPOSICAO + " == " + idPosicao;
			Cursor c = db.query(AccessPointTable, AccessPointbyPosition, where, null, null, null, null);
			
			while (c.moveToNext()) {
				AccessPoint accesspoint = new AccessPoint();
				accesspoint.setId(c.getLong(0));
				accesspoint.setbssid(c.getString(1));
				accesspoint.setessid(c.getString(2));
				accesspoint.setconfianca(c.getFloat(3));
				list.add(accesspoint);
			}
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<AccessPoint> getByIdObservacao(long idObservacao) {
		List<AccessPoint> list = new ArrayList<AccessPoint>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT ap." + AccessPoint.ID + ", ap." + AccessPoint.BSSID + ", ap." + AccessPoint.ESSID + ", ap." + AccessPoint.CONFIANCA + ", ap." + AccessPoint.IDPOSICAO 
					+ " FROM " + AccessPointTable + " ap INNER JOIN " + LeituraWiFiTable + " lwifi ON lwifi.idAccessPoint=ap.id "
					+ " WHERE lwifi.idObservacao=" + idObservacao, null);
			
			while (c.moveToNext()){
				AccessPoint accesspoint = new AccessPoint();
				accesspoint.setId(c.getLong(0));
				accesspoint.setbssid(c.getString(1));
				accesspoint.setessid(c.getString(2));
				accesspoint.setconfianca(c.getFloat(3));
				accesspoint.setidPosicao(c.getLong(4));
				list.add(accesspoint);
			}
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public List<AccessPoint> getByFuncaoPosicaoIdPosicao(long idPosicao) {
		List<AccessPoint> list = new ArrayList<AccessPoint>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT ap." + AccessPoint.ID + ", ap." + AccessPoint.BSSID + ", ap." + AccessPoint.ESSID + ", ap." + AccessPoint.CONFIANCA + ", ap." + AccessPoint.IDPOSICAO 
					+ " FROM " + AccessPointTable + " ap INNER JOIN " + FuncaoPosicaoTable + " fp ON fp.idAccessPoint=ap.id "
					+ " WHERE fp.idPosicao=" + idPosicao, null);
			
			while (c.moveToNext()){
				AccessPoint accesspoint = new AccessPoint();
				accesspoint.setId(c.getLong(0));
				accesspoint.setbssid(c.getString(1));
				accesspoint.setessid(c.getString(2));
				accesspoint.setconfianca(c.getFloat(3));
				accesspoint.setidPosicao(c.getLong(4));
				list.add(accesspoint);
			}
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public List<AccessPoint> getByObservacaoIdPosicao(long idPosicao) {
		List<AccessPoint> list = new ArrayList<AccessPoint>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT ap." + AccessPoint.ID + ", ap." + AccessPoint.BSSID + ", ap." + AccessPoint.ESSID + ", ap." + AccessPoint.CONFIANCA + ", o." + AccessPoint.IDPOSICAO 
					+ " FROM " + AccessPointTable + " ap INNER JOIN " + LeituraWiFiTable + " lwifi ON lwifi.idAccessPoint=ap.id "
					+ " INNER JOIN " + ObservacaoTable + " o ON o.id=lwifi.idObservacao "
					+ " WHERE o.idPosicao=" + idPosicao, null);
			
			while (c.moveToNext()){
				AccessPoint accesspoint = new AccessPoint();
				accesspoint.setId(c.getLong(0));
				accesspoint.setbssid(c.getString(1));
				accesspoint.setessid(c.getString(2));
				accesspoint.setconfianca(c.getFloat(3));
				accesspoint.setidPosicao(c.getLong(4));
				list.add(accesspoint);
			}
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}
	
	public List<AccessPoint> getAll() {
		List<AccessPoint> list = new ArrayList<AccessPoint>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(DataManager.AccessPointTable, AccessPointCol, null, null, null, null, null);
			
			while (c.moveToNext()) {
				AccessPoint accesspoint = new AccessPoint();
				accesspoint.setId(c.getLong(0));
				accesspoint.setbssid(c.getString(1));
				accesspoint.setessid(c.getString(2));
				accesspoint.setconfianca(c.getFloat(3));
				accesspoint.setidPosicao(c.getLong(4));
				list.add(accesspoint);
			}
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public Map<String, AccessPoint> getAllAsBSSIDMap() {
		Map<String, AccessPoint> aps = new HashMap<String, AccessPoint>();
		
		List<AccessPoint> allaps = getAll();
		for (AccessPoint ap : allaps)
			aps.put(ap.getbssid(), ap);
		
		return aps;
	}

	public Map<Long, AccessPoint> getAllAsIDMap() {
		Map<Long, AccessPoint> aps = new HashMap<Long, AccessPoint>();
		
		List<AccessPoint> allaps = getAll();
		for (AccessPoint ap : allaps)
			aps.put(ap.getId(), ap);
		
		return aps;
	}

	public void update(AccessPoint obs) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("UPDATE " + ObservacaoTable + " SET " + 
					AccessPoint.IDPOSICAO + "=" + obs.getidPosicao() +
					" WHERE ID=" + obs.getId());
		} finally {
			db.close();
		}
	}
	
}
