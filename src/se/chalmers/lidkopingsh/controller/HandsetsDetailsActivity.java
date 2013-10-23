package se.chalmers.lidkopingsh.controller;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HandsetsDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.od_root);
		
		WeakReference<ImageView> wr = new WeakReference<ImageView>(
				(ImageView) findViewById(R.id.orderDrawing));
		wr.get().setImageBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.sample_order_middle));
		wr.get().setVisibility(View.VISIBLE);
	}
}
