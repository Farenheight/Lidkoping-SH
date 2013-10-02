package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.model.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * A list fragment representing a list of Orders. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link OrderDetailsFragment}.
 * 
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 * 
 * TODO: Is it good code having anonymous listeners for the search and sort
 */
public class OrderListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/** The current activated item position. Only used on tablets. */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/** List containing all orders shown in the main order list. */
	private List<Order> mOrderList;

	/** Adapter responsible for the main order list view */
	private OrderAdapter mOrderAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public OrderListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}

		// Important that the header view gets initialized before the list
		// adapter
		initHeaderView();
		initOrderListViewAdapter();
	}

	/**
	 * Adds the header view to the main order list containing a the station
	 * spinner and the search/filter field
	 */
	private void initHeaderView() {
		getListView().addHeaderView(
				LayoutInflater.from(getActivity()).inflate(
						R.layout.custom_orderlist_header, null));
		initStationSpinner();
		initSearch();
	}

	private void initStationSpinner() {

		Spinner spinnerStations = (Spinner) getView().findViewById(
				R.id.spinnerStations);

		// When the user chooses an item in the stations spinner, sort the order
		// list accordingly
		spinnerStations.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				Station station = (Station) parent.getItemAtPosition(pos);
				mOrderAdapter.sort(new StationComparator<Order>(station));
				mOrderAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Sets up the Adapter and gets it data from the {@link ModelHandler}
		ArrayList<Station> stationList = (ArrayList<Station>) ModelHandler
				.getModel(getActivity()).getStations();
		ArrayAdapter<Station> stationsAdapter = new ArrayAdapter<Station>(
				getActivity(), android.R.layout.simple_spinner_item,
				stationList);
		stationsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerStations.setAdapter(stationsAdapter);
	}

	private void initSearch() {
		EditText fieldSearch = (EditText) getView().findViewById(
				R.id.fieldFilter);

		// When the user enters text in the search field, filter out relevant
		// orders
		fieldSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence currentText, int start,
					int before, int count) {
				mOrderAdapter.getFilter().filter(currentText);
			}

			@Override
			public void beforeTextChanged(CharSequence currentText, int start,
					int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * Sets up the adapter responsible for keeping the list view's data
	 */
	private void initOrderListViewAdapter() {
		mOrderList = new ArrayList<Order>(ModelHandler.getModel(getActivity())
				.getOrders());
		mOrderAdapter = new OrderAdapter(getActivity(), mOrderList);
		// Important to use this method and not ListView.setAdapter()
		setListAdapter(mOrderAdapter);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(mOrderList.get(position - 1).getId());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {

		/**
		 * Callback for when an order has been selected in the list.
		 * 
		 * @param orderId
		 *            The id of the order that was clicked.
		 */
		public void onItemSelected(int orderId);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int orderId) {
		}
	};
}
