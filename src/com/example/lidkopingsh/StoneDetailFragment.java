package com.example.lidkopingsh;

import com.example.lidkopingsh.model.ModelHandler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

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
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoneDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		View rootView = inflater.inflate(R.layout.tabs_layout,
				container, false);
				
		TabHost tabHost = (TabHost) rootView.findViewById(R.id.orderTabHost);
		tabHost.setup();
		
		//Add drawing tab
		TabHost.TabSpec drawingTab = tabHost.newTabSpec("drawingTab");
		drawingTab.setContent(R.id.tabDrawingContainer);
		drawingTab.setIndicator("Ritning"); 
		tabHost.addTab(drawingTab);

		//Add detail tab
		TabHost.TabSpec detailTab = tabHost.newTabSpec("detailTab");
		detailTab.setContent(R.id.tabDetailContainer);
		detailTab.setIndicator("Detaljer");
		tabHost.addTab(detailTab);


		return rootView;
	}



}
