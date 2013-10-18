package se.chalmers.lidkopingsh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.handler.Accessor;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import se.chalmers.lidkopingsh.server.NetworkStatusListener;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in the {@link HandsetsDetailsActivity} on handsets or to the right
 * in the {@link MainActivity}s two-pane layout if displayed on tablets.
 * 
 * @author Simon Bengtsson
 * @author Anton Jansson
 */
public class OrderDetailsFragment extends Fragment {

	/** Used as a key when sending the object between activities and fragments */
	public static final String ORDER_ID = "item_id";

	public static final String CURRENT_TAB_KEY = "current_tab_key";

	private static final String DRAWING_TAB = "drawing tab";

	private static final String DETAIL_TAB = "DETAILS tab";

	private static final String TASK_TAB = "task_tab";

	/**
	 * Size of largest image width or height, of order drawing.
	 */
	private static final int MAX_IMAGE_SIZE = 1800;

	/** The order displayed by this StoneDetailFragment */
	private Order mOrder;

	/** The root view that contains everything */
	private View mRootView;

	private TabHost mTabHost;

	private boolean mUse2Tabs;

	private List<ProgressBar> progressIndicators;

	private List<ToggleButton> toggleButtons;

	private NetworkWatcher mNetworkWatcher;

	private Bitmap bitmap;

	private AsyntaskImageLoader asyntaskImageLoader;

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
		mNetworkWatcher = new NetworkWatcher();
		Accessor.getServerConnector(getActivity()).addNetworkListener(
				mNetworkWatcher);

		// Gets and saves the order matching the orderId passed to the fragment
		mOrder = Accessor.getModel(this.getActivity()).getOrderById(
				getArguments().getInt(ORDER_ID));

