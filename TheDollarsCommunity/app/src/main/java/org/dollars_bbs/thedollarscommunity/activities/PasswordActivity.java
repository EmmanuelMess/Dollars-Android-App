package org.dollars_bbs.thedollarscommunity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.dollars_bbs.thedollarscommunity.BuildConfig;
import org.dollars_bbs.thedollarscommunity.MainActivity;
import org.dollars_bbs.thedollarscommunity.R;

import static org.dollars_bbs.thedollarscommunity.Utils.equal;

public class PasswordActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		View.OnClickListener c = v->{
			TextView t = ((TextView) findViewById(R.id.textView));
			assert t != null;
			if (equal(t.getText().toString(), "baccano")) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				//Clears the Stack so that BACK won't lead here.
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

			} else
				Snackbar.make(v, getString(R.string.wrong), Snackbar.LENGTH_LONG).setAction("Action", null).show();
		};

		View b = findViewById(R.id.button);
		if(BuildConfig.DEBUG) ((TextView) findViewById(R.id.textView)).setText("baccano");
		b.setOnClickListener(c);
	}

}
