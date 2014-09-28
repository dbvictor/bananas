package com.yahoo.bananas;

import android.app.Application;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.PushService;
import com.yahoo.bananas.clients.ParseClient;

public class JokeApplication extends Application {
	// Member Variables
	private static ParseClient PARSE_CLIENT = new ParseClient(); 
	
	@Override
	public void onCreate() {
		super.onCreate();
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
	}
	
	public static ParseClient getParseClient() {
		return PARSE_CLIENT;
	}
	
}