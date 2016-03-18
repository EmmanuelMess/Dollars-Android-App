package org.dollars_bbs.thedollarscommunity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	final String[] WEBS = {"http://roadrunner-forums.com/boards/", "http://dollars-worldwide.org/community/", "http://www.drrrchat.com/",
			"http://dollars-missions.tumblr.com/", "freerice.com", "https://www.kiva.org/"};

	WebView webView;
	ListView mainRSS;
	ProgressBar progressBar;
	ShareActionProvider mShareActionProvider;
	String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		/*
		//Enable in xml too!
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		*/

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		if (drawer != null)
			drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		assert navigationView != null;
		navigationView.setNavigationItemSelectedListener(this);
		//The first item is selected at start
		navigationView.getMenu().getItem(0).setChecked(true);
		onNavigationItemSelected(navigationView.getMenu().getItem(0));

		webView = (WebView) findViewById(R.id.webView);
		assert webView != null;
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new PWebViewClient());

		mainRSS = (ListView) findViewById(R.id.main_rss);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		switch (id) {
			case R.id.nav_rss_main:
				webView.setVisibility(View.GONE);
				mainRSS.setVisibility(View.VISIBLE);
				loadMainRSS();
				break;
			case R.id.nav_roadrunner_forum:
				connect(WEBS[0]);
				break;
			case R.id.nav_dollars_worldwide:
				connect(WEBS[1]);
				break;

			case R.id.nav_chat:
				startActivity(new Intent(getApplicationContext(), ChatActivity.class));
				break;
			case R.id.nav_chat_drrr:
				connect(WEBS[2]);//TODO check url
				break;

			case R.id.nav_tumblr:
				connect(WEBS[3]);
				break;
			case R.id.nav_map:
				break;
			case R.id.nav_free_rice:
				connect(WEBS[4]);//TODO check url
				break;
			case R.id.nav_kiva:
				connect(WEBS[5]);
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer != null)
			drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Test the connection, loads url if there's internet,
	 * shows dialog if not.
	 *
	 * @param url the url to load
	 */
	private void connect(String url) {
		mainRSS.setVisibility(View.GONE);
		webView.setVisibility(View.VISIBLE);
		webView.clearHistory();//resets the history so that you can't go back to another nav button's page
		if (isOnline()) {
			this.url = url;
			webView.loadUrl(url);
		} else {
			new AlertDialog.Builder(this)
					.setTitle("No internet")
					.setMessage("Unable to connect to the internet")
					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.create()
					.show();
		}
	}

	/**
	 * Checks for internet.
	 *
	 * @return true if there ie internet
	 */
	private boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void loadMainRSS() {

	}

	private class PWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (Uri.parse(url).getHost().equals(url) && !Utils.equal(url, WEBS[0]) && !Utils.equal(url, WEBS[1])) {
				// This is my web site, so do not override; let my WebView load the page
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
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

		private void unloadPage() {
			webView.destroyDrawingCache();

			if (Build.VERSION.SDK_INT >= 18) {
				webView.loadUrl("about:blank");
			} else {
				webView.clearView();
			}
		}
	}
}
