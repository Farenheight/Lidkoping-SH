package se.chalmers.lidkopingsh.server;

import java.util.Collection;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.util.Listener;

/**
 * Handles the connection to the remote server.
 * 
 * @author Anton Jansson
 * 
 */
public interface IServerConnector {

	/**
	 * Add listener for retrieving network statuses.
	 * 
	 * @param listener
	 *            The network listener.
	 */
	void addNetworkListener(NetworkStatusListener listener);

	/**
	 * Add listener for retrieving when orders has been changed after a server
	 * update.
	 * 
	 * @param listener
	 *            The listener of orders
	 */
	void addOrderChangedListener(Listener<Collection<Order>> listener);

	/**
	 * Remove listener for retrieving network statuses.
	 * 
	 * @param listener
	 *            The network listener.
	 */
	void removeNetworkStatusListener(NetworkStatusListener listener);

	/**
	 * Remove listener for retrieving when orders has been changed after a
	 * server update.
	 * 
	 * @param listener
	 *            The order listener.
	 */
	void removeOrderChangedListener(Listener<Collection<Order>> listener);

	/**
	 * Creates an AsyncTask that will send an update and then update the local
	 * database
	 * 
	 * @param event
	 *            The event which holds what has changed
	 */
	void sendUpdate(OrderChangedEvent event);

	/**
	 * Updates the local database with data from server. Runs in a different
	 * thread because of AsyncTask.
	 * 
	 * @param getAll
	 *            True if everything should be fetched false otherwise
	 */
	void update(boolean getAll);
}