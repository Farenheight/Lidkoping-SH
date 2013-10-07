package se.chalmers.lidkopingsh;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

public class SearchHandler implements TextWatcher {

	private EditText mSearchField;
	private ListView mListView;
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
	public SearchHandler(EditText searchField, ListView listView) {
		mSearchField = searchField;
		mListView = listView;

		try {
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) mListView
					.getAdapter();
			mOrderAdapter = (OrderAdapter) hvla.getWrappedAdapter();
		} catch (ClassCastException e) {
			Log.e("DEBUG",
					"The provided listView has to contain an OrderAdapter "
							+ "wrapped in a HeaderViewListAdapter");
			e.printStackTrace();
		}

		mSearchField.addTextChangedListener(this);
	}

	public void restoreSearch(CharSequence searchTerm) {
		mSearchField.setText(searchTerm);
	}

	private void search(CharSequence searchTerm) {
		mOrderAdapter.getFilter().filter(searchTerm);
		// if (mActivatedOrder != null) {
		// mListView.setItemChecked(
		// mOrderAdapter.indexOf(mActivatedOrder) + 1, true);
		// }
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
		search(currentText);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
