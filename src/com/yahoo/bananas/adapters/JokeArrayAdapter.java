package com.yahoo.bananas.adapters;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.bananas.R;
import com.yahoo.bananas.activities.DetailActivity;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.util.Util;

public class JokeArrayAdapter extends ArrayAdapter<Joke> {
	public JokeArrayAdapter(Context context, List<Joke> objects){
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Joke joke = getItem(position);
		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.joke_item, parent, false);
		} else {
			v = convertView;
		}
		// Find views within template
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		TextView  tvRealName     = (TextView ) v.findViewById(R.id.tvRealName);
		TextView  tvUserName     = (TextView ) v.findViewById(R.id.tvUserName);
		TextView  tvTime         = (TextView ) v.findViewById(R.id.tvTime);
		TextView  tvBody         = (TextView ) v.findViewById(R.id.tvBody);
		TextView  tvUpVotes		 = (TextView ) v.findViewById(R.id.tvUpVotes);
		TextView  tvDownVotes	 = (TextView ) v.findViewById(R.id.tvDownVotes);
		TextView  tvShares		 = (TextView ) v.findViewById(R.id.tvShares);
		// Clear existing image (needed if it was reused)
		ivProfileImage.setImageResource(android.R.color.transparent);
		// Populate views with joke data.
//		ImageLoader imageLoader = ImageLoader.getInstance();  // Universal loader we will use to get the image for us (asynchronously)
//		imageLoader.displayImage(joke.getUser().getImageUrl(), ivProfileImage); // Asynchronously load image using universal loader.
//		tvRealName.setText(joke.getUser().getRealName());
//		tvUserName.setText("@"+joke.getUser().getScreenName());
		if (joke.getCreatedAt() != null)
			tvTime.setText(Util.getRelativeTimeAgo(joke.getCreatedAt().toString()));
		tvBody.setText(joke.getText());
		tvUpVotes.setText(String.valueOf(joke.getVotesUp()));
		tvDownVotes.setText(String.valueOf(joke.getVotesDown()));
		tvShares.setText(String.valueOf(joke.getShares()));
//		tvBody.setTag(joke);
		// Store the user into the image so that when they click on it, we can know which user to show profile.
//		ivProfileImage.setTag(joke.getUser());
		
		tvBody.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),DetailActivity.class);
				intent.putExtra("joke", joke);
				v.getContext().startActivity(intent);
			}
		});
		
		return v;
	}
	
	
	
	
}
