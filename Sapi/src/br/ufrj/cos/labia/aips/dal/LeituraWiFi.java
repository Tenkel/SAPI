package br.ufrj.cos.labia.aips.dal;


public class LeituraWiFi{
	
	public static final String ID = "id";
	public static final String IDACCESSPOINT = "idAccessPoint";
	public static final String IDOBSERVACAO = "idObservacao";
	public static final String VALOR = "valor";
	
	public static final String FK_LEITURAWIFI_ACCESSPOINT = "fk_LeituraWiFi_AccessPoint";
	public static final String FK_LEITURAWIFI_OBSERVACAO = "fk_LeituraWiFi_Observacao";
	public static final String FK_WIFIREADING_ACCESSPOINT_IDX = "fk_WiFiReading_AccessPoint_idx";
	public static final String FK_WIFIREADING_SAMPLE_IDX = "fk_WiFiReading_Sample_idx";

	public static final String[] FIELDS = {ID, IDACCESSPOINT, IDOBSERVACAO, VALOR};
	public static final String STRFIELDS = ID + ", " + IDACCESSPOINT + ", " + IDOBSERVACAO + ", " + VALOR;
	
	private Long id;
	private Long idAccessPoint;
	private Long idObservacao;
	private Integer valor;

	public LeituraWiFi() {

	}

	public LeituraWiFi(Long idAccessPoint, Long idObservacao, Integer valor) {
		super();
		this.idAccessPoint = idAccessPoint;
		this.idObservacao = idObservacao;
		this.valor = valor;
	}

	public LeituraWiFi(LeituraWiFi other) {
		idAccessPoint = other.idAccessPoint;
		idObservacao = other.idObservacao;
		valor = other.valor;
	}

	public Long getId() {
		return id;
	}
	
	public Long getIdAccessPoint() {
		return idAccessPoint;
	}

	public Long getIdObservacao() {
		return idObservacao;
	}

	public Integer getValor() {
		return valor;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setidAccessPoint(Long idAccessPoint) {
		this.idAccessPoint = idAccessPoint;
	}

	public void setidObservacao(Long idObservacao) {
		this.idObservacao = idObservacao;
	}

	public void setvalor(Integer valor) {
		this.valor = valor;
	}
	
}
