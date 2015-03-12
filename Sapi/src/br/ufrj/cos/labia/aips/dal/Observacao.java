package br.ufrj.cos.labia.aips.dal;


public class Observacao{
	public static final String ID = "id";
	public static final String IDPOSICAO = "idPosicao";
	public static final String INSTANTECOLETA = "instanteColeta";
	public static final String FK_OBSERVACAO_POSICAO = "fk_Observacao_Posicao";
	public static final String FK_SAMPLE_LOCAL_IDX = "fk_Sample_Local_idx";
	
	public static final String[] FIELDS = new String[] {ID, INSTANTECOLETA, IDPOSICAO};
	
	private Long id;
	
	private Long idPosicao;
	
	private Long timestamp;
	
	public Observacao(){
	}
	
	public Observacao(Long timestamp, Long idPosicao){
		super();
		this.timestamp = timestamp;
		this.idPosicao = idPosicao;
	}
	
	public Observacao(Observacao other) {
		timestamp = other.timestamp;
		idPosicao = other.idPosicao;
	}

	public Long getId() {
		return id;
	}

	public Long gettimestamp() {
		return timestamp;
	}


	public Long getIdPosicao() {
		return idPosicao;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void settimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setidPosicao(Long idPosicao) {
		this.idPosicao = idPosicao;
	}

	@Override
	public String toString() {
		return "Observacao [id=" + id + ", timestamp=" + timestamp + ", idPosicao="
				+ idPosicao + "]";
	}
	
}
