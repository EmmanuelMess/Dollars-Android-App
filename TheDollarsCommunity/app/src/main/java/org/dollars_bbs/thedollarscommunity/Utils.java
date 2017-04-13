package org.dollars_bbs.thedollarscommunity;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;

import java.io.IOException;
import java.util.Objects;
/**
 * @author Emmanuel
 *         on 2016-03-17, at 12:02.
 */
public class Utils {

	/**
	 * Checks for internet.
	 *
	 * @return true if there ie internet
	 */
	public static boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Compares two objects in a fully compatible non deprecated way
	 * @param o1 Object 1
	 * @param o2 Object 2
	 * @return Object 1 == Object 2
	 */
	public static boolean equal(Object o1, Object o2) {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Objects.equals(o1, o2)) || o1.equals(o2);
	}

	public static class SimpleOnTextChanged implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

}
