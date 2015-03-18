package br.ufrj.cos.labia.aips.ips;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;

public interface IPS {
	
	public void learn(Reading reading, Location location);
	
	public Map<Location, Float> predict(Reading reading);

	public float getConfidence();
	
	public float getProbability();
	
	public float getConfidence(Location location);
	
	public void close();

	public String getNomeVersao();

	public void save(Context context, String filename);
	
	// TODO Add save and load methods
	
	// TODO Add getVersionName()
	
}
