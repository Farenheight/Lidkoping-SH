package se.chalmers.lidkopingsh;

import java.util.ArrayList;

import se.chalmers.lidkopingsh.handler.Accessor;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.server.NetworkStatusListener;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * {@link OrderSelectedcks} interface.
 * 
 * @author Simon Bengtsson
 * 
 */
public class OrderListFragment extends ListFragment implements
		NetworkStatusListener {

	/* Bundle keys representing the activated item position. */
	private static final String ACTIVATED_ORDER_ID = "activated_position";
	private final String SEARCH_TERM = "search_term_key";
	private final String CURRENT_STATION_POS = "current_station_pos_key";

	/** The fragment's callback object, which is notified of list item clicks. */
	private OrderSelectedCallbacks mOrderSelectedCallbacks;

	/** Adapter responsible for the main order list view */
	private OrderAdapter mOrderAdapter;

	/** The current activated item. Only used on tablets. */
	private Order mActivatedOrder;

	private SearchHandler mSearchHandler;
	private SortHandler mSortHandler;
	private DataSetObserver orderListObserver;
	private ArrayAdapter<Station> mStationsAdapter;

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
		if (!(activity instanceof OrderSelectedCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mOrderSelectedCallbacks = (OrderSelectedCallbacks) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Accessor.getServerConnector(getActivity()).addNetworkListener(this);
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.list_root_inner, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getListView().addHeaderView(
				LayoutInflater.from(getActivity()).inflate(
						R.layout.list_header, null));

		// Setup list view and it's adapter
		mOrderAdapter = new OrderAdapter(getActivity(), new ArrayList<Order>(
				Accessor.getModel(getActivity()).getOrders()));
		setListAdapter(mOrderAdapter);

		initStationSpinner();

		// Restore the previously serialized state on screen orientation change
		if (savedInstanceState != null) {
			mSearchHandler.restoreSearch(savedInstanceState
					.getCharSequence(SEARCH_TERM));
			mSortHandler.restoreSort(savedInstanceState
					.getInt(CURRENT_STATION_POS));

			if (savedInstanceState.containsKey(ACTIVATED_ORDER_ID)) {
				mActivatedOrder = Accessor.getModel(getActivity())
						.getOrderById(
								savedInstanceState.getInt(ACTIVATED_ORDER_ID));
			}
		}
	}

	public void initOrderAdapter() {
		mOrderAdapter = new OrderAdapter(getActivity(), new ArrayList<Order>(
				Accessor.getModel(getActivity()).getOrders()));
		setListAdapter(mOrderAdapter);
		orderListObserver = new OrderListObserver();
		mOrderAdapter.registerDataSetObserver(orderListObserver);
		Log.i("OrderListFragment", "New Order Adapter created");
	}

	private void initStationSpinner() {
		// Sets up the station spinner and it's adapter
		Spinner stationSpinner = (Spinner) getView().findViewById(
				R.id.station_spinner);
		
		initStationAdapter();
		stationSpinner.setAdapter(mStationsAdapter);
		// Setup helper handlers to features in this fragment
		mSortHandler = new SortHandler(stationSpinner, mOrderAdapter);
		mSearchHandler = new SearchHandler((EditText) getView().findViewById(
				R.id.search_field), mOrderAdapter);
	}
	
	private void initStationAdapter(){
		mStationsAdapter = new ArrayAdapter<Station>(getActivity(),
				R.layout.spinner_white_text, (ArrayList<Station>) Accessor
						.getModel(getActivity()).getStations());
		mStationsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public void onDestroy() {
		mOrderAdapter.unregisterDataSetObserver(orderListObserver);
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mActivatedOrder = Accessor.getModel(getActivity()).getOrderById(
				mOrderAdapter.getItem(position - 1).getId());
		mOrderSelectedCallbacks.onItemSelected(mActivatedOrder.getId());
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
				mSortHandler.getCurrentStationPos());
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
	public interface OrderSelectedCallbacks {

		/**
		 * Callback for when an order has been selected in the list.
		 * 
		 * @param orderId
		 *            The id of the order that was clicked.
		 */
		public void onItemSelected(int orderId);
	}

	@Override
	public void startedUpdate() {
	}

	@Override
	public void finishedUpdate() {
		initOrderAdapter();
		mOrderAdapter.notifyDataSetChanged();
		mOrderAdapter.refreshSort();
		Log.d("OrderListFragment",
				"Orders in OrderAdapter: " + mOrderAdapter.getCount());
		mStationsAdapter.notifyDataSetChanged();
		initStationSpinner();
	}

	@Override
	public void networkProblem(String message) {
	}

	@Override
	public void authenticationFailed() {
	}
}
