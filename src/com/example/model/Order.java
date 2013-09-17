package com.example.model;

import java.util.List;

/**
 * A class representing an Order.
 * 
 * @author Kim
 * 
 */
public class Order implements Listener<Product> {

	private int id;
	private final long timeCreated = System.currentTimeMillis();
	private long lastTimeUpdate = timeCreated;
	private String cementary;
	private long orderDate;
	private String orderNumber;
	private Customer customer;
	private List<Listener<Order>> orderListeners;
	private List<Product> products;

	public int getId() {
		return id;
	}

	public long getTimeCreated() {
		return timeCreated;
	}

	public long getLastTimeUpdate() {
		return lastTimeUpdate;
	}

	public String getCementary() {
		return cementary;
	}

	public long getOrderDate() {
		return orderDate;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<Product> getProducts() {
		return products;
	}

	/**
	 * Adds an orderlistener to this Orders listeners.
	 * 
	 * @param listener
	 *            the interested listener for this object
	 */
	public void addOrderListener(Listener<Order> listener) {
		orderListeners.add(listener);
	}

	/**
	 * Removes an orderlistener from this object.
	 * 
	 * @param listener
	 *            the uninterested listener
	 */
	public void removeOrderListener(Listener<Order> listener) {
		orderListeners.remove(listener);
	}

	/**
	 * Notify listeners on change.
	 */
	public void notifyOrderListeners() {
		lastTimeUpdate = System.currentTimeMillis();
		for (Listener<Order> listener : orderListeners) {
			listener.changed(this);
		}
	}

	@Override
	public void changed(Product product) {
		notifyOrderListeners();
	}

}
