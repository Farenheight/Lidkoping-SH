package com.example.model;
/**
 * A task is something that needs to be done to finish a product. 
 * @author Robin Gronberg
 *
 */
public class Task {
	/**
	 * The name of the task
	 */
	private String name;
	/**
	 * The status of the task
	 */
	private boolean status;
	/**
	 * Get whenever this Task is finished
	 * @return true if Task is finished, false otherwise.
	 */
	public boolean getStatus() {
		return status;
	}
	/**
	 * Set if this task is finished or not
	 * @param status true if finished, false otherwise
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	/**
	 * Get the name of this task
	 * @return The name of this task
	 */
	public String getName() {
		return name;
	}
	/**
	 * Create a new task.
	 * @param name The name of the task
	 * @param status The status of the task. true if done. false otherwise. 
	 */
	public Task(String name, boolean status){
		this.name = name;
		this.status = status;
	}
	/**
	 * Create a new unfinished task.
	 * @param name The name of the task
	 */
	public Task(String name){
		this.name = name;
	}
}
