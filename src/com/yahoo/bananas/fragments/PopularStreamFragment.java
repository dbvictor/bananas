package com.yahoo.bananas.fragments;

import java.sql.Date;
import java.util.List;

import android.os.Bundle;

import com.yahoo.bananas.clients.ParseClient.FindJokes;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;

public class PopularStreamFragment extends JokesListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	/** Query for the correct offline jokes for the particular fragment type. */
	@Override
	protected List<Joke> getOfflineJokes(){
		return Joke.retrieveAll();
	}

	@Override
	protected void getJokes(Date lastDate, String lastObjectId,
			String optUserId, List<Category> optCategories, FindJokes handler) {
		// TODO Auto-generated method stub
		
	}


}
