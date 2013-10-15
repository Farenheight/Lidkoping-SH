package se.chalmers.lidkopingsh.handler;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import android.os.AsyncTask;

/**
 * Handles the getting of updates and updating of the local database afterwards. Is run in a new thread.
 * 
 * @author Olliver Mattsson
 * @author Alexander Härenstam
 */

public class AsyncTaskGet extends AsyncTask<ServerLayer, Void, List<Order>> {
	private final boolean getAll;
	private final OrderDbLayer layer;
	
	public AsyncTaskGet(boolean getAll, OrderDbLayer layer) {
		this.getAll = getAll;
		this.layer = layer;
	}
	
	@Override
	protected List<Order> doInBackground(ServerLayer... serverLayer) {
		return serverLayer[0].getUpdates(getAll);
	}
	
	/**
	 * Method is run automatically after the doInBackground method and updates database in GUI thread.
	 * 
	 * @param orders The orders returned from the database
	 */
	public void onPostExecute(List<Order> orders) {
		layer.updateDatabase(orders);
	}
}
