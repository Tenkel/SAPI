package com.tenkel.sapi.kde;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

public class KDEPlace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7484324410054490302L;
	
	private Long location;

	private Map<String, KDEFunction> mKDE;

	// Add other sensors

	public KDEPlace(Long location) {
		this.location = location;
		mKDE = new HashMap<String, KDEFunction>();
	}

	public KDEPlace(Long location, List<KDEFunction> KDEs) {
		this.location = location;
		for (KDEFunction kde : KDEs)
			addFunction(kde);
	}

	public Map<String, KDEFunction> getFunctions() {
		return mKDE;
	}
	
	public float getTendency(Reading reading){
		float tendency = 0;
		KDEFunction kde;
		for(WIFISignal signal : reading.getSignals())
			if( (kde = mKDE.get(signal.getBSSID())) != null)
				tendency += kde.log_prob(signal.getLevel());
			else
				tendency += KDEFunction.KDEstd;
		
		return tendency;
		
	}
	
	public void processAll(){
		for( KDEFunction kde : mKDE.values())
			kde.learn();
	}
	
	public void addReading(Reading reading) {
		storeReading(reading);
		try {
			for (KDEFunction f : mKDE.values()) {
				f.learn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}
	}
	
	public void storeReading(Reading reading){
		for(WIFISignal signal : reading.getSignals())
			storeWIFISignal(signal);
	}
	
	public void storeWIFISignal(WIFISignal signal){
		if(!mKDE.containsKey(signal.getBSSID())){
			KDEFunction kde = new KDEFunction(signal.getBSSID());
			kde.memorize(signal.getLevel());
			mKDE.put(kde.getBSSID(), kde);
		}
		else
			mKDE.get(signal.getBSSID()).memorize(signal.getLevel());		
	}
	
	public double addWIFISignal(WIFISignal signal){
		if(!mKDE.containsKey(signal.getBSSID())){
			KDEFunction kde = new KDEFunction(signal.getBSSID());
			mKDE.put(kde.getBSSID(), kde);
			return kde.learn(signal.getLevel());
		}
		else
			return mKDE.get(signal.getBSSID()).learn(signal.getLevel());		
	}

	public double addFunction(KDEFunction KDEfunc) {
		if (!mKDE.containsKey(KDEfunc.getBSSID())){
			mKDE.put(KDEfunc.getBSSID(), KDEfunc);
			return KDEfunc.bandwidth();
		}
		else {
			KDEFunction updated_kde = mKDE.get(KDEfunc.getBSSID());
			updated_kde.merge(KDEfunc);
			mKDE.remove(KDEfunc.getBSSID());
			mKDE.put(KDEfunc.getBSSID(), updated_kde);
			return updated_kde.bandwidth();
		}
	}

	public Long getLocationId() {
		return location;
	}

	public void setLocation(Long location) {
		this.location = location;
	}

}
