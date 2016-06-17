package org.dollars_bbs.thedollarscommunity.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.dollars_bbs.thedollarscommunity.BuildConfig;
import org.dollars_bbs.thedollarscommunity.IO;
import org.dollars_bbs.thedollarscommunity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.dollars_bbs.thedollarscommunity.Utils.equal;

public class ChatActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

	private static final String SEVER = "http://roadrunner-forums.com/boards/App/";
	private static final String[] PHPs = {"chat.php", "send_msg.php"};
	private final String[][] TABS = {{"GLOBAL", "LOCAL", "PRIVATE"}, {"GLOBAL", "PRIVATE"}};
	private final int REQUEST_ACCESS_COARSE_LOCATION = 1;
	private static final int SELECT_PHOTO = 2;
	private static final int CHAT_REFRESH = 1000;

	private ShareActionProvider mShareActionProvider;
	private boolean hasLocalizationAccess;
	private static ChatFragment.ChatRefresherThread thread;
	private static EmojiPopup emojiPopup;
	private static ImageView emojiButton;

	// When requested, this adapter returns a Fragment, representing an object in the collection.
	PCollectionPagerAdapter mCollectionPagerAdapter;
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

		if (ContextCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: 2016-04-09 ask only once!!!
			ActivityCompat.requestPermissions((RegistrationActivity) getApplicationContext(),
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

			// TODO: 2016-04-09 explain the permission
		} else {
			hasLocalizationAccess = ContextCompat.checkSelfPermission(getApplicationContext(),
					Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		}

		// ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
		mCollectionPagerAdapter = new PCollectionPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		assert mViewPager != null;
		mViewPager.setAdapter(mCollectionPagerAdapter);
	}

	@Override
	public void onBackPressed() {
		if (emojiPopup != null && emojiPopup.isShowing()) {
			emojiPopup.dismiss();
		} else {
			super.onBackPressed();
		}
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
		mCollectionPagerAdapter.notifyDataSetChanged();
	}

	private class SpinnerSaveImageAsyncTask extends IO.SaveImageAsyncTask {
		@Override
		protected void onPostExecute(Bitmap o) {
			if (failed == null) ;//TODO send(setImageBitmap(o));
			else
				failed.printStackTrace();
		}
	}

	public class PCollectionPagerAdapter extends FragmentPagerAdapter {
		public PCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			int fragmentType = (hasLocalizationAccess && i <= 1) || i == 0? ChatFragment.CHAT:ChatFragment.USER_LIST;
			Fragment fragment = new ChatFragment();
			Bundle args = new Bundle();
			args.putInt(ChatFragment.ARG_ITEM, fragmentType);
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

	// Instances of this class are fragments representing a single object in our collection.
	public static class ChatFragment extends Fragment implements AdapterView.OnItemClickListener, AsyncResponse {
		public static final int CHAT = 0, USER_LIST = 1;
		public static final String ARG_ITEM = "item";

		private ArrayList<String> nicks = new ArrayList<>();
		private ArrayList<String> msgs = new ArrayList<>();
		private int FRAGMENT_TYPE;
		private SharedPreferences userData;
		private ChatItemAdapter mAdapter;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			FRAGMENT_TYPE = getArguments().getInt(ARG_ITEM);
			userData = getContext().getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// The last two arguments ensure LayoutParams are inflated properly.
			return inflater.inflate(R.layout.fragment_chat, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			if (FRAGMENT_TYPE == CHAT) {
				View msgingLayout = view.findViewById(R.id.msgingLayout);
				assert msgingLayout != null;
				msgingLayout.setVisibility(View.VISIBLE);

				mAdapter = new ChatItemAdapter(getContext(), R.layout.item_chat, null, nicks, msgs);
				ListView chatBox = (ListView) view.findViewById(R.id.chatView);
				chatBox.setAdapter(mAdapter);
				chatBox.setOnItemClickListener(this);

				emojiButton = (ImageView) view.findViewById(R.id.main_activity_emoji);
				emojiButton.setOnClickListener(v->{
					//emojiPopup.toggle();
				});

				final EmojiEditText msgText = (EmojiEditText) view.findViewById(R.id.emojiEditText);
				assert msgText != null;
				msgText.setVisibility(View.VISIBLE);

				emojiPopup = EmojiPopup.Builder.fromRootView(msgingLayout)
						.setOnEmojiPopupShownListener(()->emojiButton.setImageResource(R.drawable.ic_keyboard_24dp))
						.setOnEmojiPopupDismissListener(()->emojiButton.setImageResource(R.drawable.ic_mood_24dp))
						.setOnSoftKeyboardCloseListener(()->emojiPopup.dismiss()).build(msgText);

				msgText.setOnEditorActionListener((v, actionId, event)->{
					if (actionId == EditorInfo.IME_ACTION_SEND) {
						send(msgText.getText().toString());
						return true;
					}
					return false;
				});

				ImageView send = (ImageView) view.findViewById(R.id.sendImg);
				send.setOnClickListener(view1->send(msgText.getText().toString()));

				thread = new ChatRefresherThread();
				thread.start();
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO: 2016-03-30 create dialog for profile or private messaging
		}

		/**
		 * DO NOT modify the chat from here!
		 *
		 * @param msg the message
		 */
		private void send(String msg) {
			if (BuildConfig.DEBUG && FRAGMENT_TYPE != CHAT) throw new AssertionError();
			if (getView() != null && !equal(msg.replace(" ", ""), "")) {
				((EditText) getView().findViewById(R.id.emojiEditText)).setText("");
				HashMap<String, String> data = new HashMap<>();
				data.put("time", Float.toString(System.currentTimeMillis()));
				data.put("chat", "global");
				data.put("nick", userData.getString(getString(R.string.user_file_nick), "missingno"));
				data.put("isText", Integer.toString(0));
				data.put("msg", msg);

				PostResponseAsyncTask t = new PostResponseAsyncTask(getContext(), data, this);
				t.execute(SEVER + PHPs[1]);
			}
		}

		@Override
		public void processFinish(String jsonString) {
			// TODO: 2016-04-15 check if msg was sent
		}

		private class ChatRefresherThread {
			private Handler mHandler = new Handler();
			private boolean stop;
			private boolean finished = true;
			private int lastId = 0,
					msgsNeeded = 0;

			public void start() {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						if (msgsNeeded != 0 && finished) {
							finished = false;
							HashMap<String, String> data = new HashMap<>();
							if (msgsNeeded == 0)
								data.put("lastId", "0");
							else
								data.put("amount", Integer.toString(msgsNeeded));

							PostResponseAsyncTask t = new PostResponseAsyncTask(getContext(), data, (jsonString->{
								final ListView chatBox = (ListView) getView().findViewById(R.id.chatView);
								assert chatBox != null;

								if (msgsNeeded == 0) {
									ArrayList<NewMsgsModelClass> chat = new JsonConverter<NewMsgsModelClass>().toArrayList(jsonString, NewMsgsModelClass.class);
									msgsNeeded = chat.get(chat.size() - 1).id - lastId;
								} else {
									ArrayList<ChatMsgModelClass> chat = new JsonConverter<ChatMsgModelClass>().toArrayList(jsonString, ChatMsgModelClass.class);
									lastId = chat.get(chat.size() - 1).id;
									msgsNeeded = chat.get(chat.size() - 1).id - lastId;
									for (int i = 0; i < chat.size(); i++) {
										nicks.add(chat.get(i).nick);

										if (chat.get(i).isImage) {
											// TODO: 2016-04-16 send img
										} else {
											msgs.add(chat.get(i).message);
										}
									}

									mAdapter.notifyDataSetChanged();
									finished = true;
								}
							}));
							t.execute(SEVER + PHPs[0]);
						}
						if (!stop) mHandler.postDelayed(this, CHAT_REFRESH);
					}
				};
				mHandler.postDelayed(r, CHAT_REFRESH);
			}

			public void stop() {
				stop = true;
			}

			private class NewMsgsModelClass {
				@SerializedName("id")
				public int id;
			}

			private class ChatMsgModelClass {
				@SerializedName("id")
				public int id;

				@SerializedName("nick")
				public String nick;

				@SerializedName("isImage")
				public boolean isImage;

				@SerializedName("msg")
				public String message;
			}
		}

		private class ChatItemAdapter extends ArrayAdapter<String> {

			HashMap<String, Integer> mExtrasMap = new HashMap<>();
			List<Bitmap> mImages = null;
			List<String> mNicks = null;
			int[] mDistances = null;
			/**
			 * Extras can be used either as message or description
			 */
			List<String> mExtras = null;

			public ChatItemAdapter(Context context, int textViewResourceId, List<Bitmap> images, List<String> nicks, List<String> msgs) {
				this(context, textViewResourceId, images, nicks, null, msgs);
			}

			public ChatItemAdapter(Context context, int textViewResourceId, List<Bitmap> images, List<String> nicks, int[] distances,
			                       List<String> extras) {
				super(context, textViewResourceId, extras);
				mImages = images;
				mNicks = nicks;
				if (distances != null)
					mDistances = distances;
				mExtras = extras;

				for (int i = 0; i < extras.size(); i++)
					mExtrasMap.put(extras.get(i), i);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = convertView != null? convertView:inflater.inflate(R.layout.item_chat, parent, false);//May need to check for appropriate type

				//ImageView avatar = (ImageView) rowView.findViewById(R.id.avatarImage);
				//avatar.setImageBitmap(mImages[position]);

				TextView nick = (TextView) rowView.findViewById(R.id.nickView);
				nick.setText(mNicks.get(position));

				if (mDistances != null) {
					rowView.findViewById(R.id.distanceLayout).setVisibility(View.VISIBLE);
					TextView dists = (TextView) rowView.findViewById(R.id.distanceView);
					dists.setText(mDistances[position]);
				}

				TextView msg = (TextView) rowView.findViewById(R.id.extraView);
				msg.setText(mExtras.get(position));

				return rowView;
			}


			@Override
			public long getItemId(int position) {
				String item = getItem(position);
				return mExtrasMap.get(item);
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}
		}
	}

}