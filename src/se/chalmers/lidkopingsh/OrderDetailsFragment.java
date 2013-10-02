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
import android.util.Log;
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
 * contained in the {@link OrderDetailsActivity} on handsets or to the right in
 * the {@link MainActivity}s two-pane layout if displayed on tablets.
 * 
 * TODO: Implement initDetails and initTasks. Otherwise checked.
 */
public class OrderDetailsFragment extends Fragment {

	/** Used as a key for sending */
	public static final String ORDER_ID = "item_id";

	/** The order displayed by this StoneDetailFragment */
	private Order mOrder;

	/** The root view that contains everything else */
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
	 * Every tab needs an identical task container. However views cannot have
	 * multiple parents and can't be cloned directly. Here that is solved by
	 * running initTasks multiple times and thereby create completely new task
	 * layouts for every tab.
	 * 
	 * TODO: Consider not putting the task container outside of the tabs. It
	 * would make more sense graphically as well.
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
		// initTasks(); //TODO: Comment in when vertical task container is
		// active

		// Add detail tab
		TabHost.TabSpec detailTab = tabHost.newTabSpec("detailTab");
		detailTab.setContent(R.id.flow_layout);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);
		initDetails();
		initTasks();
	}

	private void initTasks() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		if (mOrder.getProducts().size() < 1) {
			Log.e("DEBUG", "Size of product list is: "
					+ mOrder.getProducts().size());
		}
		for (Product p : mOrder.getProducts()) {
			// Create a new view that contains the tasks of a single product
			View productView = (View) inflater.inflate(
					R.layout.orderdetails_infotab_product, null);
			LinearLayout layout = (LinearLayout) rootView
					.findViewById(R.id.task_card);
			// TODO Fix to actual type of Product
			((TextView) productView.findViewById(R.id.idName)).setText("Stone");
			layout.addView(productView);
			for (final Task t : p.getTasks()) {
				View taskView = (View) inflater.inflate(
						R.layout.orderdetails_infotab_task, null);
				((TextView) taskView.findViewById(R.id.idName)).setText(t
						.getStation().getName());
				layout = (LinearLayout) productView
						.findViewById(R.id.flow_layout);
				layout.addView(taskView);
				
				ToggleButton btn = (ToggleButton) taskView
						.findViewById(R.id.idTaskToggleButton);
				btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton toggleButton,
							boolean isChecked) {
						if (isChecked) {

							t.setStatus(Status.DONE);
						} else {
							t.setStatus(Status.NOT_DONE);
						}
					}
				});

			}
		}
	}

	// List<Task> tasks = mOrder.getProducts().get(0).getTasks();
	// for (int i = 0; i < mOrder.getProducts().get(0).getTasks().size(); i++) {
	// LinearLayout layout = (LinearLayout) rootView
	// .findViewById(R.id.task_container);
	// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.5f);
	// layout.addView(btn, params);
	// }

	/**
	 * Make image zoomable by attaching PhotoView lib
	 */
	private void initDrawing() {
		ImageView orderDrawing = (ImageView) rootView
				.findViewById(R.id.orderDrawing);

		// Set the image displayed TODO: get from model
		orderDrawing.setImageDrawable(getResources().getDrawable(
				R.drawable.test_headstone_drawing));

		// Attach a PhotoViewAttacher, which takes care of all of the zooming
		// functionality.
		new PhotoViewAttacher(orderDrawing);
	}

	/**
	 * Adds info such as customer name, order number etc to rootView
	 * 
	 * @param inflater
	 * @param container
	 */
	private void initDetails() {
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
		if (mOrder.getProducts().size() < 1) {
			Log.e("DEBUG", "Size of product list is: "
					+ mOrder.getProducts().size());
		}
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
