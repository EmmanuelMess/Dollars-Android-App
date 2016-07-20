package org.dollars_bbs.thedollarscommunity.rss_io;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;

import org.dollars_bbs.thedollarscommunity.Notifications;
import org.dollars_bbs.thedollarscommunity.Utils;
/**
 * @author Emmanuel
 *         on 2016-07-20, at 16:21.
 */
public class RSSCheckService extends IntentService {

	public RSSCheckService() {
		super("rss update service");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(Utils.isOnline())
			Notifications.setRSSNotif(this, null);
	}
}

