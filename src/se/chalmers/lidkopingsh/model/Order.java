package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.util.Listener;
import se.chalmers.lidkopingsh.util.Syncable;
import se.chalmers.lidkopingsh.util.Syncher;

/**
 * A class representing an Order.
 * 
 * @author Kim Kling
 * @author Robin Gronberg
 * 
 */
public class Order implements Listener<OrderChangedEvent>, Syncable<Order> {
	private int id;
	private final long timeCreated;
	private long lastTimeUpdate;
	private long lastTimeSync;
	private String cemetery;
	private long orderDate;
	private String orderNumber;
	private String idName;
	private String cemeteryBoard;
	private String cemeteryBlock;
	private String cemeteryNumber;
	private Customer customer;
	private transient List<Listener<OrderChangedEvent>> orderListeners;
	private transient List<Listener<Order>> orderSyncedListeners;
	private List<Product> products;
	private List<Image> images;
	private String deceased;
	private boolean archived = false;
	private boolean cancelled = false;

	/**
	 * Creates an Order with the specified properties. For unknown or not
	 * existing properties, use null.
	 */
	public Order(int id, String orderNumber, String idName, long timeCreated,
			long lastTimeUpdated, String cemetery, String cemeteryBoard,
			String cemeteryBlock, String cemeteryNumber, long orderDate,
			Customer customer, List<Product> products, List<Image> images, String deceased) {
		this.id = id;
		this.orderNumber = orderNumber != null ? orderNumber : "";
		this.idName = idName != null ? idName : "";
		this.timeCreated = timeCreated;
		this.lastTimeUpdate = this.lastTimeSync = lastTimeUpdated;
		this.cemetery = cemetery != null ? cemetery : "";
		this.cemeteryBoard = cemeteryBoard != null ? cemeteryBoard : "";
		this.cemeteryBlock = cemeteryBlock != null ? cemeteryBlock : "";
		this.cemeteryNumber = cemeteryNumber != null ? cemeteryNumber : "";
		this.orderDate = orderDate;
		this.customer = customer.clone();
		this.images = images;
		this.deceased = deceased;
		orderListeners = new ArrayList<Listener<OrderChangedEvent>>();
		orderSyncedListeners = new ArrayList<Listener<Order>>();
		this.products = new ProductList(products);
		
		if (products != null) {
			for (Product p : products) {
				p.addProductListener(this);
			}
		}
	}

	/**
	 * Creates a new order with an order.
	 * 
	 * @param o the order to use values from.
	 */
	public Order(Order o) {
		this(o.id, o.orderNumber, o.idName, o.timeCreated, o.lastTimeUpdate,
				o.cemetery, o.cemeteryBoard, o.cemeteryBlock, o.cemeteryNumber,
				o.orderDate, o.customer, o.products, o.images, o.getDeceased());
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
		return cemeteryBlock;
	}

	public String getCemetaryNumber() {
		return cemeteryNumber;
	}

	public long getLastTimeUpdate() {
		return lastTimeUpdate;
	}

	public String getCemetary() {
		return cemetery;
	}

	public long getOrderDate() {
		return orderDate;
	}

	public List<Image> getImages() {
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
	
	public String getDeceased() {
		return deceased;
	}

	/**
	 * Get the number of {@link Station}'s left until station. Returns
	 * Integer.MAX_VALUE if Order doesn't have Product with a @ {@link Task}
	 * with station or if station has already passed.
	 * 
	 * @param station
	 *            The Station to check.
	 * @return The number of stations left until station.
	 */
	public int getNumOfStationsLeft(Station station) {
		int min = Integer.MAX_VALUE;
		for (Product p : getProducts()) {
			int stationsLeft = p.getNumOfStationsLeft(station);
			if (stationsLeft < min) {
				min = stationsLeft;
			}
		}
		return min;
	}

	/**
	 * Returns this order's progress in percentage of tasks done.
	 * 
	 * @return The percentage of tasks done.
	 * 
	 *         TODO: Save value as instance variable so loops is not required if
	 *         nothing is changed
	 */
	public int getProgress() {
		double taskCount = 0;
		double doneTaskCount = 0;
		for (Product product : getProducts()) {
			for (Task task : product.getTasks()) {
				taskCount++;
				if (task.getStatus() == Status.DONE) {
					doneTaskCount++;
				}
			}
		}
		if(taskCount > 0) {
			return (int) Math.round((doneTaskCount / taskCount) * 100);
		} 
		// If no tasks, consider the order as done
		else {
			return 100;
		}
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
		notifyOrderListeners(new OrderChangedEvent(this, product, null));
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
			notifyOrderListeners(new OrderChangedEvent(this, p, null));
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
	public void addOrderListener(Listener<OrderChangedEvent> listener) {
		if (orderListeners == null) {
			orderListeners = new ArrayList<Listener<OrderChangedEvent>>();
		}
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
	public void notifyOrderListeners (OrderChangedEvent event) {
		lastTimeUpdate += 1000;
		for (Listener<OrderChangedEvent> listener : orderListeners) {
			listener.changed(event);
		}
	}

	/**
	 * Notify syncedListeners when synced.
	 */
	public void notifySyncedListeners(Order order) {
		if (orderSyncedListeners != null) {
			for (Listener<Order> listener : orderSyncedListeners) {
				listener.changed(order);
			}
		}
	}

	@Override
	public void changed(OrderChangedEvent event) {
		notifyOrderListeners(new OrderChangedEvent(this, event.getProduct(), event.getTask()));
	}

	@Override
	public boolean sync(Order newData) {
		if (newData != null && this.id == newData.id
				&& getClass() == newData.getClass()) {
			// If this object is newer then newData, switch the sync
			if (this.lastTimeUpdate > newData.lastTimeUpdate) {
				return newData.sync(this);
			} else {
				this.cemetery = newData.cemetery;
				this.customer = newData.customer;
				this.lastTimeUpdate = newData.lastTimeUpdate;
				this.orderDate = newData.orderDate;
				this.orderNumber = newData.orderNumber;
				this.idName = newData.idName;
				if (newData.getImages() != null && getImages() != null) {
					images = Syncher.syncList(images, newData.getImages());
				}
				if (newData.getProducts() != null && products != null) {
					products = Syncher.syncList(products, newData.getProducts());
				}
				this.lastTimeSync = newData.lastTimeUpdate;
				notifySyncedListeners(this);
				return true;
			}
		} else {
			notifySyncedListeners(null);
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
					&& this.cemetery.equals(or.getCemetary())
					&& this.cemeteryBoard.equals(or.getCemeteryBoard())
					&& this.cemeteryBlock.equals(or.getCemetaryBlock())
					&& this.cemeteryNumber.equals(or.getCemetaryNumber())
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
	private class ProductList extends ArrayList<Product> {
		private static final long serialVersionUID = 2154927418889429341L;

		public ProductList(Collection<Product> collection) {
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
				((Product) object).removeEventListener(Order.this);
			}
			return super.remove(object);
		}
	}

	@Override
	public String toString() {
		return id + "";
	}
	
	public boolean isRemoved() {
		return cancelled || archived;
	}

}
