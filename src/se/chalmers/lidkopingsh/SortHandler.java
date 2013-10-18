package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

/**
 * Class handling the sort feature
 * 
 * @author Simon Bengtsson
 * 
 */
public class SortHandler implements OnItemSelectedListener {

	private Spinner mStationSpinner;
	private OrderAdapter mOrderAdapter;
	private Station mCurrentStation;

	public SortHandler(Spinner mStationSpinner, OrderAdapter orderAdapter) {
		this.mStationSpinner = mStationSpinner;
		this.mStationSpinner.setOnItemSelectedListener(this);
		this.mOrderAdapter = orderAdapter;
	}

	public void restoreSort(int selectionIndex) {
		mStationSpinner.setSelection(selectionIndex);
		mCurrentStation = (Station) mStationSpinner.getSelectedItem();
		sort();
	}

	public void sort() {
		mOrderAdapter.sort(new StationComparator<Order>(mCurrentStation),
				mCurrentStation);
	}

	/**
	 * @return The position of the current selected station in this sort
	 *         handler's station spinner.
	 */
	public int getCurrentStationPos() {
		return mStationSpinner.getSelectedItemPosition();
	}

	// When the user chooses an item in the stations spinner, sort the order
	// list accordingly
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		mCurrentStation = (Station) parent.getItemAtPosition(pos);
		mOrderAdapter.sort(new StationComparator<Order>(mCurrentStation),
				mCurrentStation);
		mOrderAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

}
