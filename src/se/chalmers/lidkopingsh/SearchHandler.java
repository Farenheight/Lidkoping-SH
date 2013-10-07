package se.chalmers.lidkopingsh;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SearchHandler<T> implements TextWatcher {

	private EditText mSearchField;
	private ListView mListView;
	private ArrayAdapter mListAdapter;

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
		mSearchField.addTextChangedListener(this);
		mListView = listView;
		mListAdapter = (ArrayAdapter) mListView.getAdapter();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence currentText, int start, int before,
			int count) {
		mListAdapter.getFilter().filter(currentText);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
