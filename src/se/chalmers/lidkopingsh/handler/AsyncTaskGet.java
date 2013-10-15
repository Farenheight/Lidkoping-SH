
package se.chalmers.lidkopingsh.handler;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import android.os.AsyncTask;
import android.util.Log;

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
	
	protected void onPreExecute() {
		if (layer.getNetworkListener() != null) {
			layer.startUpdate();
		}
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
	protected void onPostExecute(List<Order> orders) {
		if (orders != null) {
			layer.updateDatabase(orders);
		}
		if (layer.getNetworkListener() != null) {
			layer.endUpdate();
		}
		if(orders == null) {
			layer.noNetwork("Kunde inte koppla upp mot server");
		}
	}
}
