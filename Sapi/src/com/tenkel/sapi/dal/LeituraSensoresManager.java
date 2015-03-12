package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LeituraSensoresManager extends DataManager {

	public LeituraSensoresManager(Context c) {
		super(c);
	}
	
	public void save(LeituraSensores leiturasensores) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put(LeituraSensores.IDOBSERVACAO, leiturasensores.getidObservacao());
			values.put(LeituraSensores.IDUNIDADESENSORES, leiturasensores.getidUnidadeSensores());
			values.put(LeituraSensores.ACCX,leiturasensores.getaccX());
			values.put(LeituraSensores.ACCY,leiturasensores.getaccY());
			values.put(LeituraSensores.ACCZ,leiturasensores.getaccZ());
			values.put(LeituraSensores.TEMP,leiturasensores.gettemp());
			values.put(LeituraSensores.GRAVX,leiturasensores.getgravX());
			values.put(LeituraSensores.GRAVY,leiturasensores.getgravY());
			values.put(LeituraSensores.GRAVZ,leiturasensores.getgravZ());
			values.put(LeituraSensores.GYROX,leiturasensores.getgyroX());
			values.put(LeituraSensores.GYROY,leiturasensores.getgyroY());
			values.put(LeituraSensores.GYROZ,leiturasensores.getgyroZ());
			values.put(LeituraSensores.LIGHT,leiturasensores.getlight());
			values.put(LeituraSensores.LINACCX,leiturasensores.getlinAccX());
			values.put(LeituraSensores.LINACCY,leiturasensores.getlinAccY());
			values.put(LeituraSensores.LINACCZ,leiturasensores.getlinAccZ());
			values.put(LeituraSensores.MAGX,leiturasensores.getmagX());
			values.put(LeituraSensores.MAGY,leiturasensores.getmagY());
			values.put(LeituraSensores.MAGZ,leiturasensores.getmagZ());
			values.put(LeituraSensores.ORIENTX,leiturasensores.getorientX());
			values.put(LeituraSensores.ORIENTY,leiturasensores.getorientY());
			values.put(LeituraSensores.ORIENTZ,leiturasensores.getorientZ());
			values.put(LeituraSensores.PRESS,leiturasensores.getpress());
			values.put(LeituraSensores.HUM,leiturasensores.gethum());
			values.put(LeituraSensores.ROTX,leiturasensores.getrotX());
			values.put(LeituraSensores.ROTY,leiturasensores.getrotY());
			values.put(LeituraSensores.ROTZ,leiturasensores.getrotZ());
			values.put(LeituraSensores.ROTSCALAR,leiturasensores.getrotScalar());
			leiturasensores.setId(db.insert(DataManager.LeituraSensoresTable, null, values));
		} finally {
			db.close();
		}
	}

	public List<LeituraSensores> getByidObservacao(long idObservacao) {
		List<LeituraSensores> list = new ArrayList<LeituraSensores>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = LeituraSensores.IDOBSERVACAO + " == " + idObservacao;
			Cursor c = db.query(LeituraSensoresTable, LeituraSensores.FIELDS, where, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<LeituraSensores> getByidUnidadeSensores(long idUnidadeSensores) {
		List<LeituraSensores> list = new ArrayList<LeituraSensores>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = LeituraSensores.IDUNIDADESENSORES + " == " + idUnidadeSensores;
			Cursor c = db.query(LeituraSensoresTable, LeituraSensores.FIELDS, where, null, null, null, null);
			
			while (c.moveToNext()) 
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<LeituraSensores> getAll() {
		List<LeituraSensores> list = new ArrayList<LeituraSensores>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(LeituraSensoresTable, LeituraSensores.FIELDS,
					null, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	private LeituraSensores readRecord(Cursor c) {
		LeituraSensores leiturasensores = new LeituraSensores();
		leiturasensores.setId(c.getLong(0));
		leiturasensores.setidObservacao(c.getLong(1));
		leiturasensores.setidUnidadeSensores(c.getLong(2));
		leiturasensores.setaccX(c.getFloat(3));
		leiturasensores.setaccY(c.getFloat(4));
		leiturasensores.setaccZ(c.getFloat(5));
		leiturasensores.settemp(c.getFloat(6));
		leiturasensores.setgravX(c.getFloat(7));
		leiturasensores.setgravY(c.getFloat(8));
		leiturasensores.setgravZ(c.getFloat(9));
		leiturasensores.setgyroX(c.getFloat(10));
		leiturasensores.setgyroY(c.getFloat(11));
		leiturasensores.setgyroZ(c.getFloat(12));
		leiturasensores.setlight(c.getFloat(13));
		leiturasensores.setlinAccX(c.getFloat(14));
		leiturasensores.setlinAccY(c.getFloat(15));
		leiturasensores.setlinAccZ(c.getFloat(16));
		leiturasensores.setmagX(c.getFloat(17));
		leiturasensores.setmagY(c.getFloat(18));
		leiturasensores.setmagZ(c.getFloat(19));
		leiturasensores.setorientX(c.getFloat(20));
		leiturasensores.setorientY(c.getFloat(21));
		leiturasensores.setorientZ(c.getFloat(22));
		leiturasensores.setpress(c.getFloat(23));
		leiturasensores.sethum(c.getFloat(24));
		leiturasensores.setrotX(c.getFloat(25));
		leiturasensores.setrotY(c.getFloat(26));
		leiturasensores.setrotZ(c.getFloat(27));
		leiturasensores.setrotScalar(c.getFloat(28));
		return leiturasensores;
	}

}
