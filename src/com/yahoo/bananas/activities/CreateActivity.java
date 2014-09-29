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
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokeApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.clients.JokeClient;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.Tweet;

public class CreateActivity extends Activity {
	private ParseClient client;
	private JokeClient twitterClient;
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
		client = JokeApplication.getParseClient();
		// Remember views for easy access later.
		etBody           = (EditText) findViewById(R.id.etNewJoke      );
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
			final Joke joke = new Joke();
			joke.setText(etBodyText);
			// 1. Send text to Joke Service
			final CreateActivity parentThis = this;
			client.create(joke, new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.d("debug", e.toString());
						Toast.makeText(parentThis, "FAILED!", Toast.LENGTH_SHORT).show();
						// Don't return to timeline.  Allow them a chance to retry.  They can always hit the back button.
					} else {
						Toast.makeText(parentThis, "Posted", Toast.LENGTH_SHORT).show();
						// 2. Return result to timeline activity
						Intent i = new Intent();
						i.putExtra(JokeStreamActivity.JOKE, joke);
						setResult(RESULT_OK, i);
						finish();					
					}
				}
			});
			
			
			twitterClient.createJoke(etBodyText, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					Log.d("json", "Created JSON: "+json.toString());
					Tweet tweet = Tweet.fromJSON(json);
					Toast.makeText(parentThis, "Posted", Toast.LENGTH_SHORT).show();
					// 2. Return result to timeline activity
					Intent i = new Intent();
					i.putExtra(JokeStreamActivity.TWEET, tweet);
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
