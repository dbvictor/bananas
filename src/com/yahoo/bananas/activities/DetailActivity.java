package com.yahoo.bananas.activities;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.bananas.R;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.util.Util;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Toast.makeText(this, "Detail View", Toast.LENGTH_SHORT).show();
		TextView tvJoke = (TextView) findViewById(R.id.tvJoke);
		Joke joke = (Joke) getIntent().getSerializableExtra("joke");
		tvJoke.setText(joke.getText());
		TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
		Date createdAt = joke.getCreatedAt();
		if (createdAt != null) {
			tvCreatedAt.setText(Util.getRelativeTimeAgo(createdAt.toString()));
		}
		
	}
}
