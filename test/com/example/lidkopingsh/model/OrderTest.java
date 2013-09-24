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

	@Test
	public void testSync() {
		Order o0 = new Order(0, "2", "OM", 2837203547257l,
				System.currentTimeMillis(), "Kvanum", 0l, new Customer());
		Order o1 = new Order(0, "3", "OK", 2837203547257l,
				System.currentTimeMillis(), "Lish", 2l, new Customer());
		Order o2 = new Order(0, "3", "OK", 2837203547257l,
				System.currentTimeMillis(), "Lish", 2l, new Customer());
		assertFalse(o0.equals(o1));
		assertTrue(o1.equals(o2));
		o0.sync(o1);
		assertTrue(o0.equals(o1));
		assertTrue(o0.equals(o2));
		
		Product p0 = new Product(0,"MaterialFarg0","Beskrivning0","Frontarbete0",null);
		Product p1 = new Product(1,"MaterialFarg1","Beskrivning1","Frontarbete1",null);
		o0.addProduct(p0);
		o0.addProduct(p1);
		Product p2 = new Product(0,"MaterialFarg2","Beskrivning2","Frontarbete2",null);
		Product p3 = new Product(1,"MaterialFarg3","Beskrivning3","Frontarbete3",null);
		o1.addProduct(p2);
		o1.addProduct(p3);
		assertFalse(o0.equals(o1));
		o0.sync(o1);
		assertTrue(o0.equals(o1));
		
	}
}
