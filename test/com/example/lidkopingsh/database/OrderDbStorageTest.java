package com.example.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(getInstrumentation().getContext());
		dbStorage.clear();

		super.setUp();
	}

	@Test
	public void testInsertSelect() {
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

	private Order getOrderFullyPopulated() {
		Order order = getOrder("130942", "O.S.", getCustomer());
		
		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone("Beskrivning", getStoneTasks()));
		products.add(getSocle("Sockel under mark", getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	private Order getOrderWithNullFields() {
		Order order = getOrder("130942", "O.S.", getCustomer());
		
		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, getStoneTasks()));
		products.add(getSocle(null, getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	private Order getOrderWithoutTasks() {
		Order order = getOrder("130942", "O.S.", getCustomer());
		
		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, null));
		products.add(getSocle(null, null));
		order.addProducts(products);

		return order;
	}

	private Customer getCustomer() {
		return new Customer("Mr", "Namn Efternamn",
				"Adress gata 5", "123 45 Stad", "email@test.se", (int) System.currentTimeMillis());
	}

	private Order getOrder(String orderNumber, String idName, Customer customer) {
		return new Order(1, orderNumber, idName,
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogård", System.currentTimeMillis(), customer);
	}

	private Product getSocle(String description, List<Task> tasks) {
		return new Product((int) System.currentTimeMillis(), "Hallandia",
				description, "Polerad", tasks);
	}

	private List<Task> getSocleTasks() {
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task((int) System.currentTimeMillis(), "Sågning",
				Status.DONE));
		tasks2.add(new Task((int) System.currentTimeMillis(), "Slipning"));
		return tasks2;
	}

	private Stone getStone(String description, List<Task> tasks) {
		return new Stone((int) System.currentTimeMillis(), "Hallandia",
				description, "Polerad", tasks, "NB 49", "Råhugget",
				"Helvetica nedhuggen i guld", "Blomma nedhuggen i guld");
	}

	private List<Task> getStoneTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task((int) System.currentTimeMillis(), "Sågning",
				Status.DONE));
		tasks.add(new Task((int) System.currentTimeMillis(), "Råhuggning"));
		tasks.add(new Task((int) System.currentTimeMillis(), "Gravering"));
		tasks.add(new Task((int) System.currentTimeMillis(), "Målning"));
		return tasks;
	}
}
