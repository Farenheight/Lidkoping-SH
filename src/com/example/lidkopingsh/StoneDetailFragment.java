package com.example.lidkopingsh;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
			int orderPos = getArguments().getInt(ARG_ITEM_ID);
			//mOrder = ModelHandler.getModel().getOrderById();		// TODO: Get order id.
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stone_detail,
				container, false);
		if (mOrder != null) {
			initInfo(rootView);
			initTasks(inflater, container, rootView);
		}

		return rootView;
	}

	/**
	 * Adds tasks to rootView. TODO: Will need refactoring when products is in
	 * list instead of orders
	 * 
	 * @param inflater
	 * @param container
	 * @param rootView
	 */
	private void initTasks(LayoutInflater inflater, ViewGroup container,
			View rootView) {
		// TODO: Only uses the tasks from the first product...
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
					if (isChecked) {
						task.setStatus(Status.DONE);
					} else {
						task.setStatus(Status.NOT_DONE);
					}
				}
			});
			LinearLayout layout = (LinearLayout) rootView
					.findViewById(R.id.task_container);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.5f);
			layout.addView(btn, params);
		}

	}

	/**
	 * Adds info such as customer name, order number etc to rootView
	 * 
	 * @param inflater
	 * @param container
	 * @param rootView
	 */
	private void initInfo(View rootView) {

		((TextView) rootView.findViewById(R.id.stone_name))
				.setText("Order nummer: " + mOrder.getOrderNumber());
		((TextView) rootView.findViewById(R.id.stone_desc))
				.setText("Kund namn: " + mOrder.getCustomer().getName());
	}
}
