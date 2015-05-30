package com.tenkel.sapi.dal;

public class Posicao{
	public static final String ID = "id";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String IDANDAR = "idAndar";
	public static final String IDREMOTO = "idRemoto";
	public static final String REFERENCIA = "referencia";
	public static final String NOME = "nome";
	public static final String PROPAGANDA = "propaganda";
	public static final String ATIVO = "ativo";
	
	public static final String FK_POSICAO_ANDAR = "fk_Posicao_Andar";
	public static final String FK_POSICAO_ANDAR_IDX = "fk_Posicao_Andar_idx";
	
	public static final String[] FIELDS = new String[] {ID, X, Y, IDANDAR, IDREMOTO, REFERENCIA, NOME, PROPAGANDA, ATIVO};

	private Long id;
	private Long idAndar;
	private Double x;
	private Double y;
	private Long idRemoto;
	private String referencia;
	private String nome;
	private String propaganda;
	private boolean ativo;
	
	public Posicao() {

	}

	public Posicao(Long idAndar, Double x, Double y, String referencia, String nome, String propaganda, boolean ativo) {
		super();
		this.idAndar = idAndar;
		this.x = x;
		this.y = y;
		this.referencia = referencia;
		this.nome = nome;
		this.propaganda = propaganda;
		this.ativo = ativo;
	}

	public Long getId() {
		return id;
	}
	
	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Long getIdAndar() {
		return idAndar;
	}

	public Long getIdRemoto() {
		return idRemoto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public void setIdAndar(Long idAndar) {
		this.idAndar = idAndar;
	}

	public void setIdRemoto(Long idRemoto) {
		this.idRemoto = idRemoto;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPropaganda() {
		return propaganda;
	}

	public void setPropaganda(String propaganda) {
		this.propaganda = propaganda;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
