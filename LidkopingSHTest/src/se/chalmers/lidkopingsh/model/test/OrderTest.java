package se.chalmers.lidkopingsh.model.test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import se.chalmers.lidkopingsh.util.Listener;

public class OrderTest {

	private Station firstStation;
	private Station secondStation;
	private Station thirdStation;

	@Before
	public void setUp() {
		firstStation = new Station(0, "Station0");
		secondStation = new Station(1, "Station1");
		thirdStation = new Station(2, "Station2");
	}

	@Test
	public void testGetNumOfStationsLeft() {
		long time = System.currentTimeMillis();
		Order firstOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);
		Order secondOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);
		Order thirdOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);

		firstOrder.addProduct(new Product(Arrays.asList(new Task[] {
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.NOT_DONE) })));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[] {
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.DONE) })));
		int stationsLeft = firstOrder.getNumOfStationsLeft(secondStation);
		assertTrue("Should have 0 station left, has: " + stationsLeft,
				stationsLeft == 0);
		stationsLeft = firstOrder.getNumOfStationsLeft(thirdStation);
		assertTrue("Should have 1 station left, has: " + stationsLeft,
				stationsLeft == 1);
		stationsLeft = firstOrder.getNumOfStationsLeft(firstStation);
		assertTrue("Should have infinit stations left, has: " + stationsLeft,
				stationsLeft == Integer.MAX_VALUE);
		stationsLeft = secondOrder.getNumOfStationsLeft(secondStation);
		assertTrue("Should have one station left, has: " + stationsLeft,
				stationsLeft == 0);
		stationsLeft = thirdOrder.getNumOfStationsLeft(thirdStation);
		assertTrue("Should have infinit stations left, has: " + stationsLeft,
				stationsLeft == Integer.MAX_VALUE);
	}

	@Test
	public void testProgress() {
		long time = System.currentTimeMillis();
		Order firstOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);
		Order secondOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);
		Order thirdOrder = new Order(1, "130001", "K.J", time, time,
				"Goteborg", null, null, null, time, new Customer("", "", "",
						"", "", 0), null, null,null);

		firstOrder.addProduct(new Product(Arrays.asList(new Task[] {
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.DONE) })));
		assertTrue("All done should equal 100 percent. Was: " + firstOrder.getProgress(),
				firstOrder.getProgress() == 100);

		secondOrder.addProduct(new Product(Arrays.asList(new Task[] {
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.NOT_DONE) })));
		assertTrue("Two of two done should equal 50 percent",
				secondOrder.getProgress() == 50);
		
		assertTrue("No products should equal 100 percent",
				thirdOrder.getProgress() == 100);
	}

	@Test
	public void testEquals() {
		// TODO filled with null to avoid compilation errors.
		long time = System.currentTimeMillis();

		Order o1 = new Order(1, "130001", "K.J", time, time, "Goteborg", null,
				null, null, time, new Customer("", "", "", "", "", 0), null,
				null,null);
		Order o2 = new Order(1, "130001", "K.J", time, time, "Goteborg", null,
				null, null, time, new Customer("", "", "", "", "", 0), null,
				null,null);
		assertTrue(o1.equals(o2));

		Product p = new Product(new ArrayList<Task>());
		o2.addProduct(p);
		assertFalse(o1.equals(o2));

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		o2.removeProduct(p);
		assertFalse(o1.equals(o2));
	}

	@Test
	public void testSync() {
		SyncListener syncListener = new SyncListener();
		// TODO filled with null to avoid compilation errors
		Order o0 = new Order(0, "2", "OM", 2837203547257l,
				System.currentTimeMillis(), "Kvanum", null, null, null, 0l,
				new Customer("", "", "", "", "", 0), null, null,null);
		Order o1 = new Order(0, "3", "OK", 2837203547257l,
				System.currentTimeMillis(), "Lish", null, null, null, 2l,
				new Customer("", "", "", "", "", 0), null, null,null);
		Order o2 = new Order(0, "3", "OK", 2837203547257l,
				System.currentTimeMillis(), "Lish", null, null, null, 2l,
				new Customer("", "", "", "", "", 0), null, null,null);
		assertFalse(o0.equals(o1));
		assertTrue(o1.equals(o2));

		o0.addSyncOrderListener(syncListener);
		o0.sync(o1);
		assertTrue(syncListener.hasSynced);
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

	private class SyncListener implements Listener<Order> {
		public boolean hasSynced = false;

		@Override
		public void changed(Order object) {
			hasSynced = true;
		}
	}

	@Test
	public void testListeners() {
		OrderListener listener = new OrderListener();
		Order order0 = new Order(0, "13555", "OV", System.currentTimeMillis(),
				System.currentTimeMillis(), "Kvanum", null, "", "",
				System.currentTimeMillis(),
				new Customer("", "", "", "", "", 0), null, null,null);

		order0.addOrderListener(listener);

		Product product0 = new Product(0, "", "", "", null);
		Task task0 = new Task(new Station(0, "Task0"));
		Task task1 = new Task(new Station(1, "Task1"));

		Product product1 = new Stone(1, "", "", "", null, "", "", "", "", null);
		Task task2 = new Task(new Station(2, "Task2"));
		Task task3 = new Task(new Station(3, "Task3"));

		order0.addProduct(product0);
		order0.addProduct(product1);

		product0.addTask(task0);
		product0.addTask(task1);

		product1.addTask(task2);
		product1.addTask(task3);

		Order order1 = new Order(0, "", "", System.currentTimeMillis(),
				System.currentTimeMillis(), null, "", "", "",
				System.currentTimeMillis(),
				new Customer("", "", "", "", "", 0), null, null,null);

		Product product2 = new Product(0, "", "", "", null);
		Task task4 = new Task(new Station(0, "Task0"));
		Task task5 = new Task(new Station(1, "Task1"));

		Product product3 = new Stone(1, "", "", "", null, "", "", "", "", null);
		Task task6 = new Task(new Station(2, "Task2"));
		Task task7 = new Task(new Station(3, "Task3"));

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

	private class OrderListener implements Listener<OrderChangedEvent> {
		public boolean getchanged = false;

		@Override
		public void changed(OrderChangedEvent event) {
			getchanged = true;
		}
	};
}
