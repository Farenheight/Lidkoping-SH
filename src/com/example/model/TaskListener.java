package com.example.model;
/**
 * Anything that needs to listen to a task is a {@link TaskListener}
 * @author robin
 *
 */
public interface TaskListener {
	/**
	 * When a task is changed notify listeners.
	 * @param task
	 */
	public void taskChanged(Task task);
}
