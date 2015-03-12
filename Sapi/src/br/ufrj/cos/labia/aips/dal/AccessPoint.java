package br.ufrj.cos.labia.aips.dal;




public class AccessPoint{
	
	public static final String ID = "id";
	public static final String IDPOSICAO = "idPosicao";
	public static final String BSSID = "bssid";
	public static final String ESSID = "essid";
	public static final String CONFIANCA = "confianca";
	public static final String FK_ACCESSPOINT_POSICAO = "fk_AccessPoint_Posicao";
	public static final String FK_ACCESSPOINT_LOCAL_IDX = "fk_AccessPoint_Local_idx";
	
	private Long id;

	private Long idPosicao;
	
	private String bssid;
	
	private String essid;
	
	private Float confianca;
	
	public AccessPoint() {

	}

	public AccessPoint(Long idPosicao, String bssid, String essid, Float confianca) {
		super();
		this.idPosicao = idPosicao;
		this.bssid = bssid;
		this.essid = essid;
		this.confianca = confianca;
	}

	public Long getId() {
		return id;
	}
	
	public String getbssid() {
		return bssid;
	}

	public String getessid() {
		return essid;
	}

	public Long getidPosicao() {
		return idPosicao;
	}
	
	public Float getconfianca() {
		return confianca;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setbssid(String bssid) {
		this.bssid = bssid;
	}

	public void setessid(String essid) {
		this.essid = essid;
	}

	public void setidPosicao(Long idPosicao) {
		this.idPosicao = idPosicao;
	}
	
	public void setconfianca(Float confianca){
		this.confianca = confianca;
	}
	
}
