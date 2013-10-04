package se.chalmers.lidkopingsh.database;

import java.util.Collection;

import org.junit.Test;

import se.chalmers.lidkopingsh.model.Order;
import android.test.AndroidTestCase;

/**
 *  
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 */
public class ServerLayerTest extends AndroidTestCase {

	private OrderDbStorage dbStorage;
	private static Collection<Order> originalData;

	@Override
	protected void setUp() throws Exception {
		dbStorage = new OrderDbStorage(this.getContext());
		dbStorage.clear();

		originalData = new OrderDbStorage(this.getContext()).query(null, null,
				null);

		super.setUp();
	}

	@Test
	public void testGetUpdates() {
		ServerLayer serverLayer = new ServerLayer(
				"http://lidkopingsh.kimkling.net/api/", this.getContext());
		Collection<Order> ordersBefore = dbStorage.query(null, null, null);
		Collection<Order> ordersAfter = serverLayer.getUpdates();
		
		assertTrue(ordersBefore.size() < ordersAfter.size());
	}

}
