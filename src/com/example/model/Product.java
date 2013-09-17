package com.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A product have different tasks that is needed to complete the product
 * 
 * @author Robin Gronberg
 * 
 */
public class Product implements TaskListener {
	/**
	 * Create a new product with tasks
	 * 
	 * @param tasks
	 *            The tasks which is needed to complete this product.
	 */
	public Product(List<Task> tasks) {
		this.listeners = new ArrayList<ProductListener>();
		this.tasks = new ArrayList<Task>(tasks);
	}

	/**
	 * Create a new product no tasks
	 */
	public Product() {
		this(new ArrayList<Task>());
	}

	/**
	 * The id of this Product
	 */
	private int id;
	/**
	 * What material and color this product should have
	 */
	private String materialColor;
	/**
	 * A description about this product
	 */
	private String description;
	/**
	 * What kind of frontwork this Product should have
	 */
	private String frontWork;
	/**
	 * The {@link ProductListener}s that should listen when a task is changed on
	 * this product.
	 */
	private List<ProductListener> listeners;
	/**
	 * The {@link Task}s that this product has.
	 */
	private List<Task> tasks;

	@Override
	public void changed(Task task) {
		notifyProductListeners();
	}

	/**
	 * Notify listeners that tasks have been changed
	 */
	private void notifyProductListeners() {
		for (ProductListener l : listeners) {
			l.changed(this);
		}
	}

	/**
	 * Get all the tasks this product has
	 * 
	 * @return All the tasks this product has.
	 */
	public List<Task> getTasks() {
		return new ArrayList<Task>(tasks);
	}

	/**
	 * Add a task to this product task list.
	 * 
	 * @param task
	 *            The {@link Task} to add.
	 * @param index
	 *            The index this task should get in the list of tasks. index =
	 *            -1 or index >= tasks.length adds the task last in the list.
	 * @return true if Tasks was modified. false otherwise
	 */
	public boolean addTask(Task task, int index) {
		if (!tasks.contains(task)) {
			if (index == -1 || index >= tasks.size()) {
				tasks.add(task);
			} else {
				tasks.add(index, task);
			}
			task.addTaskListener(this);
			notifyProductListeners();
			return true;
		}
		return false;
	}

	/**
	 * remove Task from this product task list.
	 * 
	 * @param task
	 *            The {@link Task} to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public boolean removeTask(Task task) {
		if (tasks.contains(task)) {
			notifyProductListeners();
			tasks.remove(task);
			task.removeTaskListener(this);
			return true;
		} else
			return false;
	}
}
