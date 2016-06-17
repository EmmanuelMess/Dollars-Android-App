package org.dollars_bbs.thedollarscommunity.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import org.dollars_bbs.thedollarscommunity.IO;
import org.dollars_bbs.thedollarscommunity.R;

import java.io.FileNotFoundException;

public class SendImageActivity extends AppCompatActivity {

	public static final String BITMAP = "bitmap";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_image);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		assert imageView != null;

		Bitmap image = null;
		try {
			image = IO.decodeUri(getIntent().getExtras().getParcelable(BITMAP), 100, true, getContentResolver());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(), ChatActivity.class));
		}

		imageView.setImageBitmap(image);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(view->{
			// TODO: 2016-03-30
		});
	}

}
