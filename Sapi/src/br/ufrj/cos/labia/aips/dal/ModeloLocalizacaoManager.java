package br.ufrj.cos.labia.aips.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ModeloLocalizacaoManager extends DataManager {

	public ModeloLocalizacaoManager(Context context) {
		super(context);
	}

	public ModeloLocalizacao getFirstByIdLocal(long idLocal) {
		
		ModeloLocalizacao ml = null;
		SQLiteDatabase db = getReadableDatabase();

		try {
			String where = ModeloLocalizacao.IDLOCAL + " == " + idLocal;
			Cursor c = db.query(ModeloLocalizacaoTable, ModeloLocalizacao.FIELDS, where, null, null, null, null);
			
			if (c.moveToNext())
				ml = readRecord(c);
			
			c.close();
		} finally {
			db.close();
		}

		return ml;
	}

	public void save(ModeloLocalizacao ml) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			ml.setId(db.insert(ModeloLocalizacaoTable, null, 
					createRecord(ml)));
		} finally {
			db.close();
		}
	}

	public void update(ModeloLocalizacao ml) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.update(ModeloLocalizacaoTable, createRecord(ml), 
					ModeloLocalizacao.ID + "=" + ml.getId(), null);
		} finally {
			db.close();
		}
	}

	private ContentValues createRecord(ModeloLocalizacao ml) {
		ContentValues values = new ContentValues();
		values.put(ModeloLocalizacao.IDLOCAL, ml.getIdLocal());
		values.put(ModeloLocalizacao.NOMEVERSAO, ml.getNomeVersao());
		values.put(ModeloLocalizacao.FILENAME, ml.getFilename());
		return values;
	}

	private ModeloLocalizacao readRecord(Cursor c) {
		ModeloLocalizacao ml = new ModeloLocalizacao();
		ml.setId(c.getLong(0));
		ml.setIdLocal(c.getLong(1));
		ml.setNomeVersao(c.getString(2));
		ml.setFilename(c.getString(3));
		return ml;
	}

	public void delete(ModeloLocalizacao ml) {
		if (ml.getId() == null) return;
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.delete(ModeloLocalizacaoTable, ModeloLocalizacao.ID + "=" + ml.getId(), null);
		} finally {
			db.close();
		}
	}
	
}
