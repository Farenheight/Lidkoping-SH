package se.chalmers.lidkopingsh.database;

import java.util.Collection;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;

/**
 * Handles communication between Model and Order database.
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
	}

	@Override
	public IModel getModel() {
		Collection<Order> orders = db.query(null, null, null);
		return new MapModel(orders, db.getStations());
	}

	@Override
	public void ordersChanged(Collection<Order> added,
			Collection<Order> changed, Collection<Order> removed) {
		if (added != null) {
			db.insert(added);
		}
		if (changed != null) {
			db.updateOrders(changed);
		}
		if (removed != null) {
			db.delete(removed);
		}
	}
}
