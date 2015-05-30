package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AndarManager extends DataManager {
	
	public AndarManager(Context context) {
		super(context);
	}

	public void save(Andar andar) {
		SQLiteDatabase db = getWritableDatabase();
		Long id = (long) -1;
		
		try {
			ContentValues values = new ContentValues();
			values.put(Andar.IDLOCAL, andar.getIdLocal());
			values.put(Andar.NOME, andar.getNome());
			values.put(Andar.URIMAPA, andar.getURIMapa());
			values.put(Andar.CAMADAS, andar.getCamadas());
			values.put(Andar.IDREMOTO, andar.getIdRemoto());
			values.put(Andar.X1, andar.getX1());
			values.put(Andar.Y1, andar.getY1());
			values.put(Andar.X2, andar.getX2());
			values.put(Andar.Y2, andar.getY2());
			values.put(Andar.IMAGE, andar.getImage());
			if((id = db.insert(AndarTable, null, values))==-1)
				setByIdRemoto(andar, andar.getIdRemoto());
			else
				andar.setId(id);
		} finally {
			db.close();
		}
	}
	
	private void setByIdRemoto(Andar andar, Long idRemoto) {
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = Andar.IDREMOTO + "=" + idRemoto;
			Cursor c = db.query(AndarTable, Andar.FIELDS, where, null, null, null, null);
			
			if (c.moveToFirst()) {
				andar.setId(c.getLong(0));
				andar.setIdLocal(c.getLong(1));
				andar.setNome(c.getString(2));
				andar.setURIMapa(c.getString(3));
				andar.setCamadas(c.getString(4));
				andar.setIdRemoto(c.getLong(5));
				andar.setX1(c.getDouble(6));
				andar.setY1(c.getDouble(7));
				andar.setX2(c.getDouble(8));
				andar.setY2(c.getDouble(9));
				andar.setImage(c.getBlob(10));
			}
			c.close();
		} finally {
			db.close();
		}
	}

	public List<Andar> getAllWithNullIdLocal() {
		List<Andar> list = new ArrayList<Andar>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = Andar.IDLOCAL + " IS NULL";
			Cursor c = db.query(AndarTable, Andar.FIELDS, where, null, null, null, null);
			if(c.moveToFirst())
			do
			 {
				Andar andar = new Andar();
				andar.setId(c.getLong(0));
				andar.setIdLocal(c.getLong(1));
				andar.setNome(c.getString(2));
				andar.setURIMapa(c.getString(3));
				andar.setCamadas(c.getString(4));
				andar.setIdRemoto(c.getLong(5));
				andar.setX1(c.getDouble(6));
				andar.setY1(c.getDouble(7));
				andar.setX2(c.getDouble(8));
				andar.setY2(c.getDouble(9));
				andar.setImage(c.getBlob(10));
				list.add(andar);
			}
			while (c.moveToNext());
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public void delete(Andar andar) {
		if (andar.getId() == null) 
			return;
		
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.delete(AndarTable, "id=?", new String[] {andar.getId().toString()});
		} finally {
			db.close();
		}
	}

	public Andar getFirstById(long id) {
		Andar andar = null;
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = Andar.ID + "=" + id;
			Cursor c = db.query(AndarTable, Andar.FIELDS, where, null, null, null, null);
			
			if (c.moveToNext()) {
				andar = new Andar();
				andar.setId(c.getLong(0));
				andar.setIdLocal(c.getLong(1));
				andar.setNome(c.getString(2));
				andar.setURIMapa(c.getString(3));
				andar.setCamadas(c.getString(4));
				andar.setIdRemoto(c.getLong(5));
				andar.setX1(c.getDouble(6));
				andar.setY1(c.getDouble(7));
				andar.setX2(c.getDouble(8));
				andar.setY2(c.getDouble(9));
				andar.setImage(c.getBlob(10));
			}
			c.close();
		} finally {
			db.close();
		}

		return andar;
	}

	public List<Andar> getByIdLocal(long idLocal) {
		List<Andar> list = new ArrayList<Andar>();
		SQLiteDatabase db = getReadableDatabase();
		
		try {
			String where = Andar.IDLOCAL + "=" + idLocal;
			Cursor c = db.query(AndarTable, Andar.FIELDS, where, null, null, null, null);
			
			while (c.moveToNext()) {
				Andar andar = new Andar();
				andar.setId(c.getLong(0));
				andar.setIdLocal(c.getLong(1));
				andar.setNome(c.getString(2));
				andar.setURIMapa(c.getString(3));
				andar.setCamadas(c.getString(4));
				andar.setIdRemoto(c.getLong(5));
				andar.setX1(c.getDouble(6));
				andar.setY1(c.getDouble(7));
				andar.setX2(c.getDouble(8));
				andar.setY2(c.getDouble(9));
				andar.setImage(c.getBlob(10));
				list.add(andar);
			}
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public void update(Andar andar) {
		SQLiteDatabase db = getWritableDatabase();
		
		String where = Andar.ID + "=" + andar.getId();
		
		try {
			ContentValues values = new ContentValues();
			//values.put(Andar.IDLOCAL, andar.getIdLocal());
			values.put(Andar.NOME, andar.getNome());
			values.put(Andar.URIMAPA, andar.getURIMapa());
			values.put(Andar.CAMADAS, andar.getCamadas());
			values.put(Andar.IDREMOTO, andar.getIdRemoto());
			values.put(Andar.X1, andar.getX1());
			values.put(Andar.Y1, andar.getY1());
			values.put(Andar.X2, andar.getX2());
			values.put(Andar.Y2, andar.getY2());
			values.put(Andar.IMAGE, andar.getImage());
			
			db.update(AndarTable, values, where, null);
		} finally {
			db.close();
		}
	}

}
