package org.dollars_bbs.thedollarscommunity.rss_io;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import org.dollars_bbs.thedollarscommunity.MainActivity;
import org.dollars_bbs.thedollarscommunity.Notifications;
import org.dollars_bbs.thedollarscommunity.R;
import org.dollars_bbs.thedollarscommunity.Utils;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * @author Emmanuel
 *         on 2016-07-20, at 16:21.
 */
public class RSSCheckService extends IntentService {

	public static final String[] BOARDS_KEYS = {"mainData", "missionData", "newsData", "animationData", "artData",
			"comicsData", "filmsData", "foodsData", "gamesData", "literatureData", "musicData", "personalData",
			"sportsData", "technologyData", "randomData"};

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
			SharedPreferences rssData = getApplicationContext().getSharedPreferences(getString(R.string.rss_file_key), Context.MODE_PRIVATE);
			Map<Integer, ArrayList<String>> newRSSFields = new HashMap<>(MainActivity.RSS.length);
			boolean notEmpty = false;

			for (int i = 0; i < BOARDS_KEYS.length; i++) {
				try {
					String s = rssData.getString(BOARDS_KEYS[i], "0");
					HttpURLConnection c = (HttpURLConnection) new URL("").openConnection();
					c.connect();
					c.setRequestProperty("If-Modified-Since", s);

					if (c.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
						newRSSFields.put(i, new ArrayList<>());
						SharedPreferences.Editor e = rssData.edit().putString(s, c.getHeaderField("Last-Modified"));

						RSSFeed r = new RSSReader().load(MainActivity.RSS[i]);
						Date d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(c.getHeaderField("Last-Modified"));

						for (RSSItem elem : r.getItems())
							if (elem.getPubDate().after(d))
								newRSSFields.get(i).add(elem.getTitle());

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
							e.apply();
						else
							e.commit();

						notEmpty = true;
					}
				} catch (IOException | RSSReaderException | ParseException e) {
					e.printStackTrace();
				}
			}

			if(notEmpty)
				Notifications.setRSSNotif(this, newRSSFields);
		}
	}
}

