package com.yahoo.bananas;

import android.content.Context;
import android.widget.Toast;

import com.activeandroid.app.Application;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.PushService;
import com.yahoo.bananas.activities.JokeStreamActivity;
import com.yahoo.bananas.clients.OfflineClient;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.clients.TwitterClient;

public class JokesApplication extends Application {
	// Member Variables
	private static final OfflineClient OFFLINE_CLIENT = new OfflineClient(); 
	private static final ParseClient   PARSE_CLIENT   = new ParseClient(); 
	private static       Context       RESTCLIENT_CONTEXT;
	
	@Override
	public void onCreate() {
		super.onCreate();
		JokesApplication.RESTCLIENT_CONTEXT = this;
		// Application-wide initialization code
		// 1. Start loading / caching offline information in the background.
		OFFLINE_CLIENT.cacheJokeStatesAsync(); // Start loading these now so they will be ready when when we start to use them.
		// 2. Parse Setup
		// 2.1. Initialize with app and client keys.
		Parse.initialize(this, "jYfEj0TBOHMNGWq7nRYu6fOzssrhE65PDjjPP86l", "gaugOELSbfQCvTMGdmwz2n4OYu7fW92DS1kwofdN");
		// 2.2. Parse Test
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();	
		// 2.3. Setup Push Notifications
		PushService.setDefaultPushCallback(this, JokeStreamActivity.class);
		// 2.4. Login
		PARSE_CLIENT.login();
		Toast.makeText(this, "Parse Logged In", Toast.LENGTH_SHORT).show();
		// 3. ImageLoader Setup -- Create global configuration and initialize ImageLoader with this configuration
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(defaultOptions)
			.build();
		ImageLoader.getInstance().init(config);
	}

	public static OfflineClient getOfflineClient() {
		return OFFLINE_CLIENT;
	}
	
	public static ParseClient getParseClient() {
		return PARSE_CLIENT;
	}
	
	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, RESTCLIENT_CONTEXT);
	}
	
}