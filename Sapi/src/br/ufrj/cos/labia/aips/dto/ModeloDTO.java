package br.ufrj.cos.labia.aips.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class ModeloDTO {

	public String versaoModelo;
    
	public String dataRecebimentoDoModelo;
    
	public ModeloDTO() {
		
	}
	
	public ModeloDTO(JSONObject json) throws JSONException {
		dataRecebimentoDoModelo = json.getString("dataRecebimentoDoModelo");
		versaoModelo = json.getString("versaoModelo");
	}
}
