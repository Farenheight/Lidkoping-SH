package com.example.model;

import java.util.ArrayList;
import java.util.List;
/**
 * A product have different tasks that is needed to complete the product
 * @author Robin Gronberg
 *
 */
public class Product {
	/**
	 * Create a new product with listeners and tasks
	 * @param listeners The listeners that listens when a task is changed on this product. 
	 * @param tasks The tasks which is needed to complete this product.
	 */
	public Product(List<ProductListener> listeners,List<Task> tasks){
		this.listeners =  new ArrayList<ProductListener>(listeners);
		this.tasks = new ArrayList<Task>(tasks);
	}
	/**
	 * The id of this Product
	 */
	private int id;
	/**
	 * The last time this Product was changed
	 */
	private int lastTimeUpdate;
	/**
	 * The {@link ProductListener}s that should listen when a task is changed on this product.
	 */
	private List<ProductListener> listeners;
	/**
	 * The {@link Task}s that this product has.
	 */
	private List<Task> tasks;
	/**
	 * Notify listeners that tasks have been changed
	 */
	private void notifyProductListeners(){
		for(ProductListener l : listeners){
			l.taskChanged(this);
		}
		//TODO: Update time when this Product was last changed
		lastTimeUpdate = 111111;
	}
	/**
	 * Set the status of a Task. If that task is not a part of this product tasks,
	 * IllegalArgumentException is called. 
	 * @param task The {@link Task} to set status on.
	 * @param status The status this {@link Task} should have.
	 */
	public void setTaskStatus(Task task, boolean status){
		if(tasks.contains(task)){
			task.setStatus(status);
		}else{
			throw new IllegalArgumentException("This product doesn't have that kind of task");
		}
		notifyProductListeners();
	}
	/**
	 * Add a task to this product task list.
	 * @param task The {@link Task} to add.
	 * @param index The index this task should get in the list of tasks. 
	 * index = -1 or index >= tasks.length adds the task last in the list.
	 * @return true if Tasks was modified. false otherwise
	 */
	public boolean addTask(Task task, int index){
		if(!tasks.contains(task)){
			if(index == -1 || index >= tasks.size()){
				tasks.add(task);				
			}
			else{
				tasks.add(index, task);
			}
			notifyProductListeners();
			return true;
		}
		return false;
	}
	/**
	 * remove Task from this product task list.
	 * @param task The {@link Task} to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public boolean removeTask(Task task){
		if(tasks.contains(task)){
			notifyProductListeners();
			tasks.remove(task);
			return true;
		}
		else return false;
	}
}
