package com.yahoo.bananas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.yahoo.bananas.JokesApplication;
import com.yahoo.bananas.R;
import com.yahoo.bananas.clients.ParseClient;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.User;

public class DetailActivity extends Activity {

	private Joke joke;
	
	private static final int    ACTIVITY_PROFILE     = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		setActionBarColor();

		joke = (Joke) getIntent().getSerializableExtra("joke");
		
		Toast.makeText(this, "Detail View", Toast.LENGTH_SHORT).show();
		
		TextView tvBody = (TextView) findViewById(R.id.tvJoke);
		tvBody.setText(joke.getText());
		
		final TextView tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
		tvLikeCount.setText(String.valueOf(joke.getVotesUp()));
		final TextView tvDislikeCount = (TextView) findViewById(R.id.tvDislikeCount);
		tvDislikeCount.setText(String.valueOf(joke.getVotesDown()));
		
		tvBody.setMovementMethod(new ScrollingMovementMethod());
		TextView tvJokeTitle = (TextView)findViewById(R.id.tvJokeTitle);
		tvJokeTitle.setText(joke.getTitle());
		Button btClickHereToReadMoreFrom = (Button)findViewById(R.id.btClickHereToReadMoreFrom);
		btClickHereToReadMoreFrom.setText(" See all jokes from "+joke.getCreatedByUser().getRealName() + " ");
		ImageView ivCreationUser = (ImageView)findViewById(R.id.ivCreationUser);
		ImageLoader.getInstance().displayImage(joke.getCreatedByUser().getImageUrl(), ivCreationUser);	
		
		final ImageView ivLike = (ImageView) findViewById(R.id.ivLike);
		final ImageView ivDislike = (ImageView) findViewById(R.id.ivDislike);
		ivLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordJokeVote(v.getContext(),joke,true,false,tvLikeCount,tvDislikeCount);
				ivLike.setImageResource(R.drawable.ic_up_voted);
				ivDislike.setImageResource(R.drawable.ic_down);
				tvLikeCount.setText(String.valueOf(joke.getVotesUp()));
				tvDislikeCount.setText(String.valueOf(joke.getVotesDown()));
			}
		});
		ivDislike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				recordJokeVote(v.getContext(),joke,false,true,tvLikeCount,tvDislikeCount);
				ivLike.setImageResource(R.drawable.ic_up);
				ivDislike.setImageResource(R.drawable.ic_down_voted);
				tvLikeCount.setText(String.valueOf(joke.getVotesUp()));
				tvDislikeCount.setText(String.valueOf(joke.getVotesDown()));
			}
		});
		
		// Mark the joke read
		final DetailActivity parentThis = this;
		JokesApplication.getParseClient().jokeRead(joke, true, new SaveCallback() {
			@Override public void done(ParseException e) {
				if(e!=null){
					Log.e("ERROR","Failed to mark joke read: "+e.getClass().getSimpleName()+" = "+e.getMessage(),e);
					Toast.makeText(parentThis, "Mark Read FAILED!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void setActionBarColor() {
		ColorDrawable colorDrawable = new ColorDrawable();
		//http://html-color-codes.info
		colorDrawable.setColor(Color.parseColor("#4099FF"));
		getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	public void moreFromThisUser(View v){
		User u = joke.getCreatedByUser();
		if(u!=null){ // Error if not there.
			Intent i = new Intent(this,ProfileActivity.class);
			i.putExtra("user", u);
			startActivityForResult(i, ACTIVITY_PROFILE);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);			
		} else {
			Toast.makeText(this, "Image Missing User Info!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private static void recordJokeVote(final Context context, final Joke joke, final boolean voteUp, final boolean voteDown, final TextView tvLikeCount,final TextView tvDislikeCount){
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
						tvLikeCount.setText(String.valueOf(joke.getVotesUp()));
						tvDislikeCount.setText(String.valueOf(joke.getVotesDown()));
					}else{
						Toast.makeText(context, "No Change", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	
	/** Override the back button behavior to override default exit animation. */
	@Override
	public void onBackPressed(){
		finish();					
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}	
	
}
