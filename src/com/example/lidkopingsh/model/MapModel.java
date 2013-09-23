package com.example.lidkopingsh.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapModel implements IModel {
	private Map<Integer, Order> orders;
	private Map<Integer, Product> products;

	public MapModel() {
		this(new HashMap<Integer, Order>(), new HashMap<Integer, Product>());
	}

	public MapModel(Map<Integer, Order> o, Map<Integer, Product> p) {
		products = new HashMap<Integer, Product>(p);
		orders = new HashMap<Integer, Order>(o);
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
}
