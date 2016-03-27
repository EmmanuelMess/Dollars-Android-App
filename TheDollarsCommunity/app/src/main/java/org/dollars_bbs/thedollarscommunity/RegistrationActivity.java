package org.dollars_bbs.thedollarscommunity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.fourmob.datetimepicker.date.DatePickerDialog;

public class RegistrationActivity extends AppCompatActivity {

	private static final int SELECT_PHOTO = 100;
	private static final int MAX_LIFE_LENGTH = 100,
			MIN_LIFE_LENGTH = 7;
	private static final String DATE_PICKER_TAG = "datepicker";
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

		SharedPreferences userData = getApplicationContext().getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE);
		final SharedPreferences.Editor userDataEditor = userData.edit();

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

		EditText nicknameT = (EditText) findViewById(R.id.nickEdit);
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
						Bitmap userImage = decodeUri(selectedImage, 100, true);
						(new SaveAsyncTask()).execute(userImage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
		}
	}

	/**
	 * Downsamples images: http://stackoverflow.com/a/5086706/3124150 (modified).
	 *
	 * @param selectedImage image to downsample
	 * @param REQUIRED_SIZE the size you want
	 * @param isWidth if the REQUIRED_SIZE value is the height
	 * @return downsample'd bitmap
	 * @throws FileNotFoundException if there's no image
	 */
	private Bitmap decodeUri(Uri selectedImage, final int REQUIRED_SIZE, boolean isWidth) throws FileNotFoundException {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if(isWidth) {
				if (width_tmp/2 < REQUIRED_SIZE) {
					break;
				}
			} else {
				if(height_tmp/2 < REQUIRED_SIZE) {
					break;
				}
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

	}

	private class SaveAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private Exception failed;

		@Override
		protected void onPreExecute() {
			imageProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			try {
				IO.saveImage(params[0], IO.USER_IMAGE);
			} catch (IOException e) {
				failed = e;
				return null;
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(Bitmap o) {
			imageProgressBar.setVisibility(View.GONE);

			if(failed == null)
				imageB.setImageBitmap(o);
			else
				failed.printStackTrace();

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
