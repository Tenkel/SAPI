package com.tenkel.sapi.dal;


public class FuncaoPosicao{
	
	public static final String ID = "id";
	public static final String IDPOSICAO = "idPosicao";
	public static final String IDACCESSPOINT = "idAccessPoint";
	public static final String IDTIPO = "idTipo";
	public static final String URI = "uri";
	
	
	public static final String FK_FUNCAOPOSICAO_POSICAO = "fk_FuncaoPosicao_Posicao";
	public static final String FK_FUNCAOPOSICAO_ACCESSPOINT = "fk_FuncaoPosicao_AccessPoint";
	public static final String FK_POSFUNC_LOCAL_IDX = "fk_PosFunc_Local_idx";
	public static final String FK_POSFUNC_ACCESSPOINT_IDX = "fk_PosFunc_AccessPoint_idx";
	public static final String FK_FUNCAOPOSICAO_FUNCAOTIPO = "fk_FuncaoPosicao_FuncaoTipo";
	public static final String UNIQUE_FUNCAOTIPO = "unique_FuncaoTipo";
		

	public static final String[] FIELDS = {ID, IDPOSICAO, IDACCESSPOINT, IDTIPO, URI};
	public static final String STRFIELDS = ID + ", " + IDPOSICAO + ", " + IDACCESSPOINT + ", " + IDTIPO + ", " + URI;
	
	private Long id;
	private Long idPosicao;

	private Long idAccessPoint;
	private Long idTipo;
	private String uri;

	public FuncaoPosicao() {

	}

	public FuncaoPosicao(Long idPosicao, Long idAccessPoint, Long idTipo, String uri) {
		super();
		this.idAccessPoint = idAccessPoint;
		this.idPosicao = idPosicao;
		this.idTipo = idTipo;
		this.uri = uri;
	}

	public FuncaoPosicao(FuncaoPosicao other) {
		idAccessPoint = other.idAccessPoint;
		idPosicao = other.idPosicao;
		idTipo = other.idTipo;
		uri = other.uri;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdPosicao() {
		return idPosicao;
	}

	public void setIdPosicao(Long idPosicao) {
		this.idPosicao = idPosicao;
	}

	public Long getIdAccessPoint() {
		return idAccessPoint;
	}

	public void setIdAccessPoint(Long idAccessPoint) {
		this.idAccessPoint = idAccessPoint;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
