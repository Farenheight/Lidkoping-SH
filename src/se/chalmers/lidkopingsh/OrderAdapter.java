package se.chalmers.lidkopingsh;

import java.util.List;

import se.chalmers.lidkopingsh.model.ModelFilter;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrderAdapter extends FilterableAdapter<Order, String> {

	private int mResourceId;
	private int mTextResourceId;
	private List<Order> mOrderList;
	private LayoutInflater mInflater;

	public OrderAdapter(Context context, int resourceId, int textResourceId,
			List<Order> orderList) {
		super(context, resourceId, textResourceId, orderList);
		this.mResourceId = resourceId;
		this.mTextResourceId = textResourceId;
		this.mOrderList = orderList;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TextView textView;

		if (convertView == null) {
			if (mResourceId == 0) {
				throw new IllegalStateException(
						"No view specified for this Adapter. Construct with resource ID, or override getView()");
			}
			view = mInflater.inflate(mResourceId, parent, false);

		} else {
			view = convertView;
		}

		try {
			if (mTextResourceId == 0) {
				textView = (TextView) view;
			} else {
				textView = (TextView) view.findViewById(mTextResourceId);
			}

		} catch (ClassCastException e) {
			throw new IllegalStateException(
					"This Adapter needs a text view. Pass proper resource IDs on construction, or override getView()");
		}

		Order item = getItem(position);
		textView.setText(item.getIdName());

		return view;
	}

	@Override
	protected String prepareFilter(CharSequence seq) {
		return seq.toString();
	}

	@Override
	protected boolean passesFilter(Order order, String constraint) {
		ModelFilter modelFilter = new ModelFilter();
		return modelFilter.passesFilter(order, constraint);
	}

}
