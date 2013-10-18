package se.chalmers.lidkopingsh.server;

/**
 * Listener for when network status changes, i.e. network problem,
 * authentication failed and when updates starts and finishes.
 * 
 * @author Olliver Mattsson
 * @author Robin Gronberg
 * 
 */
public interface NetworkStatusListener {
	/**
	 * This method is called before updating from the network
	 */
	public void startedUpdate();

	/**
	 * This method is called after updating from the network
	 */
	public void finishedUpdate();

	/**
	 * This method is called when network for some reason is unreachable
	 * 
	 * @param message
	 *            Error message
	 */
	public void networkProblem(String message);

	/**
	 * Notifies about API key being invalid
	 */
	public void authenticationFailed();

}
