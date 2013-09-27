package com.example.lidkopingsh;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lidkopingsh.model.ModelHandler;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
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
	public static final String ORDER_ID = "item_id";

	/**
	 * The Order that this fragment is presenting.
	 */
	private Order mOrder;

	/** Attacher to imageView to zoom lib **/
	private PhotoViewAttacher mAttacher;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ORDER_ID)) {
			int orderID = getArguments().getInt(ORDER_ID);
			mOrder = ModelHandler.getModel(this.getActivity()).getOrderById(orderID);
		}
	}
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.tabs_layout, container, false);

		TabHost tabHost = (TabHost) rootView.findViewById(R.id.orderTabHost);
		tabHost.setup();

		// Add drawing tab
		TabHost.TabSpec drawingTab = tabHost.newTabSpec("drawingTab");
		drawingTab.setContent(R.id.tabDrawingContainer);
		drawingTab.setIndicator("Ritning");
		tabHost.addTab(drawingTab);

		// Add detail tab
		TabHost.TabSpec detailTab = tabHost.newTabSpec("detailTab");
		detailTab.setContent(R.id.tabDetailContainer);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);

		if (mOrder != null) {
			initOrderDrawing(rootView);
			initInfo(rootView);
			initTasks(inflater, container, rootView);
		}

		return rootView;
	}

	/**
	 * Make image zoomable by attaching PhotoView lib
	 */
	private void initOrderDrawing(View rootView) {
		ImageView orderDrawing = (ImageView) rootView
				.findViewById(R.id.orderDrawing);

		// Set the Drawable displayed
		Drawable bitmap = getResources().getDrawable(
				R.drawable.test_headstone_drawing);

		// Attach a PhotoViewAttacher, which takes care of all of the zooming
		// functionality.
		mAttacher = new PhotoViewAttacher(orderDrawing);
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
		// TODO: Vas, sockel etc tasks.
		List<Task> tasks = mOrder.getProducts().get(0).getTasks();
		for (int i = 0; i < mOrder.getProducts().get(0).getTasks().size(); i++) {
			final Task task = tasks.get(i);
			ToggleButton btn = (ToggleButton) inflater.inflate(
					R.layout.task_toggler, container, false);
			btn.setChecked(task.getStatus() == Status.DONE);
			btn.setText(task.getStation().getName());
			btn.setTextOff(task.getStation().getName());
			btn.setTextOn(task.getStation().getName());
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
				.getCemetary());

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
