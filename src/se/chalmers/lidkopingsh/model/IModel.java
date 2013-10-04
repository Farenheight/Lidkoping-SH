package se.chalmers.lidkopingsh.model;

import java.util.Collection;
import java.util.NoSuchElementException;

public interface IModel {

	/**
	 * Returns the product with the specified id.
	 * 
	 * @param id
	 *            int
	 * @return product with specified id
	 * @throws NoSuchElementException
	 *             when the id does not exist
	 */
	public abstract Product getProductById(int id)
			throws NoSuchElementException;

	/**
	 * Returns the order with the specified id.
	 * 
	 * @param id
	 *            int
	 * @return product with specified id
	 * @throws NoSuchElementException
	 *             when the id does not exist
	 */
	public abstract Order getOrderById(int id) throws NoSuchElementException;
	
	public abstract void addOrder(Order o);

	public abstract void removeOrder(Order o);

	public abstract Collection<Order> getOrders();
	
	public abstract Collection<Station> getStations();
}