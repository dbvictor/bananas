package com.yahoo.bananas.fragments;

import java.sql.Date;
import java.util.List;

import android.os.Bundle;

import com.yahoo.bananas.clients.ParseClient.FindJokes;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;


public class NewestStreamFragment extends JokesListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/** Call the correct client API method for this fragment's tweets. 
	 * @param lastDate 
	 * @param optUserId 
	 * @param optCategories 
	 * @param handler */
	@Override
	protected void getJokes(Date lastDate, String lastObjectId, String optUserId, List<Category> optCategories, FindJokes handler){
		getParseClient().getJokesNewest(lastDate, lastObjectId, optUserId, optCategories, handler);
	}
	
	/** Query for the correct offline tweets for the particular fragment type. */
	@Override
	protected List<Joke> getOfflineJokes(){
		return Joke.retrieveAll();
	}

	
}
