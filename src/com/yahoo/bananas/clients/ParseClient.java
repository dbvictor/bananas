package com.yahoo.bananas.clients;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
    
    /** (Cached) get the user with the specified userid (user object id). */
    private void getUser(String userObjectId){
    	// TODO
    }
    
    private void receiveMessage() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
		query.setLimit(PAGESIZE);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> messages, ParseException e) {
				if (e == null) {
					final ArrayList<Message> newMessages = new ArrayList<Message>();					
					for (int i = messages.size() - 1; i >= 0; i--) {
						final Message message = new Message();
						message.userId = messages.get(i).getString(USER_ID_KEY);
						message.text = messages.get(i).getString("message");
						newMessages.add(message);
					}
					addItemstoListView(newMessages);
				} else {
					Log.d("message", "Error: " + e.getMessage());
				}
			}
		});
	}
    

}
