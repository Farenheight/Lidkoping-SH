package se.chalmers.lidkopingsh.controller;

import java.util.ArrayList;

import se.chalmers.lidkopingsh.model.IdNameFilter;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.server.NetworkStatusListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		NetworkStatusListener, ListDataWatcher {

	/* Bundle keys for remembering state on screen orientation change etc */
	private static final String ACTIVATED_ORDER_ID = "activated_position";
	private final String SEARCH_TERM = "search_term_key";
	private final String CURRENT_STATION_POS = "current_station_pos_key";

	/** The fragment's callback object, which is notified of list item clicks. */
	private OrderSelectedCallbacks mOrderSelectedCallbacks;

	/** Adapter responsible for the main order list view */
	private OrderAdapter mOrderAdapter;

	/** The current activated item. Only used on tablets. */
	private Order mActivatedOrder;

	/** Handler for the search feature */
	private SearchHandler mSearchHandler;

	/** Handler for the sort feature */
	private SortHandler mSortHandler;

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
		Accessor.getServerConnector().addNetworkListener(this);
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.list_root_inner, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		// Adds the header view to the list view
		getListView().addHeaderView(
				LayoutInflater.from(getActivity()).inflate(
						R.layout.list_header, null));

		// Setup list view and it's adapter
		mOrderAdapter = new OrderAdapter(getActivity(), new ArrayList<Order>(
				Accessor.getModel().getOrders()));
		setListAdapter(mOrderAdapter);

		// Setup handlers for features in this fragment
		mSortHandler = new SortHandler((Spinner) getView().findViewById(
				R.id.station_spinner), getActivity(), this);
		mSearchHandler = new SearchHandler((EditText) getView().findViewById(
				R.id.search_field), this);

		// Restore the previously serialized state on screen orientation change
		if (savedInstanceState != null) {
			mSearchHandler.restoreSearch(savedInstanceState
					.getCharSequence(SEARCH_TERM));
			mSortHandler.restoreSort(savedInstanceState
					.getInt(CURRENT_STATION_POS));

			// Doesn't contain key if no order has been selected yet
			if (savedInstanceState.containsKey(ACTIVATED_ORDER_ID)) {
				mActivatedOrder = Accessor.getModel().getOrderById(
						savedInstanceState.getInt(ACTIVATED_ORDER_ID));
			}
		}
	}

	@Override
	public void onDestroy() {
		Accessor.getServerConnector().removeNetworkStatusListener(this);
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mActivatedOrder = Accessor.getModel().getOrderById(
				mOrderAdapter.getItem(position - 1).getId());
		mOrderSelectedCallbacks.onItemSelected(mActivatedOrder.getId());
	}

	// Serialize and persist the activated item position, current search
	// term and current choosen station
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		//
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
		// Update all data, sorting, searching when update finished
		new UpdateOrderListTask().execute((Object) null);
	}

	@Override
	public void networkProblem(String message) {
	}

	@Override
	public void authenticationFailed() {
	}

	// When the order list changed, this will be called
	@Override
	public void listDataChanged() {
		new UpdateOrderListTask().execute((Object) null);
	}

	/**
	 * An task which will update the order list with the new orders
	 * asynchronously when executed.
	 * 
	 * @author Simon Bengtsson
	 * 
	 */
	private class UpdateOrderListTask extends AsyncTask<Object, Object, Void> {

		@Override
		protected Void doInBackground(Object... params) {

			// Updates to new data
			mOrderAdapter.updateOrders(Accessor.getModel().getOrders());

			// 1. Filter it
			mOrderAdapter.filter(mSearchHandler.getCurrentSearchTerm()
					.toString(), new IdNameFilter());

			// No need to do something more if no results from the filtering
			if (mOrderAdapter.getCount() != 0) {
				// 2. Sort the orders (only the filtered ones if filtered)
				mOrderAdapter.sort(mSortHandler.getCurrentStation());

				// 3. Mark the correct order in the list view (if a order has
				// been
				// previously selected)
				if (mActivatedOrder != null) {
					getListView().setItemChecked(
							mOrderAdapter.indexOf(mActivatedOrder) + 1, true);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i("OrderListFragment", "Order list updated. First order is: "
					+ mOrderAdapter.getItem(0).getIdName());
			mOrderAdapter.notifyDataSetChanged();
		}
	}
}
