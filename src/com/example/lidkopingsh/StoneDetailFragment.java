package com.example.lidkopingsh;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lidkopingsh.model.ModelHandler;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Task;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in a {@link StoneListActivity} in two-pane mode (on tablets) or a
 * {@link StoneDetailActivity} on handsets.
 */
public class StoneDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The Order that this fragment is presenting.
	 */
	private Order mOrder;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			Log.d("DEBUG", "Should have views order: "
					+ getArguments().getString(ARG_ITEM_ID));
			mOrder = ModelHandler.getModel().getOrders().get(0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stone_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mOrder != null) {
			((TextView) rootView.findViewById(R.id.stone_name))
					.setText("Order nummer: " + mOrder.getOrderNumber());
			((TextView) rootView.findViewById(R.id.stone_desc))
					.setText("Kund namn: " + mOrder.getCustomer().getName());

			LinearLayout layout = (LinearLayout) rootView
					.findViewById(R.id.task_container);

			// TODO: This will need refactoring when products is in list instead
			// of orders
			Log.d("DEBUG", "Task list size: " + mOrder.getProducts().get(0).getTasks().size());
			List<Task> tasks = mOrder.getProducts().get(0).getTasks();
			for (int i = 0; i < mOrder.getProducts().get(0).getTasks().size(); i++) {
				final Task task = tasks.get(i);
				ToggleButton btn = (ToggleButton) inflater.inflate(
						R.layout.task_toggler, container, false);
				btn.setChecked(task.getStatus() == Status.DONE);
				btn.setText(task.getName());
				btn.setTextOff(task.getName());
				btn.setTextOn(task.getName());
				btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton toggleButton,
							boolean isChecked) {
						if(isChecked) {
							task.setStatus(Status.DONE);
						} else {
							task.setStatus(Status.NOT_DONE);
						}
					}
				});
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						0.5f);
				layout.addView(btn, params);
			}
		}

		return rootView;
	}
}
