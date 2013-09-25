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

		Product p0 = new Product(0, "MaterialFarg0", "Beskrivning0",
				"Frontarbete0", null);
		Product p1 = new Product(1, "MaterialFarg1", "Beskrivning1",
				"Frontarbete1", null);
		o0.addProduct(p0);
		o0.addProduct(p1);
		Product p2 = new Product(0, "MaterialFarg2", "Beskrivning2",
				"Frontarbete2", null);
		Product p3 = new Product(1, "MaterialFarg3", "Beskrivning3",
				"Frontarbete3", null);
		o1.addProduct(p2);
		o1.addProduct(p3);
		assertFalse(o0.equals(o1));
		o0.sync(o1);
		assertTrue(o0.equals(o1));

	}

	@Test
	public void testListeners() {
		OrderListener listener = new OrderListener();
		Order order0 = new Order(0, "13555", "OV", System.currentTimeMillis(),
				System.currentTimeMillis(), "Kvanum",
				System.currentTimeMillis(), new Customer());
		
		order0.addOrderListener(listener);
		
		Product product0 = new Product(0, "", "", "");
		Task task0 = new Task(0, "Task0");
		Task task1 = new Task(1, "Task1");
		
		Product product1 = new Stone(1, "", "", "", null, "", "", "", "");
		Task task2 = new Task(2, "Task2");
		Task task3 = new Task(3, "Task3");
		
		order0.addProduct(product0);
		order0.addProduct(product1);
		
		product0.addTask(task0);
		product0.addTask(task1);
		
		product1.addTask(task2);
		product1.addTask(task3);
		
		Order order1 = new Order(0, "", "", System.currentTimeMillis(),
				System.currentTimeMillis(), "", System.currentTimeMillis(),
				new Customer());
		Product product2 = new Product(0, "", "", "");
		Task task4 = new Task(0, "Task0");
		Task task5 = new Task(1, "Task1");
		
		Product product3 = new Stone(1, "", "", "", null, "", "", "", "");
		Task task6 = new Task(2, "Task2");
		Task task7 = new Task(3, "Task3");
		
		order1.addProduct(product2);
		order1.addProduct(product3);
		
		product2.addTask(task4);
		product2.addTask(task5);
		
		product3.addTask(task6);
		product3.addTask(task7);
		
		listener.getchanged = false;
		order0.sync(order1);
		assertFalse(listener.getchanged);
		task3.setStatus(Status.DONE);
		assertTrue(listener.getchanged);
	}

	private class OrderListener implements Listener<Order> {
		public boolean getchanged = false;

		@Override
		public void changed(Order object) {
			getchanged = true;
		}
	};
}
