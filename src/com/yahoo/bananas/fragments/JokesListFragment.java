package com.yahoo.bananas.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.bananas.Bananas;
import com.yahoo.bananas.R;
import com.yahoo.bananas.adapters.JokeArrayAdapter;
import com.yahoo.bananas.clients.OldClient;
import com.yahoo.bananas.listeners.EndlessScrollListener;
import com.yahoo.bananas.models.Tweet;
import com.yahoo.bananas.util.InternetStatus;

abstract public class JokesListFragment extends Fragment {
	private OldClient      client;
	private ArrayList<Tweet>   jokes;
	private JokeArrayAdapter  aJokes;
	private ListView		   lvJokes;
	private long               lastItemId;
	private InternetStatus     internetStatus;
	private SwipeRefreshLayout swipeContainer;	

	/** Non-view / non-UI related initialization. (fires before onCreateView()) */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Non-view initialization
		client         = Bananas.getRestClient();
		internetStatus = new InternetStatus(getActivity());
		lastItemId     = 0; // Always start from 0.
		jokes         = new ArrayList<Tweet>();
		aJokes        = new JokeArrayAdapter(getActivity(), jokes); // WARNING: RARELY USE getActivity().  Other usage is likely improper.
		populateJokeStream(true);
	}
	
	/** View/UI-related initialization. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate Layout
		View v = inflater.inflate(R.layout.fragment_jokes_list, container, false); // false = don't attach to container yet.
		// Assign view preferences
		setupSwipeContainer(v);
		lvJokes = (ListView) v.findViewById(R.id.lvJokes);
		lvJokes.setAdapter(aJokes);
		setupEndlessScroll();
		// Return layout view
		return v;
	}
	
	/** Setup swipe down to refresh. */
	private void setupSwipeContainer(View v){
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateJokeStream(true); // true = start from beginning again
                setupEndlessScroll(); // Resetup endless scroll in case it previously hit the bottom and stopped scrolling further again. 
            } 
        });
        // Configure the refreshing colors
        swipeContainer.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);		
	}
	
	private void setupEndlessScroll(){
		lvJokes.setOnScrollListener(new EndlessScrollListener() {
			/** The endless scroll listener will call us whenever its count says that we need more.  We don't care what page it is on, we just get more. */
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				populateJokeStream(false); 
			}
		});
	}
	
	/**
	 * After Internet/network toggled back on, re-setup anything necessary.  Such as:
	 * 1. Start endless scroll again, which might have thought it reached the end and stopped trying for more.
	 * 2. Load data for the first time if we never had retrieved (or found) any yet, because endless scroll will not load more if already 0. */
	public void onInternetResume(){
		// Re-setup endless scroll if re-enabled.
		setupEndlessScroll(); // Re-enable endless scrolling because if it hit end before, it would not try again.
		if(jokes.size()==0) populateJokeStream(true); // If no tweets so far, then the app just started and we have to re-run populate because it could have ran already and found no network.
	}

	
	/** Delegate the adding to the internal adapter. */
	//NOT NEEDED: public void addAll(List<Tweet> tweets){
	//NOT NEEDED:	aTweets.addAll(tweets);
	//NOT NEEDED:}
	
	/** Insert a new tweet at any position. */
	public void insert(Tweet joke, int position){
		aJokes.insert(joke, position);		
	}
	
	/** Subclasses can get the JokeClient instance. */
	protected OldClient getClient(){
		return client;
	}
	
    /** Populate the list with tweets. */
	public void populateJokeStream(final boolean refresh){
		Log.d("DVDEBUG", "+ TimelineActivity.populateTimeline()");
		if(!internetStatus.isAvailable()){ // If no network, don't allow create tweet.
			Toast.makeText(getActivity(), "Network Not Available!", Toast.LENGTH_SHORT).show();
			if(refresh) populateJokeStreamOffline(refresh);
		}else{
			final JokesListFragment parentThis = this;
			if(refresh) lastItemId = 0; // If told to refresh from beginning, start again from 0.
			getJokes(lastItemId, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONArray json) {
					Log.d("json", parentThis.getClass().getSimpleName()+" JSON: "+json.toString());
					if(refresh) aJokes.clear(); // If told to refresh from beginning, clear existing results
					ArrayList<Tweet> retrievedJokes = Tweet.fromJSON(json);
					aJokes.addAll(retrievedJokes);
					lastItemId = jokes.get(jokes.size()-1).getUid(); // record the last item ID we've seen now, so we know where to continue off from next time.
	                // Now we call setRefreshing(false) to signal refresh has finished
	                swipeContainer.setRefreshing(false);
	                // Persist results we found so far.  Save all tweets we ever find.  The specific fragment type only matters later when we query for specific kinds to load offline.
	                try{
	                	for(Tweet j : retrievedJokes){
	                		j.getUser().save();
	                		j.save();
	                	}
						Log.d("persist", "Persisted Timeline Results");
	                }catch(Exception e){
						Log.e("error", e.toString());
						Toast.makeText(parentThis.getActivity(), "PERSIST FAILED!", Toast.LENGTH_SHORT).show();
	                }
				}
				@Override
				public void onFailure(Throwable e, String s) {
					Log.e("error", e.toString());
					Log.e("error", s.toString());
				}
			});
		}
	}
	
	/** Populate the timeline based on offline content. */
	private void populateJokeStreamOffline(final boolean refresh){
		List<Tweet> retrievedJokes = getOfflineJokes();
		aJokes.addAll(retrievedJokes);
		lastItemId = jokes.get(jokes.size()-1).getUid(); // record the last item ID we've seen now, so we know where to continue off from next time.
		Toast.makeText(getActivity(), "Offline Content: "+retrievedJokes.size(), Toast.LENGTH_SHORT).show();
        swipeContainer.setRefreshing(false);
	}

	/** Call the correct client API method for this fragment's tweets. */
	abstract protected void getJokes(long lastItemId, AsyncHttpResponseHandler handler);
	
	/** Query for the correct offline tweets for the particular fragment type. */
	abstract protected List<Tweet> getOfflineJokes();
	
}
