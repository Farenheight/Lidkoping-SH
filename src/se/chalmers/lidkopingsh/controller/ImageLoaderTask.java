package se.chalmers.lidkopingsh.controller;

import java.lang.ref.WeakReference;

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
	private WeakReference<ImageView> mWeakRefImageView;
	private View mLoadingView;
	private final int MAX_IMAGE_SIZE = 1800;

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
		mWeakRefImageView = new WeakReference<ImageView>(imageView);
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
        if (mWeakRefImageView != null && bitmap != null) {
            final ImageView imageView = mWeakRefImageView.get();
            if (imageView != null) {
        		imageView.setImageBitmap(bitmap);
        		PhotoViewAttacher pva = new PhotoViewAttacher(imageView);
        		pva.setMaximumScale(6f);
        		mLoadingView.setVisibility(View.GONE);
        		imageView.setVisibility(View.VISIBLE);
            }
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
		BitmapFactory.decodeFile(imgPath, options);

		// Current images has to thin lines to scale well
		// TODO: Increase contrast of image so scaling is possible
		options.inSampleSize = 1;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
		
		return reScaleIfToLarge(bitmap);
	}
	
	private Bitmap reScaleIfToLarge(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > MAX_IMAGE_SIZE ) {
			height *= (float) MAX_IMAGE_SIZE / width;
			width = MAX_IMAGE_SIZE;
		}
		if (height > MAX_IMAGE_SIZE) {
			width *= (float) MAX_IMAGE_SIZE / height;
			height = MAX_IMAGE_SIZE;
		}
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}
}
