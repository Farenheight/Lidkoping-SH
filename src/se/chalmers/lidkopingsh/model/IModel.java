package se.chalmers.lidkopingsh.model;

import java.util.Collection;
import java.util.List;
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
	public Product getProductById(int id) throws NoSuchElementException;

	/**
	 * Returns the order with the specified id.
	 * 
	 * @param id
	 *            int
	 * @return product with specified id
	 * @throws NoSuchElementException
	 *             when the id does not exist
	 */
	public Order getOrderById(int id) throws NoSuchElementException;

	public int getFirstUncompletedIndex(List<Order> sortedList, Station station);

	public void addOrder(Order o);
	
	public void removeOrder(Order o);

	public Collection<Order> getOrders();

	public Collection<Station> getStations();
}