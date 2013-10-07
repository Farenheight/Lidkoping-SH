package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import se.chalmers.lidkopingsh.util.Listener;
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
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in the {@link HandsetsDetailsActivity} on handsets or to the right
 * in the {@link MainActivity}s two-pane layout if displayed on tablets.
 * 
 */
public class OrderDetailsFragment extends Fragment implements Listener<Order> {

	/** Used as a key when sending the object between activities and fragments */
	public static final String ORDER_ID = "item_id";

	private final String DRAWING_TAB = "drawing tab";
	private final String DETAIL_TAB = "details tab";

	/** The order displayed by this StoneDetailFragment */
	private Order mOrder;

	/** The root view that contains everything */
	private ViewGroup rootView;

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
		rootView = (ViewGroup) inflater.inflate(R.layout.od_root, container,
				false);

		// Gets and saves the order matching the orderId passed to the fragment
		mOrder = ModelHandler.getModel(this.getActivity()).getOrderById(
				getArguments().getInt(ORDER_ID));

		mOrder.addSyncOrderListener(this);

		// Collects data from mOrder and initialize the views accordingly
		initTabs();
		initTaskCont();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * Sets up the tab host for this stone detail view with one drawing tab and
	 * one details tab. Data is also collected from mOrder and added to the
	 * tab's views.
	 * 
	 */
	private void initTabs() {
		final TabHost tabHost = (TabHost) rootView
				.findViewById(R.id.orderTabHost);
		tabHost.setup();

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
			}
		});

		// Add drawing tab
		TabHost.TabSpec drawingTab = tabHost.newTabSpec(DRAWING_TAB);
		drawingTab.setContent(R.id.tabDrawingContainer);
		drawingTab.setIndicator("Ritning");
		tabHost.addTab(drawingTab);
		initDrawing();

		// Add detail tab
		TabHost.TabSpec detailTab = tabHost.newTabSpec(DETAIL_TAB);
		detailTab.setContent(R.id.tab_info_container);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);
		initDetails();
	}

	/**
	 * Setups a task container with all it's data.
	 */
	private View initTaskCont() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout rootTaskCont = (LinearLayout) rootView
				.findViewById(R.id.root_task_cont);
		for (Product p : mOrder.getProducts()) {
			ViewGroup productView = (ViewGroup) inflater.inflate(
					R.layout.od_product_task_cont, null);
			// TODO Fix to actual type of Product
			((TextView) productView.findViewById(R.id.task_name)).setText(p
					.getType().getName());
			for (final Task task : p.getTasks()) {
				productView.addView(initTaskView(inflater, task));
			}
			rootTaskCont.addView(productView);
		}
		return rootTaskCont;
	}

	private View initTaskView(LayoutInflater inflater, final Task task) {
		View taskView = inflater.inflate(R.layout.od_task_cont, null);

		// Set task name
		((TextView) taskView.findViewById(R.id.task_name)).setText(task
				.getStation().getName());

		// Set the task toggler
		final ToggleButton btn = (ToggleButton) taskView
				.findViewById(R.id.task_toggler);
		// Prevents android from setting the toogle button to false on screen
		// orientation change etc
		btn.setSaveEnabled(false);
		btn.setChecked(task.getStatus() == Status.DONE);
		btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton,
					boolean isChecked) {
				if (task.getStatus() == Status.DONE) {
					task.setStatus(Status.NOT_DONE);
				} else {
					task.setStatus(Status.DONE);
				}
			}
		});
		return taskView;
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
				.getCemeteryBoard());
		((TextView) rootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCemetary());

		Stone stone = mOrder.getStone();
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

	/**
	 * TODO: Look in to this method and see if it really does what it should
	 */
	@Override
	public void changed(Order order) {
		if (order != mOrder) {
			if (order == null) {
				// TODO: Display error message to user
				// TODO: Consider a better way than sending null to let GUI know
				Log.d("DEBUG", "Server not about changes!");
			} else {
				throw new IllegalArgumentException(
						"The changed object should be the one displayed in the GUI");
			}
		} else {
			ViewGroup taskContainer = (ViewGroup) rootView
					.findViewById(R.id.root_task_cont);
			taskContainer.removeAllViews();
			initTaskCont();
		}
	}

}
