package com.yahoo.bananas.fragments;

import java.util.List;

import android.os.Bundle;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yahoo.bananas.models.Tweet;

public class PopularStreamFragment extends JokesListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/** Call the correct client API method for this fragment's tweets. */
	@Override
	protected void getJokes(long lastItemId, AsyncHttpResponseHandler handler){
		getClient().getPopularJokeStream(lastItemId, handler);		
	}
	
	/** Query for the correct offline tweets for the particular fragment type. */
	@Override
	protected List<Tweet> getOfflineJokes(){
		return Tweet.retrieveAll();
	}

}
