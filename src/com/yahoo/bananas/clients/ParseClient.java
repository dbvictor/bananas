package com.yahoo.bananas.clients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.User;

public class ParseClient {
	// Constants
    private static final int PAGESIZE = 20;
	
    // ========================================================================
    // PARSE SETUP
    // ========================================================================
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
    
    /** Get current logged-in Parse user ID (not name). */
    public String getUserId(){
        String userid = ParseUser.getCurrentUser().getObjectId();
        return userid;
    }
    /**
     * (Cached) Get the current logged-in User.  This should be a cached lookup, but if it isn't,
     * this method will synchronously retrieve it.
     */
    public User getUser(){
    	User user = CACHE_USERS.get(getUserId());
    	if(user==null) user = getUserLatest(getUserId()); // Automatically caches it if found.
    	return user;
    }

    // ========================================================================
    // DATA OPERATIONS
    // ========================================================================
    
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
	 * @param optCategories
	 *          (Optional) Limit jokes to specific categories.
	 *          NULL: defaults to all categories.
	 *          EMPTY: Empy list results in no categories (0 results)
	 * @param handler
	 *          Asynchronous callback mechanism for you to handle results. 
	 * @param handler - callback handler to return results when they are ready.
	 */
	public void getJokesNewest(Date lastDate, String lastObjectId, String optUserId, List<Category> optCategories, ParseClient.FindJokes handler){
		getJokesNewest(lastDate,lastObjectId,optUserId,optCategories,FindJokes.fromParseObjects(handler));
	}
	/** Same as getJokesNewest(...,ParseClient.FindJokes), except returns the unconverted ParseObjects for you. */
	private void getJokesNewest(Date lastDate, String lastObjectId, String optUserId, List<Category> optCategories, FindCallback<ParseObject> handler){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Joke.TABLE);
		query.setLimit(PAGESIZE);
		if(lastDate!=null) query.whereLessThan(Joke.COL.CREATEDAT, lastDate);
		// We will have to consider that multiple can have the same date, but objectId isn't sequential
		// - so we also sort secondarily by objectid.
		if(lastObjectId!=null) query.whereLessThan(Joke.COL.OBJECTID , lastObjectId);
		if(optCategories!=null) query.whereContainedIn(Joke.COL.CATEGORY, Category.toDbValue(optCategories));
		if(optUserId!=null) query.whereEqualTo(Joke.COL.CREATEDAT, optUserId);
		query.orderByDescending(Joke.COL.CREATEDAT);
		query.addDescendingOrder(Joke.COL.OBJECTID);
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
	 * @param optCategories
	 *          (Optional) Limit jokes to specific categories.
	 *          NULL: defaults to all categories.
	 *          EMPTY: Empy list results in no categories (0 results)
	 * @param handler
	 *          Asynchronous callback mechanism for you to handle results. 
	 * @param handler - callback handler to return results when they are ready.
	 */
	public void getJokesPopular(int lastVotesUp, String lastObjectId, List<Category> optCategories, ParseClient.FindJokes handler){
		getJokesPopular(lastVotesUp,lastObjectId,optCategories,FindJokes.fromParseObjects(handler));
	}
	/** Same as getJokesNewest(...,ParseClient.FindJokes), except returns the unconverted ParseObjects. */
	private void getJokesPopular(int lastVotesUp, String lastObjectId, List<Category> optCategories, FindCallback<ParseObject> handler){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Joke.TABLE);
		query.setLimit(PAGESIZE);
		if(lastVotesUp>=0) query.whereLessThan(Joke.COL.VOTESUP, lastVotesUp);
		// We will have to consider that multiple can have the same date, but objectId isn't sequential
		// - so we also sort secondarily by objectid.
		if(lastObjectId!=null) query.whereLessThan(Joke.COL.OBJECTID , lastObjectId);
		if(optCategories!=null) query.whereContainedIn(Joke.COL.CATEGORY, Category.toDbValue(optCategories));
		query.orderByDescending(Joke.COL.VOTESUP);
		query.addDescendingOrder(Joke.COL.OBJECTID);
		query.findInBackground(handler);
	}
	
	/**
	 * Get User objects for any userid value specified in the Jokes.
	 * @param jokes - the jokes you already retrieved but without User objects populated yet.
	 * @param handler - async response handler once we finish populating the jokes with user objects.           
	 */
	private void getJokesUsers(final List<Joke> jokes, final ParseClient.FindJokes handler){
		Log.d("trace", "+getJokesUsers(Collection.size="+jokes.size()+")");
		// 1. Determine all the users we need.
		HashSet<String> userObjectIds = new HashSet<String>(jokes.size()); 
		for(Joke j : jokes) userObjectIds.add(j.getCreatedBy());
		// 2. Get Users (from cache or retrieve and put in cache)
		getUsers(userObjectIds, new FindUsers() {
			@Override
			public void done(List<User> users, ParseException e) {
				if(e!=null)	Log.e("PARSE ERROR","Get Users for Jokes Failed: "+e.getMessage(),e);
				Log.d("debug", "getJokesUsers found users="+((users!=null)? users.size(): "null")+")");
				// 3. Put the users into Jokes.
				if(users!=null){
					// We need a map to quickly find the user given the user ID.
					HashMap<String,User> mapIdToUser = new HashMap<String,User>(users.size());
					for(User u : users) mapIdToUser.put(u.getObjectId(), u);
					// Now just populate users we found into jokes
					for(Joke j : jokes) j.setCreatedBy(mapIdToUser.get(j.getCreatedBy()));
				}
				// 4. Return to caller the original jokes (now filled out)
				handler.done(jokes, e);
			}
		});
	}
	/**
	 * Get User objects for any userid value specified in the Joke.
	 * @param joke - the jokes you already retrieved but without User objects populated yet.
	 * @param handler - async response handler once we finish populating the jokes with user objects.           
	 */
	private void getJokesUsers(final Joke joke, final ParseClient.FindJoke handler){
		// Reuse list-based method, just convert the interface.
		ArrayList<Joke> jokes = new ArrayList<Joke>(1);
		jokes.add(joke);
		getJokesUsers(jokes, new FindJokes() {
			@Override
			public void done(List<Joke> results, ParseException e) {
				// ignore list result, we already have the joke, it would have been updated within the list.
				handler.done(joke, e);
			}
		});
	}
	
	
    /**
     * (Cached) Get the user with the specified userid (user object id).
     * Use this for most operation when you can most efficiently load from the cache.
     * @return NULL if not found, else non-null user with this object id.
     */
    public void getUser(final String userObjectId, final ParseClient.FindUser handler){
    	// First look in cache
    	User u = CACHE_USERS.get(userObjectId);
    	// If found in cache, return that, else retrieve & store in cache
    	if(u!=null){
    		handler.done(u, null);
    	// If not in cache, retrieve & store in cache
    	}else{
    		getUserLatest(userObjectId, handler); // Automatically caches in the FindUser handler.
    		/* UNECESSARY: Don't need to cache here.  It is automatically cached in the FindUser handler. 
    		new ParseClient.FindUser() {
				@Override
				public void done(User user, ParseException e) {
					// Cache for next time
					if(user!=null) CACHE_USERS.put(userObjectId, user);
					handler.done(user, e); // Pass back through the handler they passed to us.
				}
			}); */
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
    	User user = null;
    	try{
			ParseQuery<ParseUser> query = ParseUser.getQuery(); //NO: ParseQuery.getQuery(User.TABLE);
			query.setLimit(1);
			query.get(userObjectId); // Use built-in for objectId, not custom column: query.whereEqualTo(User.COL.OBJECTID, userObjectId);
			List<ParseUser> results = null;
		    results = query.find();
		    if(results.size()>0) user = User.fromParseObject(results.get(0));
		}catch(ParseException e){ throw new RuntimeException(e.getMessage(),e); }
    	if(user!=null) CACHE_USERS.put(userObjectId, user);
    	return user;
    }
    /**
     * (Non-Cached) Get the latest user info with the specified userid (user object id) from the remote store.
     * Use this when you really want to get only the latest info, such as to show a profile page updates.
     * Use getUser() for most other operations, which loads from cache when already retrieved.
     */
	public void getUserLatest(String userObjectId, final ParseClient.FindUser handler){
		getUserLatest(userObjectId,FindUser.fromParseObject(handler));
	}
	/** Same as getUserLatest(...,ParseClient.FindUser), except returns unconverted ParseObjects. */
    private void getUserLatest(String userObjectId, FindCallback<ParseUser> handler){
    	try{
    		// To query for System table User, we have to use a special ParseUser.getQuery()
			//NO: ParseQuery<ParseObject> query = ParseQuery.getQuery(User.TABLE);
    		ParseQuery<ParseUser> query = ParseUser.getQuery();
			query.setLimit(1);
			query.get(userObjectId); // Use built-in for objectId, not custom column: query.whereEqualTo(User.COL.OBJECTID, userObjectId);
			query.findInBackground(handler);
		}catch(ParseException e){ throw new RuntimeException(e.getMessage(),e); } // Just propagate as unchecked runtime exception.
    }

    /**
     * (Cached) Get the users with the specified userids (user object id).
     * Use this for most operation when you can most efficiently load from the cache.
     * @return NULL if not found, else non-null user with this object id.
     */
	public void getUsers(Collection<String> userObjectIds, final ParseClient.FindUsers handler){
		Log.d("trace", "+getUsers(Collection.size="+userObjectIds.size()+")");
		// 1. First, look in the cache.
		// + determine what users we still need.
		final ArrayList<User  > users       = new ArrayList<User  >(userObjectIds.size());
		final ArrayList<String> usersNeeded = new ArrayList<String>(userObjectIds.size());
		for(String userObjectId : userObjectIds){
			User u = CACHE_USERS.get(userObjectId);
			if(u!=null) users.add(u);
			else        usersNeeded.add(userObjectId);
		}
		// 2. If all found, return
		if(usersNeeded.size()<=0){
			handler.done(users,null);
		// 3. Else retrieve users we are missing.
		}else{
			getUsersLatest(usersNeeded, new FindUsers() {
				@Override
				public void done(List<User> results, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get Users Latest Failed: "+e.getMessage(),e);
					Log.d("DEBUG", "getUsersLatest found '"+((results!=null)? results.size() : 0)+"' users.");
					// Combine the new results with the ones we already had.
					if(results!=null) users.addAll(results);
					// Tell the caller we're done
					handler.done(users, e);
				}
			});
		}
	}
    
    /**
     * (Non-Cached) Get the latest user info with the specified userids (user object ids) from the remote store.
     * Use this when you really want to get only the latest info, such as to show a profile page updates.
     * Use getUser() for most other operations, which loads from cache when already retrieved.
     */
	public void getUsersLatest(Collection<String> userObjectIds, ParseClient.FindUsers handler){
		getUsersLatest(userObjectIds,FindUsers.fromParseObjects(handler));
	}
	/** Same as getUserLatest(...,ParseClient.FindUser), except returns unconverted ParseObjects. */
    private void getUsersLatest(Collection<String> userObjectIds, FindCallback<ParseUser> handler){
		// To query for System table User, we have to use a special ParseUser.getQuery()
		//NO: ParseQuery<ParseObject> query = ParseQuery.getQuery(User.TABLE);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setLimit(userObjectIds.size());
		query.whereContainedIn(User.COL.OBJECTID, userObjectIds);
		query.findInBackground(handler);
    }
	
    /** Update existing User. */
	public void update(final User user, final SaveCallback handler){
		List<ParseUser> users = new ArrayList<ParseUser>(1);
		users.add(user.toParseObject());
		ParseUser.saveAllInBackground(users, new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e!=null)	Log.e("PARSE ERROR","Update Joke Failed: "+e.getMessage(),e);
				// Cache the updated user if it succeeds, or the user will keep seeing the old cached user.
				if(e==null) CACHE_USERS.put(user.getObjectId(), user);
				// Report to the caller that it was done.
				handler.done(e);
			}
		});
	}
	
    /** Update existing Joke. */
	public void update(Joke joke, SaveCallback handler){
    	update(joke.toParseObject(),handler);
	}
	
    /** Create a new ParseObject, assuming it has been filled out. */
    private void update(final ParseObject po, SaveCallback handler){
    	po.saveInBackground(handler);
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
					Log.d("trace", "FindJokes.fromParseObjects::done() (Collection.size="+((resultsPO!=null)? resultsPO.size() : "null")+")");
					if(e!=null)	Log.e("PARSE ERROR","Get Jokes Failed: "+e.getMessage(),e);
					if(resultsPO==null) resultsPO = new ArrayList<ParseObject>(0); // Avoid NPE or more conditional logic.
					List<Joke> resultsJokes = Joke.fromParseObjects(resultsPO);
					if(e==null) JokesApplication.getParseClient().getJokesUsers(resultsJokes,handler);
					else        handler.done(resultsJokes, e);
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
					Joke joke = null;
					if((resultsPO!=null)&&(resultsPO.size()>0)) Joke.fromParseObject(resultsPO.get(0));
					if(e!=null) JokesApplication.getParseClient().getJokesUsers(joke,handler);
					else        handler.done(joke,e);
				}
			};
		}
	}

	public abstract static class FindUsers{
		public abstract void done(List<User> results, ParseException e);
		static FindCallback<ParseUser> fromParseObjects(final ParseClient.FindUsers handler){
			return new FindCallback<ParseUser>(){
				@Override 
				public void done(List<ParseUser> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get Users Failed: "+e.getMessage(),e);
					// Convert to Users
					if(resultsPO==null) resultsPO = new ArrayList<ParseUser>(0); // Avoid NPE or more conditional logic.
					List<User> resultsUsers = User.fromParseObjects(resultsPO);
					// Cache Users (or update in case any changed)
					for(User u : resultsUsers) CACHE_USERS.put(u.getObjectId(), u); // Update cache with the user.
					// Report to caller's handler with the converted users.
					handler.done(resultsUsers,e);
				}
			};
		}
	}
	
	public abstract static class FindUser{
		public abstract void done(User user, ParseException e);
		static FindCallback<ParseUser> fromParseObject(final ParseClient.FindUser handler){
			return new FindCallback<ParseUser>(){
				@Override 
				public void done(List<ParseUser> resultsPO, ParseException e) {
					if(e!=null)	Log.e("PARSE ERROR","Get User Failed: "+e.getMessage(),e);
					if((resultsPO!=null) && (resultsPO.size()>1)){
						Log.e("QUERY ERROR","Expected single user result, instead found '"+resultsPO.size()+"' results.");
						if(e==null) e = new ParseException(ParseException.OTHER_CAUSE,"Expected single user result, instead found '"+resultsPO.size()+"' results."); 
					}
					// Convert to User
					User user = null;
					if((resultsPO!=null)&&(resultsPO.size()>0)) user = User.fromParseObject(resultsPO.get(0));
					// Cache Users (or update in case any changed)
					if(user!=null) CACHE_USERS.put(user.getObjectId(), user); // Update cache with the user.
					// Report to caller's handler with the converted users.
					handler.done(user,e);
				}
			};
		}
	}
	
	/** delete an existing Joke. */
	public void delete(Joke joke, DeleteCallback handler){
    	delete(joke.toParseObject(),handler);
	}
    /** delete an existing joke in background */
    private void delete(final ParseObject po, DeleteCallback handler){
    	po.deleteInBackground(handler);
    }
    
}
