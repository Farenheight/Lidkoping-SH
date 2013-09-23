package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class Model {
	private List<Order> orders;

	public Model() {
		orders = new ArrayList<Order>();
	}

	public Model(ArrayList<Order> o){
		orders = new ArrayList<Order>(o);
	}
	
	/**
	 * Returns the product with the specified id.
	 * @param id int
	 * @return product with specified id
	 * @throws NoSuchElementException when the id does not exist
	 */
	public Product getProductById(int id) throws NoSuchElementException {
		for (Order o : orders) {
			for (Product p : o.getProducts()) {
				if (p.getId() == id) {
					return p;
				}
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * Returns the order with the specified id.
	 * @param id int
	 * @return product with specified id
	 * @throws NoSuchElementException when the id does not exist
	 */
	public Order getOrderById(int id) throws NoSuchElementException {
		for (Order o : orders) {
			if (o.getId() == id) {
				return o;
			}
		}
		throw new NoSuchElementException();
	}

	public void addOrder(Order o) {
		orders.add(o);
	}
	
	public void addOrders(Collection<Order> orders) {
		this.orders.addAll(orders);
	}

	public void removeOrder(Order o) {
		orders.remove(o);
	}

	public void replaceOrders(ArrayList<Order> list) {
		orders = list;
	}

	public List<Order> getOrders() {
		return orders;
	}
}
