package com.yahoo.bananas.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.CategoryFiltersFragment;
import com.yahoo.bananas.models.Settings;

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
	}
	
}
