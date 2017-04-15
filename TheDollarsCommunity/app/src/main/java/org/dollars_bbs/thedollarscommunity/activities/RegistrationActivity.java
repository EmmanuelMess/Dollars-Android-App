package org.dollars_bbs.thedollarscommunity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;

import org.dollars_bbs.thedollarscommunity.IO;
import org.dollars_bbs.thedollarscommunity.R;
import org.dollars_bbs.thedollarscommunity.Utils;
import org.dollars_bbs.thedollarscommunity.backend.RetrofitLoad;
import org.dollars_bbs.thedollarscommunity.backend.User;
import org.dollars_bbs.thedollarscommunity.backend.UserService;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

	private static final int SELECT_PHOTO = 1;
	private static final int MAX_LIFE_LENGTH = 100,
			MIN_LIFE_LENGTH = 7;
	private static final String DATE_PICKER_TAG = "datepicker";

	private SharedPreferences.Editor userDataEditor;
	private TextInputEditText nicknameT, descriptionT;
	private Spinner genderS;
	private Calendar calendar;
	private DatePickerDialog datePickerDialog;
	private Button birthB;
	private ImageButton imageB;
	private ProgressBar imageProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// TODO: 2016-04-09 ask for EXTERNAL_STORAGE permission

		SharedPreferences userData = getApplicationContext().getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE);
		userDataEditor = userData.edit();

		imageProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		imageB = (ImageButton) findViewById(R.id.userImageButton);

		View.OnClickListener imageL = v -> {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
		};

		imageB.setOnClickListener(imageL);

		final Button registerB = (Button) findViewById(R.id.registerButton);

		nicknameT = (TextInputEditText) findViewById(R.id.nickEdit);

		nicknameT.addTextChangedListener(new Utils.SimpleOnTextChanged() {
			public void afterTextChanged(Editable s) {
				registerB.setEnabled(s.length() != 0);
			}
		});

		descriptionT = (TextInputEditText) findViewById(R.id.descriptionEdit);

		genderS = (Spinner) findViewById(R.id.spinner);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderS.setAdapter(adapter);

		calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - MIN_LIFE_LENGTH);

		birthB = (Button) findViewById(R.id.button2);
		birthB.setText(date(calendar, userDataEditor));

		datePickerDialog = DatePickerDialog.newInstance((datePickerDialog1, year, month, day) -> {
			calendar.set(year, month, day);
			birthB.setText(date(calendar, userDataEditor));
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setYearRange(calendar.get(Calendar.YEAR) - MAX_LIFE_LENGTH,
				calendar.get(Calendar.YEAR) - MIN_LIFE_LENGTH);
		datePickerDialog.setVibrate(false);
		datePickerDialog.setCloseOnSingleTapDay(false);

		birthB.setOnClickListener(v -> datePickerDialog.show(getSupportFragmentManager(), DATE_PICKER_TAG));

		registerB.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					try {
						Uri selectedImage = imageReturnedIntent.getData();
						Bitmap userImage = IO.decodeUri(selectedImage, 100, true, getContentResolver());
						(new SpinnerSaveImageAsyncTask()).execute(userImage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
		}
	}

	@Override
	public void onClick(View v) {
		String nick = nicknameT.getText().toString();
		long birth = 0l;//datePickerDialog.getSelectedDay().;
		String desc = descriptionT.getText().toString(),
				gender = getResources().getStringArray(R.array.gender_array)[genderS.getSelectedItemPosition()];

		userDataEditor.putString(getString(R.string.user_file_nick), nick);
		userDataEditor.putString(getString(R.string.user_file_description), desc);
		userDataEditor.putString(getString(R.string.user_file_gender), gender);
		userDataEditor.putInt(getString(R.string.user_file_registered), 1);
		userDataEditor.apply();

		User user = new User(nick, birth, desc, gender);
		UserService service = RetrofitLoad.loadRetrofit().create(UserService.class);
		Call<User> createCall = service.create(user);
		createCall.enqueue(new Callback<User>() {
			@Override
			public void onResponse(Call<User> _, Response<User> resp) {
				if (resp.code() != 200) {
					Toast.makeText(getApplicationContext(), "Error " + resp.code() + ": " + resp.message(),
							Toast.LENGTH_SHORT).show();
				} else {
					startActivity(new Intent(getApplicationContext(), ChatActivity.class));
				}
			}

			@Override
			public void onFailure(Call<User> _, Throwable t) {
				t.printStackTrace();
				Toast.makeText(getApplicationContext(), "Error sending", Toast.LENGTH_LONG).show();
			}
		});
	}

	private class SpinnerSaveImageAsyncTask extends IO.SaveImageAsyncTask {
		@Override
		protected void onPreExecute() {
			imageProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Bitmap o) {
			if (failed == null)
				imageB.setImageBitmap(o);
			else
				failed.printStackTrace();

			imageProgressBar.setVisibility(View.GONE);
			super.onPostExecute(o);
		}

	}

	private String date(Calendar d, SharedPreferences.Editor editor) {
		editor.putInt(getString(R.string.user_file_birth_day), d.get(Calendar.DAY_OF_MONTH));
		editor.putInt(getString(R.string.user_file_birth_month), d.get(Calendar.MONTH));
		editor.putInt(getString(R.string.user_file_birth_year), d.get(Calendar.YEAR));

		return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(d.getTime()); // TODO: 2016-03-25 make month lowercase
	}

}
