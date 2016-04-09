package org.dollars_bbs.thedollarscommunity;

import android.os.Build;

import java.util.Objects;
/**
 * @author Emmanuel
 *         on 2016-03-17, at 12:02.
 */
public class Utils {

	/**
	 * Compares two objects in a fully compatible non deprecated way
	 * @param o1 Object 1
	 * @param o2 Object 2
	 * @return Object 1 == Object 2
	 */
	public static boolean equal(Object o1, Object o2) {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Objects.equals(o1, o2)) || o1.equals(o2);
	}

}
