package se.chalmers.lidkopingsh.util;
/**
 * 
 * @author Olliver Mattsson
 * @author Robin Gronberg
 *
 */
public interface NetworkUpdateListener {
	/**
	 * This method is called before updating from the network
	 */
	public void startUpdate();
	/**
	 * This method is called after updating from the network
	 */
	public void endUpdate();
	/**
	 * This method is called when network for some reason is unreachable
	 * @param message Error message
	 */
	public void noNetwork(String message);
	
}
