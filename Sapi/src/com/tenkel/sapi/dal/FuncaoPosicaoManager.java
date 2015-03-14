package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class FuncaoPosicaoManager extends DataManager {
	
	SQLiteStatement maxvalue;

	public FuncaoPosicaoManager(Context c) {
		super(c);
	}
	
	public void save(FuncaoPosicao funcaoposicao) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put(FuncaoPosicao.IDACCESSPOINT, funcaoposicao.getIdAccessPoint());
			values.put(FuncaoPosicao.IDPOSICAO, funcaoposicao.getIdPosicao());
			values.put(FuncaoPosicao.IDTIPO, funcaoposicao.getIdTipo());
			values.put(FuncaoPosicao.URI, funcaoposicao.getUri());
			funcaoposicao.setId(db.insert(FuncaoPosicaoTable, null, values));
		} finally {
			db.close();
		}
	}

	public List<FuncaoPosicao> getByidAccessPoint(long idAccessPoint) {
		List<FuncaoPosicao> list = new ArrayList<FuncaoPosicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT " + FuncaoPosicao.STRFIELDS 
					+ " FROM " + FuncaoPosicaoTable 
					+ " WHERE " + FuncaoPosicao.IDACCESSPOINT + "=" + idAccessPoint, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<FuncaoPosicao> getByidPosicao(long idPosicao) {
		List<FuncaoPosicao> list = new ArrayList<FuncaoPosicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT " + FuncaoPosicao.STRFIELDS 
					+ " FROM " + FuncaoPosicaoTable 
					+ " WHERE " + FuncaoPosicao.IDPOSICAO + "=" + idPosicao, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}
	/*
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

	public List<FuncaoPosicao> getByidPosicao(long idPosicao) {
		List<FuncaoPosicao> list = new ArrayList<FuncaoPosicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT " + LeituraWiFi.STRFIELDS 
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

	public List<FuncaoPosicao> getByidObservacao(long idObservacao) {
		List<FuncaoPosicao> list = new ArrayList<FuncaoPosicao>();
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
*/
	public List<FuncaoPosicao> getAll() {
		List<FuncaoPosicao> list = new ArrayList<FuncaoPosicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(FuncaoPosicaoTable, FuncaoPosicao.FIELDS, null, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	private FuncaoPosicao readRecord(Cursor c) {
		FuncaoPosicao record = new FuncaoPosicao();
		record.setId(c.getLong(0));
		record.setIdPosicao(c.getLong(1));
		record.setIdAccessPoint(c.getLong(2));
		record.setIdTipo(c.getLong(3));
		record.setUri(c.getString(4));
		return record;
	}

}
