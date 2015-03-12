package br.ufrj.cos.labia.aips.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SalaManager extends DataManager {

	public SalaManager(Context context) {
		super(context);
	}

	public void save(Sala sala) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put(Sala.IDANDAR, sala.getIdAndar());
			values.put(Sala.NOME, sala.getNome());
			values.put(Sala.IDPOLIGONO, sala.getIdPoligono());
			values.put(Sala.IDREMOTO, sala.getIdRemoto());
			values.put(Sala.COORDENADAS, sala.getCoordenadas());
			sala.setId(db.insert(SalaTable, null, values));
		} finally {
			db.close();
		}
	}
	
}
