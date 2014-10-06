package com.yahoo.bananas.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.UserJokeStreamFragment;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.InternetStatus;

public class ProfileActivity extends FragmentActivity {
	private InternetStatus internetStatus;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		setActionBarColor();
		internetStatus = new InternetStatus(this);
		user = (User) getIntent().getSerializableExtra("user");
		if (user == null) {
			Toast.makeText(getApplicationContext(), "User is unavailable", Toast.LENGTH_SHORT).show();
		} else {
			loadProfile();
			// Tell the fragment to load a specific user too.
			// - Load dynamically so we can control the constructor to pass arguments to it.
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			UserJokeStreamFragment userTimeline = UserJokeStreamFragment.newInstance(user);
			ft.replace(R.id.flProfileJokeStreamContainer, userTimeline);
			ft.commit();
		}
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	/**
	 * Load the user profile.
	 * @param optUser
	 *          (Optional) Specofic user profile to load if not the current user.
	 *          NULL: loads current user by default if none specified.
	 */
	private void loadProfile(){
		// If we already have the user object, just display it.
		if(user!=null){
			// note: we don't need a network check because (1) we already have it, and (2) image url is probably cached if they already displayed it once.
			populateProfileHeader();			
		// Otherwise we'll have to look it up.
		}else if(!internetStatus.isAvailable()){ // If no network, load offline profile
			Toast.makeText(this, "Network Not Available!", Toast.LENGTH_SHORT).show();
			loadProfileOffline();
		}
	}
	
	private void loadProfileOffline(){
		//TODO
		Toast.makeText(this, "Offline Profile Not Implemented", Toast.LENGTH_SHORT).show();
		return;		
	}
	
	private void populateProfileHeader(){
		// Set action bar to this user.
		getActionBar().setTitle(user.getRealName());
		// Get access to our views.
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		// Set the user values to these views.
		ImageLoader.getInstance().displayImage(user.getImageUrl(), ivProfileImage);
		
		TextView tvUserName = (TextView) findViewById(R.id.tvRealName);
		tvUserName.setText(user.getRealName());
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
