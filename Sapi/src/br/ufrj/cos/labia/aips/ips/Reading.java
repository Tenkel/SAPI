package br.ufrj.cos.labia.aips.ips;

import java.util.List;

public class Reading {

	private List<WIFISignal> mSignals;
	
	// Add other sensors
	
	public Reading() {
		super();
	}

	public Reading(List<WIFISignal> signals) {
		super();
		setSignals(signals);
	}

	public List<WIFISignal> getSignals() {
		return mSignals;
	}

	public void setSignals(List<WIFISignal> mSignals) {
		this.mSignals = mSignals;
	}

}
