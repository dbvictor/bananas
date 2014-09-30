package com.yahoo.bananas.fragments;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.yahoo.bananas.clients.ParseClient.FindJokes;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.TwitterUser;

public class UserJokeStreamFragment extends JokesListFragment {
	private static final String DEBUG = "B_DEBUG";
	private static final String UID = "uid";
	/** If show a custom user jokestream instead of the current user. (-1 for default current user) */
	private long optCustomUid = -1;

	/** [For static loading] The activity can tell the jokestream to show a custom user instead
	 *  of defaulting to the current user.  Set to NULL (default) for current user. */
	public void setCustomUser(TwitterUser user){ // Needed if we do the static loading alternative.
		if(user==null) optCustomUid  = -1;
		else           optCustomUid = user.getUid();
	}
	
	/** [For dynamic loading] The activity can pass a custom user instead of defaulting to the current user.
	 *  Set to NULL (default) for current user. */
    public static UserJokeStreamFragment newInstance(TwitterUser user) {
    	UserJokeStreamFragment f = new UserJokeStreamFragment();
        Bundle args = new Bundle();
        if(user!=null) args.putLong(UID, user.getUid());
        f.setArguments(args);
        Log.e(DEBUG,"new fragment: "+user);
        if(user!=null) Log.e(DEBUG,"new fragment: "+user.getUid());
        return f;
    }	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		optCustomUid = getArguments().getLong(UID, -1);
		super.onCreate(savedInstanceState);
	}
	
	
	/** Query for the correct offline tweets for the particular fragment type. */
	@Override
	protected List<Joke> getOfflineJokes(){
		return Joke.retrieveAll();
	}

	@Override
	protected void getJokes(FindJokes handler) {
	}


}
