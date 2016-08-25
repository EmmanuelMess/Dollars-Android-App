package org.dollars_bbs.thedollarscommunity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.dollars_bbs.thedollarscommunity.activities.ChatActivity;
import org.dollars_bbs.thedollarscommunity.activities.RegistrationActivity;
import org.dollars_bbs.thedollarscommunity.activities.SettingsActivity;
import org.dollars_bbs.thedollarscommunity.rss_io.RSSScheduledServiceHelper;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {
	public static final String FROM_NOTIFICATION = "from notification",
			FIRST_OPEN = "first open";

	private static final String DOLLARS_MAP = "https://www.google.com/maps/d/edit?mid=z2X8CpD7CsTQ.kxb7k1wenQa4";
	private final String[] WEBS = {"http://roadrunner-forums.com/boards/", "http://dollars-worldwide.org/community/", "http://www.drrrchat.com/",
					"http://drrr.com/",	"http://dollars-missions.tumblr.com/", "http://freerice.com", "https://www.kiva.org/",
					"http://roadrunner-forums.com/boards/index.php?action=vthread&forum=6&topic=8#msg25"};


	private WebView webView;
	private ListView mainRSS;
	private int currentRSSFeedNum = 0, rssLoadFailed = -1;
	private RSSFeed RSSFeeds[] = new RSSFeed[RSSRelatedConstants.RSS.length];
	private ProgressBar progressBar;
	private ShareActionProvider mShareActionProvider;
	private NavigationView navigationView;
	private boolean isRegistered;
	private AsyncTask asyncWebViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// TODO: 2016-04-09 ask for INTERNET permission

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		if (drawer != null)
			drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		assert navigationView != null;
		navigationView.setNavigationItemSelectedListener(this);

		webView = (WebView) findViewById(R.id.webView);
		assert webView != null;
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new PWebViewClient());

		mainRSS = (ListView) findViewById(R.id.main_rss);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(pref.getBoolean(FIRST_OPEN, true)) {
			SharedPreferences.Editor prefEdit = pref.edit();

			for(int j = 0; j < RSSRelatedConstants.RSS.length; j++) {
				prefEdit.putBoolean(RSSRelatedConstants.BOARDS_KEYS[j], (j == 0 || j == 1 || j == 2 || j == 11));

			}
			prefEdit.putBoolean(FIRST_OPEN, false);

			prefEdit.apply();


			if(pref.getBoolean(RSSRelatedConstants.NOTIF_KEYS[0], true))
				RSSScheduledServiceHelper.startScheduled(this);
		}

		Menu menu = navigationView.getMenu();
		for(int i = 0; i < RSSRelatedConstants.RSS.length; i++) {
			if (pref.getBoolean(RSSRelatedConstants.BOARDS_KEYS[i], false))
				menu.add(R.id.group_rss, i, 0, getString(RSSRelatedConstants.BOARDS_TITLE_KEYS[i]))
						.setIcon(R.drawable.ic_rss_feed_white_24dp);
			else if(menu.findItem(i) != null)
				menu.removeItem(i);
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		if(getIntent().getBooleanExtra(FROM_NOTIFICATION, false))
			Notifications.resetRSSNotif(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		//The first item is selected
		navigationView.getMenu().getItem(0).setChecked(true);
		onNavigationItemSelected(navigationView.getMenu().getItem(0));

		View headerLayout = navigationView.getHeaderView(0);
		assert headerLayout != null;
		SharedPreferences userData = getApplicationContext().getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE);
		isRegistered = userData.getInt(getString(R.string.user_file_registered), 0) == 1;

		if(isRegistered) {
			Bitmap userImage = null;
			try {
				userImage = IO.recoverImage(IO.USER_IMAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (userImage != null)
				((ImageView) headerLayout.findViewById(R.id.userImage)).setImageBitmap(userImage);

			((TextView) headerLayout.findViewById(R.id.nickText)).setText(userData.getString(getString(R.string.user_file_nick), "missingno"));
		} else {
			headerLayout.findViewById(R.id.navHeader).setVisibility(View.GONE);
		}
	}


	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		//Gets the compat share action provider
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.action_share:
				//Creates intent and shares
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.setType("text/plain");
				sendIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
				mShareActionProvider.setShareIntent(sendIntent);
				return true;
			case R.id.action_contact:
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		webView.clearHistory();//resets the history so that you can't go back to another nav button's page
		currentRSSFeedNum = -1;
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if(item.getSubMenu() == findViewById(R.id.group_rss))
			for(int i = 0; i < RSSRelatedConstants.BOARDS_TITLE_KEYS.length; i++)
				if(getString(RSSRelatedConstants.BOARDS_TITLE_KEYS[i]) == item.getTitle())
					loadRSS(i);
		else {
			switch (id) {
				case R.id.nav_roadrunner_forum:
					connect(WEBS[0]);
					break;
				case R.id.nav_dollars_worldwide:
					connect(WEBS[1]);
					break;

				case R.id.nav_chat:
					if (isRegistered)
						startActivity(new Intent(getApplicationContext(), ChatActivity.class));
					else
						startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
					break;
				case R.id.nav_chat_durarara:
					connect(WEBS[2]);//TODO check url
					break;
				case R.id.nav_chat_dollars_drrr:
					connect(WEBS[3]);//TODO check url
					break;

				case R.id.nav_tumblr:
					connect(WEBS[4]);
					break;
				case R.id.nav_map:
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
							Uri.parse(DOLLARS_MAP));
					startActivity(intent);
					break;
				case R.id.nav_free_rice:
					connect(WEBS[5]);
					break;
				case R.id.nav_kiva:
					connect(WEBS[6]);
					break;

				case R.id.nav_settings:
					if (!BuildConfig.DEBUG)
						Toast.makeText(getApplicationContext(), "Not yet", Toast.LENGTH_LONG).show();
					else
						startActivity(new Intent(getApplicationContext(), SettingsActivity.class));// TODO: 2016-04-10
					break;
				case R.id.nav_feedback:
					connect(WEBS[7]);//TODO check url
					break;
			}
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer != null)
			drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		if (rssLoadFailed != -1) {
			loadRSS(rssLoadFailed);
		} else {
			connect(RSSFeeds[currentRSSFeedNum].getItems().get(position).getLink().toString());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();//Goes back to last page
				return true;
			} else if (currentRSSFeedNum != -1 && mainRSS.getVisibility() == View.GONE) {//Goes back to RSS list
				unloadPage();
				loadRSS(currentRSSFeedNum);
				return true;
			}
		}
		// Keybubble up to the default system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Test the connection, loads url if there's internet,
	 * shows dialog if not.
	 *
	 * @param web the url to load
	 */
	private void connect(String web) {
		mainRSS.setVisibility(View.GONE);
		webView.setVisibility(View.VISIBLE);

		Uri uri = Uri.parse(web);

		if (showErrorIfOffline()) {
			boolean isFromDollarsBBS = Utils.equal(uri.getHost(), RSSRelatedConstants.DOLLARS_BBS);
			webView.getSettings().setBuiltInZoomControls(!isFromDollarsBBS);

			if (isFromDollarsBBS) {
				asyncWebViewLoader = new AsyncTask<Void, Void, String>() {
					public void onPreExecute() {
						progressBar.setVisibility(View.VISIBLE);
					}

					public String doInBackground(Void v[]) {
						try {
							HttpURLConnection c = (HttpURLConnection) new URL(web).openConnection();
							c.connect();

							String html = "";
							InputStream content = (InputStream) c.getContent();

							BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
							String s = "";
							while ((s = buffer.readLine()) != null) {
								html += s;
							}

							int start = html.indexOf("<ul id=\"pagemenu\">"),
									end = html.indexOf("<div id=\"posts\">");
							html = html.substring(0, start) + html.substring(end);//This deletes the header

							start = html.indexOf("<div id=\"sidebar\">");
							end = html.indexOf("<div id=\"footer\">");
							html = html.substring(0, start) + html.substring(end);//This deletes the sidebar

							html = html.replace("</head>",
									"<style media=\"screen\" type=\"text/css\">" +
									"body {" +
										"width: inherit;" +
									"}" +
									"#posts {" +
										"float: none;" +
										"width: inherit;" +
									"}" +
									"#posts .thread {" +
										"padding: 0px;" +
									"}" +
									"#posts .allreplies {" +
										"margin: 0px;" +
										"padding: 15px;" +
									"}" +
									"#posts .reply {" +
										"margin: 1px;" +
										"padding: 0px;" +
									"}" +
									".replytext {" +
										"padding: 10px;" +
									"}" +
									"</style>" +
									"</head>");

							return html;
						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}
					}

					public void onPostExecute(String html) {
						progressBar.setVisibility(View.GONE);
						webView.loadDataWithBaseURL("http://dollars-bbs.org/main/css/", html, "text/html", "UTF-8", null);
					}
				}.execute();
			} else
				webView.loadUrl(web);
		}
	}

	/**
	 * Loads the RSS list, shows error if something fails.
	 */
	private void loadRSS(final int RSSNumber) {// TODO: 2016-03-26 Cache to accelerate next opening
		webView.setVisibility(View.GONE);
		mainRSS.setVisibility(View.VISIBLE);

		if (showErrorIfOffline()) {
			AdapterView.OnItemClickListener l = this;

			new AsyncTask<Void, Void, Void>() {
				ArrayList<String> items = new ArrayList<>();

				public void onPreExecute() {
					progressBar.setVisibility(View.VISIBLE);
					currentRSSFeedNum = RSSNumber;
				}

				public Void doInBackground(Void v[]) {
					if (RSSFeeds[RSSNumber] == null) {
						try {
							RSSFeeds[RSSNumber] = new RSSReader().load(RSSRelatedConstants.RSS[RSSNumber]);
						} catch (RSSReaderException e) {
							failedFetch(RSSNumber);
						}
					}

					return null;
				}

				public void onPostExecute(Void v) {
					if (RSSFeeds[RSSNumber] != null) {
						for (int i = 0; i < 20 && RSSFeeds[RSSNumber].getItems().size() > i; i++)
							items.add(RSSFeeds[RSSNumber].getItems().get(i).getTitle());

						rssLoadFailed = -1;
					} else {
						items.add(getApplicationContext().getString(R.string.failed_feed_load));
					}

					progressBar.setVisibility(View.GONE);

					PArrayAdapter adapter = new PArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, items);
					mainRSS.setAdapter(adapter);
					mainRSS.setOnItemClickListener(l);
				}
			}.execute();
		}
	}

	private void failedFetch(int rss) {
		Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.failed_feed_fetch),
				Toast.LENGTH_SHORT).show();
		rssLoadFailed = rss;
	}

	private boolean showErrorIfOffline() {
		if (Utils.isOnline() || Build.FINGERPRINT.contains("generic")) {//isOnline() DOES NOT work on some emulators, hack from here: http://stackoverflow.com/a/5864867/3124150
			return true;
		} else {
			new AlertDialog.Builder(this)
					.setTitle("No internet")
					.setMessage("Unable to connect to the internet")
					.setPositiveButton(android.R.string.ok, (dialog, which)->{

					})
					.create()
					.show();
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public void unloadPage() {
		webView.destroyDrawingCache();

		if (asyncWebViewLoader != null && asyncWebViewLoader.getStatus() != AsyncTask.Status.FINISHED) {
			progressBar.setVisibility(View.GONE);
			asyncWebViewLoader.cancel(true);
		} else
			asyncWebViewLoader = null;


		if (Build.VERSION.SDK_INT >= 18) {
			webView.loadUrl("about:blank");
		} else {
			webView.clearView();
		}
	}

	private class PWebViewClient extends WebViewClient {// TODO: 2016-03-20 add resizing capabilities to the WebView 

		//All the webs that don't require selected links to be loaded on other browser
		private final String[] SELECT_WEBS = {WEBS[0],  WEBS[1],  WEBS[2], WEBS[3], WEBS[4], WEBS[7]};

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
				return false;
			else
				return overrideUrlLoading(Uri.parse(url));

		}

		@TargetApi(Build.VERSION_CODES.N)
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			return overrideUrlLoading(request.getUrl());
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			unloadPage();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}

		private boolean overrideUrlLoading(Uri url) {
			if (isSelectWeb(url.toString()))// This is my web site, so do not override; let my WebView load the page
				return false;
			
			// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, url);
			startActivity(intent);
			return true;
		}

		private boolean isSelectWeb(String web) {
			for(String w : SELECT_WEBS) {
				if(Utils.equal(w, web)) return true;
			}

			return false;
		}
	}

	private class PArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<>();

		public PArrayAdapter(Context context, int textViewResourceId,
		                     List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

}
