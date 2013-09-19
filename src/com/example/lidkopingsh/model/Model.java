package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Model {
	private List<Order> orders;

	public Model() {
		orders = new ArrayList<Order>();
	}

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

	public Order getOrderByOrderId(int id) throws NoSuchElementException {
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
