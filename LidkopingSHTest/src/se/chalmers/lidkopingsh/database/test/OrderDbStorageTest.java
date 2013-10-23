package se.chalmers.lidkopingsh.database.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.database.OrderDbFiller;
import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.model.Order;
import android.test.AndroidTestCase;

/**
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbStorageTest extends AndroidTestCase {

	private OrderDbStorage dbStorage;

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage();
		dbStorage.clear();

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		dbStorage.clear();
		
		super.tearDown();
	}

	/**
	 * Test if a fullypopulated order is inserted properly in the database and can be extracted
	 */
	public void testInsertSelectFullyPopulated() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	/**
	 * Test if an order with null fields is inserted properly in the database and can be extracted
	 */
	public void testInsertSelectWithNullFields() {
		Order order = OrderDbFiller.getOrderWithNullFields("O.R.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	/**
	 * Test if an order without tasks is inserted properly in the database and can be extracted
	 */
	public void testInsertSelectWithoutTasks() {
		Order order = OrderDbFiller.getOrderWithoutTasks("O.T.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	/**
	 * Tests if you can insert mutiple orders and extract all from the database at the same time
	 */
	public void testInsertSelectMultiple() {
		List<Order> initOrders = new ArrayList<Order>();
		Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
		Order order2 = OrderDbFiller.getOrderWithoutTasks("O.T.");
		initOrders.add(order1);
		initOrders.add(order2);

		dbStorage.insert(initOrders);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 2);
		assertTrue(orders.contains(order1) && orders.contains(order2));
	}

	/**
	 * Test if an order without products is inserted properly in the database and can be extracted
	 */
	public void testInsertOrderWithoutProducts() {
		Order order = OrderDbFiller.getOrderWithoutProducts("O.G.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);
		
		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	/**
	 * Tests if you can delete orders from the database
	 */
	public void testDelete() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));

		dbStorage.delete(order);
		orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 0);
	}

	/**
	 * Tests if it's possible to update orders stored in the database
	 */
	public void testUpdate() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));

		order.addProduct(OrderDbFiller.getStone("Testbeskrivning",
				OrderDbFiller.getStoneTasks()));
		dbStorage.update(order);
		orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}
}
