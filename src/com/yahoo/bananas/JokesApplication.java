package com.yahoo.bananas;

import com.activeandroid.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.PushService;
import com.yahoo.bananas.clients.TwitterClient;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.activities.JokeStreamActivity;

public class JokesApplication extends Application {
	// Member Variables
	private static final ParseClient PARSE_CLIENT = new ParseClient(); 
	private static       Context     RESTCLIENT_CONTEXT;
	
	@Override
	public void onCreate() {
		super.onCreate();
		JokesApplication.RESTCLIENT_CONTEXT = this;
		// Application-wide initialization code
		// 1. Parse Setup
		// 1.1. Initialize with app and client keys.
		Parse.initialize(this, "jYfEj0TBOHMNGWq7nRYu6fOzssrhE65PDjjPP86l", "gaugOELSbfQCvTMGdmwz2n4OYu7fW92DS1kwofdN");
		// 1.2. Parse Test
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();	
		// 1.3. Setup Push Notifications
		PushService.setDefaultPushCallback(this, JokeStreamActivity.class);
		// 1.4. Login
		PARSE_CLIENT.login();
		Toast.makeText(this, "Parse Logged In", Toast.LENGTH_SHORT).show();
		// 2. ImageLoader Setup -- Create global configuration and initialize ImageLoader with this configuration
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(defaultOptions)
			.build();
		ImageLoader.getInstance().init(config);
	}
	
	public static ParseClient getParseClient() {
		return PARSE_CLIENT;
	}
	
	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, RESTCLIENT_CONTEXT);
	}
	
}