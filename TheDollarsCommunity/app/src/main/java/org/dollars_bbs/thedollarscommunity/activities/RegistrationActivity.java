package org.dollars_bbs.thedollarscommunity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;

import org.dollars_bbs.thedollarscommunity.IO;
import org.dollars_bbs.thedollarscommunity.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

	private static final int SELECT_PHOTO = 1;
	private static final int MAX_LIFE_LENGTH = 100,
			MIN_LIFE_LENGTH = 7;
	private static final String DATE_PICKER_TAG = "datepicker";

	private SharedPreferences.Editor userDataEditor;
	private static Button birthB;
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
		assert imageB != null;

		View.OnClickListener imageL = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
		};

		imageB.setOnClickListener(imageL);

		final Button registerB = (Button) findViewById(R.id.registerButton);
		assert registerB != null;

		TextInputEditText nicknameT = (TextInputEditText) findViewById(R.id.nickEdit);
		assert nicknameT != null;

		nicknameT.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				registerB.setEnabled(s.length() != 0);
				userDataEditor.putString(getString(R.string.user_file_nick), s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		TextInputEditText descriptionT = (TextInputEditText) findViewById(R.id.descriptionEdit);
		assert descriptionT != null;

		descriptionT.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				userDataEditor.putString(getString(R.string.user_file_description), s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		Spinner genderS = (Spinner) findViewById(R.id.spinner);
		assert genderS != null;

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		genderS.setAdapter(adapter);

		genderS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				userDataEditor.putString(getString(R.string.user_file_gender), getResources().getStringArray(R.array.gender_array)[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - MIN_LIFE_LENGTH);

		birthB = (Button) findViewById(R.id.button2);
		assert birthB != null;
		birthB.setText(date(calendar, userDataEditor));

		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
				calendar.set(year, month, day);
				birthB.setText(date(calendar, userDataEditor));
			}

		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setYearRange(calendar.get(Calendar.YEAR) - MAX_LIFE_LENGTH, calendar.get(Calendar.YEAR) - MIN_LIFE_LENGTH);
		datePickerDialog.setVibrate(false);
		datePickerDialog.setCloseOnSingleTapDay(false);

		birthB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datePickerDialog.show(getSupportFragmentManager(), DATE_PICKER_TAG);
			}
		});

		View.OnClickListener registerL = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userDataEditor.putInt(getString(R.string.user_file_registered), 1);

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
					userDataEditor.apply();
				else
					userDataEditor.commit();

				startActivity(new Intent(getApplicationContext(), ChatActivity.class));
			}
		};

		registerB.setOnClickListener(registerL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch(requestCode) {
			case SELECT_PHOTO:
				if(resultCode == RESULT_OK){
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

	private class SpinnerSaveImageAsyncTask extends IO.SaveImageAsyncTask {
		@Override
		protected void onPreExecute() {
			imageProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Bitmap o) {
			if(failed == null)
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

		return 	new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(d.getTime()); // TODO: 2016-03-25 make month lowercase
	}

}
