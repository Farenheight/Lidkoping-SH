package se.chalmers.lidkopingsh.database;

import java.util.Collection;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;

/**
 * Handles communication between Model and Order database.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbLayer implements ILayer {

	/**
	 * The database to be use
	 */
	private final OrderDbStorage db;

	/**
	 * Creates a layer for communication between model and Order database.
	 */
	public OrderDbLayer() {
		db = new OrderDbStorage();
	}

	@Override
	public IModel getModel() {
		Collection<Order> orders = db.query(null, null, null);
		return new MapModel(orders, db.getStations());
	}

	@Override
	public void ordersChanged(Collection<Order> added,
			Collection<Order> changed, Collection<Order> removed, IModel currentModel) {
		if (added != null) {
			db.insert(added);
		}
		if (changed != null) {
			db.updateOrders(changed);
		}
		if (removed != null) {
			db.delete(removed);
		}
		currentModel.setStations(db.getStations());
	}
}
