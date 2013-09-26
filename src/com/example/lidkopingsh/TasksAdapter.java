package com.example.lidkopingsh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.lidkopingsh.model.Order;

public class TasksAdapter extends ArrayAdapter<Order> {

	/** The view id that is going to be used to layout the list items */
	private int mViewResource;
	/** The Resource id for the text view in each list item */
	private int mFieldId;
	private LayoutInflater mInflater;
	private List<Order> orders;

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
		return new StoneFilter();
	}

	private class StoneFilter extends Filter { 

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				// If no input, everything returned
				results.values = orders;
				results.count = orders.size();
			} else {
				// Filtering logic
				List<Order> newOrderList = new ArrayList<Order>();

				for (Order order : orders) {
					String idName = order.getIdName().toUpperCase();
					String c = constraint.toString().toUpperCase();
					Log.d("DEBUG", "ID name: " + idName + " Constraint: " + c);
					if (idName.startsWith(c))
						newOrderList.add(order);
				}

				results.values = newOrderList;
				results.count = newOrderList.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			if (results.count == 0){
			} else {
				clear();
				addAll((Collection<Order>) results.values);
				notifyDataSetChanged();
			}
		}

	}

}
