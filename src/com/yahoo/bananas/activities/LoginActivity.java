package com.yahoo.bananas.activities;

import org.json.JSONObject;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.clients.TwitterClient;
import com.yahoo.bananas.models.TwitterUser;
import com.yahoo.bananas.models.User;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setActionBarColor();
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		ProgressBar pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
		pbLogin.setVisibility(ProgressBar.INVISIBLE);

		Toast.makeText(this, "Logged into Twitter", Toast.LENGTH_SHORT).show();
		// Update User Info from Twitter Info
		updateUserFromTwitterAsync();
		// Move onto JokeStream
		startNextActivity();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		ProgressBar pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
		pbLogin.setVisibility(ProgressBar.INVISIBLE);
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		ProgressBar pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
		pbLogin.setVisibility(ProgressBar.VISIBLE);
		getClient().connect();
	}
	
	/** Update Anonymous Parse User from Twitter Info (real name, screen name, image URL, etc). */
	private void updateUserFromTwitterAsync(){
		final ParseClient   parseClient   = JokesApplication.getParseClient();
		final TwitterClient twitterClient = JokesApplication.getTwitterClient();
		final Application   toastContext  = getApplication();
		// PLAN
		// 0. Chain the following into embedded asynchronous calls so it all happens in the background.
		// 1. Get Existing Parse User
		// 2. Get Twitter User
		// 3. Set Twitter Info into Parse User
		// 4. Update Parse User
		// IMPLEMENT
		// 1. Get Existing Parse User
		parseClient.getUser(parseClient.getUserId(), new ParseClient.FindUser() {
			@Override
			public void done(final User userP, ParseException e) {
				if(e!=null){ // If error, just report
					Log.e("PARSE", e.toString());
					Toast.makeText(toastContext, "Get Parse Profile FAILED!", Toast.LENGTH_SHORT).show();
				}else if(userP==null){ // If not found, just report
					Log.e("PARSE", "Parse User '"+parseClient.getUserId()+"' not found.");
					Toast.makeText(toastContext, "Parse User NOT FOUND!", Toast.LENGTH_SHORT).show();
				}else{ // If found, continue
					// 2. Get Twitter User
					twitterClient.getMyProfile(new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject json) {
							Log.d("json", "MyInfo JSON: "+json.toString());
							TwitterUser userT = TwitterUser.fromJSON(json);
							if(userT==null){
								Log.e("PARSE", "Twitter User not found.");
								Toast.makeText(toastContext, "Twitter User NOT FOUND!", Toast.LENGTH_SHORT).show();
							}else{
								// 3. Set Twitter Info into Parse User
								userP.setImageUrl(userT.getImageUrl());
								userP.setRealName(userT.getRealName());
								// 4. Update Parse User
								parseClient.update(userP, new SaveCallback() {
									@Override
									public void done(ParseException e) {
										if(e!=null){ // If error, just report
											Log.e("PARSE", e.toString());
											Toast.makeText(toastContext, "Update Parse Profile FAILED!", Toast.LENGTH_SHORT).show();
										}else{
											Log.d("PARSE", "Updated Parse User from Twitter");
											Toast.makeText(toastContext, "User Updated from Twitter", Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
						}
						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("TWITTER", e.toString());
							Log.d("TWITTER", s.toString());
							Toast.makeText(toastContext, "Twitter Profile FAILED!", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}
	
	/** Skip logon, stay anonymous, go straight to using app anonymously. */
	public void skipLogin(View view) {
		startNextActivity();
	}

	/** Move onto next activity after logon. */
	private void startNextActivity(){
		// Move onto JokeStream
		Intent i = new Intent(this, JokeStreamActivity.class);
		startActivity(i);
	}

}
