package com.yahoo.bananas.clients;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e JokeClient or FlickrClient
 * 
 */
public class JokeClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "3lJk4HiHvchVNEndu8NdOhfLW";       // Change this
	public static final String REST_CONSUMER_SECRET = "rwBFXO4bpNF1dPBlVZ0XGunSoKc4ZZtaIsyGQxNoHcgWQQHAFG"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public JokeClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
	
	public void getNewestJokeStream(long lastItemId, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if(lastItemId>0) params.put("max_id",""+(lastItemId-1)); // Subtract 1 because max id will return results inclusive of that ID, and we only want the next older ones.  Use max & subtract because we are going for older IDs.
		params.put("count","25");
		client.get(url, params, handler);
	}

	public void getPopularJokeStream(long lastItemId, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if(lastItemId>0) params.put("max_id",""+(lastItemId-1)); // Subtract 1 because max id will return results inclusive of that ID, and we only want the next older ones.  Use max & subtract because we are going for older IDs.
		params.put("count","25");
		client.get(url, params, handler);
	}

	/**
	 * Get the timeline for the specified user.
	 * @param optUid
	 *          (Optional) ID of the user you want to display timeline for.
	 *          -1: defaults to current user.
	 * @param lastItemId
	 *          Last item ID retrieved (for endless scolling to continue).
	 *          0: refresh from the beginning.
	 * @param handler - callback handler to return results when they are ready.
	 */
	public void getUserJokeStream(long optUid, long lastItemId, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		if(optUid>-1) params.put("user_id", ""+optUid);
		if(lastItemId>0) params.put("max_id",""+(lastItemId-1)); // Subtract 1 because max id will return results inclusive of that ID, and we only want the next older ones.  Use max & subtract because we are going for older IDs.
		params.put("count","25");
		client.get(url, params, handler);
	}
	
	public void getMyProfile(AsyncHttpResponseHandler handler){
		String url = getApiUrl("account/verify_credentials.json");
		client.get(url, null, handler);
	}
	
	public void createJoke(String tweet, AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",tweet);
		client.post(url, params, handler);
	}
	
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	/* public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	} */

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}