package com.yahoo.bananas.clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.parse.ParseException;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.JokeState;

/**
 * Client for interacting with local persistent store (SQLite) for offline behavior.
 * 
 * This should duplicate all the actions supported by the Parse client, and any other offline-only
 * storage.  We put all the interactions in one place for better maintainability & class design
 * than mixing it everywhere else.
 */
public class OfflineClient {
    /** Cache for JokeStates retrieved by joke's objectId. */
    private static ConcurrentHashMap<String,JokeState> CACHE_JOKESTATES = null;
	
	/** Load (or reload) the JokeState cache (CACHE_JOKESTATES) from offline storage. */
	private void cacheJokeStates(){
		ConcurrentHashMap<String,JokeState> map = new ConcurrentHashMap<String,JokeState>();
		List<JokeState> list = getAll(JokeState.class);
		for(JokeState js : list) map.put(js.getJokeObjectId(), js);
		CACHE_JOKESTATES = map;
	}
	
	/** Asynchronously load (or reload) the JokeState cache (CACHE_JOKESTATE) from offline storage. */
	public void cacheJokeStatesAsync(){
		new AsyncTask<Void,Void,Void>(){
			@Override
		    protected Void doInBackground(Void... ignore) { // Type is first one in template.
		         cacheJokeStates(); // Some long running task we don't want to wait for.
		         return null;
		     }
		}.execute();
	}

	/**
	 * (Cached) Get the JokeState for the specified joke.
	 * CACHED: This method utilizes a cache to obtain current history.
	 * @param jokeObjectId
	 *          Joke ObjectId that you want the joke state for.
	 * @param createIfNotExist
	 *          Whether you want to create a new JokeStat to return a non-null instance if there is
	 *          no user history on this joke yet, or just return NULL if not exist.
	 *          TRUE: If not exist, return a new JokeStat instance.
	 *          FALSE: If not exist, return null.  
	 * @param handler
	 *          Return result will be passed back through the handler.
	 *          If cache is not loaded, this task will run asynchronously and call back when ready.  
	 * @return NULL if createIfNotExist==false and no joke state history exists for this joke by this user.
	 *         Else non-null joke state history for the joke by this user.
	 */
	public void getJokeState(String jokeObjectId, boolean createIfNotExist, GetJokeState handler){
		getJokeStates(Arrays.asList(jokeObjectId),createIfNotExist,GetJokeState.fromJokeStates(handler));
	}
	private JokeState getJokeState(String jokeObjectId, boolean createIfNotExist){
		// Always load from cache, but if not yet loaded, load now.
		if(CACHE_JOKESTATES==null) cacheJokeStates();
		JokeState js = CACHE_JOKESTATES.get(jokeObjectId);
		if((js==null) && createIfNotExist) js = new JokeState(jokeObjectId);
		return js;
	}
	/** Asynchronous callback for getJokeState() result. */
	public abstract static class GetJokeState{
		public abstract void done(JokeState jokeState, Exception e);
		static GetJokeStates fromJokeStates(final GetJokeState handler){
			return new GetJokeStates() {
				@Override
				public void done(Map<String,JokeState> results, Exception e) {
					JokeState js = null;
					if(results!=null){
						if(results.size()==0) js = null;
						else if(results.size()==1) js = results.values().iterator().next();
						else if(e==null) e = new RuntimeException("Found '"+results.size()+"' results when only single result expected.");
					}
					handler.done(js, e);
				}
			};
		}
	}

