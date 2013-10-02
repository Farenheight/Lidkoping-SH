package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	/**
	 * Lock used to modify the content of {@link #mObjects}. Any write operation
	 * performed on the array should be synchronized on this lock. This lock is
	 * also used by the filter (see {@link #getFilter()} to make a synchronized
	 * copy of the original array of data.
	 */
	private final Object mLock = new Object();

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
		View view;

		// If recycled view not obtained from android, inflate a new one
		if (convertView == null) {
			view = mInflater.inflate(R.layout.custom_list_item, parent, false);
		} else {
			view = convertView;
		}

		Order order = getItem(position);
		TextView text;

		// Id name
		text = (TextView) view.findViewById(R.id.id_name);
		text.setText(order.getIdName());

		// Customer name TODO: Change to deceased's name if available later
		text = (TextView) view.findViewById(R.id.deceased_name);
		text.setText(order.getCustomer().getName());

		// Other details
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(order.getOrderDate());
		String date = cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ cal.get(Calendar.MONTH);
		text = (TextView) view.findViewById(R.id.other_details);
		text.setText(date + " - " + order.getCemetary());

		return view;
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
	 * An array filter constrains the content of the array adapter with a
	 * prefix. Each item that does not start with the supplied prefix is removed
	 * from the list.
	 */
	private class ArrayFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<Order>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<Order> list;
				synchronized (mLock) {
					list = new ArrayList<Order>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				ArrayList<Order> values;
				synchronized (mLock) {
					values = new ArrayList<Order>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<Order> newValues = new ArrayList<Order>();

				for (int i = 0; i < count; i++) {
					final Order value = values.get(i);
					final String valueText = value.toString().toLowerCase();

					// First match against the whole, non-splitted value
					if (valueText.startsWith(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with
						// space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
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
