package br.ufrj.cos.labia.aips.ips;

public class WIFISignal {

	private String BSSID;
	
	private int level;

	public WIFISignal(String bSSID, int level) {
		super();
		BSSID = bSSID;
		this.level = level;
	}

	public String getBSSID() {
		return BSSID;
	}

	public int getLevel() {
		return level;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
