package se.chalmers.lidkopingsh.database;

import java.util.Collection;

import se.chalmers.lidkopingsh.model.Order;

/**
 * Handling communication between remote server and local database.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 *
 */
public class ServerLayer extends AbstractServerLayer {
	/**
	 * Creates a new ServerLayer with a set server path.
	 * @param serverPath
	 */
	public ServerLayer(String serverPath){
		this.serverPath = serverPath;
	}

	@Override
	public void getUpdates() {
		
		
	}

	@Override
	public void sendUpdate(Order order) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Update the local database.
	 */
	private void updateDatabase() {
		// TODO Auto-generated method stub
		
	}
	
}
