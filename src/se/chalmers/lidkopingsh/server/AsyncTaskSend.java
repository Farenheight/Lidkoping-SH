package se.chalmers.lidkopingsh.server;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.server.ServerHelper.ResponseSend;
import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

/**
 * Handles the sending of updates and the updating of the local database
 * afterwards. Is run in a new thread.
 * 
 * @author Olliver Mattsson
 * @author Alexander Härenstam
 */

class AsyncTaskSend extends AsyncTask<Void, Void, List<Order>> {
	private final ServerHelper serverHelper;
	private final OrderChangedEvent event;
	private final ServerConnector connector;
	private ResponseSend response;

	public AsyncTaskSend(OrderChangedEvent event, ServerHelper serverLayer,
			ServerConnector connector) {
		this.serverHelper = serverLayer;
		this.event = event;
		this.connector = connector;
	}

	protected void onPreExecute() {
		connector.startedUpdate();
	}

	@Override
	protected List<Order> doInBackground(Void... none) {
		List<Order> orders = null;
		try {
			response = serverHelper.sendUpdate(event.getOrder());
			orders = serverHelper.getUpdates(false);
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * Method is run automatically after the doInBackground method and updates
	 * database in GUI thread.
	 * 
	 * @param orders
	 *            The orders returned from the database
	 */
	protected void onPostExecute(List<Order> orders) {
		se.chalmers.lidkopingsh.model.Status status = event.getTask()
				.getStatus();
		if (orders != null) {
			connector.notifyDataChanged(orders);
		}
		if (response == null) {
			connector.notifyNetworkProblem("Kunde inte koppla upp sig mot servern");
		}
		if (response != null && !response.isSuccess()) {
			event.getTask().setStatus(status);
		}
		connector.finishedUpdate();
	}

}
