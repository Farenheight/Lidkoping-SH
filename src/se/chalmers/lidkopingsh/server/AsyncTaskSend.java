package se.chalmers.lidkopingsh.server;

import java.util.List;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
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
	private Exception exception;

	public AsyncTaskSend(OrderChangedEvent event, ServerHelper serverLayer,
			ServerConnector connector) {
		this.serverHelper = serverLayer;
		this.event = event;
		this.connector = connector;
	}

	@Override
	protected void onPreExecute() {
		connector.startedUpdate();
	}

	@Override
	protected List<Order> doInBackground(Void... none) {
		List<Order> orders = null;
		try {
			serverHelper.sendUpdate(event.getOrder());
			orders = serverHelper.getUpdates(false);
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
				connector.notifyNetworkProblem("Kunde inte koppla upp sig mot servern");
			} else if (exception instanceof AuthenticationException) {
				connector.notifyAuthenticationFailed();
			} else {
				throw new IllegalStateException(exception);
			}
		} else if (orders != null) {
			connector.notifyDataChanged(orders);
		}
		connector.finishedUpdate();
	}
}
