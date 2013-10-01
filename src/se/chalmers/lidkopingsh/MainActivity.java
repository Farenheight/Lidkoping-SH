package se.chalmers.lidkopingsh;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.lidkopingsh.R;

/**
 * An activity containing only a {@link OrderListFragment} on handsets and also
 * a {@link OrderDetailFragment} on tablets.
 * 
 * This activity also implements the required
 * {@link OrderListFragment.Callbacks} interface to listen for item selections.
 * 
 * TODO: Class is checked. Remove this.
 */
public class MainActivity extends FragmentActivity implements
		OrderListFragment.Callbacks {


	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTabletMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stone_twopane);

		new FlowLayout(this);
		// The detail container view will be present only in the large-screen
		// layouts (res/values-sw600dp). If this view is present, then the
		// activity should be in two-pane mode.
		if (findViewById(R.id.stone_detail_container) != null) {
			mTabletMode = true;
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((OrderListFragment) getSupportFragmentManager().findFragmentById(
					R.id.stone_list)).setActivateOnItemClick(true);
		}
		
		//Debug only
		//printDPI();
	}

	private void printDPI() {
		switch (getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
		    Log.d("DEBUG", "Running on LDPI");
		    break;
		case DisplayMetrics.DENSITY_MEDIUM:
			 Log.d("DEBUG", "Running on MDPI");
		    break;
		case DisplayMetrics.DENSITY_HIGH:
			 Log.d("DEBUG", "Running on HDPI");
		    break;
		case DisplayMetrics.DENSITY_XHIGH:
			 Log.d("DEBUG", "Running on XHDPI");
		    break;
		}
	}

	/**
	 * Callback method from {@link OrderListFragment.Callbacks} indicating that
	 * the order with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int orderId) {
		// On tablets, show the detail view in this activity by adding or
		// replacing the detail fragment
		// TODO: Explore the possibility of saving the created fragment/activity
		// instead of creating a new each time the user chooses a stone
		if (mTabletMode) {
			Bundle arguments = new Bundle();
			arguments.putInt(OrderDetailFragment.ORDER_ID, orderId);
			OrderDetailFragment fragment = new OrderDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.stone_detail_container, fragment).commit();
		}
		// On handsets, start the detail activity for the selected item ID.
		else {
			Intent detailIntent = new Intent(this, OrderDetailActivity.class);
			detailIntent.putExtra(OrderDetailFragment.ORDER_ID, orderId);
			startActivity(detailIntent);
		}
	}
}
