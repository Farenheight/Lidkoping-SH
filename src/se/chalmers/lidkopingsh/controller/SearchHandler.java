package se.chalmers.lidkopingsh.controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SearchHandler implements TextWatcher {

	private EditText mSearchField;
	private ListDataWatcher mListDataWatcher;

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
	public SearchHandler(EditText searchField, ListDataWatcher listDataWatcher) {
		mSearchField = searchField;
		mListDataWatcher = listDataWatcher;
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

	/**
	 * Saves the current search term and notify listeners of the orderAdapter
	 * that it has changed which is supposed to call search()
	 */
	@Override
	public void onTextChanged(CharSequence currentText, int start, int before,
			int count) {
		mListDataWatcher.listDataChanged();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
