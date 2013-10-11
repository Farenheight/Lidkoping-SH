package se.chalmers.lidkopingsh.handler.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import android.test.AndroidTestCase;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.handler.ServerLayer;
import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;

public class ServerLayerTest extends AndroidTestCase{
	OrderDbStorage dbStorage;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();
	}
	
	@Test
	public void testGetUpdate() {
		ServerLayer serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", this.getContext());
		Collection<Order> ordersBefore = dbStorage.query(null, null, null);
		List<Order> ordersAfter = serverLayer.getUpdates();
		 
		assertTrue(ordersBefore.size() < ordersAfter.size());
	}
	
	public void testSendUpdate() {
		ServerLayer serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", this.getContext());
		boolean success = serverLayer.sendUpdate(new Order(1000000, "1300002",
				"F,F", System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrka", "Board", "blocket", "nummer", (long) 1312300,
				new Customer("titel", "name", "address", "postAddress",
						"email", 1000300), new LinkedList<Product>(),
				new LinkedList<Image>()));
		assertTrue(success);
	}

}
