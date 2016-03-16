package org.dollars_bbs.thedollarscommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	WebView webView;

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
		if(drawer != null)
			drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if(navigationView != null)
			navigationView.setNavigationItemSelectedListener(this);

		webView = (WebView) findViewById(R.id.webView);
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_contact) {

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		switch(id) {
			case R.id.nav_rss_main:
				break;
			case R.id.nav_roadrunner_forum:
				break;
			case R.id.nav_dollars_worldwide:
				connect("http://www.dollars-worldwide.org/");//TODO check url
				break;

			case R.id.nav_chat_all:
				break;
			case R.id.nav_chat_local:
				break;
			case R.id.nav_chat_drrr:
				connect("http://www.drrrchat.com/");//TODO check url
				break;

			case R.id.nav_share:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.setType("text/url");
				startActivity(Intent.createChooser(sendIntent, webView.getUrl()));
				break;
			//case R.id.nav_send:
			//	break;

			case R.id.nav_tumblr:
				connect("http://dollars-missions.tumblr.com/");//TODO check url
				break;
			case R.id.nav_map:
				break;
			case R.id.nav_free_rice:
				connect("freerice.com");//TODO check url
				break;
			case R.id.nav_kiva:
				connect("https://www.kiva.org/");
				break;

			default:
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if(drawer != null)
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
		webView.clearHistory();//resets the history so that you can't go back to another nav button's page
		if(isOnline()) {
			webView.loadUrl(url);
		} else {
			new AlertDialog.Builder(this)
					.setTitle("No internet")
					.setMessage("Unable to connect to the internet")
					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
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
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(); }

		return false;
	}
}
