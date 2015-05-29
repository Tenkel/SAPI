package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PosicaoManager extends DataManager {

	public PosicaoManager(Context c) {
		super(c);
	}
	
	public boolean save(Posicao posicao) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			posicao.setId(db.insert(PosicaoTable, null, buildRecord(posicao)));
		} finally {
			db.close();
		}
		
		if(posicao.getId()==-1)
			return false;
		return true;
	}

	public List<Posicao> getByidAndar(long idAndar) {
		List<Posicao> list = new ArrayList<Posicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(PosicaoTable, Posicao.FIELDS, 
					Posicao.IDANDAR + "=" + idAndar, null, null, null, null);
			if(c.moveToFirst())
			do
				list.add(readRecord(c));
			while (c.moveToNext());
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<Posicao> getAll() {
		List<Posicao> list = new ArrayList<Posicao>();
		SQLiteDatabase db = getReadableDatabase();
		
		try {
			Cursor c = db.query(PosicaoTable, Posicao.FIELDS, null, null, null, null, null);
			
			while(c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public void delete(Posicao posicao) {
		if (posicao.getId() == null) 
			return;
		
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.delete(PosicaoTable, "id=?", new String[] {posicao.getId().toString()});
		} finally {
			db.close();
		}
	}

	public Posicao getFirstById(Long id) {
		if (id == null)
			return null;
		
		SQLiteDatabase db = getReadableDatabase();
		Posicao posicao = null;
		
		try {
			Cursor c = db.query(PosicaoTable, Posicao.FIELDS, 
					Posicao.ID + "=" + id, null, null, null, null);
			
			if (c.moveToFirst())
				posicao = readRecord(c);
			
			c.close();
		} finally {
			db.close();
		}
		
		return posicao;
	}

	public Map<Long, Posicao> getAllAsIDMap() {
		Map<Long, Posicao> map = new HashMap<Long, Posicao>();
		
		List<Posicao> all = getAll();
		for (Posicao single : all)
			map.put(single.getId(), single);
		
		return map;
	}

	private Posicao readRecord(Cursor c) {
		Posicao posicao = new Posicao();
		posicao.setId(c.getLong(0));
		posicao.setX(c.getDouble(1));
		posicao.setY(c.getDouble(2));
		posicao.setIdAndar(c.getLong(3));
		posicao.setIdRemoto(c.getLong(4));
		posicao.setReferencia(c.getString(5));
		posicao.setNome(c.getString(6));
		posicao.setPropaganda(c.getString(7));
		posicao.setAtivo(c.getInt(8)!=0);
		return posicao;
	}
	
	public Posicao getEmptyRemote(){
		SQLiteDatabase db = getWritableDatabase();
		
		Long maxremote = (long) 0;
		
		Cursor c = db.rawQuery("SELECT MAX(" + Posicao.IDREMOTO + ") FROM " + PosicaoTable, null);
		
		if(c.moveToFirst())
			maxremote = c.getLong(0);
		
		Posicao posicao = new Posicao();
		
		posicao.setIdRemoto(maxremote);
		
		return posicao;
	}

	public void update(Posicao pos) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("UPDATE " + PosicaoTable + " SET " + 
					Posicao.PROPAGANDA + "= \"" + pos.getPropaganda() + "\", " + 
					Posicao.ATIVO + "=" + (pos.isAtivo()? 1 : 0) + 
					" WHERE "+ Posicao.IDREMOTO + "=" + pos.getIdRemoto());
		} finally {
			db.close();
		}
	}

	
	private ContentValues buildRecord(Posicao posicao) {
		ContentValues values = new ContentValues();
		values.put(Posicao.X, posicao.getX());
		values.put(Posicao.Y, posicao.getY());
		values.put(Posicao.IDANDAR, posicao.getIdAndar());
		values.put(Posicao.IDREMOTO, posicao.getIdRemoto());
		values.put(Posicao.REFERENCIA, posicao.getReferencia());
		values.put(Posicao.NOME, posicao.getNome());
		values.put(Posicao.PROPAGANDA, posicao.getPropaganda());
		values.put(Posicao.ATIVO, posicao.isAtivo() ? 1 : 0);
		return values;
	}

}
