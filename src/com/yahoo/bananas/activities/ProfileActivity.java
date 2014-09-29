package com.yahoo.bananas.activities;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.bananas.R;
import com.yahoo.bananas.Bananas;
import com.yahoo.bananas.fragments.UserJokeStreamFragment;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.InternetStatus;

public class ProfileActivity extends FragmentActivity {
	private InternetStatus internetStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		internetStatus = new InternetStatus(this);
		// Detect if they want us to load a specific user.
		User optUser = (User) getIntent().getSerializableExtra("user"); // NULL if no other user.
		loadProfile(optUser);
		// Tell the fragment to load a specific user too.
		// - Load dynamically so we can control the constructor to pass arguments to it.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		UserJokeStreamFragment userTimeline = UserJokeStreamFragment.newInstance(optUser);
		ft.replace(R.id.flProfileJokeStreamContainer, userTimeline);
		ft.commit();
		// - Otherwise we could have statically loaded and set info by a custom method.
		//STATIC ALTERNATIVE: UserJokeStreamFragment userTimeline = (UserJokeStreamFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentUserTimeline);
		//STATIC ALTERNATIVE: userTimeline.setCustomUser(optUser);
		//STATIC ALTERNATIVE: move super.onCreate() to bottom.
	}
	
	/**
	 * Load the user profile.
	 * @param optUser
	 *          (Optional) Specofic user profile to load if not the current user.
	 *          NULL: loads current user by default if none specified.
	 */
	private void loadProfile(User optUser){
		// If we already have the user object, just display it.
		if(optUser!=null){
			// note: we don't need a network check because (1) we already have it, and (2) image url is probably cached if they already displayed it once.
			populateProfileHeader(optUser);			
		// Otherwise we'll have to look it up.
		}else if(!internetStatus.isAvailable()){ // If no network, don't allow create tweet.
			Toast.makeText(this, "Network Not Available!", Toast.LENGTH_SHORT).show();
			loadProfileOffline();
		}else{
			final ProfileActivity parentThis = this;
			Bananas.getRestClient().getMyProfile(new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					Log.d("json", "MyInfo JSON: "+json.toString());
					User u = User.fromJSON(json);
					populateProfileHeader(u);
				}
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
					Toast.makeText(parentThis, "PROFILE FAILED!", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	private void loadProfileOffline(){
		//TODO
		Toast.makeText(this, "Offline Profile Not Implemented", Toast.LENGTH_SHORT).show();
		return;		
	}
	
	private void populateProfileHeader(User u){
		// Set action bar to this user.
		getActionBar().setTitle("@"+u.getScreenName());
		// Get access to our views.
		TextView  tvRealName     = (TextView)  findViewById(R.id.tvRealName    );
		TextView  tvTagline      = (TextView)  findViewById(R.id.tvTagline     );
		TextView  tvFollowers    = (TextView)  findViewById(R.id.tvFollowers   );
		TextView  tvFollowing    = (TextView)  findViewById(R.id.tvFollowing   );
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		// Set the user values to these views.
		tvRealName .setText(u.getRealName());
		tvTagline  .setText(u.getDescription());
		tvFollowers.setText(u.getFollowersCount() + " Followers");
		tvFollowing.setText(u.getFriendsCount() + " Following");
		ImageLoader.getInstance().displayImage(u.getImageUrl(), ivProfileImage);
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
