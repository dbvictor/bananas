package com.yahoo.bananas.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
	private EditText etTitle;
	private Spinner spCategory;
	private ImageView ivCategory;
	private TextView tvCharsRemaining;
	private MenuItem menuItemActionDone;
	// Constants
	private static final int MAX_LENGTH = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		parseClient = JokesApplication.getParseClient();
		// Remember views for easy access later.
		etBody           = (EditText) findViewById(R.id.etNewJoke      );
		etTitle			 = (EditText) findViewById(R.id.etTitle);
		spCategory		 = (Spinner)  findViewById(R.id.spCategories   );
		ivCategory		 = (ImageView)findViewById(R.id.ivCategory     );
		tvCharsRemaining = (TextView) findViewById(R.id.tvCharsRemaining);
		tvCharsRemaining.setText(""+MAX_LENGTH+" remaining" );
		// Setup events
		setupTextChangeListener();
		setupSpinner();
		setActionBarColor();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        this.menuItemActionDone = menu.findItem(R.id.actionDone);
        menuItemActionDone.setEnabled(false);
        return true;
    }
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	private void setupSpinner() {
		SpinnerAdapter spAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, Category.getList());
		spCategory.setAdapter(spAdapter);
		spCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String spinnerSelection = spCategory.getSelectedItem().toString();
				Category category = Category.valueOf(spinnerSelection);
				ivCategory.setImageResource(category.getImageResourceId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
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
		        menuItemActionDone.setEnabled(true);
			}
		});
	}
	
	public void save(MenuItem menuItem){
		String etBodyText = etBody.getText().toString();
		String spinnerSelection = spCategory.getSelectedItem().toString();
		String etTitleText = etTitle.getText().toString();

		// If empty, don't allow send.
		if((etBodyText==null)||(etBodyText.trim().length()<=0)){
			Toast.makeText(this, "Nothing to Post!", Toast.LENGTH_SHORT).show();
		// Else send joke!
		}else{
			final ProgressBar pbCreate = (ProgressBar) findViewById(R.id.pb_create);
			pbCreate.setVisibility(ProgressBar.VISIBLE);

			joke = new Joke();
			joke.setCreatedBy(JokesApplication.getParseClient().getUser());
			joke.setText(etBodyText);
			joke.setTitle(etTitleText);
			joke.setCategory(Category.valueOf(spinnerSelection));
			// 1. Send text to Joke Service
			final CreateActivity parentThis = this;
			parseClient.create(joke, new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					pbCreate.setVisibility(ProgressBar.INVISIBLE);
					if (e != null) {
						Log.d("debug", e.toString());
						Toast.makeText(parentThis, "FAILED!", Toast.LENGTH_SHORT).show();
						// Don't return to timeline.  Allow them a chance to retry.  They can always hit the back button.
					} else {
						Toast.makeText(parentThis, "Posted", Toast.LENGTH_SHORT).show();
						// 2. Return result to timeline activity
						Intent i = new Intent();
						i.putExtra(JokeStreamActivity.INTENT_JOKE, joke);
						setResult(RESULT_OK, i);
						finish();					
						overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
					}
				}
			});
		}
	}
	
	/** Override the back button behavior to override default exit animation. */
	@Override
	public void onBackPressed(){
		finish();					
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
	}	
	
}
