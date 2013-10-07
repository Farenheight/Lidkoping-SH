package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.IModel;
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
import android.view.ViewGroup;
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
 * TODO: Consider splitting into SearchHandler, SortHandler etc...
 * 
 */
public class OrderListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String ACTIVATED_ORDER_ID = "activated_position";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * search term.
	 */
	private final String SEARCH_TERM = "search_term_key";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current station id.
	 */
	private final String CURRENT_STATION_POS = "current_station_pos_key";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks. Initialized
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/** List containing all orders shown in the main order list. */
	private List<Order> mOrderList;

	/** Adapter responsible for the main order list view */
	private OrderAdapter mOrderAdapter;

	/** The current search term filtering out orders */
	private CharSequence mSearchTerm;

	/** The current station that is sorting the orders */
	private Station mCurrentStation;

	/** Data singelton keeping all data. */
	private IModel mModel;

	/** The current activated item. Only used on tablets. */
	private Order mActivatedOrder;

	private Spinner mStationSpinner;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mModel = ModelHandler.getModel(getActivity());
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.list_root_inner, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getListView().addHeaderView(
				LayoutInflater.from(getActivity()).inflate(
						R.layout.list_header, null));
		initStationSpinner();
		initSearch();

		mStationSpinner = ((Spinner) getView().findViewById(
				R.id.station_spinner));

		initOrderListViewAdapter();

		// Restore the previously serialized state on screen orientation change
		if (savedInstanceState != null) {
			mSearchTerm = savedInstanceState.getCharSequence(SEARCH_TERM);
			mStationSpinner.setSelection(savedInstanceState
					.getInt(CURRENT_STATION_POS));

			if (savedInstanceState.containsKey(ACTIVATED_ORDER_ID)) {
				mActivatedOrder = mModel.getOrderById(savedInstanceState
						.getInt(ACTIVATED_ORDER_ID));
			}
		}

		// Set active order. Null if no orders have been viewed.
		if (mActivatedOrder != null) {
			getListView().setItemChecked(
					mOrderAdapter.indexOf(mActivatedOrder) + 1, true);
		}

		// Filter TODO: This is only needed on orientation change
		mOrderAdapter.getFilter().filter(mSearchTerm);
		((EditText) getView().findViewById(R.id.search_field))
				.setText(mSearchTerm);

		// Sorts the orders after
		mCurrentStation = (Station) mStationSpinner.getSelectedItem();
		mOrderAdapter.sort(new StationComparator<Order>(mCurrentStation),
				mCurrentStation);
		mOrderAdapter.notifyDataSetChanged();
	}

	private void initStationSpinner() {

		Spinner spinnerStations = (Spinner) getView().findViewById(
				R.id.station_spinner);

		// When the user chooses an item in the stations spinner, sort the order
		// list accordingly
		spinnerStations.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				mCurrentStation = (Station) parent.getItemAtPosition(pos);
				mOrderAdapter.sort(
						new StationComparator<Order>(mCurrentStation),
						mCurrentStation);
				mOrderAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Sets up the station spinner's adapter keeping it's data
		ArrayAdapter<Station> stationsAdapter = new ArrayAdapter<Station>(
				getActivity(), android.R.layout.simple_spinner_item,
				(ArrayList<Station>) mModel.getStations());
		stationsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerStations.setAdapter(stationsAdapter);
	}

	private void initSearch() {
		EditText fieldSearch = (EditText) getView().findViewById(
				R.id.search_field);

		// When the user enters text in the search field, filter out relevant
		// orders
		fieldSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence currentText, int start,
					int before, int count) {
				mSearchTerm = currentText;
				mOrderAdapter.getFilter().filter(mSearchTerm);
				if (mActivatedOrder != null) {
					getListView().setItemChecked(
							mOrderAdapter.indexOf(mActivatedOrder) + 1, true);
				}
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
	 * Sets up the adapter responsible for keeping the order list view's data
	 */
	private void initOrderListViewAdapter() {
		mOrderList = new ArrayList<Order>(ModelHandler.getModel(getActivity())
				.getOrders());
		mOrderAdapter = new OrderAdapter(getActivity(), mOrderList);
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
		mActivatedOrder = mModel.getOrderById(mOrderAdapter.getItem(
				position - 1).getId());
		mCallbacks.onItemSelected(mActivatedOrder.getId());
	}

	// Serialize and persist the activated item position, current search
	// term and current choosen station
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mActivatedOrder != null) {
			outState.putInt(ACTIVATED_ORDER_ID, mActivatedOrder.getId());
		}

		outState.putCharSequence(SEARCH_TERM, mSearchTerm);

		outState.putInt(CURRENT_STATION_POS,
				mStationSpinner.getSelectedItemPosition());
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched. Set to true on tablets.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
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
