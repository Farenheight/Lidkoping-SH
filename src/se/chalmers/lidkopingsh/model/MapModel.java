package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import se.chalmers.lidkopingsh.util.Listener;
import android.util.Log;

public class MapModel implements IModel {
	private Map<Integer, Order> orders;
	private Map<Integer, Product> products;
	private Collection<Station> stations;
	private Collection<DataChangedListener> dataChangedListeners;
	private Collection<Listener<OrderChangedEvent>> orderChangedListeners;

	public MapModel(Collection<Order> o, Collection<Station> s) {
		this.products = new HashMap<Integer, Product>();
		this.orders = new HashMap<Integer, Order>();
		this.dataChangedListeners = new ArrayList<DataChangedListener>();
		this.orderChangedListeners = new ArrayList<Listener<OrderChangedEvent>>();

		for (Order or : o) {
			this.orders.put(or.getId(), or);
			for (Product p : or.getProducts()) {
				this.products.put(p.getId(), p);
			}
			or.addOrderListener(orderChangedListener);
		}
		this.stations = new ArrayList<Station>(s);
	}

	@Override
	public void addDataChangedListener(DataChangedListener listener) {
		dataChangedListeners.add(listener);
	}

	@Override
	public void removeDataChangedListener(DataChangedListener listener) {
		dataChangedListeners.remove(listener);
	}

	@Override
	public void addOrderChangedListener(Listener<OrderChangedEvent> listener) {
		orderChangedListeners.add(listener);
	}

	@Override
	public void removeOrderChangedListener(Listener<OrderChangedEvent> listener) {
		orderChangedListeners.remove(listener);
	}

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

	public MapModel(Collection<Order> o) {
		this(o, new ArrayList<Station>());
	}

	@Override
	public Product getProductById(int id) throws NoSuchElementException {
		Product p = products.get(id);
		if (p == null) {
			throw new NoSuchElementException();
		} else {
			return p;
		}
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
		if (o.getProducts() != null) {
			for (Product p : o.getProducts()) {
				products.put(p.getId(), p);
			}
		}

		// TODO When adding orders, check if the order have tasks that
		// does not exist in any other order.
	}

	@Override
	public void removeOrder(Order o) {
		orders.remove(o.getId());
		for (Product p : orders.get(o.getId()).getProducts()) {
			products.remove(p.getId());
		}

		// TODO When removing orders, check if the order have tasks that
		// does not exist in any other order.
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

	private void sync(Collection<Order> orders) {
		Log.d("Model", "Syncing model");
		Collection<Order> added = new ArrayList<Order>();
		Collection<Order> changed = new ArrayList<Order>();
		Collection<Order> removed = new ArrayList<Order>();

		for (Order o : orders) {
			if (o.isRemoved()) {
				removeOrder(o);
				removed.add(o);
			} else {
				try {
					Order order = getOrderById(o.getId());
					if (order != null) {
						order.sync(o);
						changed.add(o);
					}
				} catch (NoSuchElementException e) {
					o.addOrderListener(orderChangedListener);
					addOrder(o);
					added.add(o);
				}
			}
		}

		for (DataChangedListener l : dataChangedListeners) {
			l.ordersChanged(added, changed, removed,this);
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
