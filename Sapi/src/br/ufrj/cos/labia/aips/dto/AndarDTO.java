package br.ufrj.cos.labia.aips.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AndarDTO {
	
	public List<PontoDeColetaDTO> PontosColeta;
	
	public List<SalaDTO> Salas;
	
	public Long Id;
	
	public String Nome;
	
	public String UriGMLAndar;
	
	public double[] CoordenadasGml;

	public AndarDTO(JSONObject obj) throws JSONException {
		
		PontosColeta = new ArrayList<PontoDeColetaDTO>();
		JSONArray children = obj.getJSONArray("PontosColeta");
		for (int i=0;i<children.length();++i)
			PontosColeta.add(new PontoDeColetaDTO(children.getJSONObject(i)));
		
		Salas = new ArrayList<SalaDTO>();
		children = obj.getJSONArray("Salas");
		for (int i=0;i<children.length();++i)
			Salas.add(new SalaDTO(children.getJSONObject(i)));
		
		Id = obj.getLong("Id");
		Nome = obj.getString("Nome");
		UriGMLAndar = obj.getString("UriGMLAndar");
		
		if (UriGMLAndar.equals("null"))
			UriGMLAndar = "http://cos.ufrj.br/~diegosouza/plan.png";
		
		String coordenadas = obj.getString("CoordenadasGml");
		String[] coordenada = coordenadas.split(" ");
		CoordenadasGml = new double[coordenada.length];
		for (int i=0;i<coordenada.length;++i)
			CoordenadasGml[i] = Double.parseDouble(coordenada[i]);
	}
	
}
