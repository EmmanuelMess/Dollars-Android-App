package org.dollars_bbs.thedollarscommunity;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Emmanuel
 *         on 2016-03-24, at 14:44.
 */
public class IO {
	public static final int USER_IMAGE = 0;

	private static final String FOLDER = "The Dollars Community",
			USER_IMAGE_NAME = "userImage.png";
	private static final int USER_IMAGE_WIDTH = 75;

	protected static Bitmap recoverImage(int dataType) throws IOException {
		switch(dataType) {
			case USER_IMAGE:
				File sdCardDirectory = checkDir();
				File image = new File(sdCardDirectory, USER_IMAGE_NAME);
				if(image.exists())
					return BitmapFactory.decodeFile(sdCardDirectory + File.separator + USER_IMAGE_NAME);
				break;
		}

		return null;
	}

	public static boolean saveImage(Bitmap b, int dataType) throws IOException {
		switch(dataType) {
			case USER_IMAGE://http://stackoverflow.com/a/9397142/3124150 (modified).
				File image = new File(checkDir(), USER_IMAGE_NAME);

				// Encode the file as a PNG image.
				FileOutputStream outStream = new FileOutputStream(image);
				b.compress(Bitmap.CompressFormat.PNG, USER_IMAGE_WIDTH, outStream);
				outStream.flush();
				outStream.close();
				break;
		}

		return true;
	}

	private static File checkDir() {
		File dir = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER);
		dir.mkdir();
		return dir;
	}

	/**
	 * Downsamples images: http://stackoverflow.com/a/5086706/3124150 (modified).
	 *
	 * @param selectedImage image to downsample
	 * @param REQUIRED_SIZE the size you want
	 * @param isWidth if the REQUIRED_SIZE value is the height
	 * @return downsample'd bitmap
	 * @throws FileNotFoundException if there's no image
	 */
	public static Bitmap decodeUri(Uri selectedImage, final int REQUIRED_SIZE, boolean isWidth, ContentResolver c) throws FileNotFoundException {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(c.openInputStream(selectedImage), null, o);

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if(isWidth) {
				if (width_tmp/2 < REQUIRED_SIZE) {
					break;
				}
			} else {
				if(height_tmp/2 < REQUIRED_SIZE) {
					break;
				}
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(c.openInputStream(selectedImage), null, o2);
	}

	public static class SaveImageAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {
		protected Exception failed;

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			try {
				IO.saveImage(params[0], IO.USER_IMAGE);
			} catch (IOException e) {
				failed = e;
				return null;
			}
			return params[0];
		}
	}
}
