package com.example.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProductListenerTest implements Listener<Product>{
	boolean hasBeenNotified = false;
	@Test
	private void test() {
		hasBeenNotified = false;
		Product p = new Product();
		Task t = new Task("Task");
		p.addTask(t, 0);
		p.addProductListener(this);
		t.setStatus(Status.DONE);
		assertEquals(true, hasBeenNotified);
	}

	@Override
	public void changed(Product product) {
		hasBeenNotified = true;
	}
	

}
