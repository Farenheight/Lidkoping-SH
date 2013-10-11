package se.chalmers.lidkopingsh.handler.test;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.handler.ServerLayer;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import android.test.AndroidTestCase;

public class ServerLayerTest extends AndroidTestCase{
	OrderDbStorage dbStorage;
	List<Order> orders;

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
		orders = serverLayer.getUpdates(true);
		 
		assertTrue(ordersBefore.size() < orders.size());
	}
	
	public void testSendUpdate() {
		ServerLayer serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", this.getContext());
		orders = serverLayer.getUpdates(true);
		Order order = new Order(orders.get(0));
		if (order.getProducts() != null) {
			for (Product p : order.getProducts()) {
				if (p.getTasks() != null) {
					p.getTasks().get(0).setStatus(Status.NOT_DONE);
				}
			}
		}
		order.sync(order);
		boolean success = serverLayer.sendUpdate(order);
		
		assertTrue(success);
	}

}
