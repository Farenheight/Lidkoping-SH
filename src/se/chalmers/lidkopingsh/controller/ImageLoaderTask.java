package se.chalmers.lidkopingsh.controller;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

/**
 * A task that loads an image asynchronously and sets it to it's specified
 * {@link ImageView}
 * 
 * @author Simon Bengtsson
 * 
 */
public class ImageLoaderTask extends AsyncTask<Object, Object, Bitmap> {

	private final int REQUESTED_WIDTH_IMAGE = 500;
	private final int REQUESTED_HEIGHT_IMAGE = 1000;
	private String mImagePath;
	private ImageView mImageView;
	private View mLoadingView;

	/**
	 * Creates a new Task that loads an image asynchronously and sets it to the
	 * specified {@link ImageView}
	 * 
	 * @param imagePath
	 *            The path to the image that is going to be displayed
	 * @param imageView
	 *            The {@link ImageView} that is going to display the loaded
	 *            image when the task is finished
	 * @param loadingView
	 *            The {@link View} to hide and replace with the specified
	 *            {@link ImageView} when the image is loaded
	 */
	public ImageLoaderTask(String imagePath, ImageView imageView,
			View loadingView) {
		mImagePath = imagePath;
		mImageView = imageView;
		mLoadingView = loadingView;
	}

	/**
	 * Loads the image from the image path in the constructor
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Bitmap doInBackground(Object... params) {
		return decodeBitmapFromPath(mImagePath, REQUESTED_WIDTH_IMAGE,
				REQUESTED_HEIGHT_IMAGE);
	}

	/**
	 * Sets
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);
		PhotoViewAttacher pva = new PhotoViewAttacher(mImageView);
		pva.setMaximumScale(8f);
		mLoadingView.setVisibility(View.GONE);
		mImageView.setVisibility(View.VISIBLE);

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
		BitmapFactory.decodeFile(imgPath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Current images has to thin lines to scale well
		// TODO: Increase contrast of image so scaling is possible
		options.inSampleSize = 1;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imgPath, options);
	}

	/**
	 * Calculates how much the image can be scaled, the inSampleSize. See
	 * documentation of the public field inSample size in
	 * {@link BitmapFactory.Options} for more info
	 * 
	 * @param options
	 *            The bitmap options for the image
	 * @param reqWidth
	 *            The requested width
	 * @param reqHeight
	 *            The requested height
	 * @return How much the image can be scaled, the inSampleSize.
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

}
