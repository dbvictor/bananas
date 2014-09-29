package com.yahoo.bananas.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.yahoo.bananas.R;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Toast.makeText(this, "Detail View", Toast.LENGTH_SHORT).show();
	}
}
