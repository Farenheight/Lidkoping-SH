package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class OrderTest {

	@Test
	public void testEquals() {
		long time = System.currentTimeMillis();
		Order o1 = new Order(1, "130001", "K.J", time, time, "Goteborg", time,
				new Customer());
		Order o2 = new Order(1, "130001", "K.J", time, time, "Goteborg", time,
				new Customer());
		assertTrue(o1.equals(o2));
		
		Product p = new Product();
		o2.addProduct(p);
		assertFalse(o1.equals(o2));
		
		o2.removeProduct(p);
		assertFalse(o1.equals(o2));
	}
}
