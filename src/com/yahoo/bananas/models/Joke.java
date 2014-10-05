package com.yahoo.bananas.models;

import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
	public static final String TABLE         = "Jokes";
	public static final class COL{
		public static final String OBJECTID  = "objectId";  // (automatic) generated row ID.
		public static final String CREATEDAT = "createdAt"; // (automatic) timestamp row created
		public static final String CREATEDBY = "createdBy"; // User table's objectId
		public static final String CATEGORY  = "category";  // Joke category
		public static final String TITLE     = "title";     // Joke title
		public static final String TEXT      = "text";      // Joke text
		public static final String VOTESUP   = "votesUp";   // User up votes / likes
		public static final String VOTESDOWN = "votesDown"; // User down votes / dislikes
		public static final String SHARES    = "shares";    // # times shared
	}
	
	// Member Variables
	@Column(name = COL.OBJECTID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE) // avoid duplicates
	private String objectId;

	@Column(name = COL.CREATEDAT)
	private Date createdAt;
	
	@Column(name = COL.CREATEDBY) //We'll manage references manually -- , onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private String createdBy; // User table's objectId.
	private User   createdByUser; // User retrieved from createdBy value.
	
	@Column(name = COL.CATEGORY)
	private Integer categoryDbValue = Category.Other.toDbValue();

	@Column(name = COL.TITLE)
	private String title;
	
	@Column(name = COL.TEXT)
	private String text;

	@Column(name = COL.VOTESUP)
	private int votesUp = 0;

	@Column(name = COL.VOTESDOWN)
	private int votesDown = 0;

	@Column(name = COL.SHARES)
	private int shares = 0;

	// ----- ACCESS -----
	/** (Automatically generated) row ID. */
	public String getObjectId() {
		return objectId;
	}

	/** (Automatically generated) row creation timestamp. */
	public Date getCreatedAt() {
		return createdAt;
	}

	/** User object's objectId that created this joke (not username). */
	public String getCreatedBy() {
		return createdBy;
	}
	//NOT NEEDED: public void setCreatedBy(String createdByUserObjectId) {
	//NOT NEEDED:	this.createdBy = createdByUserObjectId;
	//NOT NEEDED:}
	/** Sets both CreatedBy and CreatedByUser based on the User and its objectId value. */
	public void setCreatedBy(User createdByUser) {
		this.createdBy     = createdByUser.getObjectId();
		this.createdByUser = createdByUser;
	}
	/**
	 * Get the full user object that corresponds to the getCreatedBy() user objectId.
	 * @return NULL if not found or not populated, else the non-null user that corresponds to the getCreatedBy() user objectId.
	 */ 
	public User getCreatedByUser() {
		// If no user found, return a dummy user so that an image URL will be generated automatically.
		if(createdByUser==null){
			createdByUser = new User();
			createdByUser.setObjectId(createdBy);
		}
		return createdByUser;
	}

	/** Joke category. */
	public Category getCategory() {
		return Category.fromDbValue(categoryDbValue);
	}
	public void setCategory(Category category) {
		this.categoryDbValue = category.toDbValue();
	}

	/** Joke title. */
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	/** Joke text. */
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
    	if (objectId !=null) po.setObjectId(objectId); // Use built-in for system column, not: po.put(COL.OBJECTID  ,objectId );
    	// Cannot set system-controlled column: if (createdAt != null ) po.put(COL.CREATEDAT ,createdAt      );
    	po.put(COL.CREATEDBY ,createdBy      );
    	po.put(COL.CATEGORY  ,categoryDbValue);
    	po.put(COL.TITLE     ,title          );
    	po.put(COL.TEXT      ,text           );
    	po.put(COL.VOTESUP   ,votesUp        );
    	po.put(COL.VOTESDOWN ,votesDown      );
    	po.put(COL.SHARES    ,shares         );
    	return po;
    }
    
    public static Joke fromParseObject(ParseObject po){
    	Joke j = new Joke();
    	j.objectId        = po.getObjectId();  // Use built-in for system column, not: po.getstring(COL.OBJECTID  );
    	j.createdAt       = po.getCreatedAt(); // Use built-in for system column, not: po.getDate  (COL.CREATEDAT );
    	j.createdBy       = po.getString(COL.CREATEDBY );
    	j.categoryDbValue = po.getInt   (COL.CATEGORY  );
    	j.title           = po.getString(COL.TITLE     );
    	j.text            = po.getString(COL.TEXT      );
    	j.votesUp         = po.getInt   (COL.VOTESUP   );
    	j.votesDown       = po.getInt   (COL.VOTESDOWN );
    	j.shares          = po.getInt   (COL.SHARES    );
    	return j;
    }
    
    public static List<Joke> fromParseObjects(List<ParseObject> parseList){
    	ArrayList<Joke> jokeList = new ArrayList<Joke>(parseList.size());
    	for(ParseObject po : parseList) jokeList.add(Joke.fromParseObject(po));
    	return jokeList;
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
