package br.ufrj.cos.labia.aips.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class PontoDeColetaDTO {

	public Long Id;
	
	public Double x;
	
	public Double y;
	
	public PontoDeColetaDTO(JSONObject obj) throws JSONException {
		Id = obj.getLong("Id");
		x = obj.getDouble("x");
		y = obj.getDouble("y");
	}
	
}
