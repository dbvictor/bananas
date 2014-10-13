package com.yahoo.bananas.adapters;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.activities.CategoryActivity;
import com.yahoo.bananas.activities.DetailActivity;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.JokeState;
import com.yahoo.bananas.models.Theme;
import com.yahoo.bananas.models.Tweet;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.Util;

public class JokeArrayAdapter extends ArrayAdapter<Joke> {
	private static final int JOKE_BODY_READABLE_LIMIT = 200;
	private Theme theme;

	public JokeArrayAdapter(Context context, List<Joke> objects, Theme theme){
		super(context, 0, objects);
		this.theme = theme;
	}
	
	public void setTheme(Theme theme){
		this.theme = theme;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Joke joke = getItem(position);
		View v;
		// If we changed themes, we have to re-inflate
		if((convertView!=null)&&(!convertView.getTag().equals(theme))){
			Log.d("theme", "JokeArrayAdapter.getView() Ignoring recycled convertView because theme changed.");
			convertView = null;
		}
		if(convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			switch(theme){
				case Grey:
					v = inflator.inflate(R.layout.joke_item_grey, parent, false);
					break;
				case White: 
					v = inflator.inflate(R.layout.joke_item_white, parent, false);
					break;
				default: throw new RuntimeException("Unexpected theme '"+theme+"'");
			}
			v.setTag(theme);
		} else {
			v = convertView;
		}
		// Find views within template
		TextView  tvUserName     = (TextView ) v.findViewById(R.id.tvUserName);
		TextView  tvTime         = (TextView ) v.findViewById(R.id.tvTime);
		TextView  tvBody         = (TextView ) v.findViewById(R.id.tvBody);
		TextView  tvTitle		 = (TextView ) v.findViewById(R.id.tvTitle);
		ImageView ivCategoryImage = (ImageView) v.findViewById(R.id.ivCategoryImage);
		JokeState userState = joke.getUserState();
		setupJokeData(userState, joke, tvUserName, tvTime, tvBody, tvTitle, ivCategoryImage);
		
		setupUserStatus(userState, v, joke);
		setupDetailViewListeners(v, joke);
		setupShareListener(v, joke, this);
		setupVoteListeners(v, joke, this);
		//NO: setupCategoryViewListener(v, joke); // Don't setup category view listeners here.  We do with in layout XML so that activity has to implement the handler.  This way categoryActivity can stop it from looping back to itself.
		//BUT: do tag it with the category so the onClick handler can know which category the image represents.
		ivCategoryImage.setTag(joke.getCategory());
		
		return v;
	}

	private void setupJokeData(JokeState userState, final Joke joke, TextView tvUserName,
			TextView tvTime, TextView tvBody, TextView tvTitle,
			ImageView ivCategoryImage) {
		// Populate views with joke data.
		User createdByUser = joke.getCreatedByUser();
		if (createdByUser != null) {
			 //username will be a string of random characters, so using real name, which the user can set to whatever they want
			tvUserName.setText(createdByUser.getRealName());	
		}
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
		ivCategoryImage.setImageResource(category.getImageResourceId());
		tvBody.setText(jokeText);
		if (userState.getRead()) {
			tvBody.setTextColor(Color.GRAY);
		}
		tvTitle.setText(joke.getTitle());
	}

	private void setupUserStatus(JokeState userState, View v, final Joke joke) {

		if (userState != null) {
			int shared = userState.getShared();
			if (shared > 0) {
				ImageView ivShares = (ImageView) v.findViewById(R.id.ivStaticShares);
				ivShares.setImageResource(R.drawable.ic_shared);
			}
			
			ImageView ivUpVotes = (ImageView) v.findViewById(R.id.ivStaticUp);
			ImageView ivDownVotes = (ImageView) v.findViewById(R.id.ivStaticDown);
			if (userState.getVotedUp()) {
				ivUpVotes.setImageResource(R.drawable.ic_up_voted);
				ivDownVotes.setImageResource(R.drawable.ic_down);
			} else if (userState.getVotedDown()) {
				ivDownVotes.setImageResource(R.drawable.ic_down_voted);
				ivUpVotes.setImageResource(R.drawable.ic_up);
			}
			boolean read = userState.getRead();
			if (read) {
				ImageView ivRead = (ImageView) v.findViewById(R.id.ivRead);
				ivRead.setVisibility(ImageView.VISIBLE);
			}
		}
		
		TextView  tvUpVotes		 = (TextView ) v.findViewById(R.id.tvUpVotes);
		TextView  tvDownVotes	 = (TextView ) v.findViewById(R.id.tvDownVotes);
		TextView  tvShares		 = (TextView ) v.findViewById(R.id.tvShares);
		
		tvUpVotes.setText(String.valueOf(joke.getVotesUp()));
		tvDownVotes.setText(String.valueOf(joke.getVotesDown()));
		tvShares.setText(String.valueOf(joke.getShares()));
	}
	
