package com.tenkel.sapi.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 *  Class that Manages all data access, especially to the database.
 */


/*
 * Extending SQLiteOpenHelper so its objects can manipulate the database
 */
public class DataManager extends SQLiteOpenHelper {

	/*
	 * Data base name and version
	 */

	private static final String DB_NAME = "database.db";
	private static final int DB_VERSION = 7;
	private Context theContext;
	
	/*
	 * String with table names (eg. private static final String LocalTable =
	 * "Local") and class (structure like) with inside table columns (eg. public
	 * static class Local)
	 */

	public static final String LocalTable = "Local";
	
	public static final String AndarTable = "Andar";

	public static final String FuncaoPosicaoTable = "FuncaoPosicao";
	
	public static final String PosicaoTable = "Posicao";
	
	public static final String ObservacaoTable = "Observacao";

	public static final String AccessPointTable = "AccessPoint";

	public static final String UsuarioTable = "Usuario";
	
	public static final String LeituraWiFiTable = "LeituraWiFi";
	
	public static final String UnidadeSensoresTable = "UnidadeSensores";
	
	public static final String ModeloLocalizacaoTable = "ModeloLocalizacao";

	public static class UnidadeSensores {
		public static final String ID = "id";
		public static final String ACC = "acc";
		public static final String TEMP = "temp";
		public static final String GRAV = "grav";
		public static final String GYRO = "gyro";
		public static final String LIGHT = "light";
		public static final String LINACC = "linAcc";
		public static final String MAG = "mag";
		public static final String ORIENT = "orient";
		public static final String PRESS = "press";
		public static final String PROX = "prox";
		public static final String HUM = "hum";
		public static final String ROT = "rot";
	}
	
	public static final String LeituraSensoresTable = "LeituraSensores";
	
	public static final String FuncaoTipoTable = "FuncaoTipo";

	public static class FuncaoTipo {
		public static final String ID = "id";
		public static final String TIPO = "tipo";
	}
	
	public static final String SalaTable = "Sala";

	
	
