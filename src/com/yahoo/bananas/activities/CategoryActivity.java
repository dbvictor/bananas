package com.yahoo.bananas.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.CategoryJokeStreamFragment;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.User;

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
		// Get the user that they clicked on.
		User u = (User) v.getTag(); // We stored the user object in JokeArrayAdapter when the image is created.
		if(u!=null){ // Error if not there.
			Intent i = new Intent(this,ProfileActivity.class);
			i.putExtra("user", u);
			startActivity(i);
		} else {
			Toast.makeText(this, "Image Missing User Info!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/** When the user clicks on a category image in the jokes list. */
	public void onCategoryClick(View v){
		// Do nothing.  These are only the images of the category they are already viewing.  No need to re-show the same activity.
	}
	
}