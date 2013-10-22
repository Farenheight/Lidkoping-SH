package se.chalmers.lidkopingsh.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.util.Listener;
import android.os.Handler;
import android.util.Log;

/**
 * Handles the connection to the remote server.
 * 
 * @author Anton Jansson
 * @author Simon Bengtsson
 * 
 */
public class ServerConnector implements Listener<OrderChangedEvent> {
	/** Time in milliseconds, update from server interval. */
	private final long UPDATE_INTERVAL = 100000;

	private final ServerHelper helper;
	private final Timer timer;
	private final IModel model;

	/** Listeners for network statuses, such as network problems etc. */
	private List<NetworkStatusListener> networkListeners;
	/** Listeners for when orders has been changed after a server update. */
	private List<Listener<Collection<Order>>> orderListeners;

	/**
	 * Creates a ServerConnector for handling server connection.
	 * 
	 * @param context
	 *            The context for accessing local storage.
	 * @param model
	 *            The model for retrieving updates.
	 */
	public ServerConnector(IModel model) {
		helper = new ServerHelper();
		networkListeners = new ArrayList<NetworkStatusListener>();
		orderListeners = new ArrayList<Listener<Collection<Order>>>();
		this.model = model;
		update(true);

		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTimerTask(), UPDATE_INTERVAL,
				UPDATE_INTERVAL);
	}

	/**
	 * Add listener for retrieving network statuses.
	 * 
	 * @param listener
	 *            The network listener.
	 */
	public void addNetworkListener(NetworkStatusListener listener) {
		networkListeners.add(listener);
	}

	/**
	 * Add listener for retrieving when orders has been changed after a server
	 * update.
	 * 
	 * @param listener
	 *            The listener of orders
	 */
	public void addOrderChangedListener(Listener<Collection<Order>> listener) {
		orderListeners.add(listener);
	}

	@Override
	public void changed(OrderChangedEvent event) {
		sendUpdate(event);
	}

	/**
	 * Notifies all network listeners that authentication failed.
	 */
	public void notifyAuthenticationFailed() {
		for (NetworkStatusListener l : networkListeners) {
			l.authenticationFailed();
		}
	}

	/**
	 * Notifies all order changed listeners that data changed.
	 */
	public void notifyDataChanged(Collection<Order> orders) {
		for (Listener<Collection<Order>> l : orderListeners) {
			l.changed(orders);
		}
	}

	/**
	 * Notifies all network listeners that update is finished.
	 */
	public void notifyFinishedUpdate() {
		for (NetworkStatusListener l : networkListeners) {
			l.finishedUpdate();
		}
	}

	/**
	 * Notifies all network listeners that a network problem occurred.
	 */
	public void notifyNetworkProblem(String message) {
		for (NetworkStatusListener l : networkListeners) {
			l.networkProblem(message);
		}
	}

	/**
	 * Notifies all network listeners that update is started.
	 */
	public void notifyStartedUpdate() {
		for (NetworkStatusListener l : networkListeners) {
			l.startedUpdate();
		}
	}

	/**
	 * Remove listener for retrieving network statuses.
	 * 
	 * @param listener
	 *            The network listener.
	 */
	public void removeNetworkStatusListener(NetworkStatusListener listener) {
		networkListeners.remove(listener);
	}

	/**
	 * Remove listener for retrieving when orders has been changed after a
	 * server update.
	 * 
	 * @param listener
	 *            The order listener.
	 */
	public void removeOrderChangedListener(Listener<Collection<Order>> listener) {
		orderListeners.remove(listener);
	}

	/**
	 * Creates an AsyncTask that will send an update and then update the local
	 * database
	 * 
	 * @param event
	 *            The event which holds what has changed
	 */
	public void sendUpdate(OrderChangedEvent event) {
		Log.d("ServerConnection",
				"Sending update to server, via AsyncTaskSend. (updating data before send)");
		new AsyncTaskSend(event, helper, this, model.getOrders()).execute();
	}

	/**
	 * Updates the local database with data from server. Runs in a different
	 * thread because of AsyncTask.
	 * 
	 * @param getAll
	 *            True if everything should be fetched false otherwise
	 */
	public void update(boolean getAll) {
		Log.d("ServerConnection",
				"Getting update from server, via AsyncTaskGet.");
		new AsyncTaskGet(getAll, helper, this, model.getOrders()).execute();
	}

	/**
	 * A TimerTask that updates from server in a specified interval.
	 */
	private class UpdateTimerTask extends TimerTask {
		private Handler handler;

		public UpdateTimerTask() {
			handler = new Handler();
		}

		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					update(false); // Update orders from
									// server
				}
			});
		}
	}
}
