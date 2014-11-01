package com.yahoo.bananas.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.CategoryFiltersFragment;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Settings;
import com.yahoo.bananas.models.Theme;

public class SettingsActivity extends FragmentActivity {
	CategoryFiltersFragment categoryFiltersFragment;
	Settings                settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setActionBarColor();
		settings = (Settings) getIntent().getSerializableExtra(JokeStreamActivity.INTENT_SETTINGS);
		if(settings==null) Toast.makeText(this, "MISSING SETTINGS!", Toast.LENGTH_SHORT).show();;
		// Load theme choices
		setupThemeSpinner();
		// Tell the fragment to load a specific user too.
		// - Load dynamically so we can control the constructor to pass arguments to it.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		categoryFiltersFragment = CategoryFiltersFragment.newInstance(settings); // TODO pass info here.
		ft.replace(R.id.flCategoryFilters, categoryFiltersFragment);
		ft.commit();
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	private void setupThemeSpinner(){
		final Spinner spinner = (Spinner) findViewById(R.id.spTheme);
		// Load All Choices
		SpinnerAdapter adapter = new ArrayAdapter<Theme>(this, android.R.layout.simple_list_item_1, Theme.getList());
		spinner.setAdapter(adapter);
		// Set to Current Setting
		setSpinnerToValue(spinner,settings.getTheme());
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Theme selected = (Theme) spinner.getSelectedItem();
				settings.setTheme(selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	/** Utility to set spinners back to a value based on their value instead of by position. */
	private void setSpinnerToValue(Spinner spinner, Theme value) {
	    int index = 0;
	    SpinnerAdapter adapter = spinner.getAdapter();
	    for (int i = 0; i < adapter.getCount(); i++) {
	        if (adapter.getItem(i).equals(value)) {
	            index = i;
	            break; // terminate loop
	        }
	    }
	    spinner.setSelection(index);
	}	
	
	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}
	
	/** Menu selection to turn on/off Internet to simulate offline. */
	public void save(MenuItem menuItem){
		// Persist Changes
		settings.save(this);
		// Return Result to Previous Activity
		Intent i = new Intent();
		i.putExtra(JokeStreamActivity.INTENT_SETTINGS, settings);
		setResult(RESULT_OK, i);
		finish();					
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
	
	/** Override the back button behavior to save as well. */
	@Override
	public void onBackPressed(){
		// Do the same as save and return.
		save(null);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}	
	
}
