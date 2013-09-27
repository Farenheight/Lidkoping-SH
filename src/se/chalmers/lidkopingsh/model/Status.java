package se.chalmers.lidkopingsh.model;

public enum Status {
	DONE(0),NOT_DONE(1);
	
	private final int id;
	
	private Status(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the status with the specified id.
	 * @param id The id of the status.
	 * @return The status.
	 */
	public static Status valueOf(int id) {
		return values()[id];
	}
}
