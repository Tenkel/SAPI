package br.ufrj.cos.labia.aips.dal;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.WIFISignal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ObservacaoManager extends DataManager {
	
	public ObservacaoManager(Context c) {
		super(c);
	}
	
	private Observacao readRecord(Cursor c) {
		Observacao observacao = new Observacao();
		observacao.setId(c.getLong(0));
		observacao.settimestamp(c.getLong(1));
		observacao.setidPosicao(c.getLong(2));
		return observacao;
	}
	
	public void save(Observacao observacao){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put(Observacao.INSTANTECOLETA, observacao.gettimestamp());
			values.put(Observacao.IDPOSICAO, observacao.getIdPosicao());
			observacao.setId(db.insert(ObservacaoTable, null, values));
		} finally {
			db.close();
		}
	}
	
	public List<Observacao> getByidPosicao(long idPosicao) {
		List<Observacao> list = new ArrayList<Observacao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT id, " + Observacao.INSTANTECOLETA + ", " + Observacao.IDPOSICAO + 
					" FROM " + ObservacaoTable + " WHERE idPosicao=" + idPosicao, null);

			while (c.moveToNext()) 
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}
	
	public int getCount() {
		SQLiteDatabase db = getReadableDatabase();
		int counter = 0;
		
		try {
			Cursor c = db.rawQuery("SELECT count(*) FROM " + ObservacaoTable, null);
			if (c.moveToFirst())
				counter = c.getInt(0);
			c.close();
		} finally {
			db.close();
		}
		
		return counter;
	}

	public List<Observacao> getAll() {
		List<Observacao> list = new ArrayList<Observacao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.query(ObservacaoTable, Observacao.FIELDS, null, null, null, null, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}

		return list;
	}

	public List<Observacao> getByIdAndar(long andarId) {
		List<Observacao> list = new ArrayList<Observacao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT o." + Observacao.ID + ", o." + Observacao.INSTANTECOLETA + ", o." + Observacao.IDPOSICAO + " FROM " 
					+ ObservacaoTable + " o INNER JOIN " + PosicaoTable + " p ON p.id=o.idPosicao " 
					+ " WHERE p.idAndar=" + andarId, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public List<Observacao> getByIdLocal(long idLocal) {
		List<Observacao> list = new ArrayList<Observacao>();
		SQLiteDatabase db = getReadableDatabase();

		try {
			Cursor c = db.rawQuery("SELECT o." + Observacao.ID + ", o." + Observacao.INSTANTECOLETA + ", o." + Observacao.IDPOSICAO 
					+ " FROM " + ObservacaoTable + " o INNER JOIN " + PosicaoTable + " p ON p.id=o.idPosicao "
					+ " INNER JOIN " + AndarTable + " a ON a.id=p.idAndar " 
					+ " WHERE a.idLocal=" + idLocal, null);
			
			while (c.moveToNext())
				list.add(readRecord(c));
			
			c.close();
		} finally {
			db.close();
		}
		
		return list;
	}

	public Observacao getById(long idObservacao) {
		SQLiteDatabase db = getReadableDatabase();
		
		try {
			Cursor c = db.rawQuery("SELECT id, " + Observacao.INSTANTECOLETA + ", " + Observacao.IDPOSICAO + 
					" FROM " + ObservacaoTable + " WHERE id=" + idObservacao + " LIMIT 1", null);
			
			if (c.moveToNext())
				return readRecord(c);
			
			c.close();
		} finally {
			db.close();
		}
		
		return null;
	}

	public void update(Observacao obs) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("UPDATE " + ObservacaoTable + " SET " + 
					Observacao.INSTANTECOLETA + "=" + obs.gettimestamp() + ", " + 
					Observacao.IDPOSICAO + "=" + obs.getIdPosicao() + 
					" WHERE ID=" + obs.getId());
		} finally {
			db.close();
		}
	}

	public void deleteByIdPosition(Long id) {
		if (id == null) return;
		
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DELETE FROM " + ObservacaoTable + 
					" WHERE " + Observacao.IDPOSICAO + "=" + id);
		} finally {
			db.close();
		}
	}

}
