package se.chalmers.lidkopingsh.handler.test;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.handler.ServerLayer;
import se.chalmers.lidkopingsh.model.Order;
import android.test.AndroidTestCase;

public class ServerLayerTest extends AndroidTestCase{
	OrderDbStorage dbStorage;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();
	}
	
	@Test
	public void testGetUpdate(){
		ServerLayer serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", this.getContext());
		Collection<Order> ordersBefore = dbStorage.query(null, null, null);
		List<Order> ordersAfter = serverLayer.getUpdates();
		 
		assertTrue(ordersBefore.size() < ordersAfter.size());
	}

}
