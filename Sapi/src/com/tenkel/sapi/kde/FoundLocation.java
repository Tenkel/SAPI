package com.tenkel.sapi.kde;

public class FoundLocation {

	private Long mPointId;
	private String mAndarName;
	private String mPosicaoName;
	private Float mConfianca;
	private Float mProbabilidade;
	
	// TODO Add room_id, floor_id, level_id, etc
	
	public FoundLocation() {
		super();
	}

	public FoundLocation(Long pointId, String andarName, Float Confianca, Float Probabilidade, String PosicaoName) {
		super();
		this.setPointId(pointId);
		this.setAndarName(andarName);
		this.setConfianca(Confianca);
		this.setProbabilidade(Probabilidade);
		this.setPosicaoName(PosicaoName);
	}

	public Long getPointId() {
		return mPointId;
	}

	public void setPointId(Long mPoint) {
		this.mPointId = mPoint;
	}

	public String getAndarName() {
		return mAndarName;
	}

	public void setAndarName(String mAndarName) {
		this.mAndarName = mAndarName;
	}

	public Float getConfianca() {
		return mConfianca;
	}

	public void setConfianca(Float mConfianca) {
		this.mConfianca = mConfianca;
	}

	public Float getProbabilidade() {
		return mProbabilidade;
	}

	public void setProbabilidade(Float mProbabilidade) {
		this.mProbabilidade = mProbabilidade;
	}

	public String getPosicaoName() {
		return mPosicaoName;
	}

	public void setPosicaoName(String mPosicaoName) {
		this.mPosicaoName = mPosicaoName;
	}


}
