package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import se.chalmers.lidkopingsh.util.Listener;
import se.chalmers.lidkopingsh.util.NetworkUpdateListener;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.location.Address;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in the {@link HandsetsDetailsActivity} on handsets or to the right
 * in the {@link MainActivity}s two-pane layout if displayed on tablets.
 * 
 * @author Simon Bengtsson
 * 
 */
public class OrderDetailsFragment extends Fragment implements Listener<Order> {

	/** Used as a key when sending the object between activities and fragments */
	public static final String ORDER_ID = "item_id";

	public static final String CURRENT_TAB_KEY = "current_tab_key";

	private static final String DRAWING_TAB = "drawing tab";

	private static final String DETAIL_TAB = "DETAILS tab";

	private static final String TASK_TAB = "task_tab";

	/** The order displayed by this StoneDetailFragment */
	private Order mOrder;

	/** The root view that contains everything */
	private View mRootView;

	private TabHost mTabHost;

	private boolean mTabletSize;

	private List<ProgressBar> progressIndicators;

	private List<ToggleButton> toggleButtons;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public OrderDetailsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressIndicators = new ArrayList<ProgressBar>();
		toggleButtons = new ArrayList<ToggleButton>();
		ModelHandler.getLayer(getActivity()).addNetworkListener(
				new NetworkWatcher());

		// Gets and saves the order matching the orderId passed to the fragment
		mOrder = ModelHandler.getModel(this.getActivity()).getOrderById(
				getArguments().getInt(ORDER_ID));

		mTabletSize = getArguments().getBoolean(MainActivity.IS_TABLET_SIZE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedState) {

		// Inflate the root view for the fragment. The rootView should contain
		// all other static views displayed in the fragment.
		mRootView = inflater.inflate(R.layout.od_root, container, false);

		// Collects data from mOrder and initialize the views accordingly
		initTabs(savedState == null ? null : savedState
				.getString(CURRENT_TAB_KEY));
		initTasks();

		// Hack to make the scroll view on the details tab not scroll to the
		// bottom when changing tabs
		final ScrollView innerInfoScrollView = (ScrollView) mRootView
				.findViewById(R.id.scrollview_inner_info);
		innerInfoScrollView.post(new Runnable() {
			public void run() {
				innerInfoScrollView.scrollTo(0, 0);
			}
		});

		return mRootView;
	}

	private class NetworkWatcher implements NetworkUpdateListener {

		@Override
		public void startUpdate() {
		}

		@Override
		public void endUpdate() {
			showProgressIndicators(false);
		}

		@Override
		public void noNetwork(String message) {
			// Do nothing here
		}

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

