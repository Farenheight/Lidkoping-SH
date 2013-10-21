package se.chalmers.lidkopingsh.controller;

import java.util.ArrayList;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
	private Context mContext;
	private ArrayAdapter<Station> mStationsAdapter;

	public SortHandler(Spinner mStationSpinner, OrderAdapter orderAdapter,
			Context context) {
		this.mStationSpinner = mStationSpinner;
		this.mStationSpinner.setOnItemSelectedListener(this);
		this.mOrderAdapter = orderAdapter;
		this.mContext = context;
		initStationSpinner();
	}

	/**
	 * Inits the spinner with the stations the user can sort the orders by.
	 */
	private void initStationSpinner() {
		if (mContext != null) {
			mStationsAdapter = new ArrayAdapter<Station>(mContext,
					R.layout.spinner_white_text, (ArrayList<Station>) Accessor
							.getModel(mContext).getStations());
			mStationsAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}
		mStationSpinner.setAdapter(mStationsAdapter);
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

	/**
	 * Refresh the stations in the station spinner and resorts the order list
	 */
	public void refresh() {
		mStationsAdapter.clear();
		mStationsAdapter.addAll(Accessor.getModel(mContext).getStations());
		sort();
	}

	// When the user chooses an item in the stations spinner, set the current
	// station and notify listeners that the data has changed which sorts
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		mCurrentStation = (Station) parent.getItemAtPosition(pos);
		mOrderAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}
}
