package com.example.lidkopingsh.database;

import java.util.Collection;

import android.content.Context;

import com.example.lidkopingsh.model.ILayer;
import com.example.lidkopingsh.model.IModel;
import com.example.lidkopingsh.model.MapModel;
import com.example.lidkopingsh.model.Order;

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
		}
	}

	@Override
	public void changed(Order order) {
		db.update(order);
	}

	@Override
	public IModel getModel() {
		Collection<Order> orders = db.query(null, null, null);

		for (Order order : orders) {
			order.addOrderListener(this);
		}

		return new MapModel(orders); 
	}

}
