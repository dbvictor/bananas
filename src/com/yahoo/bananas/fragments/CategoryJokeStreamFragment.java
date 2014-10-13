package com.yahoo.bananas.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.yahoo.bananas.clients.ParseClient.FindJokes;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;

public class CategoryJokeStreamFragment extends JokesListFragment {
	private static final String DEBUG    = "DEBUG";
	private static final String CATEGORY = Category.class.getSimpleName();
	private List<Category> categories;
	
	/** [For dynamic loading] The activity can pass a custom user instead of defaulting to the current user.
	 *  Set to NULL (default) for current user. */
    public static CategoryJokeStreamFragment newInstance(Category category) {
    	CategoryJokeStreamFragment f = new CategoryJokeStreamFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY, category);
        f.setArguments(args);
        Log.d(DEBUG,"new fragment: "+category);
        return f;
    }	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Category category = (Category) getArguments().getSerializable(CATEGORY);
		categories = new ArrayList<Category>(1);
		categories.add(category);
		super.onCreate(savedInstanceState);
	}
	
	/** Query for the correct offline jokes for the particular fragment type. */
	@Override
	protected List<Joke> getOfflineJokes(){
		return Joke.retrieveAll();
	}

	@Override
	protected void getJokes(FindJokes handler) {
		getParseClient().getJokesNewest(getLastDate(), getLastObjectId(), null, categories, handler);
	}

}
