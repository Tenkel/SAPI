package com.tenkel.sapi.dal;

public class Local {
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String POSICAOGLOBALLAT0 = "posicaoGlobalLat0";
	public static final String POSICAOGLOBALLONG0 = "posicaoGlobalLong0";
	public static final String POSICAOGLOBALLAT1 = "posicaoGlobalLat1";
	public static final String POSICAOGLOBALLONG1 = "posicaoGlobalLong1";
	public static final String IDREMOTO = "idRemoto";
	
	public static String[] FIELDS = new String[] {ID, NOME, POSICAOGLOBALLAT0, POSICAOGLOBALLAT1, POSICAOGLOBALLONG0, POSICAOGLOBALLONG1, IDREMOTO};
	
	private Long id;
	
	private String nome;
	
	private Double lat0;
	
	private Double long0;
	
	private Double lat1;
	
	private Double long1;

	private Long idRemoto;
	
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Double getLat0() {
		return lat0;
	}

	public Double getLong0() {
		return long0;
	}

	public Double getLat1() {
		return lat1;
	}

	public Double getLong1() {
		return long1;
	}

	public Long getIdRemoto() {
		return idRemoto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setLat0(Double lat0) {
		this.lat0 = lat0;
	}

	public void setLong0(Double long0) {
		this.long0 = long0;
	}

	public void setLat1(Double lat1) {
		this.lat1 = lat1;
	}

	public void setLong1(Double long1) {
		this.long1 = long1;
	}
	
	public void setIdRemoto(Long idRemoto) {
		this.idRemoto = idRemoto;
	}

}
