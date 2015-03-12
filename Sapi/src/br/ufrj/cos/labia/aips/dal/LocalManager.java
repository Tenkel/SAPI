package br.ufrj.cos.labia.aips.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocalManager extends DataManager {

	public LocalManager(Context context) {
		super(context);
	}

	public void save(Local local) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put(Local.NOME, local.getNome());
			values.put(Local.POSICAOGLOBALLAT0, local.getLat0());
			values.put(Local.POSICAOGLOBALLAT1, local.getLat1());
			values.put(Local.POSICAOGLOBALLONG0, local.getLong0());
			values.put(Local.POSICAOGLOBALLONG1, local.getLong1());
			values.put(Local.IDREMOTO, local.getIdRemoto());
			local.setId(db.insert(LocalTable, null, values));
		} finally {
			db.close();
		}
	}

	public List<Local> getAll() {
		List<Local> list = new ArrayList<Local>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(LocalTable, Local.FIELDS, null, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	private Local readRecord(Cursor c) {
		Local local = new Local();
		local.setId(c.getLong(0));
		local.setNome(c.getString(1));
		local.setLat0(c.getDouble(2));
		local.setLat1(c.getDouble(3));
		local.setLong0(c.getDouble(4));
		local.setLong1(c.getDouble(5));
		local.setIdRemoto(c.getLong(6));
		return local;
	}

	public void deleteByIdRemoto(Long id) {
		if (id == null) return;
		
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.delete(LocalTable, Local.IDREMOTO  + "=" + id, null);
		} finally {
			db.close();
		}
	}

	public Local getFirstById(long id) {
		SQLiteDatabase db = getReadableDatabase();
		Local local = null;

		try {
			Cursor c = db.query(LocalTable, Local.FIELDS, null, null, null, null, null, "1");
			if (c.moveToNext()) local = readRecord(c);
			c.close();
		} finally {
			db.close();
		}

		return local;
	}

}
