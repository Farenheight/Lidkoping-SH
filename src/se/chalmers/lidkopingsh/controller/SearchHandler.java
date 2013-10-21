package se.chalmers.lidkopingsh.controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Filter.FilterListener;

public class SearchHandler implements TextWatcher {

	private EditText mSearchField;
	private OrderAdapter mOrderAdapter;
	private CharSequence mCurrentSearchTerm;

	/**
	 * A class that handles the search feature
	 * 
	 * @param searchField
	 *            The field that the users utilize for performing searches
	 * @param listView
	 *            The list view with an ArrayAdapter containing the items to
	 *            filter with a search
	 * 
	 * @author Simon Bengtsson
	 */
	public SearchHandler(EditText searchField, OrderAdapter orderAdapter) {
		mSearchField = searchField;
		mOrderAdapter = orderAdapter;
		mSearchField.addTextChangedListener(this);
	}

	public void restoreSearch(CharSequence searchTerm) {
		//mSearchField.setText(searchTerm);
	}

	/**
	 * Filters the orders in the order list with it's saved saved search term
	 */
	public void search(FilterListener filterListener) {
		mOrderAdapter.getFilter().filter(mCurrentSearchTerm);
		mOrderAdapter.getFilter().filter(mCurrentSearchTerm, filterListener);
		Log.d("SearchHandler", "Searched");
	}

	public CharSequence getCurrentSearchTerm() {
		return mSearchField.getText();
	}

	/* Text Watcher */

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	/**
	 * Saves the current search term and notify listeners of the orderAdapter
	 * that it has changed which is supposed to call search()
	 */
	@Override
	public void onTextChanged(CharSequence currentText, int start, int before,
			int count) {
		mCurrentSearchTerm = currentText;
		mOrderAdapter.notifyDataSetChanged();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
