package se.chalmers.lidkopingsh;

import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.model.ModelFilter;
import se.chalmers.lidkopingsh.model.ModelHandler;
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
	private Context context;

	public TasksAdapter(Context context, int resource, int textViewResourceId,
			List<Order> orders) {
		super(context, resource, textViewResourceId, orders);
		this.mViewResource = resource;
		this.mFieldId = textViewResourceId;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.orders = orders;
		this.context = context;
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

			if (constraint.toString().equals("")) {
				Log.d("DEBUG", "Constraint is empty string");
			} else {
				Log.d("DEBUG", "Constraint: " + constraint);
			}

			// Resets list TODO:reset to moment specific list, not all orders
			clear();
			addAll(ModelHandler.getModel(context).getOrders());

			List<Order> newOrderList = (new ModelFilter()).getOrdersByFilter(
					constraint, orders);

			FilterResults results = new FilterResults();
			results.values = newOrderList;
			results.count = newOrderList.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			if (results.values != null) {
				clear();
				addAll((Collection<Order>) results.values);
				notifyDataSetChanged();
			} else {
				Log.e("DEBUG", "Filter result is null of some reason");
				Log.d("DEBUG", "Trying to filter again.");
				filter(constraint);
			}
		}
	}

}
