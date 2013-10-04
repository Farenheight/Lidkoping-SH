package se.chalmers.lidkopingsh.database;

import java.util.Collection;
import java.util.NoSuchElementException;

import se.chalmers.lidkopingsh.model.ILayer;
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

	private final OrderDbStorage db;
	private final ServerLayer serverLayer;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context) {
		db = new OrderDbStorage(context);
		//TODO: Remove server path. Set it in settings. 
		serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", context);
		if (db.query(null, null, null).isEmpty()) {
			OrderDbFiller.fillDb(db);
			Log.d("OrderdbLayer", "filled database with dummy data");
		}
	}

	@Override
	public void changed(Order order) {
		//TODO: Check if change was same as in DB.
		serverLayer.sendUpdate(order);
		updateDatabase(serverLayer.getUpdates());
		
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
	 * @param orders The orders to update
	 */
	public void updateDatabase(Collection<Order> orders) {
		IModel model = getModel();
		for (Order o : orders) {
			try {
				Order order = model.getOrderById(o.getId());
				order.sync(o);
				db.update(o);
			} catch (NoSuchElementException e) {
				o.addOrderListener(this);
				model.addOrder(o);
				db.update(o);
			}
		}
	}

}
