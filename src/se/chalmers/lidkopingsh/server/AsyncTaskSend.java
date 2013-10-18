package se.chalmers.lidkopingsh.server;

import java.util.Collection;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.server.ServerHelper.ApiResponse;
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
	private final Collection<Order> currentOrders;
	private ApiResponse response;
	private se.chalmers.lidkopingsh.model.Status status;
	private Exception exception;

	public AsyncTaskSend(OrderChangedEvent event, ServerHelper serverLayer,
			ServerConnector connector, Collection<Order> currentOrders) {
		this.serverHelper = serverLayer;
		this.event = event;
		this.connector = connector;
		this.currentOrders = currentOrders;
	}

	@Override
	protected void onPreExecute() {
		connector.notifyStartedUpdate();
	}

	@Override
	protected List<Order> doInBackground(Void... none) {
		List<Order> orders = null;
		try {
			status = event.getTask().getStatus();
			response = serverHelper.sendUpdate(event.getOrder());
			orders = serverHelper.getUpdates(false, currentOrders);
		} catch (Exception e) {
			exception = e;
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
	@Override
	protected void onPostExecute(List<Order> orders) {
		if (exception != null) {
			if (exception instanceof NetworkErrorException) {
				connector
						.notifyNetworkProblem("Kunde inte koppla upp sig mot servern");
			} else if (exception instanceof AuthenticationException) {
				connector.notifyAuthenticationFailed();
			} else {
				throw new IllegalStateException(exception);
			}
		} else if (orders != null) {
			connector.notifyDataChanged(orders);
			if (response != null && !response.isSuccess()
					&& !event.getOrder().isRemoved()) {
				// Indirectly calls this Asynctask once more, trying to send
				// update again.
				event.getTask().setStatus(status);
			}
		}
		connector.notifyFinishedUpdate();
	}
}
