package se.chalmers.lidkopingsh.controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This activity is only used on handset devices! It is representing a single
 * Stone detail screen. On tablet-size devices, item details are presented
 * side-by-side with a list of items in the {@link MainActivity}.
 * 
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link OrderDetailsFragment}.
 * 
 * @author Simon Bengtsson
 */
public class HandsetsDetailsActivity extends FragmentActivity {

	private Menu mMenu;
	private NetworkWatcher mNetworkWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.od_root);
		mNetworkWatcher = new NetworkWatcherChild();
		Accessor.getServerConnector().addNetworkListener(mNetworkWatcher);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it. If there isn't
		// a saved fragment, create it below.
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			// Send current order id
			arguments.putInt(OrderDetailsFragment.ORDER_ID, getIntent()
					.getIntExtra(OrderDetailsFragment.ORDER_ID, -1));
			// Send if running on tabelt
			arguments.putBoolean(MainActivity.IS_TABLET_SIZE, getIntent()
					.getBooleanExtra(MainActivity.IS_TABLET_SIZE, false));
			OrderDetailsFragment fragment = new OrderDetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tablet_hint_container, fragment).commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Accessor.getServerConnector().update(false);
	}

	@Override
	protected void onDestroy() {
		Accessor.getServerConnector().removeNetworkStatusListener(mNetworkWatcher);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_details_activity, menu);
		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	// Called when a button in the action bar is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Home or Up button clicked.
		case android.R.id.home:
			// Navigates up one level in the application structure.
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;
		case R.id.action_update:
			item.setActionView(R.layout.progress_indicator);
			Accessor.getServerConnector().update(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class NetworkWatcherChild extends NetworkWatcher {
		
		@Override
		public void startedUpdate() {
			Log.d("HandsetDetailActivity", "Update started");
			if (mMenu != null) {
				MenuItem updateItem = mMenu.findItem(R.id.action_update);
				updateItem.setActionView(R.layout.progress_indicator);
			}
		}

		@Override
		public void finishedUpdate() {
			if (mMenu != null) {
				MenuItem updateItem = mMenu.findItem(R.id.action_update);
				updateItem.setActionView(null);
			}
			Log.d("HandsetDetailActivity", "Update finished");
		}
	}
}
