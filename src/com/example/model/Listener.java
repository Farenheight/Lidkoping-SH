package com.example.model;

/**
 * Something that should listen to another object of type {@link T}. 
 * @author robin
 *
 * @param <T> The type of object to listen to
 */
public interface Listener <T> {
	/**
	 * Something has been changed in the object T and is notified to its listeners.
	 * @param object
	 */
	public void changed(T object);
}
