package com.yahoo.bananas.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.parse.ParseObject;

@Table(name = "Jokes")
public class Joke extends Model implements Serializable {
	// Constants
	private static final long serialVersionUID = -5588479582018973661L;
	private static final String TABLE         = "Jokes";
	private static final String COL_OBJECTID  = "objectId";  // (automatic) generated row ID.
	private static final String COL_CREATEDAT = "createdAt"; // (automatic) timestamp row created
	private static final String COL_CREATEDBY = "createdBy"; // User table's objectId
	private static final String COL_TEXT      = "text";      // Joke text
	private static final String COL_VOTESUP   = "votesUp";   // User up votes / likes
	private static final String COL_VOTESDOWN = "votesDown"; // User down votes / dislikes
	private static final String COL_SHARES    = "shares";    // # times shared
	
	// Member Variables
	@Column(name = COL_OBJECTID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE) // avoid duplicates
	private String objectId;
	
	@Column(name = COL_TEXT)
	private String text;

	@Column(name = COL_CREATEDAT)
	private Date createdAt;
	
	@Column(name = COL_CREATEDBY) //We'll manage references manually -- , onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private String createdBy; // User table's objectId.

	@Column(name = COL_VOTESUP)
	private int votesUp;

	@Column(name = COL_VOTESDOWN)
	private int votesDown;

	@Column(name = COL_SHARES)
	private int shares;

	// ----- ACCESS -----
	/** (Automatically generated) row ID. */
	public String getObjectId() {
		return objectId;
	}

	/** Joke text. */
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	/** (Automatically generated) row creation timestamp. */
	public Date getCreatedAt() {
		return createdAt;
	}

	/** User object's objectId that created this joke (not username). */
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdByUserObjectId) {
		this.createdBy = createdByUserObjectId;
	}

	/** User up votes / likes. */
	public int getVotesUp() {
		return votesUp;
	}
	public void setVotesUp(int votesUp) {
		this.votesUp = votesUp;
	}

	/** User down votes / dislikes. */
	public int getVotesDown() {
		return votesDown;
	}
	public void setVotesDown(int votesDown) {
		this.votesDown = votesDown;
	}

	/** Number of times shared with others. */
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}
	
	// ------ OTHER ------
	@Override
	public String toString(){
		return "["+objectId+"] "+text+" ("+votesUp+"/"+votesDown+":"+shares+")";
	}

    // ----- PARSE -----
    public ParseObject toParseObject(){
    	ParseObject po = new ParseObject(TABLE);
    	po.put(COL_OBJECTID  ,objectId );
    	po.put(COL_CREATEDAT ,createdAt);
    	po.put(COL_CREATEDBY ,createdBy);
    	po.put(COL_TEXT      ,text     );
    	po.put(COL_VOTESUP   ,votesUp  );
    	po.put(COL_VOTESDOWN ,votesDown);
    	po.put(COL_SHARES    ,shares   );
    	return po;
    }
	
	// ----- PERSISTENCE -----
    public static List<Joke> retrieveAll() {
        // This is how you execute a query
        List<Joke> result = new Select()
          .all()
          .from(Joke.class)
          //.where("Category = ?", category.getId())
          .orderBy("createdAt DESC")
          .execute();
        if(result==null) result = new ArrayList<Joke>();
        return result;
    }
	
}
