package org.dollars_bbs.thedollarscommunity.rss_io;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.dollars_bbs.thedollarscommunity.Notifications;
import org.dollars_bbs.thedollarscommunity.R;
import org.dollars_bbs.thedollarscommunity.RSSRelatedConstants;
import org.dollars_bbs.thedollarscommunity.Utils;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
		if (Utils.isOnline()) {

			SharedPreferences rssData = getApplicationContext().getSharedPreferences(getString(R.string.rss_file_key), Context.MODE_PRIVATE),
					pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Map<Integer, ArrayList<String>> newRSSFields = new HashMap<>(RSSRelatedConstants.RSS.length);
			SharedPreferences.Editor rssDataEditor = rssData.edit();
			long severTime = -1;
			boolean notEmpty = false;

			for (int i = 0; i < RSSRelatedConstants.BOARDS_KEYS.length; i++) {
				if (rssData.getLong(RSSRelatedConstants.BOARDS_DATA_KEYS[i], -1) == -1) {
					if (severTime == -1) {
						try {
							HttpURLConnection tempConnection = (HttpURLConnection) new URL(RSSRelatedConstants.DOLLARS_BBS).openConnection();
							tempConnection.connect();
							severTime = tempConnection.getDate();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					rssDataEditor.putLong(RSSRelatedConstants.BOARDS_DATA_KEYS[i], severTime);
					continue;
				} else if (!pref.getBoolean(RSSRelatedConstants.BOARDS_KEYS[i], false))
					continue;

				try {
					long lastCheck = rssData.getLong(RSSRelatedConstants.BOARDS_KEYS[i], 0);
					HttpURLConnection c = (HttpURLConnection) new URL(RSSRelatedConstants.RSS[i]).openConnection();
					c.setIfModifiedSince(lastCheck);
					c.connect();

					if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
						newRSSFields.put(i, new ArrayList<>());

						RSSFeed r = new RSSReader().load(RSSRelatedConstants.RSS[i]);

						for (RSSItem elem : r.getItems()) {
							HttpURLConnection itemConn = (HttpURLConnection) new URL(elem.getLink().toString()).openConnection();
							itemConn.connect();
							if (new Date(itemConn.getLastModified()).after(new Date(lastCheck))) {
								String s = elem.getTitle();
								newRSSFields.get(i).add(s.substring(0, s.lastIndexOf(" (")));
								notEmpty = true;
							} else break;
						}

						rssDataEditor.putLong(RSSRelatedConstants.BOARDS_DATA_KEYS[i], c.getDate());
					}

				} catch (IOException | RSSReaderException e) {
					e.printStackTrace();
				}
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				rssDataEditor.apply();
			else
				rssDataEditor.commit();

			if (notEmpty)
				Notifications.setRSSNotif(this, newRSSFields);
		}
	}

}

