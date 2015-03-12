package br.ufrj.cos.labia.aips.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalDTO {
    
	public Long Id;
    
    public String Nome;
    
    public Double TopLeftLat;
    
    public Double TopLeftLong;
    
    public Double BottomDownLat;
    
    public Double BottomDownLong;
    
    public List<AndarDTO> Andares;

	public LocalDTO() {
		
	}
    
    public LocalDTO(JSONObject obj) throws JSONException {
    	
    	if (obj.has("Id")) Id = obj.getLong("Id");
    	if (obj.has("Nome")) Nome = obj.getString("Nome");
    	if (obj.has("TopLeftLat")) TopLeftLat = obj.getDouble("TopLeftLat");
    	if (obj.has("TopLeftLong")) TopLeftLong = obj.getDouble("TopLeftLong");
    	if (obj.has("BottomDownLat")) BottomDownLat = obj.getDouble("BottomDownLat");
    	if (obj.has("BottomDownLong")) BottomDownLong = obj.getDouble("BottomDownLong");
    	
    	if (obj.has("Andares")) {
	    	Andares = new ArrayList<AndarDTO>();
	    	JSONArray children = obj.getJSONArray("Andares");
			for (int i=0;i<children.length();++i)
				Andares.add(new AndarDTO(children.getJSONObject(i)));
    	}
    }
    
}
