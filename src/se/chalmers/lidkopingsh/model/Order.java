package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.util.Listener;
import se.chalmers.lidkopingsh.util.Syncable;
import se.chalmers.lidkopingsh.util.SyncableArrayList;
import se.chalmers.lidkopingsh.util.SyncableList;

/**
 * A class representing an Order.
 * 
 * @author Kim
 * @author Robin Gronberg
 * 
 */
public class Order implements Listener<Product>, Syncable<Order> {
	private int id;
	private final long timeCreated;
	private long lastTimeUpdate;
	private long lastTimeSync;
	private String cemetary;
	private long orderDate;
	private String orderNumber;
	private String idName;
	private String cemeteryBoard;
	private String cemetaryBlock;
	private String cemetaryNumber;
	private Customer customer;
	private List<Listener<Order>> orderListeners;
	private List<Listener<Order>> orderSyncedListeners;
	private SyncableList<Product> products;
	private Collection<Image> images;

	/**
	 * Creates an Order with the specified properties. For unknown or not
	 * existing properties, use null.
	 */
	public Order(int id, String orderNumber, String idName, long timeCreated,
			long lastTimeUpdated, String cemetary, String cemetaryBoard,
			String cemetaryBlock, String cemetaryNumber, long orderDate,
			Customer customer, Collection<Product> products,
			Collection<Image> images) {
		this.id = id;
		this.orderNumber = orderNumber != null ? orderNumber : "";
		this.idName = idName != null ? idName : "";
		this.timeCreated = timeCreated;
		this.lastTimeUpdate = lastTimeUpdated;
		this.cemetary = cemetary != null ? cemetary : "";
		this.cemeteryBoard = cemetaryBoard != null ? cemetaryBoard : "";
		this.cemetaryBlock = cemetaryBlock != null ? cemetaryBlock : "";
		this.cemetaryNumber = cemetaryNumber != null ? cemetaryNumber : "";
		this.orderDate = orderDate;
		this.customer = customer.clone();
		this.images = images;

		orderListeners = new ArrayList<Listener<Order>>();
		orderSyncedListeners = new ArrayList<Listener<Order>>();
		this.products = new SyncableProductList(products);
		if (products != null) {
			for (Product p : products) {
				p.addProductListener(this);
			}
		}
	}

	public int getId() {
		return id;
	}

	public long getTimeCreated() {
		return timeCreated;
	}

	public String getCemeteryBoard() {
		return cemeteryBoard;
	}

	public String getCemetaryBlock() {
		return cemetaryBlock;
	}

	public String getCemetaryNumber() {
		return cemetaryNumber;
	}

	public long getLastTimeUpdate() {
		return lastTimeUpdate;
	}

	public String getCemetary() {
		return cemetary;
	}

	public long getOrderDate() {
		return orderDate;
	}

	public Collection<Image> getImages() {
		return images;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public Customer getCustomer() {
		return customer.clone();
	}

	public String getIdName() {
		return idName;
	}

	public List<Product> getProducts() {
		return products;
	}

	/**
	 * Get whenever this Order is synced with the database or not. If an order
	 * is synced. This Order should have the same data as the remote server.
	 * 
	 * @return true if synced, false otherwise
	 */
	public boolean isSynced() {
		return lastTimeSync == lastTimeUpdate;
	}

	/**
	 * Add a new product to this Order. This will notify all {@link Listener}s
	 * that something has changed on this Project. If a product with the same id
	 * is already in the list, that product is removed before this product is
	 * added.
	 * 
	 * @param product
	 *            The product to add into this {@link Order}
	 */
	public void addProduct(Product product) {
		for (Product p : products) {
			if (product.getId() == p.getId()) {
				products.remove(p);
			}
		}
		products.add(product);
		notifyOrderListeners();
	}

	/**
	 * Adds a list of {@link Product}s to this Order. If any of the products
	 * already exist in this order with the same id, that product is removed
	 * before the new product is added.
	 * 
	 * @param products
	 *            A list of {@link Product}s to add to this Order
	 */
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
	 * Add a {@link Listener} that gets notified when this Order is synced
	 * 
	 * @param listener
	 */
	public void addSyncOrderListener(Listener<Order> listener) {
		orderSyncedListeners.add(listener);
	}

	/**
	 * Removes a {@link Listener} that should not get notified anymore when this
	 * Order is synced.
	 * 
	 * @param listener
	 */
	public void removeSyncOrderListener(Listener<Order> listener) {
		orderSyncedListeners.remove(listener);
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

	/**
	 * Notify syncedListeners when synced.
	 */
	public void notifySyncedListeners() {
		for (Listener<Order> listener : orderSyncedListeners) {
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
			// If this object is newer then newData, switch the sync
			if (this.lastTimeUpdate > newData.lastTimeUpdate) {
				return newData.sync(this);
			} else {
				this.cemetary = newData.cemetary;
				this.customer = newData.customer;
				this.lastTimeUpdate = newData.lastTimeUpdate;
				this.orderDate = newData.orderDate;
				this.orderNumber = newData.orderNumber;
				this.idName = newData.idName;
				products.sync(newData.getProducts());
				this.lastTimeSync = newData.lastTimeUpdate;
				notifySyncedListeners();
				return true;
			}
		} else {
			//TODO: Notify GUI that connection failed.
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
					&& this.cemetary.equals(or.getCemetary())
					&& this.cemeteryBoard.equals(or.getCemeteryBoard())
					&& this.cemetaryBlock.equals(or.getCemetaryBlock())
					&& this.cemetaryNumber.equals(or.getCemetaryNumber())
					&& this.orderDate == or.getOrderDate()
					&& this.orderNumber.equals(or.getOrderNumber())
					&& this.idName.equals(or.getIdName())
					&& this.customer.equals(or.getCustomer())
					&& this.products.equals(or.getProducts());
		}
	}

	/**
	 * Loops through the product list to find a stone
	 * 
	 * @return The first found stone in product list
	 */
	public Stone getStone() {
		for (Product product : getProducts()) {
			if (product instanceof Stone) {
				return (Stone) product;
			}
		}
		return null;
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
