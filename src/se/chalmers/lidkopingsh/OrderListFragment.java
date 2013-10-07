package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
 * Activities containing this fragment MUST implement the
 * {@link OrderSelectedCallback} interface.
 * 
 * TODO: Consider splitting into SearchHandler and SortHandler
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
	private OrderSelectedCallback mOrderSelectedCallbacks;

	/** List containing all orders shown in the main order list. */
	private List<Order> mOrderList;

	/** Adapter responsible for the main order list view */
	private OrderAdapter mOrderAdapter;

	/** The current station that is sorting the orders */
	private Station mCurrentStation;

	/** Data singelton keeping all data. */
	private IModel mModel;

	/** The current activated item. Only used on tablets. */
	private Order mActivatedOrder;

	private Spinner mStationSpinner;

	private SearchHandler mSearchHandler;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public OrderListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its
		// orderSelectedcallbacks.
		if (!(activity instanceof OrderSelectedCallback)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mOrderSelectedCallbacks = (OrderSelectedCallback) activity;
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

		mStationSpinner = ((Spinner) getView().findViewById(
				R.id.station_spinner));

		mOrderList = new ArrayList<Order>(ModelHandler.getModel(getActivity())
				.getOrders());
		mOrderAdapter = new OrderAdapter(getActivity(), mOrderList);
		setListAdapter(mOrderAdapter);
		mOrderAdapter.registerDataSetObserver(new OrderListObserver());

		mSearchHandler = new SearchHandler((EditText) getView().findViewById(
				R.id.search_field), getListView());

		// Restore the previously serialized state on screen orientation change
		if (savedInstanceState != null) {
			mSearchHandler.restoreSearch(savedInstanceState
					.getCharSequence(SEARCH_TERM));
			mStationSpinner.setSelection(savedInstanceState
					.getInt(CURRENT_STATION_POS));

			// Mark the current order if one is previously selected
			if (savedInstanceState.containsKey(ACTIVATED_ORDER_ID)) {
				mActivatedOrder = mModel.getOrderById(savedInstanceState
						.getInt(ACTIVATED_ORDER_ID));
			}
		}

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

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mActivatedOrder = mModel.getOrderById(mOrderAdapter.getItem(
				position - 1).getId());
		mOrderSelectedCallbacks.onOrderSelected(mActivatedOrder.getId());
	}

	// Serialize and persist the activated item position, current search
	// term and current choosen station
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mActivatedOrder != null) {
			outState.putInt(ACTIVATED_ORDER_ID, mActivatedOrder.getId());
		}

		outState.putCharSequence(SEARCH_TERM,
				mSearchHandler.getCurrentSearchTerm());

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

	private class OrderListObserver extends DataSetObserver {
		
		/**
		 * When the order list's data has changed, reset the active order. Set
		 * active order.
		 */
		@Override
		public void onChanged() {
			Log.d("DEBUG", "Activated item set");
			// Null if no orders have been viewed.
			if (mActivatedOrder != null) {
				getListView().setItemChecked(
						mOrderAdapter.indexOf(mActivatedOrder) + 1, true);
			}
		}
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface OrderSelectedCallback {

		/**
		 * Callback for when an order has been selected in the list.
		 * 
		 * @param orderId
		 *            The id of the order that was selected.
		 */
		public void onOrderSelected(int orderId);
	}
}
