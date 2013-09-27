package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class Model implements IModel {
	private List<Order> orders;

	public Model() {
		orders = new ArrayList<Order>();
	}

	public Model(ArrayList<Order> o) {
		orders = new ArrayList<Order>(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getProductById(int)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getOrderById(int)
	 */
	@Override
	public Order getOrderById(int id) throws NoSuchElementException {
		for (Order o : orders) {
			if (o.getId() == id) {
				return o;
			}
		}
		throw new NoSuchElementException();
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
		orders.add(o);
	}
	
	@Deprecated
	public void addOrders(Collection<Order> orders) {
		this.orders.addAll(orders);
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
		orders.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.lidkopingsh.model.IModel#replaceOrders(java.util.ArrayList)
	 */
	public void replaceOrders(ArrayList<Order> list) {
		orders = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.lidkopingsh.model.IModel#getOrders()
	 */
	@Override
	public List<Order> getOrders() {
		return orders;
	}

	@Override
	public Collection<Station> getStations() {
		// TODO Auto-generated method stub
		return null;
	}

}
