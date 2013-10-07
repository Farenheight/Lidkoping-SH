package se.chalmers.lidkopingsh;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SearchHandler implements TextWatcher {

	private EditText mSearchField;
	private OrderAdapter mOrderAdapter;

	/**
	 * A class that handles the search feature
	 * 
	 * @param searchField
	 *            The field that the users utilize for performing searches
	 * @param listView
	 *            The list view with an ArrayAdapter containing the items to
	 *            filter with a search
	 */
	public SearchHandler(EditText searchField, OrderAdapter orderAdapter) {
		mSearchField = searchField;
		mOrderAdapter = orderAdapter;
		mSearchField.addTextChangedListener(this);
	}

	public void restoreSearch(CharSequence searchTerm) {
		mSearchField.setText(searchTerm);
	}

	public CharSequence getCurrentSearchTerm() {
		return mSearchField.getText();
	}

	/* Text Watcher */

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence currentText, int start, int before,
			int count) {
		mOrderAdapter.getFilter().filter(currentText);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
