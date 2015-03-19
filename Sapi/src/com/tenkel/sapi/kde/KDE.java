package com.tenkel.sapi.kde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tenkel.sapi.dal.ZipBuilder;
import com.tenkel.sapi.dal.ZipReader;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.IPSException;
import br.ufrj.cos.labia.aips.ips.InvalidModelException;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;

public class KDE implements IPS {

	private LongSparseArray<KDEPlace> mPlace;
	
	private LongSparseArray<Float> mPlaceConfidence;
	
	private float last_confidence = Float.NEGATIVE_INFINITY;
	
	public KDE(LongSparseArray<KDEPlace> mPlace) {
		super();
		this.mPlace = mPlace;
		mPlaceConfidence = new LongSparseArray<Float>();
	}

	public KDE() {
		super();
		mPlace = new LongSparseArray<KDEPlace>();
		mPlaceConfidence = new LongSparseArray<Float>();
	}

	private static final String KDEv0 = "com.tenkel.sapi.ips.kde.KDEv0";

	@Override
	public void learn(Reading reading, Location location) {
		KDEPlace kde_place;
		if((kde_place = mPlace.get(location.getPointId())) != null)
			kde_place.addReading(reading);
		
		else{
			kde_place = new KDEPlace(location.getPointId());
			kde_place.addReading(reading);
			mPlace.put(location.getPointId(), kde_place);
		}
			
		
	}

	@Override
	public LinkedHashMap<Location, Float> predict(Reading reading) {
		LinkedHashMap<Location, Float> hmap = new LinkedHashMap<Location, Float>(); 
		float best_confidence = Float.NEGATIVE_INFINITY; 
//		Long best_location_id = null;
		float current_confidence;
		for(int i = 0; i < mPlace.size(); i++) {
			KDEPlace place = mPlace.valueAt(i);
			if((current_confidence = place.getTendency(reading)) > best_confidence) {
//				best_location_id = place.getLocationId();
				best_confidence = current_confidence;
			}
			mPlaceConfidence.put(place.getLocationId(), current_confidence);
			hmap.put(new Location(place.getLocationId()),current_confidence);
		}
		LinkedHashMap<Location, Float> smap = sortByComparator(hmap);
		last_confidence = best_confidence/reading.getSignals().size();
		return smap;
	}

	private LinkedHashMap<Location, Float> sortByComparator(LinkedHashMap<Location, Float> hmap) {
		// Convert Map to List
		List<Map.Entry<Location, Float>> list = new LinkedList<Map.Entry<Location, Float>>(hmap.entrySet());
		 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Location, Float>>() {
					public int compare(Map.Entry<Location, Float> o1,
		                                           Map.Entry<Location, Float> o2) {
						return (o1.getValue()).compareTo(o2.getValue());
					}
				});
		 Collections.reverse(list);
				// Convert sorted map back to a Map
		LinkedHashMap<Location, Float> sortedMap = new LinkedHashMap<Location, Float>();
				for (Iterator<Map.Entry<Location, Float>> it = list.iterator(); it.hasNext();) {
					Map.Entry<Location, Float> entry = it.next();
					sortedMap.put(entry.getKey(), entry.getValue());
				}
				return sortedMap;
	}

	@Override
	public float getConfidence() {
		return last_confidence;
	}

	@Override
	public float getConfidence(Location location) {
		return (mPlaceConfidence.get(location.getPointId()) != null) ? 
				mPlaceConfidence.get(location.getPointId()) : 
				Float.NEGATIVE_INFINITY;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNomeVersao() {
		return KDEv0;
	}

	@Override
	public void save(Context context, String filename) {
		try {
			File models = context.getDir("models", Context.MODE_PRIVATE);
			File tmp = context.getDir("tmp", Context.MODE_PRIVATE);
			
			File zipFile = new File(models.getAbsolutePath(), filename);
			File kdeFile = new File(tmp.getAbsolutePath(), UUID.randomUUID().toString());
			
			// Grava o arquivo com os dados serializados em java
			ObjectOutputStream joos = new ObjectOutputStream(
					new FileOutputStream(kdeFile));
			
			joos.writeObject(mPlace);
			joos.close();
			
			// Comprime
			new ZipBuilder(zipFile)
				.put("kdeStuff", new FileInputStream(kdeFile))
				.close();
			
			// Remove os arquivos temporários
			kdeFile.delete();
			
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		throw new IPSException("Could not export model");
	}

	public static IPS load(Context context, String nomeVersao, String filename) throws InvalidModelException {
		switch(nomeVersao){
		case KDEv0:
			return loadKDEv0(context, filename);
		default:
			throw new IPSException("Versão " + nomeVersao + " do KDE não conhecida.");
		}
	}

	@SuppressWarnings("unchecked")
	private static IPS loadKDEv0(Context context, String filename) {
		try {
			File models = context.getDir("models", Context.MODE_PRIVATE);
			File zipFile = new File(models, filename);
			ByteArrayOutputStream javaStream = new ByteArrayOutputStream();

			
			new ZipReader(zipFile)
					.get("javaStuff", javaStream);
			
			ByteArrayInputStream javaFileReader = new ByteArrayInputStream(
					javaStream.toByteArray());
			
			// Carrega KDE
			ObjectInputStream ois = new ObjectInputStream(javaFileReader);
			Object obj = ois.readObject();
			LongSparseArray<KDEPlace> newmPlace = null;
			if (obj instanceof LongSparseArray<?>)
				newmPlace = (LongSparseArray<KDEPlace>) ois.readObject();
			else 
				throw new IPSException("Wrong type of model.");
			
			if (newmPlace == null)
				throw new IPSException("Failed to load the model.");
			
			return new KDE(newmPlace);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IPSException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static boolean canLoad(String nomeVersao) {
		if (nomeVersao == null) return false;
		if (nomeVersao.equals(KDEv0)) return true;
		return false;
	}

}
