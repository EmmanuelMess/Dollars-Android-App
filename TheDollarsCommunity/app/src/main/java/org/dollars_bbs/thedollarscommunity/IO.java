package org.dollars_bbs.thedollarscommunity;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Emmanuel
 *         on 2016-03-24, at 14:44.
 */
public class IO {
	protected static final int USER_IMAGE = 0;

	private static final String FOLDER = "The Dollars Community",
			USER_IMAGE_NAME = "userImage.png";

	protected static boolean saveImage(Bitmap b, int dataType) throws IOException {
		switch(dataType) {
			case USER_IMAGE://http://stackoverflow.com/a/9397142/3124150 (modified).
				File sdCardDirectory = checkDir();

				File image = new File(sdCardDirectory, USER_IMAGE_NAME);

				// Encode the file as a PNG image.
				FileOutputStream outStream = new FileOutputStream(image);
				b.compress(Bitmap.CompressFormat.PNG, 100, outStream);// 100 to keep full quality of the image
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
}
