package se.chalmers.lidkopingsh.server;

import java.util.Collection;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.model.Order;
import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

/**
 * Handles the getting of updates and updating of the local database afterwards.
 * Is run in a new thread.
 * 
 * @author Olliver Mattsson
 * @author Alexander Härenstam
 */

class AsyncTaskGet extends AsyncTask<Void, Void, List<Order>> {
	private final boolean getAll;
	private final ServerConnector connector;
	private final ServerHelper helper;
	private final Collection<Order> currentOrders;
	private Exception exception;

	public AsyncTaskGet(boolean getAll, ServerHelper helper,
			ServerConnector connector, Collection<Order> currentOrders) {
		this.getAll = getAll;
		this.connector = connector;
		this.helper = helper;
		this.currentOrders = currentOrders;
	}

	@Override
	protected void onPreExecute() {
		connector.notifyStartedUpdate();
	}

	@Override
	protected List<Order> doInBackground(Void... voids) {
		try {
			return helper.getUpdates(getAll, currentOrders);
		} catch (Exception e) {
			exception = e;
		}
		return null;
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
				throw new IllegalStateException(
						"Unhandled exception in ServerHelper.", exception);
			}
		} else if (orders != null) {
			connector.notifyDataChanged(orders);
		}
		connector.notifyFinishedUpdate();
	}
}