	/**
	 * (Cached) Get the JokeState for the specified jokes.
	 * CACHED: This method utilizes a cache to obtain current history.
	 * @param jokeObjectIds
	 *          List of Joke ObjectIds that you want the joke state for.
	 * @param createIfNotExist
	 *          Whether you want to create a new JokeStat to return a non-null instance if there is
	 *          no user history on this joke yet, or just return NULL if not exist.
	 *          TRUE: If not exist, return a new JokeStat instance.
	 *          FALSE: If not exist, return null.
	 * @param handler
	 *          Return results will be passed back through the handler.
	 *          If cache is not loaded, this task will run asynchronously and call back when ready.  
	 * @return Non-null map of 0 or more joke state instances found by Joke objectId if exists for
	 *         each joke for this user.  If createIfNotExist==true, then the list will be exactly
	 *         the number of joke state instances requested.
	 *         KEY: Joke ObjectId.
	 *         VALUE: JokeState for that joke.
	 */
	public void getJokeStates(final Collection<String> jokeObjectIds, final boolean createIfNotExist, final GetJokeStates handler){
		// If joke states are cached, just return from cache immediately.
		if(CACHE_JOKESTATES!=null) handler.done(getJokeStates(jokeObjectIds, createIfNotExist), null);
		// Otherwise run a background task to load the cache.
		else new AsyncTask<Void,Void,Void>(){
			@Override
		    protected Void doInBackground(Void... ignore) { // Type is first one in template.
				try{
					cacheJokeStates(); // Synchronously load cache
					handler.done(getJokeStates(jokeObjectIds, createIfNotExist), null); // Then run function that relies on cache.
				}catch(Exception e){ handler.done(null, e); } 
				return null;
			}
		}.execute();
	}
	private Map<String, JokeState> getJokeStates(Collection<String> jokeObjectIds, boolean createIfNotExist){
		Map<String,JokeState> jokeStates = new HashMap<String,JokeState>(jokeObjectIds.size());
		for(String jokeObjectId : jokeObjectIds) jokeStates.put(jokeObjectId,getJokeState(jokeObjectId,createIfNotExist));
		Log.d("trace", "Found joke states x"+jokeStates.size());
		return jokeStates;
	}
	/** Asynchronous callback for getJokeStates() result. */
	public abstract static class GetJokeStates{
		public abstract void done(Map<String,JokeState> results, Exception e);
	}
	/** Same as getJokeStates(...) except populates results directly into Joke objects. */
	public void getJokeStates(final List<Joke> jokes, final boolean createIfNotExist, final ParseClient.FindJokes handler){
		// Get list of joke IDs
		List<String> jokeObjectIds = new ArrayList<String>(jokes.size());
		for(Joke j : jokes) jokeObjectIds.add(j.getObjectId());
		// Call joke object ID based version
		getJokeStates(jokeObjectIds, createIfNotExist, new GetJokeStates() {
			@Override
			public void done(Map<String,JokeState> results, Exception e) {
				Log.d("trace","OfflineClient.getJokeStates.done() results="+((results!=null)? results.size() : "null"));
				if(e!=null) Log.e("ERROR","Failed to load Joke States: "+e.getClass().getSimpleName()+" = "+e.getMessage());
				if(results!=null){
					for(Joke j : jokes){
						j.setUserState(results.get(j.getObjectId()));
						if(j.getUserState()==null){
							Log.e("UNEXPECTED","Failed to find joke state for joke '"+j.getObjectId()+"' when the option was set to create if not exist!");
							if(e!=null) e = new RuntimeException("INTERNAL ERROR: Failed to find joke state for joke '"+j.getObjectId()+"' when the option was set to create if not exist!"); 
						}
					}
				}
				ParseException parseException = null;
				if(e!=null) parseException = new ParseException(e.getMessage(), e);
				handler.done(jokes, parseException);
			}
		});
	}
	
	// ========================================================================
	// STATIC UTILITY FUNCTIONS
	// ========================================================================
	// ----- PERSISTENCE -----
	/**
	 * Retrieve all instances from offline storage for the specified class.
	 * @return Non-null List of zero or more instances of the class found. 
	 */
    public static <T extends Model> List<T> getAll(Class<T> modelClass) {
        // This is how you execute a query
        List<T> result = new Select()
          .all()
          .from(modelClass)
          //NO CONDITIONS: .where("Category = ?", category.getId())
          //NO ORDER: .orderBy("createdAt DESC")
          .execute();
        if(result==null) result = new ArrayList<T>(0);
        return result;
    }

	/**
	 * Retrieve all instances from offline storage for the specified class.
	 * @param orderBy - Order by some specific attribute, such as "createdAt DESC" or "createdAt ASC".
	 * @return Non-null List of zero or more instances of the class found. 
	 */
    public static <T extends Model> List<T> getAll(Class<T> modelClass, String orderBy) {
        // This is how you execute a query
        List<T> result = new Select()
          .all()
          .from(modelClass)
          //NO CONDITIONS: .where("Category = ?", category.getId())
          .orderBy(orderBy)
          .execute();
        if(result==null) result = new ArrayList<T>(0);
        return result;
    }
    
	/** Save multiple Model subclass instances to offline persistence (SQLite). */
	private static void saveToOffline(Collection<? extends Model> list){
		try{
			ActiveAndroid.beginTransaction();
			try{
			        for (Model m : list) m.save();
			        ActiveAndroid.setTransactionSuccessful();
			}finally{
			        ActiveAndroid.endTransaction();
			}
			Log.d("sqlite","persisted "+((list.size()>0)? (""+list.iterator().next().getClass().getSimpleName()) : "Model")+" x "+list.size());
		}catch(RuntimeException e){ // Log & rethrow
			Log.e("sqlite","Error persisting "+((list.size()>0)? (""+list.iterator().next().getClass().getSimpleName()) : "Model")+" x "+list.size()+": "+e.getMessage(),e);
			throw e;
		}
	}

	/** Save multiple Model subclass instances asynchronously to offline persistence (SQLite). */
	public static void saveToOfflineAsync(final Collection<? extends Model> list){
		if(list.size()<=0) return; // Do nothing if empty.
		new AsyncTask<Void,Void,Void>(){
			@Override
		    protected Void doInBackground(Void... ignore) { // Type is first one in template.
		         // Some long-running task like downloading an image.
		         saveToOffline(list);
		         return null;
		     }
		}.execute();
	}
	/** Save Model subclass instance asynchronously to offline persistence (SQLite). */
	public static void saveToOfflineAsync(Model model){
		saveToOfflineAsync(Arrays.asList(model));
	}
	
}
