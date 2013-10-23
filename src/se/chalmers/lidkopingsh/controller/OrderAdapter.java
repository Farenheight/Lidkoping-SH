package se.chalmers.lidkopingsh.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.chalmers.lidkopingsh.model.Filter;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for the orders list
 * 
 * @author Simon Bengtsson
 * 
 */
public class OrderAdapter extends BaseAdapter {

	/**
	 * Contains the current orders in the orders list.
	 */
	private List<Order> mOrders;

	/**
	 * A copy of the original mOrders list. Restored to before re-sort and
	 * re-filter.
	 */
	private Collection<Order> mOriginalOrders;

	private LayoutInflater mInflater;

	/**
	 * The index of the order in the orderlist seperating sorted orders from not
	 * sorted ones
	 */
	private int dividerIndex;

	/**
	 * The last station sorted against
	 */
	private Station currentSortStation;

	/**
	 * 
	 * @param context
	 * @param orders
	 *            The order list this adapter will manage
	 */
	public OrderAdapter(Context context, List<Order> orders) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mOrders = new ArrayList<Order>(orders);
		// First time the filter is run, save the original values in a new
		// list and create a new model filter
		mOriginalOrders = new ArrayList<Order>(orders);
	}

	/**
	 * Sorts the content of this adapter using the specified comparator.
	 * 
	 * @param comparator
	 *            The comparator used to sort the objects contained in this
	 *            adapter.
	 */
	public void sort(Station station) {
		Collections.sort(mOrders, new StationComparator<Order>(station));
		dividerIndex = Accessor.getModel().getFirstUncompletedIndex(mOrders,
				station);
		currentSortStation = station;
	}

	@Override
	public int getCount() {
		return mOrders.size();
	}

	@Override
	public Order getItem(int position) {
		return mOrders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView;
		if (position == 0 && dividerIndex != 0) {
			listItemView = mInflater.inflate(R.layout.list_item_header, parent,
					false);
			((TextView) listItemView.findViewById(R.id.list_item_header_text))
					.setText(currentSortStation.getName());

		} else if (position == dividerIndex) {
			listItemView = mInflater.inflate(R.layout.list_item_header, parent,
					false);
			((TextView) listItemView.findViewById(R.id.list_item_header_text))
					.setText("Passerat " + currentSortStation);
		}
		// If recycled view not obtained from android, inflate a new one
		else if (convertView == null
				|| convertView.findViewById(R.id.list_header_view) != null) {
			listItemView = mInflater.inflate(R.layout.list_item, parent, false);
		} else {
			listItemView = convertView;
		}
		initListItemView(listItemView, position);
		return listItemView;
	}

	/**
	 * Populates the listItemView with the order at position
	 * 
	 * @param listItemView
	 *            The view to display
	 * @param position
	 *            The index of the order to display
	 */
	private void initListItemView(View listItemView, int position) {
		Order order = getItem(position);
		TextView tmpTextView;

		// Id name
		tmpTextView = (TextView) listItemView.findViewById(R.id.id_name);
		tmpTextView.setText(order.getIdName());

		// Deceased name
		tmpTextView = (TextView) listItemView.findViewById(R.id.deceased_name);
		tmpTextView.setText(order.getDeceased());

		// Other details
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(order.getOrderDate());
		String date = cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ cal.get(Calendar.MONTH);
		tmpTextView = (TextView) listItemView.findViewById(R.id.other_details);
		tmpTextView.setText(date + " - " + order.getCemetary() + " - "
				+ order.getProgress() + "%");
	}

	/**
	 * The index of the order.
	 * 
	 * @param order
	 * @return The index of the order.
	 */
	public int indexOf(Order order) {
		return mOrders.indexOf(order);
	}

	/**
	 * Update
	 * 
	 * @param collection
	 */
	public void updateOrders(Collection<Order> collection) {
		mOrders = new ArrayList<Order>(collection);
		mOriginalOrders = new ArrayList<Order>(collection);
	}

	/**
	 * Filter this adapters orders list to contain only the ones which matches
	 * the current constraint.
	 * 
	 * @param constraint
	 *            The constraint to filter the orders by
	 * @param filter
	 *            The filter that to decides which orders will pass
	 */
	public void filter(String constraint, Filter filter) {
		// First reset the orders to a not filtered list
		mOrders = new ArrayList<Order>(mOriginalOrders);

		// Don't filter constraint is null or of length 0
		if (constraint == null || constraint.length() == 0) {
			return;
		}

		// Iterate over the orderlist and remove those not matching the
		// constraint
		else {
			for (Iterator<Order> iterator = mOrders.iterator(); iterator
					.hasNext();) {
				if (!filter.passesFilter(iterator.next(), constraint)) {
					iterator.remove();
				}
			}
		}
	}
}