	private void initTabs(String currentTab) {
		mTabHost = (TabHost) mRootView.findViewById(R.id.orderTabHost);
		mTabHost.setup();

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
			}
		});

		// On phones, put the task container in a new tab
		if (!mTabletSize) {
			TabHost.TabSpec taskTab = mTabHost.newTabSpec(TASK_TAB);
			taskTab.setContent(R.id.task_cont_wrapper);
			taskTab.setIndicator(getTabIndicator("Moment"));
			mTabHost.addTab(taskTab);
		}

		// Add drawing tab
		TabHost.TabSpec drawingTab = mTabHost.newTabSpec(DRAWING_TAB);
		drawingTab.setContent(R.id.tabDrawingContainer);
		drawingTab.setIndicator(getTabIndicator("Ritning"));
		mTabHost.addTab(drawingTab);
		initDrawing();

		// Add detail tab
		TabHost.TabSpec detailTab = mTabHost.newTabSpec(DETAIL_TAB);
		detailTab.setContent(R.id.tab_info_container);
		detailTab.setIndicator(getTabIndicator(mTabletSize ? "Information"
				: "Info"));
		mTabHost.addTab(detailTab);
		initDetails();

		// Sets the current tab if saved
		if (currentTab != null) {
			mTabHost.setCurrentTabByTag(currentTab);
		}

	}

	// Only changing the look of the tabs
	private View getTabIndicator(String tabTitle) {
		View tabIndicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab_indicator_holo, mTabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator
				.findViewById(android.R.id.title);
		title.setText(tabTitle);
		return tabIndicator;
	}

	/**
	 * Setups a task container with all it's data.
	 */
	private void initTasks() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout rootTaskCont = (LinearLayout) mRootView
				.findViewById(R.id.task_cont);
		for (Product p : mOrder.getProducts()) {
			ViewGroup productView = (ViewGroup) inflater.inflate(
					R.layout.od_producttask_cont, null);

			((TextView) productView.findViewById(R.id.task_name)).setText(p
					.getType().getName());
			for (final Task task : p.getTasks()) {
				productView.addView(initTaskView(inflater, task));
			}
			rootTaskCont.addView(productView);
		}
	}

	private View initTaskView(LayoutInflater inflater, final Task task) {
		final View taskView = inflater.inflate(R.layout.od_task, null);

		// Set task name
		((TextView) taskView.findViewById(R.id.task_name)).setText(task
				.getStation().getName());

		// Set the task toggler
		final ToggleButton btn = (ToggleButton) taskView
				.findViewById(R.id.task_toggler);
		btn.setChecked(task.getStatus() == Status.DONE);
		toggleButtons.add(btn);
		final ProgressBar pBar = (ProgressBar) taskView
		.findViewById(R.id.task_toggler_progress_indicator);
		progressIndicators.add(pBar);
		// If not setSaveEnabaled(false), android resets the buttons on screen
		// orientation change etc
		btn.setSaveEnabled(false);
		btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton,
					boolean isChecked) {
				btn.setVisibility(View.GONE); 
				pBar.setVisibility(View.VISIBLE);
				if (task.getStatus() == Status.DONE) {
					task.setStatus(Status.NOT_DONE);
				} else {
					task.setStatus(Status.DONE);
				}
			}
		});
		return taskView;
	}

	private void showProgressIndicators(boolean visible) {
		for(ProgressBar pBar : progressIndicators) {
			pBar.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		for(ToggleButton tBtn : toggleButtons) {
			tBtn.setVisibility(visible ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Attaches the PhotoView library to the order's drawing making it possible
	 * to zoom and pan it smoothly
	 */
	private void initDrawing() {
		ImageView orderDrawing = (ImageView) mRootView
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
		((TextView) mRootView.findViewById(R.id.task_name)).setText(mOrder
				.getIdName());
		((TextView) mRootView.findViewById(R.id.orderNumber)).setText(mOrder
				.getOrderNumber());

		// General
		((TextView) mRootView.findViewById(R.id.cemetery_number))
				.setText(mOrder.getCemetaryNumber());
		((TextView) mRootView.findViewById(R.id.cemeteryBoard)).setText(mOrder
				.getCemeteryBoard());
		((TextView) mRootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCemetary());

		// Product info
		StringBuilder desc = new StringBuilder();
		StringBuilder frontWork = new StringBuilder();
		StringBuilder materialColor = new StringBuilder();

		for (Product product : mOrder.getProducts()) {
			desc.append(product.getDescription());
			frontWork.append(product.getFrontWork());
			materialColor.append(product.getMaterialColor());
		}

		((TextView) mRootView.findViewById(R.id.desc)).setText(desc);
		((TextView) mRootView.findViewById(R.id.frontProcessing))
				.setText(frontWork);
		((TextView) mRootView.findViewById(R.id.materialAndColor))
				.setText(materialColor);

		// Stone specific
		Stone stone = mOrder.getStone();
		if (stone != null) {
			((TextView) mRootView.findViewById(R.id.stoneModel)).setText(stone
					.getStoneModel());

			((TextView) mRootView.findViewById(R.id.ornament)).setText(stone
					.getOrnament());
			((TextView) mRootView.findViewById(R.id.textStyleAndProcessing))
					.setText(stone.getTextStyle());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CURRENT_TAB_KEY, mTabHost.getCurrentTabTag());
	}

	@Override
	public void changed(Order order) {
		Log.d("OrderDetailsFragment", "In changed()");
 
		if (order != mOrder) {
			if (order == null) {
				// TODO: Display error message to user
				Log.d("OrderDetails", "Server not about changes!");
			} else {
				throw new IllegalArgumentException(
						"The changed object should be the one displayed in the GUI");
			}
		} else {
			Log.d("OrderDetailsFragmnent", "Updated GUI");
			ViewGroup taskContainer = (ViewGroup) mRootView
					.findViewById(R.id.task_cont);
			taskContainer.removeAllViews();
			initTasks();
		}
	}

}