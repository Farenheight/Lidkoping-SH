/**
 * 
 */
package com.example.lidkopingsh.tabs;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lidkopingsh.R;

/**
 *
 */
public class TabDrawingFragment extends Fragment {

	/** Attacher to imageView to zoom lib **/
	private PhotoViewAttacher mAttacher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		View rootView = (LinearLayout) inflater.inflate(R.layout.tab_drawing,
				container, false);
		initOrderDrawing(rootView);
		return rootView;
	}

	/**
	 * Make image zoomable by attaching PhotoView lib
	 */
	private void initOrderDrawing(View rootView) {
		ImageView orderDrawing = (ImageView) rootView
				.findViewById(R.id.orderDrawing);

		// Set the Drawable displayed
		Drawable bitmap = getResources().getDrawable(
				R.drawable.test_headstone_drawing);

		// Attach a PhotoViewAttacher, which takes care of all of the zooming
		// functionality.
		mAttacher = new PhotoViewAttacher(orderDrawing);
	}
}
