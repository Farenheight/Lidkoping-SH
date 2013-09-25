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
	private String cemetaryBoard;
	private String graveyardNotation;
	private Customer customer;
	private List<Listener<Order>> orderListeners;
	private SyncableList<Product> products;

	/**
	 * For testing purposes only.
	 */
	public Order() {
		this(5, getNewOrderNumber(), "O.R.", System.currentTimeMillis(), System
				.currentTimeMillis(), "Kyrkogard", "Kyrkonamnd", "Notation",
				Long.parseLong("1371679200000"), new Customer("Mr",
						"Olle Bengtsson", "Testvagen 52", "416 72 Goteborg",
						"olle.bengtsson@testuser.com",
						(int) System.currentTimeMillis()));
	}

	public Order(int id, String orderNumber, String idName, long timeCreated,
			long lastTimeUpdated, String cemetary, String cemetaryBoard,
			String graveyardNotation, long orderDate, Customer customer) {
		this.id = id;
		this.orderNumber = orderNumber != null ? orderNumber : "";
		this.idName = idName != null ? idName : "";
		this.timeCreated = timeCreated;
		this.lastTimeUpdate = lastTimeUpdated;
		this.cementary = cementary != null ? cementary : "";
		this.cemetaryBoard = cemetaryBoard != null ? cemetaryBoard : "";
		this.graveyardNotation = graveyardNotation != null ? graveyardNotation
				: "";
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

	public String getCemetaryBoard() {
		return cemetaryBoard;
	}

	public String getGraveyardNotation() {
		return graveyardNotation;
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

	public void addProduct(Product product) {
		for (Product p : products) {
			if (product.getId() == p.getId()) {
				products.remove(p);
			}
		}
		products.add(product);
		notifyOrderListeners();
	}

	public void addProducts(Collection<Product> products) {
		for (Product p : products) {
			addProduct(p);
		}
	}

	/**
	 * remove Task from this product task list.
	 * 
	 * @param p
	 *            The {@link Task} to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public void removeProduct(Product p) {
		if (products.contains(p)) {
			notifyOrderListeners();
			products.remove(p);
		}
	}

	/**
	 * remove Tasks from this product task list.
	 * 
	 * @param tasks
	 *            The {@link Task}s to remove
	 * @return true if Tasks was modified. false otherwise.
	 */
	public void removeProduct(List<Product> productList) {
		for (Product p : productList) {
			removeProduct(p);
		}
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
		if (newData != null && this.id == newData.id
				&& getClass() == newData.getClass()) {
			// If this object is newer that newData, switch the sync
			if (this.lastTimeUpdate > newData.lastTimeUpdate) {
				return newData.sync(this);
			} else {
				this.cementary = newData.cementary;
				this.customer = newData.customer;
				this.lastTimeUpdate = newData.lastTimeUpdate;
				this.orderDate = newData.orderDate;
				this.orderNumber = newData.orderNumber;
				this.idName = newData.idName;
				products.sync(newData.getProducts());
				return true;
			}
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
			Order or = (Order) o;
			return this.id == or.id && this.timeCreated == or.getTimeCreated()
					&& this.lastTimeUpdate == or.getLastTimeUpdate()
					&& this.cementary.equals(or.getCementary())
					&& this.orderDate == or.getOrderDate()
					&& this.orderNumber.equals(or.getOrderNumber())
					&& this.idName.equals(or.getIdName())
					&& this.customer.equals(or.getCustomer())
					&& this.products.equals(or.getProducts());
		}
	}

	public static String getNewOrderNumber() {
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
			return super.add(object);
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
