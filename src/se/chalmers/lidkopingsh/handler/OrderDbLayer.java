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
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.model.Status;
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
	private final Context context;
	private static boolean first = true;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context) {
		this.context = context;
		db = new OrderDbStorage(context);
		// TODO: Remove server path. Set it in settings.
		serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/",
				context);
		updateDatabase(getUpdates(true));
		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTimerTask(this), 0, UPDATE_INTERVAL);
	}

	@Override
	public void changed(OrderChangedEvent event) {
		// TODO: Check if change was same as in DB.
		Status status = event.getTask().getStatus();
		boolean success = sendUpdate(event.getOrder());
		List<Order> orders = getUpdates(false);
		orders = getUpdates(false);
		if (orders == null) {
			event.getOrder().sync(null);
		} else {
			updateDatabase(orders);
		}
		if (!success) {
			event.getTask().setStatus(status);
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
			//TODO: freezes main-thread during asynctask.
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
	
	public void update(boolean getAll) {
		updateDatabase(getUpdates(getAll));
	}

}