		mUse2Tabs = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
				&& getResources().getBoolean(R.bool.isTablet);
	}

	@Override
	public void onDestroy() {
		Accessor.getServerConnector(getActivity()).removeNetworkListener(
				mNetworkWatcher);
		if (bitmap != null) {
			bitmap.recycle();
			Log.d("DEBUG", "bitmap data released");
		}
		if (asyntaskImageLoader != null) {
			asyntaskImageLoader.cancel(true);
		}
		super.onDestroy();
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

	private class NetworkWatcher implements NetworkStatusListener {

		@Override
		public void startedUpdate() {
		}

		@Override
		public void finishedUpdate() {
			showProgressIndicators(false);
			ViewGroup taskContainer = (ViewGroup) mRootView
					.findViewById(R.id.task_cont);
			taskContainer.removeAllViews();
			initTasks();
		}

		@Override
		public void networkProblem(String message) {
			// Do nothing here
		}

		@Override
		public void authenticationFailed() {
			// TODO Auto-generated method stub

		}

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
		if (!mUse2Tabs) {
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
		detailTab.setIndicator(getTabIndicator(mUse2Tabs ? "Information"
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
				if (isNetworkAvailable()) {
					btn.setVisibility(View.GONE);
					pBar.setVisibility(View.VISIBLE);
					task.setStatus(task.getStatus() == Status.DONE ? Status.NOT_DONE
							: Status.DONE);
				} else {
					toggleButton.setChecked(!isChecked);
					Toast toast = Toast.makeText(getActivity(),
									getResources().getString(R.string.network_error_change_data),2);
					toast.show();
				}
			}
		});
		return taskView;
	}

	private void showProgressIndicators(boolean visible) {
		for (ProgressBar pBar : progressIndicators) {
			pBar.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		for (ToggleButton tBtn : toggleButtons) {
			tBtn.setVisibility(visible ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Attaches the PhotoView library to the order's drawing making it possible
	 * to zoom and pan it smoothly
	 */
	private void initDrawing() {
		if (!mOrder.getImages().isEmpty()) {

			// Get image from internal storage
			String imagePath = mOrder.getImages().get(0).getImagePath();
			// Load Images
			asyntaskImageLoader = new AsyntaskImageLoader();
			asyntaskImageLoader.execute(imagePath);
		}
	}

	private class AsyntaskImageLoader extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap result = null;
			if (params.length == 0) {
				return null;
			}
			String imagePath = params[0];
			File file = new File(getActivity().getFilesDir(), imagePath);
			String filename = file.getAbsolutePath();

			while (file.exists() && result == null) {
				result = BitmapFactory.decodeFile(filename);
				Log.d("DEBUG", "Trying to load Image");
			}
			if (file.exists()) {
				Log.d("DEBUG", "Image loaded");
			} else {
				Log.d("DEBUG",
						"Image not loaded, cannot find an image on path "
								+ filename);

			}
			if (result != null) {
				// Rescale the image if it is too big
				int height = result.getHeight();
				int width = result.getWidth();
				if (width > MAX_IMAGE_SIZE) {
					height *= (float) MAX_IMAGE_SIZE / width;
					width = MAX_IMAGE_SIZE;
				}
				if (height > MAX_IMAGE_SIZE) {
					width *= (float) MAX_IMAGE_SIZE / height;
					height = MAX_IMAGE_SIZE;
				}
				result = Bitmap.createScaledBitmap(result, width, height, true);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ProgressBar pBar = (ProgressBar) mRootView
					.findViewById(R.id.orderDrawingProgressBar);
			pBar.setVisibility(View.GONE);
			if (result != null) {
				bitmap = result;
				ImageView orderDrawing = (ImageView) mRootView
						.findViewById(R.id.orderDrawing);
				orderDrawing.setVisibility(View.VISIBLE);
				// Set drawable to view
				BitmapDrawable drawable = new BitmapDrawable(getResources(),
						bitmap);
				orderDrawing.setImageDrawable(drawable);
				// Attaches the library
				PhotoViewAttacher pva = new PhotoViewAttacher(orderDrawing);
				pva.setMaximumScale(8f);
			}else{
				LinearLayout textView = (LinearLayout) mRootView
						.findViewById(R.id.no_images_found_card);
				textView.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}
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
				.setText(mOrder.getCemetaryBlock() + " "
						+ mOrder.getCemetaryNumber());
		((TextView) mRootView.findViewById(R.id.cemeteryBoard)).setText(mOrder
				.getCemeteryBoard());
		((TextView) mRootView.findViewById(R.id.cemetery)).setText(mOrder
				.getCemetary());

		// Product info
		StringBuilder desc = new StringBuilder();
		StringBuilder frontWork = new StringBuilder();
		StringBuilder materialColor = new StringBuilder();

		for (Product product : mOrder.getProducts()) {
			if (!product.getDescription().isEmpty()) {
				desc.append(product.getType() + ": " + product.getDescription()
						+ "\n");
			}
			if (!product.getFrontWork().isEmpty()) {
				frontWork.append(product.getType() + ": "
						+ product.getFrontWork() + "\n");
			}
			if (!product.getMaterialColor().isEmpty()) {
				materialColor.append(product.getType() + ": "
						+ product.getMaterialColor() + "\n");
			}
		}

		((TextView) mRootView.findViewById(R.id.desc)).setText(desc);
		((TextView) mRootView.findViewById(R.id.frontProcessing))
				.setText(frontWork);
		((TextView) mRootView.findViewById(R.id.materialAndColor))
				.setText(materialColor);

		// Stone specific
		Stone stone = mOrder.getStone();
		String stoneModel = "";
		String ornament = "";
		String textStyle = "";
		if (stone != null) {
			stoneModel = stone.getStoneModel();
			ornament = stone.getOrnament();
			textStyle = stone.getTextStyle();
		}
		((TextView) mRootView.findViewById(R.id.stoneModel))
				.setText(stoneModel);

		((TextView) mRootView.findViewById(R.id.ornament)).setText(ornament);
		((TextView) mRootView.findViewById(R.id.textStyleAndProcessing))
				.setText(textStyle);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CURRENT_TAB_KEY, mTabHost.getCurrentTabTag());
	}

	private boolean isNetworkAvailable() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}