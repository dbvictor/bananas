package com.yahoo.bananas.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.bananas.R;
import com.yahoo.bananas.models.Tweet;
import com.yahoo.bananas.util.Util;

public class JokeArrayAdapter extends ArrayAdapter<Tweet> {
	public JokeArrayAdapter(Context context, List<Tweet> objects){
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet joke = getItem(position);
		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		// Find views within template
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		TextView  tvRealName     = (TextView ) v.findViewById(R.id.tvRealName);
		TextView  tvUserName     = (TextView ) v.findViewById(R.id.tvUserName);
		TextView  tvTime         = (TextView ) v.findViewById(R.id.tvTime);
		TextView  tvBody         = (TextView ) v.findViewById(R.id.tvBody);
		// Clear existing image (needed if it was reused)
		ivProfileImage.setImageResource(android.R.color.transparent);
		// Populate views with tweet data.
		ImageLoader imageLoader = ImageLoader.getInstance();  // Universal loader we will use to get the image for us (asynchronously)
		imageLoader.displayImage(joke.getUser().getImageUrl(), ivProfileImage); // Asynchronously load image using universal loader.
		tvRealName.setText(joke.getUser().getRealName());
		tvUserName.setText("@"+joke.getUser().getScreenName());
		tvTime.setText("("+Util.getRelativeTimeAgo(joke.getCreatedAt())+")");
		tvBody.setText(joke.getBody());
		// Store the user into the image so that when they click on it, we can know which user to show profile.
		ivProfileImage.setTag(joke.getUser());
		return v;
	}
	
	
}