package org.dollars_bbs.thedollarscommunity.rss_io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RSSCheckBroadcastReceiver extends BroadcastReceiver {
	public static final int REQUEST_CODE = 0;
	public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

	// Triggered by the Alarm periodically (starts the service to run task)
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, RSSCheckService.class);
		i.putExtra("foo", "bar");
		context.startService(i);
	}

}