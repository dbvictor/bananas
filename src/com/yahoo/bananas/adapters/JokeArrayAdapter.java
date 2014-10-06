package com.yahoo.bananas.adapters;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.activities.DetailActivity;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.Tweet;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.Util;

public class JokeArrayAdapter extends ArrayAdapter<Joke> {
	private static final int JOKE_BODY_READABLE_LIMIT = 200;
	private ImageLoader imageLoader = ImageLoader.getInstance();  // Universal loader we will use to get the image for us (asynchronously)

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
//		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		TextView  tvUserName     = (TextView ) v.findViewById(R.id.tvUserName);
		TextView  tvTime         = (TextView ) v.findViewById(R.id.tvTime);
		TextView  tvBody         = (TextView ) v.findViewById(R.id.tvBody);
		TextView  tvUpVotes		 = (TextView ) v.findViewById(R.id.tvUpVotes);
		TextView  tvDownVotes	 = (TextView ) v.findViewById(R.id.tvDownVotes);
		TextView  tvShares		 = (TextView ) v.findViewById(R.id.tvShares);
		TextView  tvTitle		 = (TextView ) v.findViewById(R.id.tvTitle);
		ImageView ivCategoryImage = (ImageView) v.findViewById(R.id.ivCategoryImage);

//		TextView  tvCategory     = (TextView ) v.findViewById(R.id.tvJokeCategory);
		// Clear existing image (needed if it was reused)
//		ivProfileImage.setImageResource(android.R.color.transparent);
		// Populate views with joke data.
		User createdByUser = joke.getCreatedByUser();
		if (createdByUser != null) {
			 //username will be a string of random characters, so using real name, which the user can set to whatever they want
			tvUserName.setText(createdByUser.getRealName());	
//			imageLoader.displayImage(createdByUser.getImageUrl(), ivProfileImage); // Asynchronously load image using universal loader.
		}
//		tvRealName.setText(joke.getUser().getRealName());
//		tvUserName.setText("@"+joke.getUser().getScreenName());
		if (joke.getCreatedAt() != null)
			tvTime.setText(Util.getRelativeTimeAgo(joke.getCreatedAt().toString()));
		String jokeText = joke.getText();
		if(jokeText.length()>JOKE_BODY_READABLE_LIMIT){
			jokeText = jokeText.trim();
			jokeText = jokeText.substring(0, JOKE_BODY_READABLE_LIMIT) + "...";
		}
		Category category = joke.getCategory();
		if (category == null) {
			category = Category.Other;
		}
//		tvCategory.setText(category.getDisplayName());
		if(category==Category.Animal){
			ivCategoryImage.setImageResource(R.drawable.ic_animal);
		}else if(category==Category.Bar){
			ivCategoryImage.setImageResource(R.drawable.ic_bar);
		}else if(category==Category.Blonde){
			ivCategoryImage.setImageResource(R.drawable.ic_blonde);
		}else if(category==Category.Doctor){
			ivCategoryImage.setImageResource(R.drawable.ic_doctor);
		}else if(category==Category.Food){
			ivCategoryImage.setImageResource(R.drawable.ic_food);
		}else if(category==Category.Lawyer){
			ivCategoryImage.setImageResource(R.drawable.ic_lawyer);
		}else if(category==Category.Other){
			ivCategoryImage.setImageResource(R.drawable.ic_others);
		}else if(category==Category.Yo_Mama){
			ivCategoryImage.setImageResource(R.drawable.ic_yomama);
		}
		
		tvBody.setText(jokeText);
		tvTitle.setText(joke.getTitle());
		tvUpVotes.setText(String.valueOf(joke.getVotesUp()));
		tvDownVotes.setText(String.valueOf(joke.getVotesDown()));
		tvShares.setText(String.valueOf(joke.getShares()));
		
		//		tvBody.setTag(joke);
		// Store the user into the image so that when they click on it, we can know which user to show profile.
//		ivProfileImage.setTag(joke.getCreatedByUser());
		
		setupDetailViewListeners(v, joke);
		setupShareListener(v, joke, this);
		setupVoteListeners(v, joke, this);
		
		return v;
	}
	
	private static void setupDetailViewListeners(View v, final Joke joke){
		TextView  tvBody         = (TextView ) v.findViewById(R.id.tvBody);
		TextView  tvTitle		 = (TextView ) v.findViewById(R.id.tvTitle);
		tvBody.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),DetailActivity.class);
				intent.putExtra("joke", joke);
				v.getContext().startActivity(intent);
			}
		});
		tvTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),DetailActivity.class);
				intent.putExtra("joke", joke);
				v.getContext().startActivity(intent);
			}
		});
	}
	
	private static void setupShareListener(View v, final Joke joke, final JokeArrayAdapter adapter){
		ImageView iv = (ImageView) v.findViewById(R.id.ivStaticShares);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Create Tweet Message
				String tweetText = "See this Bananas joke at www.bananas.yahoo.com/"+joke.getObjectId()+": "+joke.getTitle();
				if(tweetText.length()>140) tweetText = tweetText.substring(0,140-3)+"...";
				// Post Tweet
				JokesApplication.getTwitterClient().createTweet(tweetText, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json) {
						Log.d("json", "Created JSON: "+json.toString());
						Tweet tweet = Tweet.fromJSON(json);
						Toast.makeText(v.getContext(), "Tweeted", Toast.LENGTH_SHORT).show();
						// Record Share Event
						recordShareEvent(v.getContext(),joke,adapter);
					}
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s.toString());
						Toast.makeText(v.getContext(), "Tweet FAILED!", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private static void setupVoteListeners(View v, final Joke joke, final JokeArrayAdapter adapter){
		ImageView ivUp = (ImageView) v.findViewById(R.id.ivStaticUp);
		ImageView ivDn = (ImageView) v.findViewById(R.id.ivStaticDown);
		ivUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordVote(v.getContext(),joke,+1,adapter);
			}
		});
		ivDn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordVote(v.getContext(),joke,-1,adapter);
			}
		});
	}
	
	/** Record that a share has happened so people can see how many shares. */
	private static void recordShareEvent(final Context context, final Joke joke, final JokeArrayAdapter adapter){
		JokesApplication.getParseClient().jokeShare(joke.getObjectId(), new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e!=null){
					Log.e("ERROR", "Failed to update share count: "+e.getMessage(),e);
					Toast.makeText(context, "Update Share Count FAILED!", Toast.LENGTH_SHORT).show();
				}else{
					// Make it show a +1 to the user, which isn't the actual latest if others shared in the meanwhile, but will look appropriate to the user until they do a refresh.
					joke.setShares(joke.getShares()+1);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	/** Record the user's vote. */
	private static void recordVote(final Context context, final Joke joke, final int voteDifference, final JokeArrayAdapter adapter){
		JokesApplication.getParseClient().jokeVote(joke.getObjectId(), voteDifference, new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e!=null){
					Log.e("ERROR", "Failed to update share count: "+e.getMessage(),e);
					Toast.makeText(context, "Update Share Count FAILED!", Toast.LENGTH_SHORT).show();
				}else{
					// Make it show a +1 to the user, which isn't the actual latest if others shared in the meanwhile, but will look appropriate to the user until they do a refresh.
					if(voteDifference>0) joke.setVotesUp  (joke.getVotesUp  ()+voteDifference);
					else                 joke.setVotesDown(joke.getVotesDown()-voteDifference);
					adapter.notifyDataSetChanged();
					Toast.makeText(context, "Voted", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}
