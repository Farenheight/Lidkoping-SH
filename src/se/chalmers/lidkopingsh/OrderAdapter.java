package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.chalmers.lidkopingsh.model.IModelFilter;
import se.chalmers.lidkopingsh.model.ModelFilter;
import se.chalmers.lidkopingsh.model.Order;
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
	 */
	private List<Order> mObjects;

	// A copy of the original mObjects array, initialized from and then used
	// instead as soon as
	// the mFilter ArrayFilter is used. mObjects will then only contain the
	// filtered values.
	private ArrayList<Order> mOriginalValues;
	private ArrayFilter mFilter;

	private LayoutInflater mInflater;

	private Context mContext;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 * @param resource
	 *            The resource ID for a layout file containing a TextView to use
	 *            when instantiating views.
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public OrderAdapter(Context context, List<Order> objects) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mObjects = objects;
	}

	/**
	 * Sorts the content of this adapter using the specified comparator.
	 * 
	 * @param comparator
	 *            The comparator used to sort the objects contained in this
	 *            adapter.
	 */
	public void sort(Comparator<? super Order> comparator) {
		if (mOriginalValues != null) {
			Collections.sort(mOriginalValues, comparator);
		} else {
			Collections.sort(mObjects, comparator);
		}
	}

	/**
	 * Returns the context associated with this array adapter. The context is
	 * used to create views from the resource passed to the constructor.
	 * 
	 * @return The Context associated with this adapter.
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCount() {
		return mObjects.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Order getItem(int position) {
		return mObjects.get(position);
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
		return createViewFromResource(position, convertView, parent);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent) {
		View listItemView;

		// If recycled view not obtained from android, inflate a new one
		if (convertView == null) {
			listItemView = mInflater.inflate(R.layout.custom_list_item, parent,
					false);
		} else {
			listItemView = convertView;
		}

		Order order = getItem(position);
		TextView tmpTextView;

		// Id name
		tmpTextView = (TextView) listItemView.findViewById(R.id.id_name);
		tmpTextView.setText(order.getIdName());

		// Customer name TODO: Change to deceased's name if available later
		tmpTextView = (TextView) listItemView.findViewById(R.id.deceased_name);
		tmpTextView.setText(order.getCustomer().getName());

		// Other details
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(order.getOrderDate());
		String date = cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ cal.get(Calendar.MONTH);
		tmpTextView = (TextView) listItemView.findViewById(R.id.other_details);
		tmpTextView.setText(date + " - " + order.getCemetary());

		return listItemView;
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
		private ModelFilter mModelFilter;

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<Order>(mObjects);
			}

			if (constraint == null || constraint.length() == 0) {
				ArrayList<Order> list;
				list = new ArrayList<Order>(mOriginalValues);
				results.values = list;
				results.count = list.size();
			} else {
				String lcConstraint = constraint.toString().toLowerCase();
				ArrayList<Order> orderList = new ArrayList<Order>(mOriginalValues);

				final ArrayList<Order> newValues = new ArrayList<Order>();
				
				if(mModelFilter == null) {
					mModelFilter = new ModelFilter();
				}

				for (Order order : orderList) {					
					if(mModelFilter.passesFilter(order, constraint.toString())) {
						newValues.add(order);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked
			mObjects = (List<Order>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
