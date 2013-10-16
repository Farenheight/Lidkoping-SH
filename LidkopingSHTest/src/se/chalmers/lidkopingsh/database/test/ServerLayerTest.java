package se.chalmers.lidkopingsh.database.test;

import java.util.List;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.handler.ServerLayer;
import se.chalmers.lidkopingsh.handler.ServerLayer.ResponseSend;
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
	
	public void testGetUpdate() {
		ServerLayer serverLayer = new ServerLayer(this.getContext());
		orders = serverLayer.getUpdates(true);
		assertTrue(orders.size() != 0);
	}
	
	public void testSendUpdate() {
		ServerLayer serverLayer = new ServerLayer(this.getContext());
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
		ResponseSend response = serverLayer.sendUpdate(order);
		
		assertTrue(response.isSuccess());
	}

}
