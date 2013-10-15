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
import android.view.MenuItem;
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
	private View rootView;

	private TabHost mTabHost;

	private String mCurrentTabTag;

	private boolean mTabletSize;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public OrderDetailsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedState) {

		mTabletSize = getArguments().getBoolean(MainActivity.IS_TABLET_SIZE);

		// Inflate the root view for the fragment. The rootView should contain
		// all other static views displayed in the fragment.
		rootView = inflater.inflate(R.layout.od_root, container, false);

		// Gets and saves the order matching the orderId passed to the fragment
		mOrder = ModelHandler.getModel(this.getActivity()).getOrderById(
				getArguments().getInt(ORDER_ID));

		// Collects data from mOrder and initialize the views accordingly
		initTabs(savedState == null ? null : savedState
				.getString(CURRENT_TAB_KEY));
		initTasks();

		// Hack to make the scroll view on the details tab not scroll to the
		// buttom when changing tabs programatically
		final ScrollView innerInfoScrollView = (ScrollView) rootView
				.findViewById(R.id.scrollview_inner_info);
		innerInfoScrollView.post(new Runnable() {
			public void run() {
				innerInfoScrollView.scrollTo(0, 0);
			}
		});

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

	private void initTabs(String currentTab) {
		mTabHost = (TabHost) rootView.findViewById(R.id.orderTabHost);
		mTabHost.setup();

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				mCurrentTabTag = tabId;
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
			mTabHost.setCurrentTabByTag(DETAIL_TAB);
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
		LinearLayout rootTaskCont = (LinearLayout) rootView
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
		View taskView = inflater.inflate(R.layout.od_task, null);

		// Set task name
		((TextView) taskView.findViewById(R.id.task_name)).setText(task
				.getStation().getName());

		// Set the task toggler
		final ToggleButton btn = (ToggleButton) taskView
				.findViewById(R.id.task_toggler);
		final ProgressBar bar = (ProgressBar) taskView
				.findViewById(R.id.task_toggler_progress_indicator);
		btn.setChecked(task.getStatus() == Status.DONE);
		btn.setSaveEnabled(false);
		btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton,
					boolean isChecked) {
				bar.setVisibility(View.VISIBLE);
				btn.setVisibility(View.GONE);
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

		// Product info
		StringBuilder desc = new StringBuilder();
		StringBuilder frontWork = new StringBuilder();
		StringBuilder materialColor = new StringBuilder();

		for (Product product : mOrder.getProducts()) {
			desc.append(product.getDescription());
			frontWork.append(product.getFrontWork());
			materialColor.append(product.getMaterialColor());
		}

		((TextView) rootView.findViewById(R.id.desc)).setText(desc);
		((TextView) rootView.findViewById(R.id.frontProcessing))
				.setText(frontWork);
		((TextView) rootView.findViewById(R.id.materialAndColor))
				.setText(materialColor);

		// Stone specific
		Stone stone = mOrder.getStone();
		if (stone != null) {
			((TextView) rootView.findViewById(R.id.stoneModel)).setText(stone
					.getStoneModel());

			((TextView) rootView.findViewById(R.id.ornament)).setText(stone
					.getOrnament());
			((TextView) rootView.findViewById(R.id.textStyleAndProcessing))
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
		Log.d("DEBUG", "In changed()");

		// Resets refresh button to refresh icon
		MenuItem refreshBtn = (MenuItem) getActivity().findViewById(
				R.id.action_update);
		refreshBtn.setActionView(null);

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
			Log.d("DEBUG", "Updated GUI");
			ViewGroup taskContainer = (ViewGroup) rootView
					.findViewById(R.id.task_cont);
			taskContainer.removeAllViews();
			initTasks();
		}
	}

}
