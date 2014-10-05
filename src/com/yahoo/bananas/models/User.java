package com.yahoo.bananas.models;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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
	public static final String TABLE         = "Users";
	public static final class COL{
		public static final String OBJECTID  = "objectId";  // (automatic) generated row ID.
		public static final String CREATEDAT = "createdAt"; // (automatic) timestamp row created
		public static final String USERNAME  = "username";  // (automatic) anonymous username.
		public static final String REALNAME  = "realName";  // User's real name
		public static final String EMAIL     = "email";     // (built-in) user's email.
		public static final String IMAGEURL  = "imageUrl";  // Profile image URL
	}

	// Member Variables
	@Column(name = COL.OBJECTID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private String objectId;
	
	@Column(name = COL.CREATEDAT)
	private Date createdAt;	

	@Column(name = COL.USERNAME)
	private String userName;
	
	@Column(name = COL.REALNAME)
	private String realName;

	@Column(name = COL.EMAIL)
	private String email;
	
	@Column(name = COL.IMAGEURL)
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
    	if(objectId!=null) po.setObjectId(objectId); // Use built-in for system column, not: po.put(COL.OBJECTID  ,objectId );
    	// Cannot set system-controlled column: if(createdAt!=null) po.put(COL.CREATEDAT ,createdAt);
    	if(userName!=null) po.put(COL.USERNAME  ,userName );
    	if(realName!=null) po.put(COL.REALNAME  ,realName );
    	if(email   !=null) po.put(COL.EMAIL     ,email    );
    	if(imageUrl!=null) po.put(COL.IMAGEURL  ,imageUrl );
    	return po;
    }
    
    public static User fromParseObject(ParseObject po){
    	User u = new User();
    	u.objectId  = po.getObjectId();  // Use built-in for system column, not: po.getstring(COL.OBJECTID  );
    	u.createdAt = po.getCreatedAt(); // Use built-in for system column, not: po.getDate  (COL.CREATEDAT );
    	u.userName  = po.getString(COL.USERNAME  );
    	u.realName  = po.getString(COL.REALNAME  );
    	u.email     = po.getString(COL.EMAIL     );
    	u.imageUrl  = po.getString(COL.IMAGEURL  );
    	return u;
    }

    public static List<User> fromParseObjects(List<ParseObject> parseList){
    	ArrayList<User> userList = new ArrayList<User>(parseList.size());
    	for(ParseObject po : parseList) userList.add(User.fromParseObject(po));
    	return userList;
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
