package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A task is something that needs to be done to finish a product.
 * 
 * @author Robin Gronberg
 * 
 */
public class Task {
	private List<Listener<Task>> listeners;
	/**
	 * The name of the task
	 */
	private String name;
	/**
	 * The status of the task
	 */
	private Status status;

	/**
	 * Get whenever this Task is finished
	 * 
	 * @return true if Task is finished, false otherwise.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set if this task is finished or not
	 * 
	 * @param status
	 *            true if finished, false otherwise
	 */
	public void setStatus(Status status) {
		this.status = status;
		notifyTaskListeners();
	}

	/**
	 * Get the name of this task
	 * 
	 * @return The name of this task
	 */
	public String getName() {
		return name;
	}

	/**
	 * Create a new task.
	 * 
	 * @param name
	 *            The name of the task
	 * @param status
	 *            The status of the task. true if done. false otherwise.
	 */
	public Task(String name, Status status) {
		this.name = name;
		this.status = status;
		this.listeners = new ArrayList<Listener<Task>>();
	}
	/**
	 * Create a new unfinished task.
	 * 
	 * @param name
	 *            The name of the task
	 */
	public Task(String name) {
		this(name,Status.NOT_DONE);
	}

	/**
	 * Add TaskListener to this Task
	 * 
	 * @param listener
	 *            The {@link TaskListener} that should listen to this
	 *            {@link Task}
	 * @return true if listeners was modified, false otherwise.
	 */
	public boolean addTaskListener(Listener<Task> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * Remove TaskListener to this Task
	 * 
	 * @param listener
	 *            The {@link TaskListener} that should not listen to this
	 *            {@link Task} anymore
	 * @return true if listeners was modified, false otherwise.
	 */
	public boolean removeTaskListener(Listener<Task> listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
			return true;
		}
		return false;
	}


	/**
	 * Notify listeners that this task have been changed
	 */
	private void notifyTaskListeners() {
		for (Listener<Task> l : listeners) {
			l.changed(this);
		}
	}
	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true;
		}else if (o == null || o.getClass() != getClass()){
			return false;
		}else{
			return ((Task)o).getStatus().equals(getStatus()) &&
					((Task)o).getName().equals(getName());
		}
	}
}
