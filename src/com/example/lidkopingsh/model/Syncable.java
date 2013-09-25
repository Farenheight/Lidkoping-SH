package com.example.lidkopingsh.model;

/**
 * Something that should can be synced from another source of data.
 * 
 * @author Robin Gronberg
 * 
 * @param <T>
 *            The type of data to sync
 */
public interface Syncable<T> {
	/**
	 * Sync this object with data. This method should be Reflexive
	 * <p>
	 * o1.sync(o2) <=> o2.sync(o1)
	 * </p>
	 * <p>
	 * After sync between t0 and t1. t0.equals(t1) should always return true.
	 * </p>
	 * 
	 * @param newData
	 *            The object to sync with this object
	 * @return true if this object was modified, false otherwise
	 */
	public boolean sync(T newData);

}
