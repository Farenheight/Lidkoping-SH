package se.chalmers.lidkopingsh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * This activity is only used on handset devices! It is representing a single
 * Stone detail screen. On tablet-size devices, item details are presented
 * side-by-side with a list of items in the {@link MainActivity}.
 * 
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link OrderDetailsFragment}.
 */
public class HandsetsDetailsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdetails_root);

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
			arguments.putInt(OrderDetailsFragment.ORDER_ID, getIntent()
					.getIntExtra(OrderDetailsFragment.ORDER_ID, -1));
			OrderDetailsFragment fragment = new OrderDetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tablet_hint_container, fragment).commit();
		}
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
		}
		return super.onOptionsItemSelected(item);
	}
}
