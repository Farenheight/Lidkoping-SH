package se.chalmers.lidkopingsh.handler;

import java.util.List;

import se.chalmers.lidkopingsh.handler.ServerLayer.ResponseSend;
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
	 * Tries to authenticate and retrieve an API key for this device.
	 * 
	 * @param username
	 * @param password
	 * @param deviceId
	 *            Unique device id.
	 * @return Response with success status, error code and message. API key is
	 *         returned in message if it exists.
	 */
	public abstract ResponseSend getApikey(String username, String password,
			String deviceId);

	/**
	 * Retrieve updates from server.
	 */
	public abstract List<Order> getUpdates(boolean getAll);

	/**
	 * Send updates to server.
	 */
	public abstract boolean sendUpdate(Order order);

	/**
	 * Get path to server.
	 * 
	 * @return String serverPath
	 */
	public String getServerPath() {
		return serverPath;
	}
}
