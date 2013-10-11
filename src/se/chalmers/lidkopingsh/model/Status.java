package se.chalmers.lidkopingsh.model;

import com.google.gson.annotations.SerializedName;

public enum Status {
	@SerializedName("0")
	NOT_DONE(0),
	@SerializedName("1")
	DONE(1);
	
	private final int id;
	
	private Status(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the status with the specified id. Used by database
	 * @param id The id of the status.
	 * @return The status.
	 */
	public static Status valueOf(int id) {
		return values()[id];
	}
}
