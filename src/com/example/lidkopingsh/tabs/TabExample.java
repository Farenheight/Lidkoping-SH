package com.example.lidkopingsh.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.lidkopingsh.R;

public class TabExample extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TabHost tabs = (TabHost) findViewById(R.id.TabHost01);

		tabs.setup();

		TabHost.TabSpec spec1 = tabs.newTabSpec("tag1");

		spec1.setContent(R.id.AnalogClock01);
		spec1.setIndicator("Analog Clock");

		tabs.addTab(spec1);

		TabHost.TabSpec spec2 = tabs.newTabSpec("tag2");
		spec2.setContent(R.id.DigitalClock01);
		spec2.setIndicator("Digital Clock");

		tabs.addTab(spec2);
	}
}