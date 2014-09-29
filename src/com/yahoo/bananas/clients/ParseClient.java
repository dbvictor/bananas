package com.yahoo.bananas.clients;

import java.sql.Date;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.bananas.models.Joke;

public class ParseClient {
	// Constants
    private static final int PAGESIZE = 20;
	
    /** Create an anonymous user using ParseAnonymousUtils */  
    public void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
		    @Override
		    public void done(ParseUser user, ParseException e) {
	            if (e != null){
	                Log.e("LOGIN", "Parse anonymous login failed.",e);
	                throw new RuntimeException("Failed login to Parse",e);
	            }
		    }
        });
    }

    /** Get logged-in user ID (not name). */
    public String getUserId(){
        String userid = ParseUser.getCurrentUser().getObjectId();
        return userid;
    }
    
    /** Post a new Joke. */
	public void create(Joke joke, SaveCallback handler){
    	create(joke.toParseObject(),handler);
	}
    /** Create a new ParseObject, assuming it has been filled out. */
    private void create(final ParseObject po, SaveCallback handler){
    	po.saveInBackground(handler);
    }
	
    /** Create a new ParseObject, assuming it has been filled out. */
    /* NO: Let the caller implement the handler. 
    private void create(final ParseObject po, final Activity optToastActivity){
    	po.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e!=null) Log.e("PARSE ERROR","Create "+po.getClass().getSimpleName()+" Failed: "+e.getMessage(),e);
				if(optToastActivity!=null) Toast.makeText(optToastActivity, "Save "+po.getClass().getSimpleName()+" Failed!", Toast.LENGTH_SHORT);
			}
		});
    }*/
    
	/**
	 * Get jokes sorted by latest first.
	 * @param lastItemDate
	 *          The date of the last joke received.  This is not sufficiently unique on its own.
	 *          NULL = refresh from beginning. 
	 * @param lastObjectId
	 *          The ObjectId of the last joke received.  This should be unique among any multiple created at the same time.
	 *          NULL = refresh from beginning. 
	 * @param optUserId
	 *          (Optional) Limit jokes to only those created by a specific user.
	 *          NULL: defaults all users.
	 * @param handler
	 *          Asynchronous callback mechanism for you to handle results. 
	 * @param handler - callback handler to return results when they are ready.
	 */
	public void getJokesNewest(Date lastDate, String lastObjectId, String optUserId, FindCallback<ParseObject> handler){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Joke.TABLE);
		query.setLimit(PAGESIZE);
		if(lastDate!=null) query.whereLessThan(Joke.COL.CREATEDAT, lastDate);
		// We will have to consider that multiple can have the same date, but objectId isn't sequential
		// - so we also sort secondarily by objectid.
		if(lastObjectId!=null) query.whereLessThan(Joke.COL.OBJECTID , lastObjectId);
		query.orderByDescending(Joke.COL.CREATEDAT);
		query.orderByDescending(Joke.COL.OBJECTID);
		query.findInBackground(handler);
	}

	/**
	 * Get jokes sorted by latest first.
	 * @param lastVotesUp
	 *          The votes up count of the last joke received.  This is not sufficiently unique on its own.
	 *          -1/NULL = refresh from beginning. 
	 * @param lastObjectId
	 *          The ObjectId of the last joke received.  This should be unique among any multiple created at the same time.
	 *          NULL = refresh from beginning. 
	 * @param optUserId
	 *          (Optional) Limit jokes to only those created by a specific user.
	 *          NULL: defaults all users.
	 * @param handler
	 *          Asynchronous callback mechanism for you to handle results. 
	 * @param handler - callback handler to return results when they are ready.
	 */
	public void getJokesNewest(int lastVotesUp, String lastObjectId, FindCallback<ParseObject> handler){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Joke.TABLE);
		query.setLimit(PAGESIZE);
		if(lastVotesUp>=0) query.whereLessThan(Joke.COL.VOTESUP, lastVotesUp);
		// We will have to consider that multiple can have the same date, but objectId isn't sequential
		// - so we also sort secondarily by objectid.
		if(lastObjectId!=null) query.whereLessThan(Joke.COL.OBJECTID , lastObjectId);
		query.orderByDescending(Joke.COL.VOTESUP);
		query.orderByDescending(Joke.COL.OBJECTID);
		query.findInBackground(handler);
	}
	
    /** (Cached) get the user with the specified userid (user object id). */
    private void getUser(String userObjectId){
    	// TODO
    }
    
}
