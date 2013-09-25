package com.example.lidkopingsh.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.lidkopingsh.R;

public class TabActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);
		
		TabHost tabHost = (TabHost) findViewById(R.id.orderTabHost);
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
	}
}