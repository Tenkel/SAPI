package com.tenkel.sapi.dal;

public class Sala {
	
	public static final String ID = "id";
	public static final String IDANDAR = "idAndar";
	public static final String NOME = "nome";
	public static final String IDPOLIGONO = "idPoligono";
	public static final String FK_SALA_ANDAR = "fk_Sala_Andar";
	public static final String FK_SALA_ANDAR_IDX = "fk_Sala_Andar_idx";
	public static final String IDREMOTO = "idRemoto";
	public static final String COORDENADAS = "coordenadas";
	
	private Long id;
	private Long idAndar;
	private String nome;
	private String idPoligono;
	private Long idRemoto;
	private String coordenadas;

	public Long getId() {
		return id;
	}

	public Long getIdAndar() {
		return idAndar;
	}

	public String getNome() {
		return nome;
	}

	public String getIdPoligono() {
		return idPoligono;
	}

	public Long getIdRemoto() {
		return idRemoto;
	}

	public String getCoordenadas() {
		return coordenadas;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdAndar(Long idAndar) {
		this.idAndar = idAndar;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setIdPoligono(String idPoligono) {
		this.idPoligono = idPoligono;
	}

	public void setIdRemoto(Long idRemoto) {
		this.idRemoto = idRemoto;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}
	
}
