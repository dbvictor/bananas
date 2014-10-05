package com.yahoo.bananas.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;

public class CreateActivity extends Activity {
	private ParseClient parseClient;
	private Joke joke;
	// Remembered Views
	private EditText etBody;
	private Spinner spCategory;
	private TextView tvCharsRemaining;
	// Constants
	private static final int MAX_LENGTH = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		parseClient = JokesApplication.getParseClient();
		// Remember views for easy access later.
		etBody           = (EditText) findViewById(R.id.etNewJoke      );
		spCategory		 = (Spinner)  findViewById(R.id.spCategories   );
		tvCharsRemaining = (TextView) findViewById(R.id.tvCharsRemaining);
		tvCharsRemaining.setText(""+MAX_LENGTH+" remaining" );
		// Setup events
		setupTextChangeListener();
		setupSpinner();
		setActionBarColor();
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	private void setupSpinner() {
		Spinner spinner = (Spinner)findViewById(R.id.spCategories);

		SpinnerAdapter spAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, Category.getCategories());
		spinner.setAdapter(spAdapter);
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
		String spinnerSelection = spCategory.getSelectedItem().toString();

		// If empty, don't allow send.
		if((etBodyText==null)||(etBodyText.trim().length()<=0)){
			Toast.makeText(this, "Nothing to Post!", Toast.LENGTH_SHORT).show();
		// Else send joke!
		}else{
			joke = new Joke();
			joke.setCreatedBy(JokesApplication.getParseClient().getUserId());
			joke.setText(etBodyText);
			joke.setCategory(Category.valueOf(spinnerSelection));
			// 1. Send text to Joke Service
			final CreateActivity parentThis = this;
			parseClient.create(joke, new SaveCallback() {
				
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
		}
	}
}
