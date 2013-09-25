/**
 * 
 */
package com.example.lidkopingsh.tabs;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.lidkopingsh.R;
import com.example.lidkopingsh.StoneDetailFragment;
import com.example.lidkopingsh.model.ModelHandler;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
import com.example.lidkopingsh.model.Task;

/**
 *
 */
public class TabInfoFragment extends Fragment {

	/**
	 * The Order that this fragment is presenting.
	 */
	private Order mOrder;
	
	public TabInfoFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("DEBUG", "getArgs(): " + getArguments());
		if (getArguments() != null) {
			if (getArguments().containsKey(StoneDetailFragment.ORDER_ID)) {
				int orderID = getArguments().getInt(
						StoneDetailFragment.ORDER_ID);
				mOrder = ModelHandler.getModel().getOrderById(orderID);
			}
		}
	} 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = (LinearLayout) inflater.inflate(R.layout.tab_info,
				container, false);
		if (mOrder != null) {
			initInfo(rootView);
			initTasks(inflater, container, rootView);
		}
		return rootView;
	}

	/**
	 * Adds tasks to rootView.
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
		// Header
		((TextView) rootView.findViewById(R.id.idName)).setText(mOrder
				.getIdName());
		((TextView) rootView.findViewById(R.id.orderNumber)).setText(mOrder
				.getOrderNumber());

		// General
		((TextView) rootView.findViewById(R.id.burialName))
				.setText("<Not yet in model>");
		((TextView) rootView.findViewById(R.id.cemeteryBoard))
				.setText("<Not yet in model>");
		((TextView) rootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCementary());

		// Stone
		Stone stone = ((Stone) mOrder.getProducts().get(0));
		((TextView) rootView.findViewById(R.id.stoneModel)).setText(stone
				.getStoneModel());
		((TextView) rootView.findViewById(R.id.materialAndColor)).setText(stone
				.getMaterialColor());
		((TextView) rootView.findViewById(R.id.ornament)).setText(stone
				.getOrnament());
		((TextView) rootView.findViewById(R.id.desc)).setText(stone
				.getDescription());
		((TextView) rootView.findViewById(R.id.frontProcessing)).setText(stone
				.getFrontWork());
		((TextView) rootView.findViewById(R.id.textStyleAndProcessing))
				.setText(stone.getTextStyle());

	}

}
