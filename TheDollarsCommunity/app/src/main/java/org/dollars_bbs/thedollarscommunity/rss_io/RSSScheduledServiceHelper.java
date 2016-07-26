package org.dollars_bbs.thedollarscommunity.rss_io;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class RSSScheduledServiceHelper {

	public static void startScheduled(Context context) {
		// Construct an intent that will execute the AlarmReceiver
		Intent intent = new Intent(context.getApplicationContext(), RSSCheckBroadcastReceiver.class);
		// Create a PendingIntent to be triggered when the alarm goes off
		final PendingIntent pIntent = PendingIntent.getBroadcast(context, RSSCheckBroadcastReceiver.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
	}

	public static void cancelAlarm(Context context) {
		Intent intent = new Intent(context.getApplicationContext(), RSSCheckBroadcastReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(context, RSSCheckBroadcastReceiver.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}
}
