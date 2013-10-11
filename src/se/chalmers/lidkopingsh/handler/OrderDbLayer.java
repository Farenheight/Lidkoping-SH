package se.chalmers.lidkopingsh.handler;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;
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
	private final long UPDATE_INTERVAL = 300000;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context) {
		db = new OrderDbStorage(context);
		// TODO: Remove server path. Set it in settings.
		serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/",
				context);
		updateDatabase(getUpdates(true));
		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTimerTask(this), 0, UPDATE_INTERVAL);
	}

	@Override
	public void changed(Order order) {
		// TODO: Check if change was same as in DB.
		boolean success = sendUpdate(order);
		List<Order> orders = getUpdates(false);
		if (!success) { //TODO: While here?
			sendUpdate(order);
		}
		orders = getUpdates(false);
		if (orders == null) {
			order.sync(null);
		} else {
			updateDatabase(orders);
		}

	}

	private boolean sendUpdate(Order order) {
		try {
			return new AsyncTaskSend(order).execute(serverLayer).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
		
	}

	public List<Order> getUpdates(boolean getAll) {
		try {
			return new AsyncTaskGet(getAll).execute(serverLayer).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		Log.d("update_database", "updates database");
		IModel model = getModel();
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
	
	public void update(boolean getAll) {
		updateDatabase(getUpdates(getAll));
	}

}
