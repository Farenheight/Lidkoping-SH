package se.chalmers.lidkopingsh.controller;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * This activity is only used on handset devices! It is representing a single
 * Stone detail screen. On tablet-size devices, item details are presented
 * side-by-side with a list of items in the {@link MainActivity}.
 * 
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link OrderDetailsFragment}.
 * 
 * @author Simon Bengtsson
 */
public class HandsetsDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.od_root);
		initDrawing();
	}

	/**
	 * Initialize the drawing asynchronously with a library for handling zooming
	 * and panning
	 */
	private void initDrawing() {
		ImageView orderDrawing = (ImageView) findViewById(R.id.orderDrawing);

		if (orderDrawing != null) {
			orderDrawing.setImageBitmap(decodeBitmapFromPath("", 1000, 1000));
			PhotoViewAttacher pva = new PhotoViewAttacher(orderDrawing);
			pva.setMaximumScale(8f);
			orderDrawing.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * Decodes a Bitmap from the specified file path that is going to be loaded
	 * near the specified requested width and height.
	 * 
	 * @param imgPath
	 *            The file path of the image to be displayed
	 * @param reqWidth
	 *            The requested width of the image
	 * @param reqHeight
	 *            The requested height of the image
	 * @return The bitmap created from the path provided
	 */
	private Bitmap decodeBitmapFromPath(String imgPath, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inScaled = true;
		BitmapFactory.decodeResource(getResources(),
				R.drawable.sample_order_middle);

		// Current images has to thin lines to scale well
		// TODO: Increase contrast of image so scaling is possible
		options.inSampleSize = 2;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.sample_order_middle);

		return bitmap;
	}
}
