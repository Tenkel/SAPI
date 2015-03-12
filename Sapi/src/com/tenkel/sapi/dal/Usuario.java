package com.tenkel.sapi.dal;

public class Usuario{
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String CHAVE = "chave";
	public static final String IMEI = "imei";
	public static final String ENDMAC = "endMac";

	private Long id;
	
	private String nome;

	private String chave;
	
	private String imei;
	
	private String endMac;
	

	public Usuario() {

	}

	public Usuario(String nome, String chave, String imei, String endMac) {
		super();
		this.nome = nome;
		this.chave = chave;
		this.imei = imei;
		this.endMac = endMac;
	}

	public Long getId() {
		return id;
	}
	
	public String getnome() {
		return nome;
	}

	public String getchave() {
		return chave;
	}

	public String getimei() {
		return imei;
	}
	
	public String getendMac() {
		return endMac;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setnome(String nome) {
		this.nome = nome;
	}

	public void setchave(String chave) {
		this.chave = chave;
	}

	public void setimei(String imei) {
		this.imei = imei;
	}
	
	public void setendMac(String endMac) {
		this.endMac = endMac;
	}

}
