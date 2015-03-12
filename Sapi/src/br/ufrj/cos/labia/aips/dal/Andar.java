package br.ufrj.cos.labia.aips.dal;

public class Andar {
	
	
	public static final String ID = "id";
	
	public static final String IDLOCAL = "idLocal";
	
	public static final String NOME = "nome";
	
	public static final String URIMAPA = "uriMapa";
	
	public static final String CAMADAS = "camadas";
	
	public static final String FK_ANDAR_LOCAL = "fk_Andar_Local";
	
	public static final String FK_ANDAR_LOCAL_IDX = "fk_Andar_Local_idx";

	public static final String IDREMOTO = "idRemoto";

	public static final String X1 = "x1";
	
	public static final String Y1 = "y1";
	
	public static final String X2 = "x2";
	
	public static final String Y2 = "y2";

	public static final String IMAGE = "image";

	public static String[] FIELDS = new String[] {ID, IDLOCAL, NOME, URIMAPA, CAMADAS, IDREMOTO, X1, Y1, X2, Y2, IMAGE};
	

	private Long id;
	
	private Long idLocal;
	
	private String nome;
	
	private String URIMapa;
	
	private String camadas;

	private Long idRemoto;
	
	private Double x1;
	
	private Double y1;
	
	private Double x2;
	
	private Double y2;

	private byte[] image;
	
	public Andar() {
		
	}
	
	public Andar(Long idLocal, String nome, String URIMapa, String camadas) {
		super();
		this.idLocal = idLocal;
		this.nome = nome;
		this.URIMapa = URIMapa;
		this.camadas = camadas;
	}


	public Long getId() {
		return id;
	}

	public Long getIdLocal() {
		return idLocal;
	}

	public String getNome() {
		return nome;
	}

	public String getURIMapa() {
		return URIMapa;
	}

	public String getCamadas() {
		return camadas;
	}

	public Long getIdRemoto() {
		return idRemoto;
	}

	public Double getX1() {
		return x1;
	}

	public Double getY1() {
		return y1;
	}

	public Double getX2() {
		return x2;
	}

	public Double getY2() {
		return y2;
	}

	public byte[] getImage() {
		return image;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setURIMapa(String uRIMapa) {
		URIMapa = uRIMapa;
	}

	public void setCamadas(String camadas) {
		this.camadas = camadas;
	}
	
	public void setIdRemoto(Long idRemoto) {
		this.idRemoto = idRemoto;
	}

	public void setX1(Double x1) {
		this.x1 = x1;
	}

	public void setY1(Double y1) {
		this.y1 = y1;
	}

	public void setX2(Double x2) {
		this.x2 = x2;
	}

	public void setY2(Double y2) {
		this.y2 = y2;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
