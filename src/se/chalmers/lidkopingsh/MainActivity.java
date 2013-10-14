package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
	private Order mCurrentOrder;

	/** Whether or not the app is running on a tablet sized device */
	private boolean mTabletSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTabletSize = getResources().getBoolean(R.bool.isTablet);
		mTabletSize = true;
		if (mTabletSize) {
			setContentView(R.layout.tablet_maincontainer);
			((OrderListFragment) getSupportFragmentManager().findFragmentById(
					R.id.order_list)).setActivateOnItemClick(true);
		} else {
			setContentView(R.layout.list_root);
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
		if (mTabletSize) {
			if(mCurrentOrderDetailsFragment != null && mCurrentOrder != null){
				mCurrentOrder.removeSyncOrderListener(mCurrentOrderDetailsFragment);
			}
			Bundle arguments = new Bundle();
			arguments.putInt(OrderDetailsFragment.ORDER_ID, orderId);
			arguments.putBoolean(IS_TABLET_SIZE, mTabletSize);
				
			mCurrentOrder = ModelHandler.getModel(this).getOrderById(orderId);
			mCurrentOrderDetailsFragment = new OrderDetailsFragment();
			mCurrentOrderDetailsFragment.setArguments(arguments);
			
			mCurrentOrder.addSyncOrderListener(mCurrentOrderDetailsFragment);
			
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.tablet_hint_container, mCurrentOrderDetailsFragment).commit();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_main, menu);
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
			ModelHandler.update(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
