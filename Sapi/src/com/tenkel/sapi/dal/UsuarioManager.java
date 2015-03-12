package com.tenkel.sapi.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioManager extends DataManager {

	public UsuarioManager(Context context) {
		super(context);
	}
	
	public void save(Usuario usuario) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put("nome", usuario.getnome());
			values.put("chave", usuario.getchave());
			values.put("imei", usuario.getimei());
			values.put("endMac", usuario.getendMac());
			usuario.setId(db.insert("usuario", null, values));
		} finally {
			db.close();
		}
	}

	public List<Usuario> getAll() {
		List<Usuario> list = new ArrayList<Usuario>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query("usuario", new String[] {"id", "nome", "chave","imei","endMac"}, null, null, null, null, null);
			
			while (c.moveToNext()) {
				Usuario usuario = new Usuario();
				usuario.setnome(c.getString(0));
				usuario.setchave(c.getString(1));
				usuario.setimei(c.getString(2));
				usuario.setendMac(c.getString(3));
				list.add(usuario);
			}
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

}
