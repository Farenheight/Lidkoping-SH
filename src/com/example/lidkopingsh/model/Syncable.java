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
	 * Sync this object with data
	 * 
	 * @param newData
	 *            The object to sync with this object
	 * @return true if this object was modified, false otherwise
	 */
	public boolean sync(T newData);
}
