package com.yahoo.bananas.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yahoo.bananas.R;
import com.yahoo.bananas.fragments.JokesListFragment;
import com.yahoo.bananas.fragments.NewestStreamFragment;
import com.yahoo.bananas.fragments.PopularStreamFragment;
import com.yahoo.bananas.listeners.FragmentTabListener;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.TwitterUser;
import com.yahoo.bananas.util.InternetStatus;

public class JokeStreamActivity extends FragmentActivity {
	public static final String JOKE = "joke";
	// Constants
	private static final String FRAGMENTTAG_NEWEST   = "newest";
	private static final String FRAGMENTTAG_POPULAR  = "popular";
	private static final int    ACTIVITY_CREATE      = 1;
	private static final int    ACTIVITY_PROFILE     = 2;
	// Member Variables
	private InternetStatus     internetStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joke_stream);
		internetStatus = new InternetStatus(this);
		setupTabs();
	}
	
	private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1 = actionBar
            .newTab()
            .setText("Newest")
            .setIcon(R.drawable.ic_home)
            .setTag("NewestStreamFragment")
            .setTabListener(new FragmentTabListener<NewestStreamFragment>(R.id.flContainer, this, FRAGMENTTAG_NEWEST, NewestStreamFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        Tab tab2 = actionBar
            .newTab()
            .setText("Popular")
            .setIcon(R.drawable.ic_mentions)
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
		Intent i = new Intent(this,ProfileActivity.class);
		//no args: i.putExtra("settings", searchFilters);
		startActivityForResult(i, ACTIVITY_PROFILE);
	}
	
	public void onProfileClick(View v){
		// Get the user that they clicked on.
		TwitterUser u = (TwitterUser) v.getTag(); // We stored the user object in JokeArrayAdapter when the image is created.
		if(u==null){ // Error if not there.
			Toast.makeText(this, "Image Missing User Info!", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(this,ProfileActivity.class);
		i.putExtra("user", u);
		startActivityForResult(i, ACTIVITY_PROFILE);
	}
	
//	public void onJokeClick(View v) {
//		Toast.makeText(this, "body clicked, getting tag", Toast.LENGTH_SHORT).show();
////		Tweet j = (Tweet) v.getTag();
////		if (j == null) {
////			Toast.makeText(this, "missing joke data", Toast.LENGTH_SHORT).show();
////			return;
////		}
////		Toast.makeText(this, "body clicked, going to Detail Activity", Toast.LENGTH_SHORT).show();
////		Intent i = new Intent(this, DetailActivity.class);
////		i.putExtra("joke", j);
////		startActivity(i);
//	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==ACTIVITY_CREATE){ // CreateActivity Result
    		if(resultCode == RESULT_OK){
    			Joke joke = (Joke) data.getSerializableExtra(JOKE);
    			if(joke!=null){
    				JokesListFragment fragmentHome = (JokesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENTTAG_NEWEST);
    				if(fragmentHome!=null){
    					fragmentHome.insert(joke, 0);
    					Toast.makeText(this, "Timeline Updated", Toast.LENGTH_SHORT).show();
    				}
    			}else Toast.makeText(this, "MISSING RESULT", Toast.LENGTH_SHORT).show();    				
    		}
    	}
    }
	
}
