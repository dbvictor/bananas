package com.yahoo.bananas.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
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

		joke = (Joke) getIntent().getSerializableExtra("joke");
		
		Toast.makeText(this, "Detail View", Toast.LENGTH_SHORT).show();
		
		TextView tvBody = (TextView) findViewById(R.id.tvJoke);
		tvBody.setText(joke.getText());
		TextView tvJokeTitle = (TextView)findViewById(R.id.tvJokeTitle);
		tvJokeTitle.setText(joke.getTitle());
		TextView tvCreationUser = (TextView) findViewById(R.id.tvCreationUser);
		tvCreationUser.setText(joke.getCreatedByUser().getRealName());
		ImageView ivCreationUser = (ImageView)findViewById(R.id.ivCreationUser);
		ImageLoader.getInstance().displayImage(joke.getCreatedByUser().getImageUrl(), ivCreationUser);
//		TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
//		Date createdAt = joke.getCreatedAt();
//		if (createdAt != null) {
//			tvCreatedAt.setText(Util.getRelativeTimeAgo(createdAt.toString()));
//		}
//		TextView tvCategory = (TextView) findViewById(R.id.tvCategory);
//		tvCategory.setText(joke.getCategory().getDisplayName());
		setActionBarColor();
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
