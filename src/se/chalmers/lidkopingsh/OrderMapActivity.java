package se.chalmers.lidkopingsh;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import se.chalmers.lidkopingsh.handler.Accessor;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.BubbleIconFactory;

/**
 * Activity containing a map with all the orders positions marked.
 * 
 * @author Simon Bengtsson
 */
public class OrderMapActivity extends FragmentActivity {

	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */
	private GoogleMap mMap;

	private List<Marker> mMarkers;

	private IModel mModel;

	private Geocoder mGeoCoder;

	private BubbleIconFactory mIconFactory;
	
	private static LatLng STANDARD_COORDINATES = new LatLng(58.3f, 14);

	private static float STANDARD_ZOOM = 6f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_root);
		mModel = Accessor.getModel(this);
		mMarkers = new ArrayList<Marker>();
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera.
	 */
	private void setUpMap() {
		initMapTypeToggleButton();
		initBubbleFactory();
		zoomToStandardLocation();
		new MapLoader().execute(mMap); // Setup map
		mMap.setMyLocationEnabled(true);
	}

	private void initBubbleFactory() {
		mIconFactory = new BubbleIconFactory(this);
		Drawable d = getResources().getDrawable(R.drawable.stone);
		mIconFactory.setTextAppearance(this, R.style.StoneIconText);
		mIconFactory.setBackground(d);
		mIconFactory.setContentPadding(5, 20, 0, 0);
	}

	private void initMapTypeToggleButton() {
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.layers_toggle_button);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton source, boolean checked) {
				if (checked) {
					mMap.setMapType(MAP_TYPE_SATELLITE);
				} else {
					mMap.setMapType(MAP_TYPE_NORMAL);
				}
			}
		});

	}
	private void zoomToStandardLocation() {
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STANDARD_COORDINATES, STANDARD_ZOOM));
	}

	private class MapLoader extends
			AsyncTask<GoogleMap, Void, Map<MarkerOptions, Order>> {

		@Override
		protected Map<MarkerOptions, Order> doInBackground(GoogleMap... params) {
			Map<MarkerOptions, Order> markers = initMarker();
			return markers;
		}

		@Override
		protected void onPostExecute(Map<MarkerOptions, Order> result) {
			super.onPostExecute(result);
			addMarkers(result);
			zoomToMarkers(100);
		}

		/**
		 * Findes where the
		 */
		private Map<MarkerOptions, Order> initMarker() {
			Map<MarkerOptions, Order> markers = new HashMap<MarkerOptions, Order>();
			for (Order order : mModel.getOrders()) {
				Address cAddress = getAddress(formatCemeteryName(order
						.getCemetary()));
				if(cAddress != null){
					LatLng cLatLng = new LatLng(cAddress.getLatitude(),
							cAddress.getLongitude());
					markers.put(
							new MarkerOptions().position(cLatLng).title(
									order.getCemetary()), order);					
				}
			}
			return markers;
		}

		private void addMarkers(Map<MarkerOptions, Order> markers) {
			for (MarkerOptions marker : markers.keySet()) {

				Marker cemeteryMarker = mMap.addMarker(marker);
				mIconFactory.makeIcon("E.G.");
				cemeteryMarker.setIcon(BitmapDescriptorFactory
						.fromBitmap(mIconFactory.makeIcon(markers.get(marker)
								.getIdName())));
				mMarkers.add(cemeteryMarker);
			}

		}

		/**
		 * @param searchTerm
		 *            The term to search for
		 * @return Address object with a latitude and longitude among other
		 *         things
		 */
		private Address getAddress(String searchTerm) {
			if (mGeoCoder == null) {
				mGeoCoder = new Geocoder(OrderMapActivity.this);
			}
			try {
				List<Address> aList = mGeoCoder.getFromLocationName(searchTerm,
						1);
				return aList.size() == 0? null : aList.get(0);
			} catch (IOException e) {
				Log.e("DEBUG",
						"Network error in getting lat and long from cemetery name");
				e.printStackTrace();
				return null;
			}
		}

		private String formatCemeteryName(String cemeteryName) {
			// TODO: Handle if cemetery is null
			cemeteryName = cemeteryName.toLowerCase(new Locale("Swedish"));
			if (cemeteryName.contains("kyrkogård")
					|| cemeteryName.contains("kyrka")) {
				return cemeteryName;
			} else {
				return cemeteryName + "kyrka";
			}
		}
		/**
		 * Zoom to the added markers
		 * 
		 * @param padding
		 *            The padding in pixels around the markers
		 */
		private void zoomToMarkers(final int padding) {
			// Pan to see all markers in view.
			// Cannot zoom to bounds until the map has a size.
			final View mapView = getSupportFragmentManager().findFragmentById(
					R.id.map).getView(); //TODO null pointer??
			if (mapView.getViewTreeObserver().isAlive()) {
				if(mMarkers.size() > 1){
					LatLngBounds.Builder bld = new LatLngBounds.Builder();
					for (int i = 0; i < mMarkers.size(); i++) {
						bld.include(mMarkers.get(i).getPosition());
					}
					LatLngBounds bounds = bld.build();
					mMap.animateCamera(CameraUpdateFactory
							.newLatLngBounds(bounds, padding));					
				}else if (mMarkers.size() == 1){
					
					mMap.animateCamera(CameraUpdateFactory
							.newLatLng(mMarkers.get(0).getPosition()));					
				}
			}
		}

	}
}
