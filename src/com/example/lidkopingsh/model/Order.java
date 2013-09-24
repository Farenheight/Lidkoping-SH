package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
	private String idName;
	private Customer customer;
	private List<Listener<Order>> orderListeners;
	private SyncableList<Product> products;

	/**
	 * For testing purposes only.
	 */
	public Order() {
	this(5, getNewOrderNumber(), "O.R.", System.currentTimeMillis(),
				System.currentTimeMillis(), "",
				Long.parseLong("1371679200000"), new Customer("Mr",
						"Olle Bengtsson", "Testvagen 52", "416 72 Goteborg",
						"olle.bengtsson@testuser.com", (int) System.currentTimeMillis()));
	}

	public Order(int id, String orderNumber, String idName, long timeCreated,
			long lastTimeUpdated, String cementary, long orderDate,
			Customer customer) {
		this.id = id;
		this.orderNumber = orderNumber;
		this.idName = idName;
		this.timeCreated = timeCreated;
		this.lastTimeUpdate = lastTimeUpdated;
		this.cementary = cementary;
		this.orderDate = orderDate;
		this.customer = customer.clone();

		orderListeners = new ArrayList<Listener<Order>>();
		products = new SyncableProductList();
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
	
	public String getIdName() {
		return idName;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void addProduct(Product p) {
		products.add(p);
	}
	
	public void addProducts(Collection<Product> products) {
		this.products.addAll(products);
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
			products.sync(newData.getProducts());
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

	/**
	 * Inner class that makes sure that listeners are added and removed properly
	 * when an {@link Order} is synced
	 * 
	 * @author robin
	 * 
	 */
	private class SyncableProductList extends SyncableArrayList<Product> {
		private static final long serialVersionUID = 2154927418889429341L;

		public SyncableProductList() {
		}

		public SyncableProductList(Collection<Product> collection) {
			super(collection);
		}

		@Override
		public void add(int index, Product object) {
			object.addProductListener(Order.this);
			super.add(index, object);
		}
		@Override
		public boolean add(Product object) {
			object.addProductListener(Order.this);
			return super.add( object);
		}

		@Override
		public boolean remove(Object object) {
			if (object instanceof Product) {
				((Product) object).removeProductListener(Order.this);
			}
			return super.remove(object);
		}
	}

}
