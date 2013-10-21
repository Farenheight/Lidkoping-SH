package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import se.chalmers.lidkopingsh.util.Listener;
import android.annotation.SuppressLint;
import android.util.Log;

/**
 * A mapmodel that holds all orders and stations
 * 
 * @author Kim Kling
 * 
 */

public class MapModel implements IModel {
	private Map<Integer, Order> orders;
	private Collection<Station> stations;

	/**
	 * Listeners for when model is synced with orders from network.
	 */
	private Collection<DataSyncedListener> dataSyncedListeners;

	/**
	 * Listeners for when something is changed in the orders.
	 */
	private Collection<Listener<OrderChangedEvent>> orderChangedListeners;

	/**
	 * Creates a new mapmodel with the specified orders and stations
	 * 
	 * @param o
	 *            The collection of orders to be held by the model
	 * @param s
	 *            The collection of stations to be held by the model
	 */
	// Model is independent from the Android SDK therefore not using
	// SparseArrays
	@SuppressLint("UseSparseArrays")
	public MapModel(Collection<Order> o, Collection<Station> s) {
		this.orders = new HashMap<Integer, Order>();
		this.dataSyncedListeners = new ArrayList<DataSyncedListener>();
		this.orderChangedListeners = new ArrayList<Listener<OrderChangedEvent>>();

		for (Order or : o) {
			this.orders.put(or.getId(), or);
			or.addOrderListener(orderChangedListener);
		}
		this.stations = new ArrayList<Station>(s);
	}

	public MapModel(Collection<Order> o) {
		this(o, new ArrayList<Station>());
	}

	@Override
	public void addDataSyncedListener(DataSyncedListener listener) {
		dataSyncedListeners.add(listener);
	}

	@Override
	public void removeDataSyncedListener(DataSyncedListener listener) {
		dataSyncedListeners.remove(listener);
	}

	@Override
	public void addOrderChangedListener(Listener<OrderChangedEvent> listener) {
		orderChangedListeners.add(listener);
	}

	@Override
	public void removeOrderChangedListener(Listener<OrderChangedEvent> listener) {
		orderChangedListeners.remove(listener);
	}

	/**
	 * Returns the index of the first order that has an uncompleted task at
	 * station
	 */
	public int getFirstUncompletedIndex(List<Order> sortedList, Station station) {
		int i = 0;
		for (Order o : sortedList) {
			if (o.getNumOfStationsLeft(station) == Integer.MAX_VALUE) {
				return i;
			}
			i++;
		}
		return i;
	}

	@Override
	public Order getOrderById(int id) throws NoSuchElementException {
		Order o = orders.get(id);
		if (o == null) {
			throw new NoSuchElementException();
		} else {
			return o;
		}
	}

	@Override
	public void addOrder(Order o) {
		if (orders.get(o.getId()) != null) {
			removeOrder(o);
		}
		orders.put(o.getId(), o);
	}

	@Override
	public void removeOrder(Order o) {
		Order order = orders.get(o.getId());
		if (order != null) {
			o.removeOrderListener(orderChangedListener);
		}
		orders.remove(o.getId());

	}

	@Override
	public void clearAllOrders() {
		sync(new ArrayList<Order>());
	}

	@Override
	public Collection<Order> getOrders() {
		return orders.values();
	}

	@Override
	public Collection<Station> getStations() {
		return new ArrayList<Station>(stations);
	}

	@Override
	public Station getStationById(int id) {
		for (Station station : stations) {
			if (station.getId() == id) {
				return station;
			}
		}
		throw new NoSuchElementException("No station with the id: " + id
				+ "is found");
	}

	/**
	 * Syncs the list of orders to the orders the model holds itself
	 * 
	 * @param orders
	 *            The list of orders to be synced with this model
	 */
	private void sync(Collection<Order> orders) {
		Log.d("Model", "Syncing model");
		Collection<Order> added = new ArrayList<Order>();
		Collection<Order> changed = new ArrayList<Order>();
		Collection<Order> removed = new ArrayList<Order>();

		for (Order o : orders) {
			if (o.isRemoved()) {
				Order order = getOrderById(o.getId());
				removeOrder(order);
				removed.add(order);
			} else {
				try {
					Order order = getOrderById(o.getId());
					if (order != null) {
						Order newOrder = new Order(o);
						order.sync(newOrder);
						changed.add(newOrder);
					}
				} catch (NoSuchElementException e) {
					Order order = new Order(o);
					order.addOrderListener(orderChangedListener);
					addOrder(order);
					added.add(order);
				}
			}
		}

		for (DataSyncedListener l : dataSyncedListeners) {
			l.ordersChanged(added, changed, removed, this);
		}
	}

	@Override
	public void changed(Collection<Order> orders) {
		sync(orders);
	}

	private Listener<OrderChangedEvent> orderChangedListener = new Listener<OrderChangedEvent>() {

		@Override
		public void changed(OrderChangedEvent event) {
			Log.d("MapModel", "Order has been changed (OrderChangedEvent).");
			for (Listener<OrderChangedEvent> l : orderChangedListeners) {
				l.changed(event);
			}
		}
	};

	@Override
	public void setStations(Collection<Station> stations) {
		this.stations.clear();
		this.stations.addAll(stations);
	}
}
