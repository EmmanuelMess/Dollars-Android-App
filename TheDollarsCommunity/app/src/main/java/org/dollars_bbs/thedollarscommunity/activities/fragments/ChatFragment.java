package org.dollars_bbs.thedollarscommunity.activities.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vanniktech.emoji.one.EmojiOneProvider;

import com.kosalgeek.genasync12.PostResponseAsyncTask;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;

import org.dollars_bbs.thedollarscommunity.BuildConfig;
import org.dollars_bbs.thedollarscommunity.R;
import org.dollars_bbs.thedollarscommunity.activities.ChatActivity;
import org.dollars_bbs.thedollarscommunity.backend.Message;
import org.dollars_bbs.thedollarscommunity.backend.MessageService;
import org.dollars_bbs.thedollarscommunity.backend.RetrofitLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.dollars_bbs.thedollarscommunity.Utils.equal;

/**
 * @author Emmanuel
 *         on 12/4/2017, at 01:16.
 */
public class ChatFragment extends BackPressFragment implements AdapterView.OnItemClickListener {
    public static final int CHAT = 0, USER_LIST = 1;
	public static final String ARG_ITEM = "item";
    public static final String ARG_NICK = "nick";
	private static final int CHAT_REFRESH = 1000;

	private boolean fragmentActive = false;
	private ChatFragment.ChatRefresherThread thread;
	private LayoutInflater inflater;
	private EmojiPopup emojiPopup;
    private ImageView emojiButton;
    private ShareActionProvider mShareActionProvider;
    private ArrayList<String> nicks = new ArrayList<>();
    private ArrayList<String> msgs = new ArrayList<>();
    private int FRAGMENT_TYPE;
    private ChatItemAdapter mAdapter;
    private String nick;
    private MessageService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FRAGMENT_TYPE = getArguments().getInt(ARG_ITEM);
        nick = getArguments().getString(ARG_NICK);

        // This line needs to be executed before any usage of EmojiTextView or EmojiEditText.
	    EmojiManager.install(new EmojiOneProvider());
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
            Retrofit retrofit = RetrofitLoad.loadRetrofit();

            service = retrofit.create(MessageService.class);

            View msgingLayout = view.findViewById(R.id.msgingLayout);
            assert msgingLayout != null;
            msgingLayout.setVisibility(View.VISIBLE);

            mAdapter = new ChatItemAdapter(getContext(), R.layout.item_chat, null, nicks, msgs);
				/*ListView chatBox = (ListView) view.findViewById(R.id.chatView);
				chatBox.setAdapter(mAdapter);
				chatBox.setOnItemClickListener(this);*/

            emojiButton = (ImageView) view.findViewById(R.id.main_activity_emoji);
            emojiButton.setOnClickListener(v->{
                emojiPopup.toggle();
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
	public void onResume() {
		super.onResume();
		fragmentActive = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		fragmentActive = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(thread != null)
			thread.stop();
	}

	@Override
	public boolean onBackPressed() {
		if (emojiPopup != null && emojiPopup.isShowing()) {
			emojiPopup.dismiss();
			return false;
		} else {
			return true;
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
            Message message = new Message(nick, false, msg);
            Call<Message> createCall = service.create(message);
            createCall.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> _, Response<Message> resp) {
                    if(resp.code() != 200) {
                        Toast.makeText(getContext(), "Error " + resp.code()
                                        + ": " + resp.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Message> _, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getContext(), "Error sending", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class ChatRefresherThread {
        private Handler mHandler = new Handler();
        private boolean stop;
        private boolean finished = true;
        private int lastId = 0,
                msgsNeeded = 0;

        void start() {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (/*msgsNeeded != 0 &&*/ finished && fragmentActive) {
                        finished = false;
                        HashMap<String, String> data = new HashMap<>();

						/*
						if (msgsNeeded == 0)
							data.put("lastId", "0");//TODO wtf?
						else data.put("amount", Integer.toString(msgsNeeded));
						*/

                        PostResponseAsyncTask t = new PostResponseAsyncTask(getContext(), data, (jsonString->{
                            final LinearLayout chatBox = (LinearLayout) getView().findViewById(R.id.chatLayout);

                            Call<List<Message>> createCall = service.all();
                            createCall.enqueue(new Callback<List<Message>>() {
                                @Override
                                public void onResponse(Call<List<Message>> _, Response<List<Message>> resp) {
                                    if(resp == null) {
                                        Toast.makeText(getContext(), "Client failed on reception",
                                                Toast.LENGTH_SHORT).show();
                                        finished = true;
                                        return;
                                    }

                                    if(resp.code() != 200) {
                                        Toast.makeText(getContext(), "Error " + resp.code()
                                                        + ": " + resp.message(),
                                                Toast.LENGTH_SHORT).show();
                                        finished = true;
                                        return;
                                    }

                                    chatBox.removeAllViews();

                                    for (Message b : resp.body()) {
                                        inflater.inflate(R.layout.item_chat, chatBox);

                                        View last = chatBox.getChildAt(chatBox.getChildCount()-1);

                                        ((TextView) last.findViewById(R.id.nickView)).setText(b.nick);
                                        ((TextView) last.findViewById(R.id.extraView)).setText(b.msg);
                                    }
                                    finished = true;
                                }

                                @Override
                                public void onFailure(Call<List<Message>> _, Throwable t) {
                                    t.printStackTrace();
                                    Toast.makeText(getContext(), "Error recieving", Toast.LENGTH_LONG).show();
                                    finished = true;
                                }
                            });

							/*
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
							}*/
                        }));
                        t.execute(ChatActivity.SEVER + ChatActivity.PHPs[0]);
                    }
                    if (!stop) mHandler.postDelayed(this, CHAT_REFRESH);
                }
            };

            mHandler.postDelayed(r, CHAT_REFRESH);
        }

        void stop() {
            stop = true;
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

        ChatItemAdapter(Context context, int textViewResourceId, List<Bitmap> images,
                        List<String> nicks, List<String> msgs) {
            this(context, textViewResourceId, images, nicks, null, msgs);
        }

        ChatItemAdapter(Context context, int textViewResourceId, List<Bitmap> images,
                        List<String> nicks, int[] distances, List<String> extras) {
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