	public DataManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		theContext = context;
	}

	// Database Creation thought SQL 'CREATE TABLE'
	@Override
	public void onCreate(SQLiteDatabase db) {

		// Local
		db.execSQL("CREATE TABLE " + LocalTable +  " (  " 
				+ Local.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Local.NOME + " TEXT, "
				+ Local.POSICAOGLOBALLAT0 + " REAL, "
				+ Local.POSICAOGLOBALLAT1 + " REAL, "
				+ Local.POSICAOGLOBALLONG0 + " REAL, "
				+ Local.POSICAOGLOBALLONG1 + " REAL, "
				+ Local.IDREMOTO + " INTEGER UNIQUE);");

		
		//Table `Andar`
		db.execSQL("CREATE TABLE "+ AndarTable + " ( "
		  + Andar.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		  + Andar.IDLOCAL + " INTEGER, "
		  + Andar.NOME + " TEXT, "
		  + Andar.URIMAPA + " TEXT, "
		  + Andar.CAMADAS + " TEXT, "
		  + Andar.IDREMOTO + " INTEGER UNIQUE, "
		  + Andar.X1 + " REAL, "
		  + Andar.Y1 + " REAL, "
	  	  + Andar.X2 + " REAL, "
		  + Andar.Y2 + " REAL, "
		  + Andar.IMAGE + " BLOB, "
		  
		  + "CONSTRAINT " + Andar.FK_ANDAR_LOCAL +
		    " FOREIGN KEY (" + Andar.IDLOCAL + ")" +
		    " REFERENCES " + LocalTable + " (" + Local.ID + ") ON DELETE CASCADE"
		  
		  +");");

		db.execSQL("CREATE INDEX " + Andar.FK_ANDAR_LOCAL_IDX + " ON " + AndarTable + " (" + Andar.IDLOCAL + " ASC);");
		
		//Table `Posicao`
		db.execSQL("CREATE TABLE "+ PosicaoTable + " ( "
				  + Posicao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + Posicao.IDANDAR + " INTEGER, "
				  + Posicao.X + " REAL, "
				  + Posicao.Y + " REAL, "
				  + Posicao.IDREMOTO + " INTEGER, "
			      + Posicao.REFERENCIA + " TEXT, "
				  + Posicao.NOME + " TEXT, "
				  + Posicao.PROPAGANDA + " TEXT, "
				  + Posicao.ATIVO + " INTEGER, "
				  
				  +"CONSTRAINT " + Posicao.UNIQUE_PER_ANDAR +
				    " UNIQUE (" + Posicao.IDREMOTO + ", " 
				    		   + Posicao.IDANDAR + ")"

				  + "CONSTRAINT " + Posicao.FK_POSICAO_ANDAR +
				    " FOREIGN KEY (" + Posicao.IDANDAR + ")" +
				    " REFERENCES " + AndarTable + " (" + Andar.ID + ") ON DELETE CASCADE"
				  
				  +");");

		db.execSQL("CREATE INDEX " + Posicao.FK_POSICAO_ANDAR_IDX + " ON " + PosicaoTable + " (" + Posicao.IDANDAR + " ASC);");
				
		//Table `Posicao`
//		db.execSQL("CREATE TABLE "+ ObservacaoTable + " ( "
//				  + Observacao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//				  + Observacao.IDPOSICAO + " INTEGER, "
//				  + Observacao.INSTANTECOLETA + " INTEGER, "
//
//				  + "CONSTRAINT " + Observacao.FK_OBSERVACAO_POSICAO +
//				    " FOREIGN KEY (" + Observacao.IDPOSICAO + ")" +
//				    " REFERENCES " + PosicaoTable + " (" + Posicao.ID + ")"
//				  
//				  +");");
//
//		db.execSQL("CREATE INDEX " + Observacao.FK_SAMPLE_LOCAL_IDX + " ON " + ObservacaoTable + " (" + Observacao.IDPOSICAO + " ASC);");
				
		//Table `Observacao`
		db.execSQL("CREATE TABLE "+ ObservacaoTable + " ( "
				  + Observacao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + Observacao.IDPOSICAO + " INTEGER, "
				  + Observacao.INSTANTECOLETA + " INTEGER, "

				  + "CONSTRAINT " + Observacao.FK_OBSERVACAO_POSICAO +
				    " FOREIGN KEY (" + Observacao.IDPOSICAO + ")" +
				    " REFERENCES " + PosicaoTable + " (" + Posicao.ID + ") ON DELETE CASCADE"
				  
				  +");");

		db.execSQL("CREATE INDEX " + Observacao.FK_SAMPLE_LOCAL_IDX + " ON " + ObservacaoTable + " (" + Observacao.IDPOSICAO + " ASC);");

		
		//Table `AccessPoint`
		db.execSQL("CREATE TABLE "+ AccessPointTable + " ( "
				  + AccessPoint.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + AccessPoint.IDPOSICAO + " INTEGER, "
				  + AccessPoint.BSSID + " TEXT UNIQUE, "
				  + AccessPoint.ESSID + " TEXT, "
				  + AccessPoint.CONFIANCA + " TEXT, "

				  + "CONSTRAINT " + AccessPoint.FK_ACCESSPOINT_POSICAO +
				    " FOREIGN KEY (" + AccessPoint.IDPOSICAO + ")" +
				    " REFERENCES " + PosicaoTable + " (" + Posicao.ID + ") ON DELETE CASCADE"
				  
				  +");");

		db.execSQL("CREATE INDEX " + AccessPoint.FK_ACCESSPOINT_LOCAL_IDX + " ON " + AccessPointTable + " (" + AccessPoint.IDPOSICAO + " ASC);");


		//Table `Usuario`
		db.execSQL("CREATE TABLE "+ UsuarioTable + " ( "
				  + Usuario.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + Usuario.NOME + " TEXT, "
				  + Usuario.CHAVE + " TEXT, "
				  + Usuario.IMEI + " TEXT, "
				  + Usuario.ENDMAC + " TEXT)");
		

		//Table `LeituraWiFi`
		db.execSQL("CREATE TABLE "+ LeituraWiFiTable + " ( "
				  + LeituraWiFi.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + LeituraWiFi.IDACCESSPOINT + " INTEGER, "
				  + LeituraWiFi.IDOBSERVACAO + " INTEGER, "
				  + LeituraWiFi.VALOR + " INTEGER, "

				  +"CONSTRAINT " + LeituraWiFi.FK_LEITURAWIFI_ACCESSPOINT +
				    " FOREIGN KEY (" + LeituraWiFi.IDACCESSPOINT + ")" +
				    " REFERENCES " + AccessPointTable + " (" + AccessPoint.ID + ") ON DELETE CASCADE "

				  +"CONSTRAINT " + LeituraWiFi.FK_LEITURAWIFI_OBSERVACAO +
				    " FOREIGN KEY (" + LeituraWiFi.IDOBSERVACAO + ")" +
				    " REFERENCES " + ObservacaoTable + " (" + Observacao.ID + ") ON DELETE CASCADE "
				  
				  +");");

		db.execSQL("CREATE INDEX " + LeituraWiFi.FK_WIFIREADING_ACCESSPOINT_IDX + " ON " + LeituraWiFiTable + " (" + LeituraWiFi.IDACCESSPOINT + " ASC);");
		

		db.execSQL("CREATE INDEX " + LeituraWiFi.FK_WIFIREADING_SAMPLE_IDX + " ON " + LeituraWiFiTable + " (" + LeituraWiFi.IDOBSERVACAO + " ASC);");
		

		//Table `UnidadeSensores`
		db.execSQL("CREATE TABLE "+ UnidadeSensoresTable + " ( "
				  + UnidadeSensores.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + UnidadeSensores.ACC + " TEXT, "
				  + UnidadeSensores.TEMP + " TEXT, "
				  + UnidadeSensores.GRAV + " TEXT, "
				  + UnidadeSensores.GYRO + " TEXT, "
				  + UnidadeSensores.LIGHT + " TEXT, "
				  + UnidadeSensores.LINACC + " TEXT, "
				  + UnidadeSensores.MAG + " TEXT, "
				  + UnidadeSensores.ORIENT + " TEXT, "
				  + UnidadeSensores.PRESS + " TEXT, "
				  + UnidadeSensores.PROX + " TEXT, "
				  + UnidadeSensores.HUM + " TEXT, "
				  + UnidadeSensores.ROT + " TEXT)");
		
		

		//Table `LeituraSensores`
		db.execSQL("CREATE TABLE "+ LeituraSensoresTable + " ( "
				  + LeituraSensores.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + LeituraSensores.IDOBSERVACAO + " INTEGER, "
				  + LeituraSensores.IDUNIDADESENSORES + " INTEGER, "
				  + LeituraSensores.ACCX + " REAL, "
				  + LeituraSensores.ACCY + " REAL, "
				  + LeituraSensores.ACCZ + " REAL, "
				  + LeituraSensores.TEMP + " REAL, "
				  + LeituraSensores.GRAVX + " REAL, "
				  + LeituraSensores.GRAVY + " REAL, "
				  + LeituraSensores.GRAVZ + " REAL, "
				  + LeituraSensores.GYROX + " REAL, "
				  + LeituraSensores.GYROY + " REAL, "
				  + LeituraSensores.GYROZ + " REAL, "
				  + LeituraSensores.LIGHT + " REAL, "
				  + LeituraSensores.LINACCX + " REAL, "
				  + LeituraSensores.LINACCY + " REAL, "
				  + LeituraSensores.LINACCZ + " REAL, "
				  + LeituraSensores.MAGX + " REAL, "
				  + LeituraSensores.MAGY + " REAL, "
				  + LeituraSensores.MAGZ + " REAL, "
				  + LeituraSensores.ORIENTX + " REAL, "
				  + LeituraSensores.ORIENTY + " REAL, "
				  + LeituraSensores.ORIENTZ + " REAL, "
				  + LeituraSensores.PRESS + " REAL, "
				  + LeituraSensores.PROX + " REAL, "
				  + LeituraSensores.HUM + " REAL, "
				  + LeituraSensores.ROTX + " REAL, "
				  + LeituraSensores.ROTY + " REAL, "
				  + LeituraSensores.ROTZ + " REAL, "
				  + LeituraSensores.ROTSCALAR + " REAL, "
				  
				  
				  +"CONSTRAINT " + LeituraSensores.FK_LEITURASENSORES_UNIDADESENSORES +
				    " FOREIGN KEY (" + LeituraSensores.IDUNIDADESENSORES + ")" +
				    "REFERENCES " + UnidadeSensoresTable + " (" + UnidadeSensores.ID + ") ON DELETE CASCADE "

				  +"CONSTRAINT " + LeituraSensores.FK_LEITURASENSORES_OBSERVACAO +
				    " FOREIGN KEY (" + LeituraSensores.IDOBSERVACAO + ")" +
				    "REFERENCES " + ObservacaoTable + " (" + Observacao.ID + ") ON DELETE CASCADE "
				  
				  +");");

		db.execSQL("CREATE INDEX " + LeituraSensores.FK_SENSORSREADING_SENSORUNITS_IDX + " ON " + LeituraSensoresTable + " (" + LeituraSensores.IDUNIDADESENSORES + " ASC);");
		

		db.execSQL("CREATE INDEX " + LeituraSensores.FK_SENSORSREADING_SAMPLE_IDX + " ON " +LeituraSensoresTable + " (" + LeituraSensores.IDOBSERVACAO + " ASC);");


		// Local
		db.execSQL("CREATE TABLE " + FuncaoTipoTable +  " (  " 
				+ FuncaoTipo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FuncaoTipo.TIPO + " TEXT)");

		//Table `FuncaoPosicao`
		db.execSQL("CREATE TABLE "+ FuncaoPosicaoTable + " ( "
				  + FuncaoPosicao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + FuncaoPosicao.IDPOSICAO + " INTEGER, "
				  + FuncaoPosicao.IDACCESSPOINT + " INTEGER, "
				  + FuncaoPosicao.IDTIPO + " INTEGER, "
				  + FuncaoPosicao.URI + " TEXT, "

				  +"CONSTRAINT " + FuncaoPosicao.FK_FUNCAOPOSICAO_POSICAO +
				    " FOREIGN KEY (" + FuncaoPosicao.IDPOSICAO + ")" +
				    "REFERENCES " + PosicaoTable + " (" + Posicao.ID + ") ON DELETE CASCADE "

				  +"CONSTRAINT " + FuncaoPosicao.FK_FUNCAOPOSICAO_ACCESSPOINT +
				    " FOREIGN KEY (" + FuncaoPosicao.IDACCESSPOINT + ")" +
				    "REFERENCES " + AccessPointTable + " (" + AccessPoint.ID + ") ON DELETE CASCADE "

				  +"CONSTRAINT " + FuncaoPosicao.FK_FUNCAOPOSICAO_FUNCAOTIPO +
				    " FOREIGN KEY (" + FuncaoPosicao.IDTIPO + ")" +
				    "REFERENCES " + FuncaoTipoTable + " (" + FuncaoTipo.ID + ") ON DELETE CASCADE "

				  +"CONSTRAINT " + FuncaoPosicao.UNIQUE_FUNCAOTIPO +
				    " UNIQUE (" + FuncaoPosicao.IDPOSICAO + ", " 
				    		   + FuncaoPosicao.IDACCESSPOINT + ")" //", " 
				    		   //+ FuncaoPosicao.IDTIPO + ")"
				  
				  +");");

		db.execSQL("CREATE INDEX " + FuncaoPosicao.FK_POSFUNC_LOCAL_IDX + " ON " + FuncaoPosicaoTable + " (" + FuncaoPosicao.IDPOSICAO + " ASC);");
		

		db.execSQL("CREATE INDEX " + FuncaoPosicao.FK_POSFUNC_ACCESSPOINT_IDX + " ON " + FuncaoPosicaoTable + " (" + FuncaoPosicao.IDACCESSPOINT + " ASC);");

		
		

		//Table `Sala`
		db.execSQL("CREATE TABLE "+ SalaTable + " ( "
				  + Sala.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + Sala.IDANDAR + " INTEGER, "
				  + Sala.NOME + " TEXT, "
				  + Sala.IDPOLIGONO + " TEXT, "
				  + Sala.COORDENADAS + " TEXT, "

				  + "CONSTRAINT " + Sala.FK_SALA_ANDAR +
				    " FOREIGN KEY (" + Sala.IDANDAR + ")" +
				    "REFERENCES " + AndarTable + " (" + Andar.ID + ") ON DELETE CASCADE "
				  
				  + ");");

		db.execSQL("CREATE INDEX " + Sala.FK_SALA_ANDAR_IDX + " ON " + SalaTable + " (" + Sala.IDANDAR + " ASC);");

		//Table `ModeloLocalizacao`
		db.execSQL("CREATE TABLE "+ ModeloLocalizacaoTable + " ( "
				  + ModeloLocalizacao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + ModeloLocalizacao.IDLOCAL+ " INTEGER NOT NULL, "
				  + ModeloLocalizacao.NOMEVERSAO + " TEXT, "
				  + ModeloLocalizacao.FILENAME + " TEXT, "

				  + "CONSTRAINT " + ModeloLocalizacao.FK_ML_LOCAL +
				    " FOREIGN KEY (" + ModeloLocalizacao.IDLOCAL + ")" +
				    "REFERENCES " + LocalTable + " (" + Local.ID + ") ON DELETE CASCADE "
				  
				  + ");");

		db.execSQL("CREATE INDEX " + ModeloLocalizacao.FK_ML_LOCAL_IDX + " ON " 
				+ ModeloLocalizacaoTable + " (" + ModeloLocalizacao.IDLOCAL+ " ASC);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DataManager", "Upgrading DB, from " + oldVersion + " to " + newVersion);
		
		for (int v=oldVersion;v<newVersion;++v) {
			if (v < 3) {
				recreateDB(db);
				break;
			} else if (v == 3) {
				migrateDBFrom3To4(db);
			} else if (v == 4) {
				migrateDBFrom4To5(db);
			} else if (v == 5) {
				migrateDBFrom5To6(db);
			} else if (v == 6) {
				migrateDBFrom6To7(db);
			} else {
				recreateDB(db);
			}
		}
	}

	private void migrateDBFrom6To7(SQLiteDatabase db) {
		Log.i("DataManager", "Migrating from 6 to 7");
		db.execSQL("CREATE TABLE "+ ModeloLocalizacaoTable + " ( "
				  + ModeloLocalizacao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				  + ModeloLocalizacao.IDLOCAL+ " INTEGER NOT NULL, "
				  + ModeloLocalizacao.NOMEVERSAO + " TEXT, "
				  + ModeloLocalizacao.FILENAME + " TEXT, "

				  + "CONSTRAINT " + ModeloLocalizacao.FK_ML_LOCAL +
				    " FOREIGN KEY (" + ModeloLocalizacao.IDLOCAL + ")" +
				    "REFERENCES " + LocalTable + " (" + Local.ID + ") ON DELETE CASCADE "
				  
				  + ");");

		db.execSQL("CREATE INDEX " + ModeloLocalizacao.FK_ML_LOCAL_IDX + " ON " 
				+ ModeloLocalizacaoTable + " (" + ModeloLocalizacao.IDLOCAL+ " ASC);");
	}

	private void migrateDBFrom5To6(SQLiteDatabase db) {
		Log.i("DataManager", "Migrating from 5 to 6");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.IMAGE + " BLOB");
	}

	private void migrateDBFrom4To5(SQLiteDatabase db) {
		Log.i("DataManager", "Migrating from 4 to 5");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.X1 + " REAL DEFAULT -1.0");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.Y1 + " REAL DEFAULT -1.0");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.X2 + " REAL DEFAULT +1.0");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.Y2 + " REAL DEFAULT +1.0");
		db.execSQL("ALTER TABLE " + SalaTable + " ADD COLUMN " + Sala.COORDENADAS + " TEXT DEFAULT ''");
	}

	private void migrateDBFrom3To4(SQLiteDatabase db) {
		Log.i("DataManager", "Migrating from 3 to 4");
		db.execSQL("ALTER TABLE " + LocalTable + " ADD COLUMN " + Local.IDREMOTO + " INTEGER DEFAULT -1");
		db.execSQL("ALTER TABLE " + AndarTable + " ADD COLUMN " + Andar.IDREMOTO + " INTEGER DEFAULT -1");
		db.execSQL("ALTER TABLE " + PosicaoTable + " ADD COLUMN " + Posicao.IDREMOTO + " INTEGER DEFAULT -1");
		db.execSQL("ALTER TABLE " + SalaTable + " ADD COLUMN " + Sala.IDREMOTO + " INTEGER DEFAULT -1");
	}

	private void recreateDB(SQLiteDatabase db) {
		Log.i("DataManager", "Recreating DB");
		db.execSQL("DROP TABLE IF EXISTS " + UsuarioTable);
		db.execSQL("DROP TABLE IF EXISTS " + UnidadeSensoresTable);
		db.execSQL("DROP TABLE IF EXISTS " + LeituraSensoresTable);
		db.execSQL("DROP TABLE IF EXISTS " + FuncaoTipoTable);
		db.execSQL("DROP TABLE IF EXISTS " + FuncaoPosicaoTable);
		db.execSQL("DROP TABLE IF EXISTS " + LeituraWiFiTable);
		db.execSQL("DROP TABLE IF EXISTS " + AccessPointTable);
		db.execSQL("DROP TABLE IF EXISTS " + ObservacaoTable);
		db.execSQL("DROP TABLE IF EXISTS " + PosicaoTable);
		db.execSQL("DROP TABLE IF EXISTS " + AndarTable);
		db.execSQL("DROP TABLE IF EXISTS " + LocalTable);
		db.execSQL("DROP TABLE IF EXISTS " + SalaTable);
		this.onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys = ON");
	}

	@Override
	public void onConfigure(SQLiteDatabase db) {
		db.setForeignKeyConstraintsEnabled(true);
		super.onConfigure(db);
	}
	
	// Export Database to a given path with a given name
	public void DBexport(String toPath, String name) throws IOException {
		Log.i("DBexport", "Exportando BD com nome "+ name +" para " + toPath);
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			String dbPath = db.getPath();
			Log.i("DBexport", "Exportando BD " + dbPath);
			FileInputStream newDb = new FileInputStream(new File(dbPath));
			FileOutputStream toFile = new FileOutputStream(new File(toPath, name));
			FileChannel fromChannel = null;
			FileChannel toChannel = null;
			try {
				fromChannel = newDb.getChannel();
				toChannel = toFile.getChannel();
				fromChannel.transferTo(0, fromChannel.size(), toChannel);
			} finally {
				try {
					if (fromChannel != null) {
						fromChannel.close();
						newDb.close();
					}
				} finally {
					if (toChannel != null) {
						toChannel.close();
						toFile.close();
					}
				}
			}
		} finally {
			db.close();
		}

	}

	// Overload DBexport for a std name called BDbackup.db
	public void DBexport( String toPath) throws IOException {
		DBexport( toPath, DB_NAME);
	}

}
