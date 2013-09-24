package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A product have different tasks that is needed to complete the product
 * 
 * @author Robin Gronberg
 * 
 */
public class Product implements Listener<Task>, Syncable<Product> {
	private int id;
	private String materialColor;
	private String description;
	private String frontWork;
	/**
	 * The {@link ProductListener}s that should listen when a task is changed on
	 * this product.
	 */
	private List<Listener<Product>> listeners;
	/**
	 * The {@link Task}s that this product has.
	 */
	private SyncableList<Task> tasks;

	/**
	 * Create a new Product
	 * 
	 * @param id
	 *            The id of the Product (from the database). The id should be
	 *            unique for each element
	 * @param materialColor
	 *            The matierial and color for this Product.
	 * @param description
	 *            The description for this Product.
	 * @param frontWork
	 *            The frontWork for this product
	 * @param tasks
	 *            The tasks this Product should go through for this product to
	 *            be completed.
	 */
	public Product(int id, String materialColor, String description,
			String frontWork, List<Task> tasks) {
		this(tasks);
		this.id = id;
		this.materialColor = materialColor;
		this.description = description;
		this.frontWork = frontWork;
	}

	/**
	 * Create a new product with tasks
	 * 
	 * @param tasks
	 *            The tasks which is needed to complete this product.
	 */
	public Product(List<Task> tasks) {
		this.listeners = new ArrayList<Listener<Product>>();
		this.tasks = new SyncableTaskList(tasks);
	}

	/**
	 * Create a new product no tasks and dummy
	 */
	public Product() {
		this(new ArrayList<Task>());
	}

	public int getId() {
		return id;
	}

	public String getMaterialColor() {
		return materialColor;
	}

	public String getDescription() {
		return description;
	}

	public String getFrontWork() {
		return frontWork;
	}

	@Override
	public void changed(Task task) {
		notifyProductListeners();
	}

	/**
	 * Notify listeners that tasks have been changed
	 */
	private void notifyProductListeners() {
		for (Listener<Product> l : listeners) {
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
			// task.addTaskListener(this);
			notifyProductListeners();
			return true;
		}
		return false;
	}

	/**
	 * Add a task at the end of this product task list.
	 * 
	 * @param task
	 *            The {@link Task} to add.
	 */
	public boolean addTask(Task task) {
		return addTask(task, -1);
	}

	/**
	 * Add tasks at the end of this product task list.
	 * 
	 * @param tasks
	 *            The {@link Task}s to add.
	 * @return true if Tasks was modified. false otherwise
	 */
	public boolean addTasks(List<Task> tasks) {
		boolean modified = false;
		for (Task task : tasks) {
			// add last in the list
			if (addTask(task, -1)) {
				modified = true;
			}
		}
		return modified;
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
			// task.removeTaskListener(this);
			return true;
		} else
			return false;
	}

	/**
	 * remove Tasks from this product task list.
	 * 
	 * @param tasks
	 *            The {@link Task}s to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public boolean removeTasks(List<Task> tasks) {
		boolean modified = false;
		for (Task task : tasks) {
			if (removeTask(task)) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Add a {@link Listener} to this Product
	 * 
	 * @param listener
	 *            The {@link Listener} that should listen to this
	 *            {@link Product}
	 * @return true if listeners was modified, false otherwise.
	 */
	public boolean addProductListener(Listener<Product> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * Removes a {@link Listener} from this Product
	 * 
	 * @param listener
	 *            The {@link Listener} that should listen to this
	 *            {@link Product}
	 * @return true if listeners was modified, false otherwise.
	 */
	public boolean removeProductListener(Listener<Product> listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
			return true;
		}
		return false;
	}

	@Override
	public boolean sync(Product newData) {
		if (newData != null && this.id == newData.id) {
			this.description = newData.description;
			this.frontWork = newData.frontWork;
			this.materialColor = newData.materialColor;
			tasks.sync(newData.getTasks());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || o.getClass() != getClass()) {
			return false;
		} else {
			Product p = ((Product) o);
			return this.id == p.id
					&& this.materialColor.equals(p.getMaterialColor())
					&& this.description.equals(p.getDescription())
					&& this.frontWork.equals(p.getFrontWork())
					&& this.tasks.equals(p.getTasks());
		}
	}

	/**
	 * Inner class that makes sure that listeners are added and removed properly
	 * when an {@link Product} is synced.
	 * 
	 * @author robin
	 * 
	 */
	private class SyncableTaskList extends SyncableArrayList<Task> {
		private static final long serialVersionUID = 4082149811877348098L;

		public SyncableTaskList() {
		}

		public SyncableTaskList(Collection<Task> collection) {
			super(collection);
		}

		@Override
		public boolean add(Task object) {
			object.addTaskListener(Product.this);
			return super.add(object);
		}

		@Override
		public boolean remove(Object object) {
			if (object instanceof Task) {
				((Task) object).removeTaskListener(Product.this);
			}
			return super.remove(object);
		}
	}
}
