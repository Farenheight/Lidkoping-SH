package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A class representing an Order.
 * 
 * @author Kim
 * 
 */
public class Order implements Listener<Product>, Syncable<Order> {
	private static int currentOrderNumberCount = 0;
	private int id;
	private final long timeCreated;
	private long lastTimeUpdate;
	private String cementary;
	private long orderDate;
	private String orderNumber;
	private Customer customer;
	private List<Listener<Order>> orderListeners;
	private List<Product> products;

	/**
	 * For testing purposes only.
	 */
	public Order() {
		this(5, getNewOrderNumber(), System.currentTimeMillis(),
				System.currentTimeMillis(), "",
				Long.parseLong("1371679200000"), new Customer("Mr",
						"Olle Bengtsson", "Testvagen 52", "416 72 Goteborg",
						"olle.bengtsson@testuser.com", (int)System.currentTimeMillis()));
	}

	public Order(int id, String orderNumber, long timeCreated,
			long lastTimeUpdated, String cementary, long orderDate,
			Customer customer) {
		this.id = id;
		this.orderNumber = orderNumber;
		this.timeCreated = timeCreated;
		this.lastTimeUpdate = lastTimeUpdated;
		this.cementary = cementary;
		this.orderDate = orderDate;
		this.customer = customer.clone();
		
		orderListeners = new ArrayList<Listener<Order>>();
		products = new ArrayList<Product>();
	}

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

	public void addProduct(Product p) {
		products.add(p);
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

	@Override
	public boolean sync(Order newData) {
		if (newData != null && this.id == newData.id) {
			this.cementary = newData.cementary;
			this.customer = newData.customer;
			this.lastTimeUpdate = newData.lastTimeUpdate;
			this.orderDate = newData.orderDate;
			this.orderNumber = newData.orderNumber;
			// TODO Sync Products
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
			return this.id == ((Order) o).id;
		}
	}

	private static String getNewOrderNumber() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int yearPart = year % 2000;

		String numPart = "";
		currentOrderNumberCount++;
		if (currentOrderNumberCount < 10) {
			numPart = "000" + currentOrderNumberCount;
		} else if (currentOrderNumberCount < 100) {
			numPart = "00" + currentOrderNumberCount;
		} else if (currentOrderNumberCount < 1000) {
			numPart = "0" + currentOrderNumberCount;
		} else {
			numPart = "" + currentOrderNumberCount;
		}

		return "" + yearPart + numPart;
	}

}
