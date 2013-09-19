package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A product have different tasks that is needed to complete the product
 * 
 * @author Robin Gronberg
 * 
 */
public class Product implements Listener<Task>, Syncable<Product> {
	private static int currentId = 0;
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
	private List<Task> tasks;
	
	/**
	 * Create a new product with tasks
	 * 
	 * @param tasks
	 *            The tasks which is needed to complete this product.
	 */
	public Product(List<Task> tasks) {
		this.listeners = new ArrayList<Listener<Product>>();
		this.tasks = new ArrayList<Task>(tasks);
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

	/**
	 * Create a new product no tasks
	 */
	public Product() {
		this(new ArrayList<Task>());
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
			task.addTaskListener(this);
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
			task.removeTaskListener(this);
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
	public boolean removeTaskListener(Listener<Product> listener) {
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
			for (Task newTask : newData.tasks) {
				// Updates all tasks that exists in both Products
				boolean synced = false;
				for (Task oldTask : this.tasks) {
					if (synced = oldTask.sync(newTask))
						break;
				}
				// Adds task if it doesn't exist on this Product
				if (!synced) {
					this.addTask(newTask, -1);
				}
			}
			// Removes all old tasks that new product don't have
			List<Task> deltaTasks = this.getTasks();
			deltaTasks.removeAll(newData.getTasks());
			removeTasks(deltaTasks);
			return true;
		} else {
			return false;
		}
	}
	@Override
	public boolean equals(Object o) {
		if(this == o){
			return true;
		}else if(o == null || o.getClass() != getClass()){
			return false;
		}else{
			return this.id == ((Product)o).id;
			//TODO check more fields.
		}
	}
	
	public static int getNewId(){
		return currentId++;
	}
}
