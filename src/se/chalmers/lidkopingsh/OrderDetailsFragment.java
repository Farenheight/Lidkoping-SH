package se.chalmers.lidkopingsh;

import java.util.List;

import se.chalmers.lidkopingsh.model.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in the {@link HandsetsDetailsActivity} on handsets or to the right in
 * the {@link MainActivity}s two-pane layout if displayed on tablets.
 * 
 */
public class OrderDetailsFragment extends Fragment {

	/** Used as a key when sending the object between activities and fragments */
	public static final String ORDER_ID = "item_id";

	/** The order displayed by this StoneDetailFragment */
	private Order mOrder;

	/** The root view that contains everything */
	private View rootView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public OrderDetailsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the root view for the fragment. The rootView should contain
		// all other static views displayed in the fragment.
		rootView = inflater.inflate(R.layout.orderdetails_root, container,
				false);

		// TODO: Consider include this again if bugs appear
		// if (getArguments().containsKey(ORDER_ID)) {

		// Gets and saves the order matching the orderId passed to the fragment
		// TODO: Consider catching NoElementFoundException.
		mOrder = ModelHandler.getModel(this.getActivity()).getOrderById(
				getArguments().getInt(ORDER_ID));

		// Collects data from mOrder and layouts the views accordingly
		initTabs();

		return rootView;
	}

	/**
	 * Sets up the tab host for this stone detail view with one drawing tab and
	 * one details tab. Data is also collected from mOrder and added to the
	 * tab's views.
	 * 
	 * TODO: Consider putting the task container outside of the tabs. It would
	 * make more sense graphically as well.
	 * 
	 */
	private void initTabs() {
		TabHost tabHost = (TabHost) rootView.findViewById(R.id.orderTabHost);
		tabHost.setup();

		// Add drawing tab
		TabHost.TabSpec drawingTab = tabHost.newTabSpec("drawingTab");
		drawingTab.setContent(R.id.tabDrawingContainer);
		drawingTab.setIndicator("Ritning");
		tabHost.addTab(drawingTab);
		initDrawing();
		// initTasks(); //TODO: Comment in when vertical task container is done

		// Add detail tab
		TabHost.TabSpec detailTab = tabHost.newTabSpec("detailTab");
		detailTab.setContent(R.id.flow_layout);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);
		initDetails();
		// initTasks();
	}

	/**
	 * TODO: Rewrite
	 */
	private void initTasks() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		if (mOrder.getProducts().size() < 1) {
			Log.e("DEBUG", "Size of product list is: "
					+ mOrder.getProducts().size());
		}
		List<Task> tasks = mOrder.getProducts().get(0).getTasks();
		for (int i = 0; i < mOrder.getProducts().get(0).getTasks().size(); i++) {
			final Task task = tasks.get(i);
			ToggleButton btn = (ToggleButton) inflater.inflate(
					R.layout.custom_task_toggler, null);
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
			// LinearLayout layout = (LinearLayout) rootView
			// .findViewById(R.id.task_container);
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.5f);
			// layout.addView(btn, params);
		}

	}

	/**
	 * Attaches the PhotoView library to the order's drawing making it possible
	 * to zoom and pan it smoothly
	 */
	private void initDrawing() {
		ImageView orderDrawing = (ImageView) rootView
				.findViewById(R.id.orderDrawing);

		// Set the image displayed TODO: get from model
		orderDrawing.setImageDrawable(getResources().getDrawable(
				R.drawable.test_headstone_drawing));

		// Attaches the library
		PhotoViewAttacher pva = new PhotoViewAttacher(orderDrawing);
		pva.setMaximumScale(8f);
	}
	

	/**
	 * Adds info such as customer name, order number etc to rootView
	 * 
	 */
	private void initDetails() {
		// Header
		((TextView) rootView.findViewById(R.id.idName)).setText(mOrder
				.getIdName());
		((TextView) rootView.findViewById(R.id.orderNumber)).setText(mOrder
				.getOrderNumber());

		// General
		((TextView) rootView.findViewById(R.id.cemetery_number))
				.setText(mOrder.getCemetaryNumber());
		((TextView) rootView.findViewById(R.id.cemeteryBoard))
				.setText(mOrder.getCemetaryBoard());
		((TextView) rootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCemetary());

		// Stone TODO: Check if the first product really is a stone...
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
