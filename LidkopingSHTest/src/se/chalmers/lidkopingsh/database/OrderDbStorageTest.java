package se.chalmers.lidkopingsh.database;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import se.chalmers.lidkopingsh.database.DataContract.OrderTable;
import se.chalmers.lidkopingsh.model.Order;
import android.test.InstrumentationTestCase;

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
	
	@Test
	public void testDelete() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
		
		dbStorage.delete(order);
		orders = dbStorage.query(null, null, null);
		
		assertTrue(orders.size() == 0);
	}
	
	@Test
	public void testUpdate() {
		Order order = OrderDbFiller.getOrderFullyPopulated("O.S.");

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
		
		order.addProduct(OrderDbFiller.getStone("Testbeskrivning", OrderDbFiller.getStoneTasks()));
		dbStorage.update(order);
		orders = dbStorage.query(null, null, null);
		
		assertTrue(orders.contains(order));
		assertTrue(orders.size() == 1);
	}
	
	@Test
	public void testWhere() {
		Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
		Order order2 = OrderDbFiller.getOrderFullyPopulated("O.R.");
		dbStorage.insert(order1);
		dbStorage.insert(order2);
		Collection<Order> orders = dbStorage.query(OrderTable.COLUMN_NAME_ID_NAME + " = ?", 
				new String[] { "O.R." }, null);
		assertTrue(orders.contains(order2));
		assertTrue(orders.size() == 1);

	}
	
	@Test
	public void testSort() {
		Order order1 = OrderDbFiller.getOrderFullyPopulated("O.S.");
		Order order2 = OrderDbFiller.getOrderFullyPopulated("O.R.");
		dbStorage.insert(order1);
		dbStorage.insert(order2);
		Collection<Order> orders = dbStorage.query(null, null, 
				OrderTable.COLUMN_NAME_ID_NAME + " ASC");
		Iterator<Order> iOrders = orders.iterator();

		assertEquals(iOrders.next(), order2);
		assertTrue(orders.size() == 2);
		
		orders = dbStorage.query(null, null, 
				OrderTable.COLUMN_NAME_ID_NAME + " DESC");
		iOrders = orders.iterator();

		assertEquals(iOrders.next(), order1);
		assertTrue(orders.size() == 2);
	}
}
