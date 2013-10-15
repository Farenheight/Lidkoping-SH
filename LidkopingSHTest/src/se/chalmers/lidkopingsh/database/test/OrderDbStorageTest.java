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
	private static Collection<Order> originalData;

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();

		originalData = new OrderDbStorage(this.getContext()).query(null, null,
				null);

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		OrderDbStorage dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();

		for (Order o : originalData) {
			dbStorage.insert(o);
		}

		super.tearDown();
	}

	public void testInsertSelectFullyPopulated() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	public void testInsertSelectWithNullFields() {
		Order order = OrderDbFiller.getOrderWithNullFields("O.R.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

	public void testInsertSelectWithoutTasks() {
		Order order = OrderDbFiller.getOrderWithoutTasks("O.T.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

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

	public void testInsertOrderWithoutProducts() {
		Order order = OrderDbFiller.getOrderWithoutProducts("O.G.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.size() == 1);
		assertTrue(orders.contains(order));
	}

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

	//
	// public void testWhere() {
	// Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
	// Order order2 = OrderDbFiller.getOrderFullyPopulated("O.R.");
	// dbStorage.insert(order1);
	// dbStorage.insert(order2);
	// Collection<Order> orders = dbStorage.query(OrderTable.COLUMN_NAME_ID_NAME
	// + " = ?",
	// new String[] { "O.R." }, null);
	//
	// assertTrue(orders.size() == 1);
	// assertTrue(orders.contains(order2));
	//
	// }
	//
	//
	// public void testSort() {
	// Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
	// Order order2 = OrderDbFiller.getOrderFullyPopulated("O.R.");
	// dbStorage.insert(order1);
	// dbStorage.insert(order2);
	// Collection<Order> orders = dbStorage.query(null, null,
	// OrderTable.COLUMN_NAME_ID_NAME + " ASC");
	// Iterator<Order> iOrders = orders.iterator();
	//
	// assertTrue(orders.size() == 2);
	// assertEquals(iOrders.next(), order2);
	//
	// orders = dbStorage.query(null, null,
	// OrderTable.COLUMN_NAME_ID_NAME + " DESC");
	// iOrders = orders.iterator();
	//
	// assertTrue(orders.size() == 2);
	// assertEquals(iOrders.next(), order1);
	// }
}
