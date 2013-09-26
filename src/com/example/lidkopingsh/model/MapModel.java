package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapModel implements IModel {
	private Map<Integer, Order> orders;
	private Map<Integer, Product> products;
	private List<Task> tasks;
	
	public MapModel(Collection<Order> o) {
		this.products = new HashMap<Integer, Product>();
		this.orders = new HashMap<Integer, Order>();
		
		for(Order or : o){
			this.orders.put(or.getId(), or);
			for(Product p : or.getProducts()){
				this.products.put(p.getId(), p);
			}
		}
		this.tasks = new ArrayList<Task>();

		// Store all possible tasks
		for (Order order : orders.values()) {
			for (Product product : order.getProducts()) {
				for (Task task : product.getTasks()) {
					boolean match = false;
					for (Task taskListItem : tasks) {
						if (task.getId() == taskListItem.getId()) {
							match = true;
						}
					}
					if (!match) {
						tasks.add(task);
					}
				} // task
			} // product
		} // order
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getProductById(int)
	 */
	@Override
	public Product getProductById(int id) throws NoSuchElementException {
		Product p = products.get(id);
		if (p == null) {
			throw new NoSuchElementException();
		} else {
			return p;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getOrderById(int)
	 */
	@Override
	public Order getOrderById(int id) throws NoSuchElementException {
		Order o = orders.get(id);
		if (o == null) {
			throw new NoSuchElementException();
		} else {
			return o;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.lidkopingsh.model.IModel#addOrder(com.example.lidkopingsh
	 * .model.Order)
	 */
	@Override
	public void addOrder(Order o) {
		if (orders.get(o.getId()) != null) {
			removeOrder(o);
		}
		orders.put(o.getId(), o);
		for (Product p : o.getProducts()) {
			products.put(p.getId(), p);
		}
		
		// TODO When adding orders, check if the order have tasks that
		// does not exist in any other order.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.lidkopingsh.model.IModel#removeOrder(com.example.lidkopingsh
	 * .model.Order)
	 */
	@Override
	public void removeOrder(Order o) {
		orders.remove(o.getId());
		for (Product p : orders.get(o.getId()).getProducts()) {
			products.remove(p.getId());
		}
		// TODO When removing orders, check if the order have tasks that
		// does not exist in any other order.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getOrders()
	 */
	@Override
	public Collection<Order> getOrders() {
		return orders.values();
	}

	@Override
	public List<Task> getAllExistingTasks() {
		return tasks;
	}
}
