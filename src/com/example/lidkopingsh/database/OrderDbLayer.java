package com.example.lidkopingsh.database;

import android.content.Context;

import com.example.lidkopingsh.model.ILayer;
import com.example.lidkopingsh.model.Model;

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
	public void changed(Model object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model getModel() {
		Model model = new Model();
		model.addOrders(db.query(null, null, null));
		model.addOrderListener(this);
		return model;
	}

}
