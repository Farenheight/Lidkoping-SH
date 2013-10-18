package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.chalmers.lidkopingsh.handler.Accessor;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter implements Filterable {

	/**
	 * Contains the list of objects that represent the data of this
	 * ArrayAdapter. The content of this list is referred to as "the array" in
	 * the documentation.
	 * 
	 * @author Simon Bengtsson
	 */
	private List<Order> mOrders;

	// A copy of the original mOrders list, initialized from and then used
	// instead as soon as the mFilter ArrayFilter is used. mOrders will then
	// only contain the filtered values.
	private ArrayList<Order> mOriginalObjects;
	private ArrayFilter mFilter;
	private ModelFilter mModelFilter;

	private LayoutInflater mInflater;

	private Context mContext;

	private int dividerIndex;

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
		mOrders = orders;
		mContext = context;
		// First time the filter is run, save the original values in a new
		// list and create a new model filter
		mOriginalObjects = new ArrayList<Order>(mOrders);
	}

	/**
	 * Sorts the content of this adapter using the specified comparator.
	 * 
	 * @param comparator
	 *            The comparator used to sort the objects contained in this
	 *            adapter.
	 */
	public void sort(Comparator<? super Order> comparator, Station station) {
		Collections.sort(mOrders, comparator);
		dividerIndex = Accessor.getModel(mContext)
				.getFirstUncompletedIndex(mOrders, station);
		currentSortStation = station;
		notifyDataSetChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCount() {
		return mOrders.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Order getItem(int position) {
		return mOrders.get(position);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * {@inheritDoc}
	 */
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
	 * Populates the {listItemView} with the order at {position}
	 */
	private void initListItemView(View listItemView, int position) {
		Order order = getItem(position);
		TextView tmpTextView;

		// Id name
		tmpTextView = (TextView) listItemView.findViewById(R.id.id_name);
		tmpTextView.setText(order.getIdName());

		// Customer name TODO: Change to deceased's name if available later
		tmpTextView = (TextView) listItemView.findViewById(R.id.deceased_name);
		tmpTextView.setText(order.getDeceased());

		// Other details
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(order.getOrderDate());
		String date = cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ cal.get(Calendar.MONTH);
		tmpTextView = (TextView) listItemView.findViewById(R.id.other_details);
		// TODO: Implement percentage done in model
		tmpTextView.setText(date + " - " + order.getCemetary() + " - "
				+ order.getProgress() + "%");
	}

	/**
	 * {@inheritDoc}
	 */
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	/**
	 * A filter that mostly sends the filtering to be done to a ModelFilter.
	 */
	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (mModelFilter == null) {
				mModelFilter = new ModelFilter();
			}

			List<Order> filteredOrders = mModelFilter.getOrdersByFilter(
					constraint, mOrders, mOriginalObjects);
			results.values = filteredOrders;
			results.count = filteredOrders.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mOrders = (List<Order>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

	}

	public int indexOf(Order order) {
		return mOrders.indexOf(order);
	}
	public void refreshSort(){
		sort(new StationComparator<Order>(currentSortStation), currentSortStation);
	}
}
