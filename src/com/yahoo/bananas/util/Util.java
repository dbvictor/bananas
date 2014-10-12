package com.yahoo.bananas.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.widget.ProgressBar;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;

public class Util {
	
	private static final String H = "h ago";
	private static final String M = "m ago";
	private static final String S = "s ago";
	private static final String D = "d ago";
	private static final String DAYS_AGO = " days ago";
	private static final String DAY_AGO = " day ago";
	private static final String HOURS_AGO = " hours ago";
	private static final String HOUR_AGO = " hour ago";
	private static final String MINUTES_AGO = " minutes ago";
	private static final String MINUTE_AGO = " minute ago";
	private static final String SECONDS_AGO = " seconds ago";
	private static final String SECOND_AGO = " second ago";
	
	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		relativeDate = relativeDate.replace(SECOND_AGO, S);
		relativeDate = relativeDate.replace(SECONDS_AGO, S);
		relativeDate = relativeDate.replace(MINUTE_AGO, M);
		relativeDate = relativeDate.replace(MINUTES_AGO, M);
		relativeDate = relativeDate.replace(HOUR_AGO, H);
		relativeDate = relativeDate.replace(HOURS_AGO, H);
		relativeDate = relativeDate.replace(DAY_AGO, D);
		relativeDate = relativeDate.replace(DAYS_AGO, D);

		return relativeDate;
	}
	
	/** Get a URL to a profile image generated from the hash value obtained from userId using gravatar. */
	public static String generateProfileImageUrl(final String userId) {
		String hex = "";
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			final byte[] hash = digest.digest(userId.getBytes());
			final BigInteger bigInt = new BigInteger(hash);
			hex = bigInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
	}
	
	/* ASYNC TEMPLATE START
	// The types specified here are the input data type, the progress type, and the result type
	private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
	     protected void onPreExecute() {
	         // Runs on the UI thread before doInBackground
	         // Good for toggling visibility of a progress indicator
	         progressBar.setVisibility(ProgressBar.VISIBLE);
	     }
	     protected Bitmap doInBackground(String... strings) { // Type is first one in template.
	         // Some long-running task like downloading an image.
	         Bitmap = downloadImageFromUrl(strings[0]);
	         return someBitmap;
	     }
	     protected void onProgressUpdate(ProgressType... values) { // Type is middle one in template.
	        // Executes whenever publishProgress is called from doInBackground
	        // Used to update the progress indicator
	        progressBar.setProgress(values[0]);
	     } 
	     protected void onPostExecute(Bitmap result) { // Type is the 3rd one in template.
	         // This method is executed in the UIThread
	         // with access to the result of the long running task
	         imageView.setImageBitmap(result);
	         // Hide the progress bar
	         progressBar.setVisibility(ProgressBar.INVISIBLE);
	     }
	}
	private void downloadImageAsync() {
	    // Now we can execute the long-running task at any time.
	    new MyAsyncTask().execute("http://images.com/image.jpg");
	}
	ASYNC TEMPLATE END */
	
}
