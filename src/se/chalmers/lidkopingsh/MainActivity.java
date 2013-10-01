package se.chalmers.lidkopingsh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * An activity containing only a {@link OrderListFragment} on handsets and also
 * a {@link OrderDetailsFragment} on tablets.
 * 
 * This activity also implements the required
 * {@link OrderListFragment.Callbacks} interface to listen for item selections.
 * 
 */
public class MainActivity extends FragmentActivity implements
		OrderListFragment.Callbacks {

	/** Whether or not the app is running on a tablet sized device */
	private boolean mTabletSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTabletSize = getResources().getBoolean(R.bool.isTablet);
		if (mTabletSize) {
			setContentView(R.layout.tablet_maincontainer);
			((OrderListFragment) getSupportFragmentManager().findFragmentById(
					R.id.order_list)).setActivateOnItemClick(true);
		} else {
			setContentView(R.layout.orderlist_root); 
		}

		printDPI();
	}

	/**
	 * TODO: Remove before release
	 */
	private void printDPI() {
		switch (getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			Log.d("DEBUG", "Running on LDPI");
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			Log.d("DEBUG", "Running on MDPI");
			break;
		case DisplayMetrics.DENSITY_HIGH:
			// ZTE Blade III
			Log.d("DEBUG", "Running on HDPI");
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			// Galaxy Nexus
			Log.d("DEBUG", "Running on XHDPI");
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			// Galaxy s4, htc One, Nexus 5?
			Log.d("DEBUG", "Running on XXHDPI");
			break;
		default:
			// Nexus 7?
			Log.d("DEBUG", "Running on a strange resolution");
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
		if (mTabletSize) {
			Bundle arguments = new Bundle();
			arguments.putInt(OrderDetailsFragment.ORDER_ID, orderId);
			OrderDetailsFragment fragment = new OrderDetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.tablet_hint_container, fragment).commit();
		}
		// On handsets, start the detail activity for the selected item ID
		else {
			Intent detailIntent = new Intent(this, HandsetsDetailsActivity.class);
			detailIntent.putExtra(OrderDetailsFragment.ORDER_ID, orderId);
			startActivity(detailIntent);
		}
	}
}
