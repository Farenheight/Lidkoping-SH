package se.chalmers.lidkopingsh.handler;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;

/**
 * Interface for a layer between remote Server and Local database.
 * 
 * @author Alexander H�renstam
 * @author Olliver Mattsson
 *
 */
public abstract class AbstractServerLayer {
	protected String serverPath;
	/**
	 * Retrieve updates from server.
	 */
	public abstract List<Order> getUpdates();
	
	/**
	 * Send updates to server.
	 */
	public abstract void sendUpdate(Order order);
	
	/**
	 * Get path to server.
	 * @return String serverPath
	 */
	public String getServerPath() {
		return serverPath;
	}
}
