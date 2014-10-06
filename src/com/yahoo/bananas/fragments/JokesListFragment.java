package com.yahoo.bananas.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.adapters.JokeArrayAdapter;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.clients.ParseClient.FindJokes;
import com.yahoo.bananas.listeners.EndlessScrollListener;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.util.InternetStatus;

abstract public class JokesListFragment extends Fragment {
	private ParseClient			parseClient;
	private ArrayList<Joke>   jokes;
	private JokeArrayAdapter  aJokes;
	private ListView		   lvJokes;
	private InternetStatus     internetStatus;
	private SwipeRefreshLayout swipeContainer;
	private String			lastObjectId;
	private Date lastDate = null;
	private String optUserId;
	private List<Category> optCategories;
	private int lastVotesUp = -1;

	/** Non-view / non-UI related initialization. (fires before onCreateView()) */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Non-view initialization
		parseClient = JokesApplication.getParseClient();
		internetStatus = new InternetStatus(getActivity());
		lastObjectId     = null; // Always start from null.
		jokes         = new ArrayList<Joke>();
		aJokes        = new JokeArrayAdapter(getActivity(), jokes); // WARNING: RARELY USE getActivity().  Other usage is likely improper.
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
		populateJokeStream(true);
		setupListViewListener();
		// Return layout view
		return v;
	}
	
	private void setupListViewListener() {
		lvJokes.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View item,
					int position, long id) {
				Joke joke = jokes.get(position);
				remove(joke);
				jokes.remove(position);
				aJokes.notifyDataSetChanged();
				return true;
			}
		});
	}
	
	private void remove(Joke joke){
		parseClient.delete(joke, new DeleteCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.d("debug", e.toString());
					Toast.makeText(getActivity(), "FAILED!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();				
				}
			}
		});
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
	public void insert(Joke joke, int position){
		aJokes.insert(joke, position);		
	}
	
	protected ParseClient getParseClient() {
		return parseClient;
	}
	
    /** Populate the list with tweets. */
	public void populateJokeStream(final boolean refresh){
		Log.d("DVDEBUG", "+ TimelineActivity.populateTimeline()");
		if(!internetStatus.isAvailable()){ // If no network, don't allow create tweet.
			Toast.makeText(getActivity(), "Network Not Available!", Toast.LENGTH_SHORT).show();
			if(refresh) populateJokeStreamOffline(refresh);
		}else{
			final JokesListFragment parentThis = this;
			if(refresh) {
				lastObjectId = null; // If told to refresh from beginning, start again from 0.
				lastVotesUp = -1;
				lastDate = null;
			}
			getJokes(new FindJokes() {
				@Override
				public void done(List<Joke> results, ParseException e) {
					if (e != null) {
						Log.e("error", e.toString());
					} else if (results != null){
						if (refresh) {
							aJokes.clear();
							aJokes.addAll(results);
							if (jokes != null && ! jokes.isEmpty()) {
								Joke lastJoke = jokes.get(jokes.size()-1);
								lastObjectId = lastJoke.getObjectId();
								lastVotesUp = lastJoke.getVotesUp();
								lastDate = lastJoke.getCreatedAt();
							} else {
								lastObjectId = null;
								lastVotesUp = -1;
								lastDate = null;
							}
							swipeContainer.setRefreshing(false);
							try {
								for (Joke j : results) {
									j.save();
								}
								Log.d("persist", "Persisted Timeline Results");
							} catch(Exception ex){
								Log.e("error", ex.toString());
								Toast.makeText(parentThis.getActivity(), "PERSIST FAILED!", Toast.LENGTH_SHORT).show();
			                }
						}
					}					
				}
			});
		}
	}
	
	/** Populate the timeline based on offline content. */
	private void populateJokeStreamOffline(final boolean refresh){
		List<Joke> retrievedJokes = getOfflineJokes();
		aJokes.addAll(retrievedJokes);
		lastObjectId = jokes.get(jokes.size()-1).getObjectId(); // record the last item ID we've seen now, so we know where to continue off from next time.
		Toast.makeText(getActivity(), "Offline Content: "+retrievedJokes.size(), Toast.LENGTH_SHORT).show();
        swipeContainer.setRefreshing(false);
	}
	
	/** Query for the correct offline tweets for the particular fragment type. */
	abstract protected List<Joke> getOfflineJokes();

	abstract protected void getJokes(FindJokes handler);

	public String getLastObjectId() {
		return lastObjectId;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public String getOptUserId() {
		return optUserId;
	}

	public List<Category> getOptCategories() {
		return optCategories;
	}

	public int getLastVotesUp() {
		return lastVotesUp;
	}
	
}
