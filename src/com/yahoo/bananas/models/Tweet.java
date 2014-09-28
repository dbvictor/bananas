package com.yahoo.bananas.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ClipData.Item;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	private static final long serialVersionUID = 7144327103228498679L;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE) // avoid duplicates
	private long   uid;
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private User   user;
	
	public static Tweet fromJSON(JSONObject json){
		Tweet tweet = new Tweet();
		// Extract values from JSON to populate the member variables.
		try{
			tweet.uid       = json.getLong  ("id"  );
			tweet.body      = json.getString("text");
			tweet.createdAt = json.getString("created_at");
			tweet.user      = User.fromJSON(json.getJSONObject("user"));
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static ArrayList<Tweet> fromJSON(JSONArray json) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(json.length());
		for(int i=0; i<json.length(); i++){
			JSONObject tweetJSON = null;
			try{
				tweetJSON = json.getJSONObject(i);
			}catch(JSONException e){
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJSON);
			if(tweet!=null) tweets.add(tweet);
		}
		return tweets;
	}

	public long getUid() {
		return uid;
	}

	public String getBody() {
		return body;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	@Override
	public String toString(){
		return body+" - "+user.getScreenName();
	}
	
	// ----- PERSISTENCE -----
    public static List<Tweet> retrieveAll() {
        // This is how you execute a query
        List<Tweet> result = new Select()
          .all()
          .from(Tweet.class)
          //.where("Category = ?", category.getId())
          .orderBy("uid DESC")
          .execute();
        if(result==null) result = new ArrayList<Tweet>();
        return result;
    }	
	
}
