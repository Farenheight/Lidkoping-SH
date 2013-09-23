package com.example.lidkopingsh.database;

import java.util.Collection;

import org.junit.Test;

import android.test.InstrumentationTestCase;

import com.example.lidkopingsh.model.Customer;
import com.example.lidkopingsh.model.Order;

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
		
		super.setUp();
	}

	@Test
	public void testSelect() {
		Customer customer = new Customer("Mr", "Namn Efternamn",
				"Adress gata 5", "123 45 Stad", "email@test.se", 23);
		Order order = new Order(1, "130942", "O.S.", System.currentTimeMillis(),
				System.currentTimeMillis(), "Kyrkogård",
				System.currentTimeMillis(), customer);

		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);
		
		assertTrue(orders.contains(order));
	}

}
