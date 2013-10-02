package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.model.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in the {@link HandsetsDetailsActivity} on handsets or to the right
 * in the {@link MainActivity}s two-pane layout if displayed on tablets.
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
		rootView = inflater.inflate(R.layout.od_root, container, false);

		// Consider include this again if bugs appear
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
		detailTab.setContent(R.id.tab_info_container);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);
		initDetails();
		initTasks();
	}

	/**
	 * TODO: Rewrite
	 */
	private void initTasks() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout rootTaskCont = (LinearLayout) rootView
				.findViewById(R.id.root_task_cont);
		for (Product p : mOrder.getProducts()) {
			// Create a new view that contains the tasks of a single product
			ViewGroup productView = (ViewGroup) inflater.inflate(
					R.layout.od_product_task_cont, null);
			// TODO Fix to actual type of Product
			((TextView) productView.findViewById(R.id.task_name))
					.setText("Stone");
			for (final Task task : p.getTasks()) {
				View taskView = inflater.inflate(R.layout.od_task_cont, null);
				
				//Set task name
				((TextView) taskView.findViewById(R.id.task_name)).setText(task
						.getStation().getName());
				
				//Set the task toggler
				ToggleButton btn = (ToggleButton) taskView
						.findViewById(R.id.task_toggler);
				btn.setChecked(task.getStatus() == Status.DONE);
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
				productView.addView(taskView);
			}
			rootTaskCont.addView(productView);
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
		((TextView) rootView.findViewById(R.id.task_name)).setText(mOrder
				.getIdName());
		((TextView) rootView.findViewById(R.id.orderNumber)).setText(mOrder
				.getOrderNumber());

		// General
		((TextView) rootView.findViewById(R.id.cemetery_number)).setText(mOrder
				.getCemetaryNumber());
		((TextView) rootView.findViewById(R.id.cemeteryBoard)).setText(mOrder
				.getCemetaryBoard());
		((TextView) rootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCemetary());

		// TODO: Check if the first product really is a stone...
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
