package com.yahoo.bananas.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
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
		tvBody.setMovementMethod(new ScrollingMovementMethod());
		TextView tvJokeTitle = (TextView)findViewById(R.id.tvJokeTitle);
		tvJokeTitle.setText(joke.getTitle());
		Button btClickHereToReadMoreFrom = (Button)findViewById(R.id.btClickHereToReadMoreFrom);
		btClickHereToReadMoreFrom.setText(" See all jokes from "+joke.getCreatedByUser().getRealName() + " ");
//		TextView tvCreationUser = (TextView) findViewById(R.id.tvCreationUser);
//		tvCreationUser.setText(joke.getCreatedByUser().getRealName());
		ImageView ivCreationUser = (ImageView)findViewById(R.id.ivCreationUser);
		ImageLoader.getInstance().displayImage(joke.getCreatedByUser().getImageUrl(), ivCreationUser);
//		TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
//		Date createdAt = joke.getCreatedAt();
//		if (createdAt != null) {
//			tvCreatedAt.setText(Util.getRelativeTimeAgo(createdAt.toString()));
//		}
//		TextView tvCategory = (TextView) findViewById(R.id.tvCategory);
//		tvCategory.setText(joke.getCategory().getDisplayName());
		
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
		} else {
			Toast.makeText(this, "Image Missing User Info!", Toast.LENGTH_SHORT).show();
		}
	}
	
}
