package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import se.chalmers.lidkopingsh.util.Listener;
import se.chalmers.lidkopingsh.util.Syncable;
import se.chalmers.lidkopingsh.util.Syncher;

/**
 * A product is something with different tasks that is needed to complete the
 * product. A Product is something made of stone, so different fields like
 * frontWork or materialColor is needed to describe how the Product should look
 * 
 * @author Robin Gronberg
 * 
 */
public class Product implements Listener<Task>, Syncable<Product> {
	private int id;
	private String materialColor;
	private String description;
	private String frontWork;
	private ProductType type;
	/**
	 * The {@link ProductListener}s that should listen when a task is changed on
	 * this product.
	 */
	private transient List<Listener<Product>> listeners;
	/**
	 * The {@link Task}s that this product has.
	 */
	private List<Task> tasks;

	/**
	 * Create a new Product
	 * 
	 * @param id
	 *            The id of the Product (from the database). The id should be
	 *            unique for each element
	 * @param materialColor
	 *            The material and color for this Product.
	 * @param description
	 *            The description for this Product.
	 * @param frontWork
	 *            The frontWork for this product
	 * @param tasks
	 *            The tasks this Product should go through for this product to
	 *            be completed.
	 */
	public Product(int id, String materialColor, String description,
			String frontWork, List<Task> tasks, ProductType type) {
		this.id = id;
		this.materialColor = materialColor != null ? materialColor : "";
		this.description = description != null ? description : "";
		this.frontWork = frontWork != null ? frontWork : "";
		this.type = type;
		this.materialColor = materialColor != null ? materialColor : "";
		this.description = description != null ? description : "";
		this.frontWork = frontWork != null ? frontWork : "";
		this.listeners = new ArrayList<Listener<Product>>();
		this.tasks = new TaskList(tasks);
		
		if (tasks != null) {
			for (Task t : tasks) {
				t.addTaskListener(this);
			}
		}
	}

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
	 */
	public Product(int id, String materialColor, String description,
			String frontWork, ProductType type) {
		this(id, materialColor, description, frontWork, new ArrayList<Task>(),
				type);
	}

	/**
	 * Create a new product with tasks
	 * 
	 * @param tasks
	 *            The tasks which is needed to complete this product.
	 */
	public Product(List<Task> tasks) {
		this(0, "", "", "", tasks, new ProductType(0, ""));
	}
	
	/**
	 * Create a new product with a product.
	 * 
	 * @param product
	 *            the product to use values from.
	 */
	public Product(Product product) {
		this(product.id, product.materialColor, product.description,
				product.frontWork, product.tasks, product.type);
	}

	public ProductType getType() {
		return type;
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
	 * Get the number of {@link Station}'s left until station. Returns
	 * Integer.MAX_VALUE if Product doesn't have a @ {@link Task} with station
	 * or if station has already passed.
	 * 
	 * @param station
	 *            The Station to check
	 * @return The number of stations left until station s.
	 */
	public int getNumOfStationsLeft(Station station) {
		int taskIndex = 0;
		for (Task t : getTasks()) {
			if (t.getStatus().equals(Status.NOT_DONE)) {
				if (t.getStation().equals(station)) {
					return taskIndex;
				} else {
					taskIndex++;
				}
			}
		}
		return Integer.MAX_VALUE;
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
		if(tasks == null){
			return null;
		}
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
	 */
	public void addTask(Task task, int index) {
		Iterator<Task> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			Task t = iterator.next();
			if (t != task && t.getStation().equals(task.getStation())) {
				iterator.remove();
			}
		}
		if (!tasks.contains(task)) {
			if (index == -1 || index >= tasks.size()) {
				tasks.add(task);
			} else {
				tasks.add(index, task);
			}
			notifyProductListeners();
		}
	}

	/**
	 * Add a task at the end of this product task list.
	 * 
	 * @param task
	 *            The {@link Task} to add.
	 */
	public void addTask(Task task) {
		addTask(task, -1);
	}

	/**
	 * Add tasks at the end of this product task list.
	 * 
	 * @param tasks
	 *            The {@link Task}s to add.
	 */
	public void addTasks(List<Task> tasks) {
		for (Task task : tasks) {
			addTask(task, -1);
		}
	}

	/**
	 * remove Task from this product task list.
	 * 
	 * @param task
	 *            The {@link Task} to remove
	 */
	public void removeTask(Task task) {
		if (tasks.remove(task)) {
			notifyProductListeners();
		}
	}

	/**
	 * remove Tasks from this product task list.
	 * 
	 * @param tasks
	 *            The {@link Task}s to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public void removeTasks(List<Task> tasks) {
		for (Task task : tasks) {
			removeTask(task);
		}
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
		if (listeners == null) {
			listeners = new ArrayList<Listener<Product>>();
		}
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
		if (newData != null && this.id == newData.id
				&& this.getClass() == getClass()) {
			this.description = newData.description;
			this.frontWork = newData.frontWork;
			this.materialColor = newData.materialColor;
			Syncher.syncList(tasks, newData.getTasks());
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
					&& ((this.description == null && p.getDescription() == null) || (this.description != null && this.description
							.equals(p.getDescription())))
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
	private class TaskList extends ArrayList<Task> {
		private static final long serialVersionUID = 4082149811877348098L;

		public TaskList(Collection<Task> collection) {
			super(collection == null? new ArrayList<Task>() : collection);
		}

		@Override
		public void add(int index, Task object) {
			object.addTaskListener(Product.this);
			super.add(index, object);
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
