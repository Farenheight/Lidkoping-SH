package se.chalmers.lidkopingsh.model;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import se.chalmers.lidkopingsh.util.Listener;

/**
 * An interface for a model
 * 
 * @author Kim Kling
 * 
 */

public interface IModel extends Listener<Collection<Order>> {

	/**
	 * Returns the order with the specified id.
	 * 
	 * @param id
	 *            The id of the order
	 * @return product with specified id
	 * @throws NoSuchElementException
	 *             when the id does not exist
	 */
	public Order getOrderById(int id) throws NoSuchElementException;

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
	public int getFirstUncompletedIndex(List<Order> sortedList, Station station);

	public void addOrder(Order o);

	public void removeOrder(Order o);

	public Collection<Order> getOrders();

	public Collection<Station> getStations();

	/**
	 * Get the station with the specified id.
	 * 
	 * @param id
	 *            The id of the station
	 * @return The station with the specified id
	 * @throws NoSuchElementException
	 *             If no order the specified id is found.
	 */
	public Station getStationById(int id);

	/**
	 * Add listeners for when data is synced from remote server.
	 * 
	 * @param listener
	 *            The listener to be added.
	 */
	void addDataSyncedListener(DataSyncedListener listener);

	/**
	 * Remove listener for when data is synced from remote server.
	 * 
	 * @param listener
	 *            The listener to be removed.
	 */
	void removeDataSyncedListener(DataSyncedListener listener);

	/**
	 * Add listeners for when order is changed.
	 * 
	 * @param listener
	 *            The listener to be added.
	 */
	void addOrderChangedListener(Listener<OrderChangedEvent> listener);

	/**
	 * Remove listener for when order is changed.
	 * 
	 * @param listener
	 *            The listener to be removed.
	 */
	void removeOrderChangedListener(Listener<OrderChangedEvent> listener);

	/**
	 * Sets the currently active stations in the model.
	 * 
	 * @param stations
	 */
	void setStations(Collection<Station> stations);

	/**
	 * Removes all orders from the model and notifies all listeners.
	 */
	void clearAllOrders();

}
