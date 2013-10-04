package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapModel implements IModel {
	private Map<Integer, Order> orders;
	private Map<Integer, Product> products;
	private Collection<Station> stations;

	public MapModel(Collection<Order> o, Collection<Station> s) {
		this.products = new HashMap<Integer, Product>();
		this.orders = new HashMap<Integer, Order>();

		for (Order or : o) {
			this.orders.put(or.getId(), or);
			for (Product p : or.getProducts()) {
				this.products.put(p.getId(), p);
			}
		}
		this.stations = new ArrayList<Station>(s);

	}

	/**
	 * Loops through the sorted list provided and returns the first occurrence
	 * of a order that has already been to the provided station.
	 * 
	 * @param sortedList
	 *            The sorted list should be ordered after the most relevant
	 *            station first.
	 * @param station
	 *            The station to
	 * @return The index of the first order that has not passed the provided
	 *         station
	 */
	public int getFirstUncompletedIndex(List<Order> sortedList, Station station) {
		// TODO: implement
		return 5;
	}

	public MapModel(Collection<Order> o) {
		this(o, new ArrayList<Station>());
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
	public Collection<Station> getStations() {
		return new ArrayList<Station>(stations);
	}
}
