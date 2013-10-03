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
	private final ServerLayer serverLayer;

	/**
	 * Creates a layer for communication between model and Order database.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbLayer(Context context, String serverPath) {
		db = new OrderDbStorage(context);
		serverLayer = new ServerLayer(serverPath);
		if (db.query(null, null, null).isEmpty()) {
			OrderDbFiller.fillDb(db);
			Log.d("OrderdbLayer", "filled database with dummy data");
		}
	}

	@Override
	public void changed(Order order) {
		//TODO: getUpdates()"sync"->(updateDatabase())->sendUpdate().
		//(timestamps), use a stack before updating local db.
		//Server manages DB check timestamp on order. Newer or Older? Merge them?
		//db.update(order);
		serverLayer.sendUpdate(order);
		serverLayer.getUpdates();
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
