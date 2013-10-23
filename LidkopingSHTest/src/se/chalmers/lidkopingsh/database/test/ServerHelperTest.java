package se.chalmers.lidkopingsh.database.test;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import se.chalmers.lidkopingsh.database.OrderDbStorage;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.server.ServerHelper;
import se.chalmers.lidkopingsh.server.ServerSettings;
import se.chalmers.lidkopingsh.server.ServerHelper.ApiResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
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
		dbStorage = new OrderDbStorage();
		dbStorage.clear(); 
	}
	
	/**
	 * Tests if an update is properly achieved from the server
	 */
	public void testGetUpdate() {
		// Saves server path
		SharedPreferences preferences = this.getContext().getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(ServerSettings.PREFERENCES_SERVER_PATH,
				"http://lidkopingsh.kimkling.net/api/");
		editor.commit();
		ServerHelper serverLayer = new ServerHelper();
		try {
			ApiResponse response = serverLayer.getApikey("dev", "dev");
			assertTrue(response.isSuccess());
			orders = serverLayer.getUpdates(true, new LinkedList<Order>());
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
		assertTrue(orders.size() != 0);
	}

}
