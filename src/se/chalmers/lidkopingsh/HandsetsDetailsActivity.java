package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.util.NetworkUpdateListener;
import android.content.Intent;
import android.net.Uri;
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
 *  @author Simon Bengtsson
 */
public class HandsetsDetailsActivity extends FragmentActivity implements NetworkUpdateListener {

	private MenuItem mItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.od_root);
		ModelHandler.getLayer(this).addNetworkListener(this);

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
	protected void onDestroy() {
		ModelHandler.getLayer(this).removeNetworkListener(this);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_details_activity, menu);
		mItem = menu.findItem(R.id.action_update);
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
			ModelHandler.update(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void startUpdate() {
		Log.d("HandsetDetailActivity", "Update started");
		mItem.setActionView(R.layout.progress_indicator);
		
	}

	@Override
	public void endUpdate() {
		mItem.setActionView(null);
		Log.d("HandsetDetailActivity", "Update finished");
	}

	@Override
	public void noNetwork(String message) {
		Log.d("HandsetDetailActivity", "Network error");
	}
}
