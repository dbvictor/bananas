package com.yahoo.bananas.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.bananas.Bananas;
import com.yahoo.bananas.R;
import com.yahoo.bananas.models.Tweet;
import com.yahoo.bananas.networking.JokeClient;

public class CreateActivity extends Activity {
	private JokeClient client;
	private Tweet joke;
	// Remembered Views
	private EditText etBody;
	private TextView tvCharsRemaining;
	// Constants
	private static final int MAX_LENGTH = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		client = Bananas.getRestClient();
		// Remember views for easy access later.
		etBody           = (EditText) findViewById(R.id.etNewTweet      );
		tvCharsRemaining = (TextView) findViewById(R.id.tvCharsRemaining);
		tvCharsRemaining.setText(""+MAX_LENGTH+" remaining" );
		// Setup events
		setupTextChangeListener();
	}
	
	private void setupTextChangeListener(){
		etBody.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				// do nothing
			}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				// do nothing
			}
			@Override public void afterTextChanged(Editable s) {
				int count = etBody.getText().toString().length();
				tvCharsRemaining.setText(""+(MAX_LENGTH-count)+" remaining" );
			}
		});
	}
	
	public void create(View v){
		String etBodyText = etBody.getText().toString();
		// If empty, don't allow send.
		if((etBodyText==null)||(etBodyText.trim().length()<=0)){
			Toast.makeText(this, "Nothing to Post!", Toast.LENGTH_SHORT).show();
		// Else send joke!
		}else{
			// 1. Send text to Joke Service
			final CreateActivity parentThis = this;
			client.createJoke(etBody.getText().toString(), new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					Log.d("json", "Created JSON: "+json.toString());
					joke = Tweet.fromJSON(json);
					Toast.makeText(parentThis, "Posted", Toast.LENGTH_SHORT).show();
					// 2. Return result to timeline activity
					Intent i = new Intent();
					i.putExtra(JokeStreamActivity.JOKE, joke);
					setResult(RESULT_OK, i);
					finish();
				}
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
					Toast.makeText(parentThis, "FAILED!", Toast.LENGTH_SHORT).show();
					// Don't return to timeline.  Allow them a chance to retry.  They can always hit the back button.
				}
			});
		}
	}
}