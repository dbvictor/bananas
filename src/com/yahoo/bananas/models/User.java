package com.yahoo.bananas.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.parse.ParseObject;
import com.yahoo.bananas.util.Util;

@Table(name = "Users")
public class User extends Model implements Serializable{
	// Constants
	private static final long serialVersionUID = 8667788773549950232L;
	private static final String TABLE         = "Users";
	private static final String COL_OBJECTID  = "objectId";  // (automatic) generated row ID.
	private static final String COL_CREATEDAT = "createdAt"; // (automatic) timestamp row created
	private static final String COL_USERNAME  = "username";  // (automatic) anonymous username.
	private static final String COL_REALNAME  = "realName";  // User's real name
	private static final String COL_EMAIL     = "email";     // (built-in) user's email.
	private static final String COL_IMAGEURL  = "imageUrl";  // Profile image URL

	// Member Variables
	@Column(name = COL_OBJECTID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private String objectId;
	
	@Column(name = COL_CREATEDAT)
	private Date createdAt;	

	@Column(name = COL_USERNAME)
	private String userName;
	
	@Column(name = COL_REALNAME)
	private String realName;

	@Column(name = COL_EMAIL)
	private String email;
	
	@Column(name = COL_IMAGEURL)
	private String imageUrl;

	// ----- ACCESS -----
	/** (Automatically generated) row ID. */
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/** (Automatically generated) timestamp that the user was first created. */
	public Date getCreatedAt() {
		return createdAt;
	}

	/** (Automatically generated) user name. */ 
	public String getUserName() {
		return userName;
	}

	/** User's real name to display. */
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/** User's email. */
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * User's profile image URL to use.
	 * @return Non-null image URL that you can display.
	 *         Automatically generates a unique image URL if none set for this user,
	 *         or else returns the user's customized image URL.
	 */
	public String getImageUrl() {
		if(imageUrl==null) return Util.generateProfileImageUrl(objectId); // If none set, make one up that will be unique to this user.
		else               return imageUrl; // Else use the one the user picked.
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	// ------ OTHER ------
	@Override
	public String toString(){
		return "["+objectId+"] "+((realName!=null)? realName : userName);
	}

    // ----- PARSE -----
    public ParseObject toParseObject(){
    	ParseObject po = new ParseObject(TABLE);
    	po.put(COL_OBJECTID  ,objectId );
    	po.put(COL_CREATEDAT ,createdAt);
    	po.put(COL_USERNAME  ,userName );
    	po.put(COL_REALNAME  ,realName );
    	po.put(COL_EMAIL     ,email    );
    	po.put(COL_IMAGEURL  ,imageUrl );
    	return po;
    }
	
	// ----- PERSISTENCE -----
    public static List<Joke> retrieveAll() {
        // This is how you execute a query
        List<Joke> result = new Select()
          .all()
          .from(User.class)
          //.where("Category = ?", category.getId())
          .orderBy("createdAt DESC")
          .execute();
        if(result==null) result = new ArrayList<Joke>();
        return result;
    }

}
