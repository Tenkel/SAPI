package com.tenkel.sapi.dal;

public class ModeloLocalizacao {
	
	public static final String ID = "id";
	public static final String IDLOCAL = "idLocal";
	public static final String NOMEVERSAO = "nomeVersao";
	public static final String FILENAME = "filename";
	
	public static final String FK_ML_LOCAL = "fk_ModeloLocalizacao_Local";
	public static final String FK_ML_LOCAL_IDX = "fk_ModeloLocalizacao_Local_idx";
	
	public static final String[] FIELDS = new String[] {ID, IDLOCAL, NOMEVERSAO, FILENAME};
	
	private Long id;
	private Long idLocal;
	private String nomeVersao;
	private String filename;

	public Long getId() {
		return id;
	}

	public Long getIdLocal() {
		return idLocal;
	}

	public String getNomeVersao() {
		return nomeVersao;
	}

	public String getFilename() {
		return filename;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}

	public void setNomeVersao(String versionName) {
		this.nomeVersao = versionName;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}
