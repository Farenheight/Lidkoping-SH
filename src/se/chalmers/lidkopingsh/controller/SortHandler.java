package se.chalmers.lidkopingsh.controller;

import java.util.ArrayList;

import se.chalmers.lidkopingsh.model.Station;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Class handling the sort feature
 * 
 * @author Simon Bengtsson
 * 
 */
public class SortHandler implements OnItemSelectedListener {

	private Spinner mStationSpinner;
	private ArrayAdapter<Station> mStationsAdapter;
	private ListDataWatcher mListDataWatcher;

	public SortHandler(Spinner mStationSpinner,
			Context context, ListDataWatcher listDataWatcher) {
		this.mStationSpinner = mStationSpinner;
		this.mStationSpinner.setOnItemSelectedListener(this);
		this.mListDataWatcher = listDataWatcher;
		initStationSpinner(context);
	}

	/**
	 * Inits the spinner with the stations the user can sort the orders by.
	 */
	private void initStationSpinner(Context context) {
			mStationsAdapter = new ArrayAdapter<Station>(context,
					R.layout.spinner_white_text, (ArrayList<Station>) Accessor
							.getModel().getStations());
			mStationsAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mStationSpinner.setAdapter(mStationsAdapter);
	}

	public void restoreSort(int selectionIndex) {
		mStationSpinner.setSelection(selectionIndex);
	}

	/**
	 * @return The position of the current selected station in this sort
	 *         handler's station spinner.
	 */
	public int getCurrentStationPos() {
		return mStationSpinner.getSelectedItemPosition();
	}

	/**
	 * Returns the sorthandlers current selected station
	 * 
	 * @return The current selected station in this sort handler's station
	 *         spinner.
	 */
	public Station getCurrentStation() {
		return (Station) mStationSpinner.getSelectedItem();
	}

	/**
	 * Refresh the stations in the station spinner and resorts the order list
	 */
	public void refresh() {
		mStationsAdapter.clear();
		mStationsAdapter.addAll(Accessor.getModel().getStations());
	}

	// When the user chooses an item in the stations spinner, set the current
	// station and notify listeners that the data has changed which sorts
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		mListDataWatcher.listDataChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}
}
