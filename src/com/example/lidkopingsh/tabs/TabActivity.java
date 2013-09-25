package com.example.lidkopingsh.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.lidkopingsh.R;

public class TabActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);
		TabHost tabs = (TabHost) findViewById(R.id.orderTabHost);
		tabs.setup();
		TabHost.TabSpec spec1 = tabs.newTabSpec("tag1");
		
		spec1.setContent(R.id.tabDrawingContainer);
		spec1.setIndicator("Analog Clock"); 

		tabs.addTab(spec1);

		TabHost.TabSpec spec2 = tabs.newTabSpec("tag2");
		spec2.setContent(R.id.tabDetailContainer);
		spec2.setIndicator("Digital Clock");

		tabs.addTab(spec2);
	}
}