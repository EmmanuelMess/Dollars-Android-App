package org.dollars_bbs.thedollarscommunity.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.dollars_bbs.thedollarscommunity.IO;
import org.dollars_bbs.thedollarscommunity.R;
import org.dollars_bbs.thedollarscommunity.activities.fragments.BackPressFragment;
import org.dollars_bbs.thedollarscommunity.activities.fragments.ChatFragment;

public class ChatActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

	public static final String SEVER = "http://roadrunner-forums.com/boards/App/";
	public static final String[] PHPs = {"chat.php", "send_msg.php"};

	private static final int SELECT_PHOTO = 2;

	private final String[][] TABS = {{"GLOBAL", "LOCAL", "PRIVATE"}, {"GLOBAL", "PRIVATE"}};
	private final int REQUEST_ACCESS_COARSE_LOCATION = 1;


    private String nick;
	private boolean hasLocalizationAccess;

	// When requested, this adapter returns a Fragment, representing an object in the collection.
	PCollectionPagerAdapter collectionPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// TODO: 2016-04-09 ask only once!!!
			ActivityCompat.requestPermissions( this,
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
			// TODO: 2016-04-09 explain the permission
		} else {
			hasLocalizationAccess = ContextCompat.checkSelfPermission(getApplicationContext(),
					Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		}

		// ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
		collectionPagerAdapter = new PCollectionPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		assert mViewPager != null;
		mViewPager.setAdapter(collectionPagerAdapter);

		SharedPreferences userData = getApplicationContext()
				.getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE);
		nick = userData.getString(getString(R.string.user_file_nick), "missingno");
	}

	@Override
	public void onBackPressed() {
		BackPressFragment currentItem =
				(BackPressFragment) collectionPagerAdapter.getItem(mViewPager.getCurrentItem());
		if(currentItem.onBackPressed())
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.action_attach:
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					Uri selectedImage = imageReturnedIntent.getData();

					Intent intent = new Intent(getApplicationContext(), SendImageActivity.class);
					intent.putExtra(SendImageActivity.BITMAP, selectedImage);
					startActivityForResult(intent, 0);
				}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		hasLocalizationAccess = grantResults[0] == PackageManager.PERMISSION_GRANTED;
		collectionPagerAdapter.notifyDataSetChanged();
	}

	private class SpinnerSaveImageAsyncTask extends IO.SaveImageAsyncTask {
		@Override
		protected void onPostExecute(Bitmap o) {
			if (failed == null) ;//TODO send(setImageBitmap(o));
			else
				failed.printStackTrace();
		}
	}

	private class PCollectionPagerAdapter extends FragmentPagerAdapter {
		PCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			int fragmentType = (hasLocalizationAccess && i <= 1) || i == 0? ChatFragment.CHAT:ChatFragment.USER_LIST;
			Fragment fragment = new ChatFragment();
			Bundle args = new Bundle();
			args.putInt(ChatFragment.ARG_ITEM, fragmentType);
			args.putString(ChatFragment.ARG_NICK, nick);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if (hasLocalizationAccess)
				return 3;
			else return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TABS[hasLocalizationAccess? 0:1][position];
		}
	}

}