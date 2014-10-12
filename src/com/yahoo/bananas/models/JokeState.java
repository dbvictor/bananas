package com.yahoo.bananas.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Joke status for the current user's action history for a specific joke, such as their voting, 
 * sharing, and read history.  This is a secondary object to be used in conjunction with the Joke
 * object.  We use this to know the user's current vote up/down and sharing so that we can prevent the
 * user from voting or sharing multiple times, and indicate it visually to the user.  We can also
 * track and indicate read history.
 * 
 * This object is currently only stored locally on the user's device.  We do not want to store too
 * much on our free Parse account at this time.  So we will depend entirely on the user history being
 * tracked within their device.
 * 
 * LIMITATIONS:
 * 1. Can be cleared by uninstalling & reinstalling the app.
 *    > It is pretty limiting and irritating to have to uninstall & reinstall and should keep such
 *      problems under control in the early time period.
 * 2. Not sync'd across multiple devices.   
 *    > We may want to store a serialized form in the user object or something that we periodically sync.
 */
@Table(name = "JokeState")
public class JokeState extends Model implements Serializable {
	// Constants
	private static final long serialVersionUID = 1380453952829236636L;
	public static final String TABLE         = "JokeState";
	public static final class COL{
		public static final String OBJECTID  = "objectId";     // (automatic) generated row ID.
		public static final String JOKEID    = "jokeObjectId"; // Joke table's objectId
		public static final String READ      = "read";         // User read joke
		public static final String VOTEDUP   = "votedUp";      // User voted up / liked
		public static final String VOTEDDOWN = "votedDown";    // User voted down / disliked
		public static final String SHARED    = "shared";       // # times shared
	}
	
	// Member Variables
	@Column(name = COL.OBJECTID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE) // avoid duplicates
	private String objectId;

	@Column(name = COL.JOKEID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE) // avoid duplicates
	private String jokeObjectId;

	@Column(name = COL.READ)
	private boolean read = false;
	
	@Column(name = COL.VOTEDUP)
	private boolean votedUp = false;

	@Column(name = COL.VOTEDDOWN)
	private boolean votedDown = false;

	@Column(name = COL.SHARED)
	private int shared = 0;

	// ----- CONSTRUCTORS -----
	/** Have to have a public default constructor */
	public JokeState(){
		super();
	}
	/** Quick one-line way to create a new instance, passing the joke ID. */
	public JokeState(String jokeObjectId){
		super();
		setJokeObjectId(jokeObjectId);
	}
	
	// ----- ACCESS -----
	/** (Automatically generated) row ID. */
	public String getObjectId() {
		return objectId;
	}

	/** Joke table's objectId for the joke that this stat is for. */
	public String getJokeObjectId() {
		return jokeObjectId;
	}
	public void setJokeObjectId(String jokeObjectId) {
		this.jokeObjectId = jokeObjectId;
	}
	
	/** User voted up / likes. */
	public boolean getVotedUp() {
		return votedUp;
	}
	public void setVotedUp(boolean votedUp) {
		this.votedUp = votedUp;
	}

	/** User voted down / disliked. */
	public boolean getVotedDown() {
		return votedDown;
	}
	public void setVotedDown(boolean votedDown) {
		this.votedDown = votedDown;
	}

	/** Number of times the user shared with others. */
	public int getShared() {
		return shared;
	}
	public void setShared(int shares) {
		this.shared = shares;
	}
	
	// ------ OTHER ------
	@Override
	public String toString(){
		return "["+objectId+"] joke "+jokeObjectId+" {up="+votedUp+",dn="+votedDown+",sh="+shared+")";
	}

	// ----- PERSISTENCE -----
    public static List<JokeState> retrieveAll() {
        // This is how you execute a query
        List<JokeState> result = new Select()
          .all()
          .from(JokeState.class)
          //NO CONDITIONS: .where("Category = ?", category.getId())
          //NO ORDER: .orderBy("createdAt DESC")
          .execute();
        if(result==null) result = new ArrayList<JokeState>(0);
        return result;
    }
	
}
