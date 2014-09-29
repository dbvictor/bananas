package com.yahoo.bananas.clients;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.bananas.models.Joke;

public class ParseClient {
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
    public void create(Joke joke, final Activity optToastActivity){
    	create(joke.toParseObject(),optToastActivity);
    }
    
    /** Create a new ParseObject, assuming it has been filled out. */
    private void create(final ParseObject po, final Activity optToastActivity){
    	po.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e!=null) Log.e("PARSE ERROR","Create "+po.getClass().getSimpleName()+" Failed: "+e.getMessage(),e);
				if(optToastActivity!=null) Toast.makeText(optToastActivity, "Save "+po.getClass().getSimpleName()+" Failed!", Toast.LENGTH_SHORT);
			}
		});
    }
    
    /** (Cached) get the user with the specified userid (user object id). */
    private void getUser(String userObjectId){
    	// TODO
    }

}
