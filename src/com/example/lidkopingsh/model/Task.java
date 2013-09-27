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
	private List<Listener<Task>> listeners;
	private Status status;
	private Station station;

	/**
	 * Create a new task.
	 * 
	 * @param name
	 *            The name of the task
	 * @param status
	 *            The status of the task. DONE if done. NOT_DONE if not done.
	 */
	public Task(Station station, Status status) {
		this.status = status;
		this.listeners = new ArrayList<Listener<Task>>();
		this.station = station;
	}

	/**
	 * Create a new unfinished task.
	 * 
	 * @param station
	 *            The station
	 */
	public Task(Station station) {
		this(station, Status.NOT_DONE);
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
		if (this.status != status) {
			notifyTaskListeners();
		}
		this.status = status;
	}

	public Station getStation() {
		return station;
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
			return this.station.equals(t.getStation())
					&& this.status.equals(t.getStatus());
		}
	}

	@Override
	public boolean sync(Task newData) {
		if (newData != null && getClass() == newData.getClass()
				&& this.station.equals(((Task) newData).station)) {
			this.setStatus(newData.getStatus());
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		return station.getName() + " " + status;
	}
}
