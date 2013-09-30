package se.chalmers.lidkopingsh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.lidkopingsh.R;

/**
 * An activity containing only a {@link StoneListFragment} on handsets and also
 * a {@link StoneDetailFragment} on tablets.
 * 
 * This activity also implements the required
 * {@link StoneListFragment.Callbacks} interface to listen for item selections.
 * 
 * TODO: Class is checked. Remove this.
 */
public class MainActivity extends FragmentActivity implements
		StoneListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTabletMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stone_list);

		// The detail container view will be present only in the large-screen
		// layouts (res/values-sw600dp). If this view is present, then the
		// activity should be in two-pane mode.
		if (findViewById(R.id.stone_detail_container) != null) {
			mTabletMode = true;
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((StoneListFragment) getSupportFragmentManager().findFragmentById(
					R.id.stone_list)).setActivateOnItemClick(true);
		}
	}

	/**
	 * Callback method from {@link StoneListFragment.Callbacks} indicating that
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
			arguments.putInt(StoneDetailFragment.ORDER_ID, orderId);
			StoneDetailFragment fragment = new StoneDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.stone_detail_container, fragment).commit();
		}
		// On handsets, start the detail activity for the selected item ID.
		else {
			Intent detailIntent = new Intent(this, StoneDetailActivity.class);
			detailIntent.putExtra(StoneDetailFragment.ORDER_ID, orderId);
			startActivity(detailIntent);
		}
	}
}
