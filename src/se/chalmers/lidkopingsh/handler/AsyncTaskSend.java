package se.chalmers.lidkopingsh.handler;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import android.os.AsyncTask;

/**
 * Handles the sending of updates and the updating of the local database afterwards. Is run in a new thread.
 * 
 * @author Olliver Mattsson
 * @author Alexander Härenstam
 */

public class AsyncTaskSend extends AsyncTask<Void, Void, List<Order>> {
	private final ServerLayer serverLayer;
	private final OrderChangedEvent event;
	private final OrderDbLayer layer;
	private boolean success;
	
	public AsyncTaskSend(OrderChangedEvent event, ServerLayer serverLayer, OrderDbLayer layer) {
		this.serverLayer = serverLayer;
		this.event = event;
		this.layer = layer;
	}
	
	protected void onPreExecute() {
		if (layer.getNetworkListener() != null) {
			layer.startUpdate();
		}
	}
	
	@Override
	protected List<Order> doInBackground(Void... none) {
		List<Order> orders = null;
		if (serverLayer.isServerAvailable()) {
			success = serverLayer.sendUpdate(event.getOrder());
			orders = serverLayer.getUpdates(false);
		}
		return orders;
	}
	
	/**
	 * Method is run automatically after the doInBackground method and updates database in GUI thread.
	 * 
	 * @param orders The orders returned from the database
	 */
	protected void onPostExecute(List<Order> orders) {
		se.chalmers.lidkopingsh.model.Status status = event.getTask().getStatus();
		if (orders == null) {
			layer.noNetwork();
		} else {
			layer.updateDatabase(orders);
		}
		if (!success && orders != null) {
			event.getTask().setStatus(status);
		}
		if (layer.getNetworkListener() != null) {
			layer.endUpdate();
		}
	}

}
