package com.yahoo.bananas;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;
import com.yahoo.bananas.activities.JokeStreamActivity;

public class JokeApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// Add your initialization code here
		Parse.initialize(this, "jYfEj0TBOHMNGWq7nRYu6fOzssrhE65PDjjPP86l", "gaugOELSbfQCvTMGdmwz2n4OYu7fW92DS1kwofdN");
		// Parse Test
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();	
		// Setup Push Notifications
		PushService.setDefaultPushCallback(this, JokeStreamActivity.class);
	}
}