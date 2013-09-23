package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

public class ExampleLayer implements ILayer {

	@Override
	public void changed(IModel object) {
		
		
	}

	@Override
	public IModel getModel() {
		Task t = new Task(1, "Test");
		List<Task> tList = new ArrayList<Task>();
		tList.add(t);
		Product p = new Stone();
		Order o = new Order();
		o.addProduct(p);
		IModel m = new Model();
		m.addOrder(o);
		return m;
	}
	
}
