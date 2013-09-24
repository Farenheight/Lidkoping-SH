package com.example.lidkopingsh.database;

import java.util.Collection;

import android.content.Context;

import com.example.lidkopingsh.model.ILayer;
import com.example.lidkopingsh.model.Model;
import com.example.lidkopingsh.model.Order;

/**
 * Handles communication between model and Order database.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbLayer implements ILayer {
	
	private final OrderDbStorage db;
	
	public OrderDbLayer(Context context) {
		db = new OrderDbStorage(context);
	}

	@Override
	public void changed(Order object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model getModel() {
		Collection<Order> orders = db.query(null, null, null);
		
		for (Order order : orders) {
			order.addOrderListener(this);
		}
		
		Model model = new Model();
		model.addOrders(orders);
		
		return model;
	}

}