package com.yahoo.bananas.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.JokesListFragment;
import com.yahoo.bananas.fragments.NewestStreamFragment;
import com.yahoo.bananas.fragments.PopularStreamFragment;
import com.yahoo.bananas.listeners.FragmentTabListener;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.Settings;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.InternetStatus;

public class JokeStreamActivity extends FragmentActivity {
	// Constants
	private static final String FRAGMENTTAG_NEWEST   = "newest";
	private static final String FRAGMENTTAG_POPULAR  = "popular";
	private static final int    ACTIVITY_CREATE      = 1;
	private static final int    ACTIVITY_PROFILE     = 2;
	private static final int    ACTIVITY_DETAIL      = 3;
	private static final int    ACTIVITY_SETTINGS    = 4;
	public  static final String INTENT_JOKE          = "joke";
	public  static final String INTENT_SETTINGS      = "settings";
	// Member Variables
	private InternetStatus     internetStatus;
	private Settings           settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_joke_stream);
		settings       = Settings.load(this);
		internetStatus = new InternetStatus(this);
		setupTabs();
		setActionBarColor();
	}

	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1 = actionBar
            .newTab()
            .setText("Newest")
//            .setIcon(R.drawable.ic_home)
            .setTag("NewestStreamFragment")
            .setTabListener(new FragmentTabListener<NewestStreamFragment>(R.id.flContainer, this, FRAGMENTTAG_NEWEST, NewestStreamFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        Tab tab2 = actionBar
            .newTab()
            .setText("Popular")
//            .setIcon(R.drawable.ic_popular)
            .setTag("PopularStreamFragment")
            .setTabListener(new FragmentTabListener<PopularStreamFragment>(R.id.flContainer, this, FRAGMENTTAG_POPULAR, PopularStreamFragment.class));

        actionBar.addTab(tab2);
    }
	
	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timeline, menu);
		MenuItem internetToggle = menu.findItem(R.id.actionInternetToggle);
		setupInternetToggle(internetToggle);
		return true;
	}

	/** Menu selection to turn on/off Internet to simulate offline. */
	public void internetToggle(MenuItem menuItem){
		internetStatus.setAppToggleEnabled(!internetStatus.isAppToggleEnabled());
		setupInternetToggle(menuItem);
		// If re-enabled, re-setup endless scroll & load data if if none loaded yet.
		if(internetStatus.isAppToggleEnabled()){
			JokesListFragment fNewest     = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_NEWEST    );
			JokesListFragment fPopular = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_POPULAR);
			if(fNewest    !=null) fNewest    .onInternetResume();
			if(fPopular!=null) fPopular.onInternetResume();
		}
	}

	/** Update the Internet status visual indicators. */
	public void setupInternetToggle(MenuItem menuItem){
		// Update the menu item to show the correct toggle state.
		// + toast just to make it clear what the current state is.
		if(internetStatus.isAppToggleEnabled()){
			Toast.makeText(this, "Internet ON", Toast.LENGTH_SHORT).show();
			menuItem.setIcon(R.drawable.ic_action_internet_off);
		}else{
			Toast.makeText(this, "Internet OFF", Toast.LENGTH_SHORT).show();
			menuItem.setIcon(R.drawable.ic_action_internet_on);
		}
	}
	
	/** Menu selection to create a new tweet. */
	public void create(MenuItem menuItem){
		if(!internetStatus.isAvailable()){ // If no network, don't allow create tweet.
			Toast.makeText(this, "Network Not Available!", Toast.LENGTH_SHORT).show();
		}else{
			//Toast.makeText(this, "Settings!", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this,CreateActivity.class);
			//no args: i.putExtra("settings", searchFilters);
			startActivityForResult(i, ACTIVITY_CREATE);
		}
	}

	/** Menu selection to view profile. */
	public void viewProfile(MenuItem menuItem){
		User user = JokesApplication.getParseClient().getUser();
		if (user != null) {
			Intent i = new Intent(this,ProfileActivity.class);
			i.putExtra("user", user);
			startActivityForResult(i, ACTIVITY_PROFILE);
		} else {
			Toast.makeText(this, "User information not available at this time.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onProfileClick(View v){
		// Get the user that they clicked on.
		User u = (User) v.getTag(); // We stored the user object in JokeArrayAdapter when the image is created.
		if(u!=null){ // Error if not there.
			Intent i = new Intent(this,ProfileActivity.class);
			i.putExtra("user", u);
			startActivityForResult(i, ACTIVITY_PROFILE);
		} else {
			Toast.makeText(this, "Image Missing User Info!", Toast.LENGTH_SHORT).show();
		}
	}

	/** Menu selection to modify settings. */
	public void modifySettings(MenuItem menuItem){
		Intent i = new Intent(this,SettingsActivity.class);
		i.putExtra(INTENT_SETTINGS, settings);
		startActivityForResult(i, ACTIVITY_SETTINGS);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==ACTIVITY_CREATE){ // CreateActivity Result
    		if(resultCode == RESULT_OK){
    			Joke joke = (Joke) data.getSerializableExtra(INTENT_JOKE);
    			if(joke!=null){
    				JokesListFragment fragmentHome = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_NEWEST);
    				if(fragmentHome!=null){
    					fragmentHome.insert(joke, 0);
    					Toast.makeText(this, "Timeline Updated", Toast.LENGTH_SHORT).show();
    				}
    			}else Toast.makeText(this, "MISSING RESULT", Toast.LENGTH_SHORT).show();    				
    		}
    	}else if(requestCode==ACTIVITY_SETTINGS){
    		if(resultCode == RESULT_OK){
    			Settings newSettings = (Settings) data.getSerializableExtra(INTENT_SETTINGS);
    			// If the settings changed, we'll need to refresh whatever they are viewing.
    			if(!settings.equals(newSettings)){
    				settings = newSettings;
	    			// Refresh because the categories they want may have changed.
					JokesListFragment fragment1 = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_NEWEST);
					JokesListFragment fragment2 = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_POPULAR);
					if(fragment1!=null) fragment1.refresh(settings);
					if(fragment2!=null) fragment2.refresh(settings);
    			}
    		}
    	}
    }
	
}
