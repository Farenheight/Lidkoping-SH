package se.chalmers.lidkopingsh.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.lidkopingsh.model.DataChangedListener;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.util.Listener;
import android.content.Context;
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

	public ServerConnector(Context context, IModel model) {
		helper = new ServerHelper(context);
		networkListeners = new ArrayList<NetworkStatusListener>();
		orderListeners = new ArrayList<Listener<Collection<Order>>>();
		this.model = model;
		update(true);

		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTimerTask(), UPDATE_INTERVAL,
				UPDATE_INTERVAL);
	}

	public void addNetworkListener(NetworkStatusListener listener) {
		networkListeners.add(listener);
	}

	public void addOrderChangedListener(Listener<Collection<Order>> listener) {
		orderListeners.add(listener);
	}

	@Override
	public void changed(OrderChangedEvent event) {
		sendUpdate(event);
	}

	public void finishedUpdate() {
		for (NetworkStatusListener l : networkListeners) {
			l.finishedUpdate();
		}
	}

	public void notifyAuthenticationFailed() {
		for (NetworkStatusListener l : networkListeners) {
			l.authinicationFailed();
		}
	}

	public void notifyDataChanged(Collection<Order> orders) {
		for (Listener<Collection<Order>> l : orderListeners) {
			l.changed(orders);
		}
	}

	public void notifyNetworkProblem(String message) {
		for (NetworkStatusListener l : networkListeners) {
			l.networkProblem(message);
		}
	}

	public void removeNetworkListener(NetworkStatusListener listener) {
		networkListeners.remove(listener);
	}

	public void removeOrderChangedListener(DataChangedListener listener) {
		orderListeners.remove(listener);
	}

	/**
	 * Creates an asynctask that will send an update and then update the local
	 * database
	 * 
	 * @param event
	 *            The event which holds what has changed
	 */
	public void sendUpdate(OrderChangedEvent event) {
		Log.d("ServerConnection",
				"Sending update to server, via AsyncTaskSend. (updating data before send)");
		// Update order(s) before sending to server, otherwise
		// request can be dismissed.
		Status status = event.getTask().getStatus();
		update(false);
		if (!event.getOrder().isRemoved()) {
			event.getTask().setStatus(status);
			new AsyncTaskSend(event, helper, this, model.getOrders()).execute();
		}
	}

	public void startedUpdate() {
		for (NetworkStatusListener l : networkListeners) {
			l.startedUpdate();
		}
	}

	/**
	 * Updates the local database with data from server. Runs in a different
	 * thread because of asynctask.
	 * 
	 * @param getAll
	 *            True if everything should be fetched false otherwise
	 */
	public void update(boolean getAll) {
		Log.d("ServerConnection",
				"Getting update from server, via AsyncTaskGet.");
		new AsyncTaskGet(getAll, helper, this, model.getOrders()).execute();
	}

	private class UpdateTimerTask extends TimerTask {
		private Handler handler;

		public UpdateTimerTask() {
			handler = new Handler(); // TODO: Add comment for this
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
