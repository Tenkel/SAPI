package br.ufrj.cos.labia.aips.ips;

public class Location {

	private Long mPointId;
	
	// TODO Add room_id, floor_id, level_id, etc
	
	public Location() {
		super();
	}

	public Location(Long pointId) {
		super();
		this.setPointId(pointId);
	}

	public Long getPointId() {
		return mPointId;
	}

	public void setPointId(Long mPoint) {
		this.mPointId = mPoint;
	}

}
