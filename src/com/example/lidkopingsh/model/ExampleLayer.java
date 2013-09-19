package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

public class ExampleLayer implements ILayer {

	@Override
	public void changed(Model object) {
		
		
	}

	@Override
	public Model getModel() {
		Task t = new Task(1, "Test");
		List<Task> tList = new ArrayList<Task>();
		tList.add(t);
		Product p = new Stone();
		Order o = new Order();
		o.addProduct(p);
		Model m = new Model();
		m.addOrder(o);
		return m;
	}
	
}
