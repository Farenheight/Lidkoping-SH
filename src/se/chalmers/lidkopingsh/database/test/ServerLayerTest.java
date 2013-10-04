package se.chalmers.lidkopingsh.database.test;

import java.util.Collection;

import org.junit.Test;

import se.chalmers.lidkopingsh.database.*;
import se.chalmers.lidkopingsh.model.Order;
import android.test.AndroidTestCase;

public class ServerLayerTest extends AndroidTestCase{
	OrderDbStorage dbStorage;

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();
	}
	
	@Test
	public void testGetUpdate(){
		ServerLayer serverLayer = new ServerLayer("http://lidkopingsh.kimkling.net/api/", this.getContext());
		Collection<Order> ordersBefore = dbStorage.query(null, null, null);
		Order[] ordersAfter = serverLayer.getUpdates();
		
		assertTrue(ordersBefore.size() < ordersAfter.length);
	}

}
