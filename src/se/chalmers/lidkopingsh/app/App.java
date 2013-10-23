package se.chalmers.lidkopingsh.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * A singleton class for providing a context to the sql-lite database and server
 * classes. Providing an activity context can introduce memory leaks as
 * Activities won't get recycled.
 * 
 * @author Simon Bengtsson
 * @author Anton Jansson
 * 
 */
public class App extends Application {

	private static App instance;

	/**
	 * Constructor called once when the application is first created.
	 */
	public App() {
		Log.d("App", "App created");
		instance = this;
	}

	/**
	 * Method for accessing the application context.
	 * 
	 * @return The application context
	 */
	public static Context getContext() {
		return instance;
	}

}