package se.chalmers.lidkopingsh;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
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

/**
 * Activity containing a map with all the orders positions marked.
 */
public class OrderMapActivity extends FragmentActivity {

	private final static String TAG = "Debug tag";

	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */
	private GoogleMap mMap;

	private List<Marker> mMarkers;

	private IModel mModel;

	private Geocoder mGeoCoder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_root);
		mModel = ModelHandler.getModel(this);
		mMarkers = new ArrayList<Marker>();
		setUpMapIfNeeded();
		mMap.setMyLocationEnabled(true);
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
	 * Get markers
	 */

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera.
	 */
	private void setUpMap() {
		initMapTypeToggleButton();
		addMarkers();
		addDebugMarkers();
		zoomToMarkers(100);
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

	// Adds ten markers in stockholm TODO: Remove
	private void addDebugMarkers() {
		Address cAddress = getAddress("eggvena kyrka");
		LatLng cLatLng = new LatLng(cAddress.getLatitude(),
				cAddress.getLongitude());
		Marker cemeteryMarker = mMap.addMarker(new MarkerOptions().position(
				cLatLng).title("Eggvena Kyrka!!"));
		cemeteryMarker.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.stone));
		mMarkers.add(cemeteryMarker);
	}

	/**
	 * Add one marker for every order in the model's order list
	 */
	private void addMarkers() {
		double debugEps = 0; // TODO: Remove
		for (Order order : mModel.getOrders()) {
			if (debugEps > 0.05) {
				break;
			}
			Address cAddress = getAddress(formatCemeteryName(order
					.getCemetary()));
			LatLng cLatLng = new LatLng(cAddress.getLatitude() + debugEps,
					cAddress.getLongitude() + debugEps);
			Marker cemeteryMarker = mMap.addMarker(new MarkerOptions()
					.position(cLatLng).title(
							order.getIdName() + " - " + order.getCemetary()));
			cemeteryMarker.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.stone));
			mMarkers.add(cemeteryMarker);
			debugEps += 0.01;
		}
	}

	/**
	 * @param searchTerm
	 *            The term to search for
	 * @return Address object with a latitude and longitude among other things
	 */
	private Address getAddress(String searchTerm) {
		if (mGeoCoder == null) {
			mGeoCoder = new Geocoder(this);
		}
		try {
			List<Address> aList = mGeoCoder.getFromLocationName(searchTerm, 1);
			return aList.get(0);
		} catch (IOException e) {
			Log.e("DEBUG",
					"Network error in getting lat and long from cemetery name");
			e.printStackTrace();
			return null;
		}
	}

	private String formatCemeteryName(String cemeteryName) {
		// TODO: Handle if cemetery is null
		cemeteryName = cemeteryName.toLowerCase();
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
				R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						// We use the new method when supported
						@SuppressLint("NewApi")
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {
							LatLngBounds.Builder bld = new LatLngBounds.Builder();
							for (int i = 0; i < mMarkers.size(); i++) {
								bld.include(mMarkers.get(i).getPosition());
							}
							LatLngBounds bounds = bld.build();
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							mMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(bounds, padding));
						}
					});
		}
	}
}
