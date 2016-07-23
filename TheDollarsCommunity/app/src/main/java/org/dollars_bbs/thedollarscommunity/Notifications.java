package org.dollars_bbs.thedollarscommunity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.dollars_bbs.thedollarscommunity.activities.SettingsActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Emmanuel
 *         on 2016-07-20, at 15:34.
 */
public class Notifications {
	private static int RSS_NOTIF_ID = 0;

	public static void setRSSNotif(Context context, Map<Integer, ArrayList<String>> RSSs) {
		String s = "";

		for(int i = 0; i < RSSs.size(); i++)
			if(RSSs.get(i) != null)
				for (String se : RSSs.get(i))
					s += se + "@" + context.getString(SettingsActivity.BOARDS_TITLE_KEYS[i]) + "\n";


		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_rss_feed_white_24dp)
						.setContentTitle("Dollars BBS RSS")
						.setContentText(s)
						.setAutoCancel(true);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			notification = mBuilder.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.ic_rss_feed_white_24dp)
					.setContentTitle("Dollars BBS RSS")
					.setContentText(s)
					.setAutoCancel(true)
					.setStyle(new NotificationCompat.BigTextStyle().bigText(s))
					.setContentText(s).build();
		} else
			notification = mBuilder.build();

		// mId allows you to update the notification later on.
		mNotificationManager.notify(RSS_NOTIF_ID, notification);
	}

}
