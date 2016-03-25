package org.dollars_bbs.thedollarscommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		View.OnClickListener c = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView t = ((TextView) findViewById(R.id.textView));
				assert t != null;
				if (Utils.equal(t.getText().toString(), "baccano")) {
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					//Clears the Stack so that BACK won't lead here.
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);

				} else
					Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
			}
		};

		View b = findViewById(R.id.button);
		if(b != null) b.setOnClickListener(c);
	}

}
