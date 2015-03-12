package com.tenkel.sapi.dal;

public class Posicao{
	public static final String ID = "id";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String IDANDAR = "idAndar";
	public static final String IDREMOTO = "idRemoto";
	
	public static final String FK_POSICAO_ANDAR = "fk_Posicao_Andar";
	public static final String FK_POSICAO_ANDAR_IDX = "fk_Posicao_Andar_idx";
	
	public static final String[] FIELDS = new String[] {ID, X, Y, IDANDAR, IDREMOTO};

	private Long id;

	private Long idAndar;
	
	private Double x;
	
	private Double y;
	
	private Long idRemoto;
	
	public Posicao() {

	}

	public Posicao(Long idAndar, Double x, Double y) {
		super();
		this.idAndar = idAndar;
		this.x = x;
		this.y = y;
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
	
}
