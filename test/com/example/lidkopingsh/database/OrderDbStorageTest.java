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
		Order order = getFullyPopulatedOrder();

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);

		assertTrue(orders.contains(order));
	}
	
	private Order getFullyPopulatedOrder() {
		Customer customer = new Customer("Mr", "Namn Efternamn",
				"Adress gata 5", "123 45 Stad", "email@test.se", 23);
		Order order = new Order(1, "130942", "O.S.",
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogård", System.currentTimeMillis(), customer);
		Collection<Product> products = new LinkedList<Product>();

		List<Task> tasks1 = new ArrayList<Task>();
		tasks1.add(new Task((int) System.currentTimeMillis(), "Sågning",
				Status.DONE));
		tasks1.add(new Task((int) System.currentTimeMillis(), "Råhuggning",
				Status.NOT_DONE));
		tasks1.add(new Task((int) System.currentTimeMillis(), "Gravering",
				Status.NOT_DONE));
		tasks1.add(new Task((int) System.currentTimeMillis(), "Målning",
				Status.NOT_DONE));
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task((int) System.currentTimeMillis(), "Sågning",
				Status.DONE));
		tasks2.add(new Task((int) System.currentTimeMillis(), "Slipning",
				Status.NOT_DONE));

		products.add(new Stone((int) System.currentTimeMillis(), "Hallandia",
				"Beskrivning", "Polerad", tasks1, "NB 49", "Råhugget",
				"Helvetica nedhuggen i guld", "Blomma nedhuggen i guld"));
		products.add(new Product((int) System.currentTimeMillis(), "Hallandia",
				"Sockel under mark", "Polerad", tasks2));
		
		order.addProducts(products);
		
		return order;
	}
}
