package br.ufrj.cos.labia.aips.ips;

import java.util.LinkedHashMap; 


import android.content.Context;

public interface IPS {
	
	public void learn(Reading reading, Location location);
	
	public LinkedHashMap<Location, Float> predict(Reading reading);

	public float getConfidence();
	
	public float getConfidence(Location location);
	
	public void close();

	public String getNomeVersao();

	public void save(Context context, String filename);
	
	// TODO Add save and load methods
	
	// TODO Add getVersionName()
	
}
