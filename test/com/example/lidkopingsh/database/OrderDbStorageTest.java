package com.example.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import android.test.InstrumentationTestCase;

import com.example.lidkopingsh.model.Customer;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Product;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
import com.example.lidkopingsh.model.Task;

/**
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 */
public class OrderDbStorageTest extends InstrumentationTestCase {

	private OrderDbStorage dbStorage;
	private Collection<Order> originalData;
	
	private static int customerId = 500;
	private static int orderId = 1;
	private static int orderNumber = 130001;
	private static int productId = 400;
	private static int taskId = 600;
	
	@BeforeClass
	private void setUpClass() {
		originalData = new OrderDbStorage(getInstrumentation().getContext()).query(null, null, null);
	}

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(getInstrumentation().getContext());
		dbStorage.clear();

		super.setUp();
	}
	
	@AfterClass
	private void tearDownClass() {
		dbStorage = new OrderDbStorage(getInstrumentation().getContext());
		dbStorage.clear();
		
		for (Order o : originalData) {
			dbStorage.insert(o);
		}
	}

	@Test
	public void testInsertSelectFullyPopulated() {
		Order order = getOrderFullyPopulated();

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
	}

	@Test
	public void testInsertSelectWithNullFields() {
		Order order = getOrderWithNullFields();

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
	}

	@Test
	public void testInsertSelectWithoutTasks() {
		Order order = getOrderWithoutTasks();

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
	}

	@Test
	public void testInsertSelectMultiple() {
		Order order1 = getOrderFullyPopulated();
		Order order2 = getOrderWithoutTasks();

		dbStorage.insert(order1);
		dbStorage.insert(order2);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order1) && orders.contains(order2));
	}

	private Order getOrderFullyPopulated() {
		Order order = getOrder("O.S.", getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone("Beskrivning", getStoneTasks()));
		products.add(getSocle("Sockel under mark", getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	private Order getOrderWithNullFields() {
		Order order = getOrder("O.T.", getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, getStoneTasks()));
		products.add(getSocle(null, getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	private Order getOrderWithoutTasks() {
		Order order = getOrder("O.U.", getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, null));
		products.add(getSocle(null, null));
		order.addProducts(products);

		return order;
	}

	private Customer getCustomer() {
		return new Customer("Mr", "Namn Efternamn", "Adress gata 5",
				"123 45 Stad", "email@test.se", customerId++);
	}

	private Order getOrder(String idName, Customer customer) {
		return new Order(orderId++, String.valueOf(orderNumber++), idName,
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogård", System.currentTimeMillis(), customer);
	}

	private Product getSocle(String description, List<Task> tasks) {
		return new Product(productId++, "Hallandia", description, "Polerad", tasks);
	}

	private List<Task> getSocleTasks() {
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task(taskId++, "Sågning", Status.DONE));
		tasks2.add(new Task(taskId++, "Slipning"));
		return tasks2;
	}

	private Stone getStone(String description, List<Task> tasks) {
		return new Stone(productId++, "Hallandia", description, "Polerad", tasks,
				"NB 49", "Råhugget", "Helvetica nedhuggen i guld",
				"Blomma nedhuggen i guld");
	}

	private List<Task> getStoneTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task(taskId++, "Sågning", Status.DONE));
		tasks.add(new Task(taskId++, "Råhuggning"));
		tasks.add(new Task(taskId++, "Gravering"));
		tasks.add(new Task(taskId++, "Målning"));
		return tasks;
	}
}