	//NO: Don't setup category view listeners here.  We do with in layout XML so that activity has to implement the handler.  This way categoryActivity can stop it from looping back to itself.
	private static void setupCategoryViewListener(View v, final Joke joke){
		ImageView ivCategoryImage = (ImageView) v.findViewById(R.id.ivCategoryImage);
		ivCategoryImage.setTag(joke.getCategory());
		ivCategoryImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),CategoryActivity.class);
				intent.putExtra(Category.class.getSimpleName(), joke.getCategory());
				v.getContext().startActivity(intent);
			}
		});
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
						recordJokeShare(v.getContext(),joke,adapter);
						ImageView ivShare = (ImageView)v;
						ivShare.setImageResource(R.drawable.ic_shared);
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
		final ImageView ivUp = (ImageView) v.findViewById(R.id.ivStaticUp);
		final ImageView ivDn = (ImageView) v.findViewById(R.id.ivStaticDown);
		ivUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordJokeVote(v.getContext(),joke,true,false,adapter);
				ivUp.setImageResource(R.drawable.ic_up_voted);
				ivDn.setImageResource(R.drawable.ic_down);
			}
		});
		ivDn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordJokeVote(v.getContext(),joke,false,true,adapter);
				ivDn.setImageResource(R.drawable.ic_down_voted);
				ivUp.setImageResource(R.drawable.ic_up);
			}
		});
	}
	
	/** Record that a share has happened so people can see how many shares. */
	private static void recordJokeShare(final Context context, final Joke joke, final JokeArrayAdapter adapter){
		// Make it show a +1 to the user for instant feedback.  This isn't the actual latest if others shared in the meanwhile, but will look appropriate to the user until they do a refresh.  It may be too confusing if it jumps by several when they click once if we did update it.
		joke.setShares(joke.getShares()+1);
		adapter.notifyDataSetChanged();
		// Change icon to show as shared.
		// TODO
		// Save to Joke
		JokesApplication.getParseClient().jokeShare(joke.getObjectId(), new SaveCallback() {
			@Override public void done(ParseException e) {
				if(e!=null){
					Log.e("ERROR", "Update Share Count FAILED!: "+e.getMessage(),e);
					Toast.makeText(context, "Update Share Count FAILED!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/** Record the user's vote. */
	private static void recordJokeVote(final Context context, final Joke joke, final boolean voteUp, final boolean voteDown, final JokeArrayAdapter adapter){
		// Change icons to show up/down vote.
		// TODO
		// Save to Joke
		JokesApplication.getParseClient().jokeVote(joke.getObjectId(), voteUp, voteDown, new ParseClient.JokeVote() {
			@Override public void done(int changedVotesUp, int changedVotesDn, ParseException e) {
				if(e!=null){
					Log.e("ERROR", "Update Vote Count FAILED!: "+e.getMessage(),e);
					Toast.makeText(context, "Update Vote Count FAILED!", Toast.LENGTH_SHORT).show();
				}else{
					// We have to know what changed from the done method in order to know how to change the #s in the UI.
					// => This is why we have the changed vote counts passed to us.
					if((changedVotesUp!=0)||(changedVotesDn!=0)){
						Toast.makeText(context, "Voted", Toast.LENGTH_SHORT).show();
						// Make it show a +1 to the user for instant feedback.  This isn't the actual latest if others shared in the meanwhile, but will look appropriate to the user until they do a refresh.  It may be too confusing if it jumps by several when they click once if we did update it.
						joke.setVotesUp  (joke.getVotesUp  ()+changedVotesUp);
						joke.setVotesDown(joke.getVotesDown()+changedVotesDn);
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(context, "No Change", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	
}
