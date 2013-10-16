package se.chalmers.lidkopingsh.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.util.NetworkUpdateListener;
import android.content.Context;
import android.util.Log;

/**
 * Handles communication between Model and Order database.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbLayer implements ILayer {

	private final Timer timer;
	private final OrderDbStorage db;
	private final ServerLayer serverLayer;
	private final long UPDATE_INTERVAL = 100000;
	private final Context context;
	private static boolean first = true;
	private OrderChangedEvent event;
	private List<NetworkUpdateListener> networkListeners;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context) {
		this.context = context;
		networkListeners = new ArrayList<NetworkUpdateListener>();
		db = new OrderDbStorage(context);
		// TODO: Remove server path. Set it in settings.
		serverLayer = new ServerLayer(context);
		update(true);
		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTimerTask(this), UPDATE_INTERVAL,
				UPDATE_INTERVAL);
	}

	@Override
	public void changed(OrderChangedEvent event) {
		sendUpdate(event);
	}

	/**
	 * Creates an asynctask that will send an update and then update the local database
	 * @param event The event which holds what has changed
	 */
	private void sendUpdate(OrderChangedEvent event) {
		new AsyncTaskSend(event,serverLayer, this).execute();
		
	}

	@Override
	public IModel getModel() {
		Collection<Order> orders = db.query(null, null, null);

		for (Order order : orders) {
			order.addOrderListener(this);
		}

		return new MapModel(orders, db.getStations());
	}

	/**
	 * Updates local database with a collection of orders
	 * 
	 * @param orders
	 *            The orders to update
	 */
	public void updateDatabase(List<Order> orders) {
		if(first) {
			for (Order o : orders) {
				db.update(o);
			}
			first = false;
		}else {
			Log.d("update_database", "updates database");
			IModel model = ModelHandler.getModel(context);
			for (Order o : orders) {
				try {
					Order order = model.getOrderById(o.getId());
					if(order != null){
						order.sync(o);
						db.update(o);
					}
				} catch (NoSuchElementException e) {
					o.addOrderListener(this);
					model.addOrder(o);
					db.insert(o);
				}
			}
		}
	}
	
	/**
	 * Updates the local database with data from server. Runs in a different thread because of asynctask.
	 * @param getAll True if everything should be fetched false otherwise
	 */
	public void update(boolean getAll) {
		new AsyncTaskGet(getAll, this).execute(serverLayer);
	}

	public OrderChangedEvent getEvent() {
		return event;
	}

	public ServerLayer getServerLayer() {
		return serverLayer;
	}
	
	public void addNetworkListener(NetworkUpdateListener listener) {
		networkListeners.add(listener);
	}
	
	@Override
	public void removeNetworkListener(NetworkUpdateListener listener) {
		networkListeners.remove(listener);
	}
	public void startUpdate() {
		for(NetworkUpdateListener l : networkListeners) {
			l.startUpdate();
		}
	}
	
	public void endUpdate() {
		for(NetworkUpdateListener l : networkListeners) {
			l.endUpdate();
		}
	}
	
	public void noNetwork(String message) {
		for(NetworkUpdateListener l : networkListeners) {
			l.noNetwork(message);
		}
	}


}
