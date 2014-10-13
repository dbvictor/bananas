package com.yahoo.bananas.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.CategoryJokeStreamFragment;
import com.yahoo.bananas.models.Category;

public class CategoryActivity extends FragmentActivity {
	private Category       category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		setActionBarColor();
		category = (Category) getIntent().getSerializableExtra(Category.class.getSimpleName());
		getActionBar().setTitle(category.getDisplayName()+" Jokes");
		// Tell the fragment to load a specific category.
		// - Load dynamically so we can control the constructor to pass arguments to it.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CategoryJokeStreamFragment userTimeline = CategoryJokeStreamFragment.newInstance(category);
		ft.replace(R.id.flCategoryJokeStreamContainer, userTimeline);
		ft.commit();
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu, this adds items to the action bar if present.
		//FUTURE: getMenuInflater().inflate(R.menu.menu_profile, menu);
		//FUTURE: return true;
		return false;
	}

	/** When the user click on a profile image in the tweet list. */
	public void onProfileClick(View v){
		// Do nothing.  These are only the images of the profile they are already viewing.  No need to re-show the same activity.
	}
	
}