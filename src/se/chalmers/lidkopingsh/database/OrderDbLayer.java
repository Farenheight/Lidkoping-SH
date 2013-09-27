package se.chalmers.lidkopingsh.database;

import java.util.Collection;

import se.chalmers.lidkopingsh.model.ILayer;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;


/**
 * Handles communication between model and Order database.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbLayer implements ILayer {

	private final OrderDbStorage db;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context) {
		db = new OrderDbStorage(context);
		if (db.query(null, null, null).isEmpty()) {
			OrderDbFiller.fillDb(db);
			Log.d("OrderdbLayer", "filled database with dummy data");
		}
	}

	@Override
	public void changed(Order order) {
		db.update(order);
		Log.d("Changed Order", "Changed objects in database");
	}

	@Override
	public IModel getModel() {
		Collection<Order> orders = db.query(null, null, null);

		for (Order order : orders) {
			order.addOrderListener(this);
		}

		return new MapModel(orders, db.getStations());
	}

}
