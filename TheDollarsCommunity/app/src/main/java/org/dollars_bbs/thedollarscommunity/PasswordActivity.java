package org.dollars_bbs.thedollarscommunity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

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
				if(t != null) {
					if (equal(t.getText().toString(), "baccano"))
						startActivity(new Intent(getApplicationContext(), MainActivity.class));
					else
						Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
				}
			}
		};

		View b = findViewById(R.id.button);
		if(b != null) b.setOnClickListener(c);
	}

	private boolean equal(Object o1, Object o2) {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Objects.equals(o1, o2)) || o1.equals(o2);
	}

}
