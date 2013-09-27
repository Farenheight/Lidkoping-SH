package se.chalmers.lidkopingsh;

import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.model.ModelFilter;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class TasksAdapter extends ArrayAdapter<Order> {

	/** The view id that is going to be used to layout the list items */
	private int mViewResource;
	/** The Resource id for the text view in each list item */
	private int mFieldId;
	private LayoutInflater mInflater;
	private List<Order> orders;
	private Filter mFilter;

	public TasksAdapter(Context context, int resource, int textViewResourceId,
			List<Order> orders) {
		super(context, resource, textViewResourceId, orders);
		this.mViewResource = resource;
		this.mFieldId = textViewResourceId;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.orders = orders;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,
				mViewResource);
	}

	private <T> View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (mFieldId == 0) {
				// If no custom field is assigned, assume the whole resource is
				// a TextView
				text = (TextView) view;
			} else {
				// Otherwise, find the TextView field within the layout
				text = (TextView) view.findViewById(mFieldId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"ArrayAdapter requires the resource ID to be a TextView", e);
		}
		Order clickedOrder = (Order) getItem(position);
		text.setText(clickedOrder.getIdName());

		return view;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new StoneFilter();
		}
		return mFilter;
	}

	private class StoneFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			List<Order> newOrderList = (new ModelFilter()).getOrdersByFilter(
					constraint, orders);
			FilterResults results = new FilterResults();
			results.values = newOrderList;
			results.count = newOrderList.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			clear();
			if (results.values != null) {
				Log.d("DEBUG", "Results: " + results.count);
				addAll((Collection<Order>) results.values);
				Log.d("DEBUG", "Results found, count: " + results.count);
				notifyDataSetChanged();
			} else {
				Log.d("DEBUG", "Results: " + results.values);
			}
		}
	}

}
