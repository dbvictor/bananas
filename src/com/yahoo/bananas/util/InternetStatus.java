package com.yahoo.bananas.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/** Check Internet status, with optional override. */
public class InternetStatus {
	// Constants
	private static final String PREF_INTERNET_ENABLED = "internetEnabled";
	// Member Variables
	private Activity          activity;
	private SharedPreferences pref;
	
	public InternetStatus(Activity anyActivity){
		activity = anyActivity;
		pref = PreferenceManager.getDefaultSharedPreferences(anyActivity);		
	}
	
	/** Change the in-app toggle switch status. */
	public void setAppToggleEnabled(boolean newValue){
		// Persist change to preferences
		Editor prefEdit = pref.edit();
		prefEdit.putBoolean(PREF_INTERNET_ENABLED, newValue);
		prefEdit.commit();
	}
	
	/** Status of the in-app toggle switch that can override actual Internet availability. */
	public boolean isAppToggleEnabled(){
		return pref.getBoolean(PREF_INTERNET_ENABLED, true); // Load current setting from preferences.
	}
	
	/** Determine if network is available and enabled within In-App toggle. */
    public boolean isAvailable() {
    	// 1. Check In-App Override to Disable Internet (easier for quick demonstrations)
    	if(!isAppToggleEnabled()) return false; // If simulated off, make it appear not working.
    	// 2. Else Check Real Network Status
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    
}
