package se.chalmers.lidkopingsh.server.test;

import java.util.ArrayList;

import org.apache.http.auth.AuthenticationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.server.ServerHelper;
import se.chalmers.lidkopingsh.server.ServerSettings;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

public class ServerHelperTest extends InstrumentationTestCase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		// Saves server path
		SharedPreferences preferences = getInstrumentation().getContext().getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(ServerSettings.PREFERENCES_SERVER_PATH,
				"https://lidkopingsh.kimkling.net/");
		editor.commit();
		
		ServerHelper helper = new ServerHelper();
		try {
			helper.getUpdates(true, new ArrayList<Order>());
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
	}

}
