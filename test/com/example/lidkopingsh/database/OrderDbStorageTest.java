package com.example.lidkopingsh.database;

import java.util.Collection;

import org.junit.Test;

import android.test.InstrumentationTestCase;

import com.example.lidkopingsh.model.Order;

/**
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbStorageTest extends InstrumentationTestCase {

	private OrderDbStorage dbStorage;
	private static Collection<Order> originalData;

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(getInstrumentation().getContext());
		dbStorage.clear();

		originalData = new OrderDbStorage(getInstrumentation().getContext())
				.query(null, null, null);

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		OrderDbStorage dbStorage = new OrderDbStorage(getInstrumentation()
				.getContext());
		dbStorage.clear();

		for (Order o : originalData) {
			dbStorage.insert(o);
		}

		super.tearDown();
	}

	@Test
	public void testInsertSelectFullyPopulated() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
	}

	@Test
	public void testInsertSelectWithNullFields() {
		Order order = OrderDbFiller.getOrderWithNullFields("O.R.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
	}

	@Test
	public void testInsertSelectWithoutTasks() {
		Order order = OrderDbFiller.getOrderWithoutTasks("O.T.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
	}

	@Test
	public void testInsertSelectMultiple() {
		Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
		Order order2 = OrderDbFiller.getOrderWithoutTasks("O.T.");

		dbStorage.insert(order1);
		dbStorage.insert(order2);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order1) && orders.contains(order2));
		assertTrue(orders.size() == 2);
	}

	@Test
	public void testInsertOrderWithoutProducts() {
		Order order = OrderDbFiller.getOrderWithoutProducts("O.G.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
	}
}
