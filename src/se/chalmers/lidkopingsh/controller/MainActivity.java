package se.chalmers.lidkopingsh.controller;

import se.chalmers.lidkopingsh.app.App;
import se.chalmers.lidkopingsh.server.ServerSettings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nullwire.trace.ExceptionHandler;

/**
 * An activity containing only a {@link OrderListFragment} on handsets and also
 * a {@link OrderDetailsFragment} on tablets.
 * 
 * This activity also implements the required
 * {@link OrderListFragment.Callbacks} interface to listen for item selections.
 * 
 * @author Simon Bengtsson
 * 
 */
public class MainActivity extends FragmentActivity implements
		OrderListFragment.OrderSelectedCallbacks {
	public static final String IS_TABLET_SIZE = "is_tablet_size";
	private OrderDetailsFragment mCurrentOrderDetailsFragment;

	/** Whether or not the app is running on a tablet sized device */
	private boolean mTabletSize;
	private SharedPreferences mSharedPreferences;
	private Menu mMenu;
	private NetworkWatcher mNetworkWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Main", "created");

		// Register an exception handler that sends unhandled exceptions to
		// http://simonbengtsson.se/lsh/stacktrace
		ExceptionHandler.register(this,
				"http://simonbengtsson.se/lsh/stacktrace_script.php");
		mSharedPreferences = App.getContext().getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);
		if (!isLoggedIn()) {
			Log.i("MainActivity", "Not logged, in. Staring login act");
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}
		Accessor.getModel(); // Create model and load data from database.
		Accessor.getServerConnector().addNetworkListener(new NetworkWatcherChild());
		mTabletSize = getResources().getBoolean(R.bool.isTablet);
		if (mTabletSize) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.tablet_maincontainer);
			((OrderListFragment) getSupportFragmentManager().findFragmentById(
					R.id.order_list)).setActivateOnItemClick(true);
		} else {
			setContentView(R.layout.list_root);
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		Log.i("MainAct", "MainAct created and user is logged in.");
	}

	private boolean isLoggedIn() {
		boolean apiEmpty = TextUtils.isEmpty(mSharedPreferences.getString(
				ServerSettings.PREFERENCES_API_KEY, null));
		boolean serverPathEmpty = TextUtils.isEmpty(mSharedPreferences
				.getString(ServerSettings.PREFERENCES_SERVER_PATH, null));
		return !apiEmpty && !serverPathEmpty;
	}

	@Override
	protected void onDestroy() {
		Log.d("Main", "destroyed");
		try {
			Accessor.getServerConnector().removeNetworkStatusListener(mNetworkWatcher);
		} catch (IllegalStateException e) {
		}
		super.onStop();
		super.onDestroy();
	}

	/**
	 * Callback method from {@link OrderListFragment.Callbacks} indicating that
	 * the order with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int orderId) {
		// On tablets, show the detail view in this activity by adding or
		// replacing the detail fragment
		if (mTabletSize) {
			Bundle arguments = new Bundle();
			arguments.putInt(OrderDetailsFragment.ORDER_ID, orderId);
			arguments.putBoolean(IS_TABLET_SIZE, mTabletSize);

			mCurrentOrderDetailsFragment = new OrderDetailsFragment();
			mCurrentOrderDetailsFragment.setArguments(arguments);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.tablet_hint_container,
							mCurrentOrderDetailsFragment).commit();
		}
		// On handsets, start the detail activity for the selected item ID
		else {
			Intent detailIntent = new Intent(this,
					HandsetsDetailsActivity.class);
			detailIntent.putExtra(OrderDetailsFragment.ORDER_ID, orderId);
			detailIntent.putExtra(IS_TABLET_SIZE, mTabletSize);
			startActivity(detailIntent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Accessor.getServerConnector().update(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_main, menu);
		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_start_map_view:
			startActivity(new Intent(this, OrderMapActivity.class));
			return true;
		case R.id.action_update:
			item.setActionView(R.layout.progress_indicator);
			Accessor.getServerConnector().update(false);
			return true;
		case R.id.action_help:
			Uri url = Uri.parse("http://simonbengtsson.se/userguide.pdf");
			Intent intent = new Intent(Intent.ACTION_VIEW, url);
			startActivity(intent);
			return true;
		case R.id.action_logout:
			mNetworkWatcher.logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class NetworkWatcherChild extends NetworkWatcher {
		
		@Override
		public void startedUpdate() {
			Log.i("MainActivity", "Update started");
			// Is null when activity just started
			if (mMenu != null) {
				MenuItem updateItem = mMenu.findItem(R.id.action_update);
				updateItem.setActionView(R.layout.progress_indicator);
			}
		}

		@Override
		public void finishedUpdate() {
			// Is null when activity just started
			if (mMenu != null) {
				MenuItem updateItem = mMenu.findItem(R.id.action_update);
				updateItem.setActionView(null);
			}
			Log.i("MainActivity", "Update finished");
		}
	}

	/**
	 * Asks the user if he really wants to exit
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Avsluta")
				.setMessage("Vill du avsluta applikationen?")
				.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				}).setNegativeButton("Nej", null).show();
	}
}
