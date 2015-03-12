package br.ufrj.cos.labia.aips.dal;

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
	
	public void save(Posicao posicao) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			posicao.setId(db.insert(PosicaoTable, null, buildRecord(posicao)));
		} finally {
			db.close();
		}
	}

	public List<Posicao> getByidAndar(long idAndar) {
		List<Posicao> list = new ArrayList<Posicao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(PosicaoTable, Posicao.FIELDS, 
					Posicao.IDANDAR + "=" + idAndar, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
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
		return posicao;
	}
	
	private ContentValues buildRecord(Posicao posicao) {
		ContentValues values = new ContentValues();
		values.put(Posicao.X, posicao.getX());
		values.put(Posicao.Y, posicao.getY());
		values.put(Posicao.IDANDAR, posicao.getIdAndar());
		values.put(Posicao.IDREMOTO, posicao.getIdRemoto());
		return values;
	}

}
