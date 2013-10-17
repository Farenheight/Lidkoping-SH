package se.chalmers.lidkopingsh.server;

import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

/**
 * Handles the getting of updates and updating of the local database afterwards. Is run in a new thread.
 * 
 * @author Olliver Mattsson
 * @author Alexander Härenstam
 */

public class AsyncTaskGet extends AsyncTask<Void, Void, List<Order>> {
	private final boolean getAll;
	private final ServerConnector connector;
	private final ServerHelper helper;
	
	public AsyncTaskGet(boolean getAll, ServerHelper helper, ServerConnector connector) {
		this.getAll = getAll;
		this.connector = connector;
		this.helper = helper;
	}
	
	protected void onPreExecute() {
			connector.startedUpdate();
	}
	
	@Override
	protected List<Order> doInBackground(Void... voids) {
		try {
			return helper.getUpdates(getAll);
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Method is run automatically after the doInBackground method and updates database in GUI thread.
	 * 
	 * @param orders The orders returned from the database
	 */
	protected void onPostExecute(List<Order> orders) {
		if (orders != null) {
			connector.notifyDataChanged(orders);
		} else {
			connector.notifyNetworkProblem("Kunde inte koppla upp mot server");
		}
		connector.finishedUpdate();
	}
}
