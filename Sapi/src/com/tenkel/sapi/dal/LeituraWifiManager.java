package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class LeituraWifiManager extends DataManager {
	
	SQLiteStatement maxvalue;

	public LeituraWifiManager(Context c) {
		super(c);
	}
	
	public void save(LeituraWiFi leiturawifi) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put(LeituraWiFi.IDACCESSPOINT, leiturawifi.getIdAccessPoint());
			values.put(LeituraWiFi.IDOBSERVACAO, leiturawifi.getIdObservacao());
			values.put(LeituraWiFi.VALOR, leiturawifi.getValor());
			leiturawifi.setId(db.insert(LeituraWiFiTable, null, values));
		} finally {
			db.close();
		}
	}

	public List<LeituraWiFi> getByidAccessPoint(long idAccessPoint) {
		List<LeituraWiFi> list = new ArrayList<LeituraWiFi>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT " + LeituraWiFi.STRFIELDS 
					+ " FROM " + LeituraWiFiTable 
					+ " WHERE " + LeituraWiFi.IDACCESSPOINT + "=" + idAccessPoint, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}
	
	public long getMaxValueByidPosicao(long idPosicao){
		SQLiteDatabase db = getReadableDatabase();
		maxvalue = db.compileStatement("SELECT MAX(" + LeituraWiFi.VALOR + ")" 
				+ " FROM "+ LeituraWiFiTable + ", " + ObservacaoTable 
				+ " WHERE " + LeituraWiFiTable + "." + LeituraWiFi.IDOBSERVACAO + " = " + ObservacaoTable + "." + Observacao.ID
							+ " AND " 
							+ ObservacaoTable + "." + Observacao.IDPOSICAO + "= ?");
		maxvalue.bindLong(1, idPosicao);
		long longmaxvalue = maxvalue.simpleQueryForLong();
		db.close();

		return longmaxvalue;
	}

	public List<LeituraWiFi> getByidPosicao(long idPosicao) {
		List<LeituraWiFi> list = new ArrayList<LeituraWiFi>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT LeituraWiFi.id, idAccessPoint, idObservacao, valor" 
					+ " FROM "+ LeituraWiFiTable + ", " + ObservacaoTable 
					+ " WHERE " + LeituraWiFiTable + "." + LeituraWiFi.IDOBSERVACAO + " = " + ObservacaoTable + "." + Observacao.ID
								+ " AND " 
								+ ObservacaoTable + "." + Observacao.IDPOSICAO + "=" + idPosicao, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<LeituraWiFi> getByidObservacao(long idObservacao) {
		List<LeituraWiFi> list = new ArrayList<LeituraWiFi>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT " + LeituraWiFi.STRFIELDS 
					+ " FROM " + LeituraWiFiTable 
					+ " WHERE " + LeituraWiFi.IDOBSERVACAO + "=" + idObservacao, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<LeituraWiFi> getAll() {
		List<LeituraWiFi> list = new ArrayList<LeituraWiFi>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(LeituraWiFiTable, LeituraWiFi.FIELDS, null, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	private LeituraWiFi readRecord(Cursor c) {
		LeituraWiFi record = new LeituraWiFi();
		record.setId(c.getLong(0));
		record.setidAccessPoint(c.getLong(1));
		record.setidObservacao(c.getLong(2));
		record.setvalor(c.getInt(3));
		return record;
	}

}
