package org.dollars_bbs.thedollarscommunity.activities.fragments;

import android.support.v4.app.Fragment;

/**
 * @author Emmanuel
 *         on 12/4/2017, at 19:17.
 */

public abstract class BackPressFragment extends Fragment {
	/**
	 *
	 * @return false if onBackPressed in container activity should not be executed, true otherwise
	 */
	public abstract boolean onBackPressed();
}
