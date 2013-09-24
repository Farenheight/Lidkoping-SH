package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A task is something that needs to be done to finish a product.
 * 
 * @author Robin Gronberg
 * 
 */
public class Task implements Syncable<Task> {
	private int id;
	private List<Listener<Task>> listeners;
	private String name;
	private Status status;

	/**
	 * Create a new task.
	 * 
	 * @param id
	 *            The id of the Task
	 * @param name
	 *            The name of the task
	 * @param status
	 *            The status of the task. true if done. false otherwise.
	 */
	public Task(int id, String name, Status status) {
		this.name = name;
		this.status = status;
		this.listeners = new ArrayList<Listener<Task>>();
		this.id = id;
	}

	/**
	 * Create a new unfinished task.
	 * 
	 * @param id
	 *            The id of the Task
	 * @param name
	 *            The name of the task
	 */
	public Task(int id, String name) {
		this(id, name, Status.NOT_DONE);
	}

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

	public int getId() {
		return id;
	}

	/**
	 * Add {@link Listener} to this Task
	 * 
	 * @param listener
	 *            The {@link TaskListener} that should listen to this
	 *            {@link Task}
	 */
	public void addTaskListener(Listener<Task> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Remove {@link Listener} to this Task
	 * 
	 * @param listener
	 *            The {@link TaskListener} that should not listen to this
	 *            {@link Task} anymore
	 */
	public void removeTaskListener(Listener<Task> listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
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
		if (o == this) {
			return true;
		} else if (o == null || o.getClass() != getClass()) {
			return false;
		} else {
			Task t = (Task) o;
			return this.id == t.getId() && this.name.equals(t.getName())
					&& this.status.equals(t.getStatus());
		}
	}

	@Override
	public boolean sync(Task newData) {
		if (newData != null && this.id == newData.id) {
			this.setStatus(newData.getStatus());
			this.name = newData.name;
			return true;
		} else {
			return false;
		}
	}
}
