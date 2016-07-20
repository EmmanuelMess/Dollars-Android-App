package org.dollars_bbs.thedollarscommunity.rss_io;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.dollars_bbs.thedollarscommunity.activities.SettingsActivity;

// WakefulBroadcastReceiver ensures the device does not go back to sleep
// during the startup of the service
public class RSSCheckOnBoot extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.NOTIF_KEYS[0], true)) {
			RSSScheduledServiceHelper.startScheduled(context);
			WakefulBroadcastReceiver.completeWakefulIntent(intent);
		}
	}
}