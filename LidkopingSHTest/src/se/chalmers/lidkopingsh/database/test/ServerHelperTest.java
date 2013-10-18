package se.chalmers.lidkopingsh.database.test;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.server.ServerHelper;
import se.chalmers.lidkopingsh.server.ServerHelper.ApiResponse;
import android.accounts.NetworkErrorException;
import android.test.AndroidTestCase;

/**
 * A class to test if the serverHelper communicates properly with the server
 * @author Olliver
 *
 */

public class ServerHelperTest extends AndroidTestCase{
	OrderDbStorage dbStorage;
	List<Order> orders;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear(); 
	}
	
	/**
	 * Tests if an update is properly achieved from the server
	 */
	public void testGetUpdate() {
		ServerHelper serverLayer = new ServerHelper(this.getContext());
		try {
			orders = serverLayer.getUpdates(true, new LinkedList<Order>());
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
		assertTrue(orders.size() != 0);
	}
	
	/**
	 * Checks if we can send an update to the server with success
	 */
	public void testSendUpdate() {
		ServerHelper serverLayer = new ServerHelper(this.getContext());
		try {
			orders = serverLayer.getUpdates(true, new LinkedList<Order>());
		} catch (AuthenticationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NetworkErrorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Order order = new Order(orders.get(0));
		if (order.getProducts() != null) {
			for (Product p : order.getProducts()) {
				if (p.getTasks() != null) {
					p.getTasks().get(0).setStatus(Status.NOT_DONE);
				}
			}
		}
		order.sync(order);
		ApiResponse response = null;
		try {
			response = serverLayer.sendUpdate(order);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NetworkErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(response.isSuccess());
	}

}
