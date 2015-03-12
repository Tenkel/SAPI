package br.ufrj.cos.labia.aips.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class SalaDTO {
	
	public SalaDTO(JSONObject obj) throws JSONException {
		Id = obj.getLong("Id");
		Nome = obj.getString("Nome");
		IdPoligono = obj.getString("IdPoligono");
		Coordenadas = obj.getString("Coordenadas");
	}

	public Long Id;
	
	public String Nome;
	
	public String IdPoligono;
	
	public String Coordenadas;
	
}
