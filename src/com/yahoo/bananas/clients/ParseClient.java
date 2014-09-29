package com.yahoo.bananas.clients;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import com.yahoo.bananas.models.User;

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
	public void getJokesNewest(Date lastDate, String lastObjectId, String optUserId, final ParseClient.FindJokes handler){
		getJokesNewest(lastDate,lastObjectId,optUserId,FindJokes.fromParseObjects(handler));
	}
	/** Same as getJokesNewest(...,ParseClient.FindJokes), except returns the unconverted ParseObjects for you. */
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
	public void getJokesPopular(int lastVotesUp, String lastObjectId, final ParseClient.FindJokes handler){
		getJokesPopular(lastVotesUp,lastObjectId,FindJokes.fromParseObjects(handler));
	}
	/** Same as getJokesNewest(...,ParseClient.FindJokes), except returns the unconverted ParseObjects. */
	public void getJokesPopular(int lastVotesUp, String lastObjectId, FindCallback<ParseObject> handler){
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
	
    /**
     * (Cached) Get the user with the specified userid (user object id).
     * Use this for most operation when you can most efficiently load from the cache.
     * @return NULL if not found, else non-null user with this object id.
     */
    public void getUser(final String userObjectId, final ParseClient.FindUser handler){
    	// First look in cache
    	User u = CACHE_USERS.get(userObjectId);
    	// If found in cache, return that
    	if(u!=null){
    		handler.done(u, null);
    	// If not in cache, retrieve & store in cache
    	}else{
    		getUserLatest(userObjectId, new ParseClient.FindUser() {
				@Override
				public void done(User user, ParseException e) {
					// Cache for next time
					if(user!=null) CACHE_USERS.put(userObjectId, user);
					handler.done(user, e); // Pass back through the handler they passed to us.
				}
			});
    	}
    }
    /** Cache for users retrieved by user's objectId. */
    private static final ConcurrentHashMap<String,User> CACHE_USERS = new ConcurrentHashMap<String,User>();

    /**
     * (Non-Cached) Get the latest user info with the specified userid (user object id) from the remote store.
     * Use this when you really want to get only the latest info, such as to show a profile page updates.
     * Use getUser() for most other operations, which loads from cache when already retrieved.
     * @return NULL if not found, else non-null user with this object id.
     */
    private User getUserLatest(String userObjectId){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(User.TABLE);
		query.setLimit(1);
		query.whereEqualTo(User.COL.OBJECTID, userObjectId);
		List<ParseObject> results = null;
		try{              results = query.find();
		}catch(ParseException e){ throw new RuntimeException(e.getMessage(),e); }
		if(results.size()>0) return User.fromParseObject(results.get(0));
		else                 return null;
    }
    /**
     * (Non-Cached) Get the latest user info with the specified userid (user object id) from the remote store.
     * Use this when you really want to get only the latest info, such as to show a profile page updates.
     * Use getUser() for most other operations, which loads from cache when already retrieved.
     */
    private void getUserLatest(String userObjectId, FindCallback<ParseObject> handler){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(User.TABLE);
		query.setLimit(1);
		query.whereEqualTo(User.COL.OBJECTID, userObjectId);
		query.findInBackground(handler);
    }
	/** Same as getJokesNewest(...,FindCallback<ParseObject>), except converts to Jokes for you. */
	public void getUserLatest(String userObjectId, final ParseClient.FindUser handler){
		getUserLatest(userObjectId,FindUser.fromParseObject(handler));
	}
    
	// ========================================================================
    // CONVERSION UTILITIES
	// ========================================================================
	public abstract static class FindJokes{
		public abstract void done(List<Joke> results, ParseException e);
		static FindCallback<ParseObject> fromParseObjects(final ParseClient.FindJokes handler){
			return new FindCallback<ParseObject>(){
				@Override 
				public void done(List<ParseObject> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get Jokes Failed: "+e.getMessage(),e);
					if(resultsPO==null) resultsPO = new ArrayList<ParseObject>(0); // Avoid NPE or more conditional logic.
					List<Joke> resultsJokes = Joke.fromParseObjects(resultsPO);
					handler.done(resultsJokes,e);
				}
			};
		}
	}
	
	public abstract static class FindJoke{
		public abstract void done(Joke joke, ParseException e);
		static FindCallback<ParseObject> fromParseObject(final ParseClient.FindJoke handler){
			return new FindCallback<ParseObject>(){
				@Override 
				public void done(List<ParseObject> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get Joke Failed: "+e.getMessage(),e);
					if((resultsPO!=null) && (resultsPO.size()>1)){
						Log.e("QUERY ERROR","Expected single joke result, instead found '"+resultsPO.size()+"' results.");
						if(e==null) e = new ParseException(ParseException.OTHER_CAUSE,"Expected single joke result, instead found '"+resultsPO.size()+"' results."); 
					}
					Joke joke = (resultsPO!=null)? Joke.fromParseObject(resultsPO.get(0)) : null;
					handler.done(joke,e);
				}
			};
		}
	}

	public abstract static class FindUsers{
		public abstract void done(List<User> results, ParseException e);
		static FindCallback<ParseObject> fromParseObjects(final ParseClient.FindUsers handler){
			return new FindCallback<ParseObject>(){
				@Override 
				public void done(List<ParseObject> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get Users Failed: "+e.getMessage(),e);
					if(resultsPO==null) resultsPO = new ArrayList<ParseObject>(0); // Avoid NPE or more conditional logic.
					List<User> resultsUsers = User.fromParseObjects(resultsPO);
					handler.done(resultsUsers,e);
				}
			};
		}
	}
	
	public abstract static class FindUser{
		public abstract void done(User user, ParseException e);
		static FindCallback<ParseObject> fromParseObject(final ParseClient.FindUser handler){
			return new FindCallback<ParseObject>(){
				@Override 
				public void done(List<ParseObject> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get User Failed: "+e.getMessage(),e);
					if((resultsPO!=null) && (resultsPO.size()>1)){
						Log.e("QUERY ERROR","Expected single user result, instead found '"+resultsPO.size()+"' results.");
						if(e==null) e = new ParseException(ParseException.OTHER_CAUSE,"Expected single user result, instead found '"+resultsPO.size()+"' results."); 
					}
					User user = (resultsPO!=null)? User.fromParseObject(resultsPO.get(0)) : null;
					handler.done(user,e);
				}
			};
		}
	}
    
}
