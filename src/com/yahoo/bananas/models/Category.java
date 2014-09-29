package com.yahoo.bananas.models;

import java.util.HashMap;

public enum Category {
	Animal(0,"Animal"),
	Bar   (1,"Bar"   ),
	Other (2,"Other" );
	
	// Members
	private Integer dbValue     = -1;
	private String  displayName = null;
	// Custom CTOR
	private Category(Integer dbValue, String displayName){
		this.dbValue     = dbValue;
		this.displayName = displayName;
	}
	// Methods
	public String  getDisplayName(){ return displayName; }
	public Integer toDbValue(){ return dbValue; }
	/**
	 * Get the enum from the DB value int.
	 * @return Non-null enum for the specified DB value.
	 * @throws RuntimeException if no enum exists with that DB value.
	 */
	public static Category fromDbValue(Integer dbValue){
		// If map not yet built, build it once now.
		if(MAP_DB_TO_ENUM==null){ // Okay if multiple threads do at the same time, only point is one will be reused after the brief moment when not exist.
			MAP_DB_TO_ENUM = new HashMap<Integer,Category>(Category.values().length);
			for(Category c : Category.values()) MAP_DB_TO_ENUM.put(c.toDbValue(), c);
		}
		Category c = MAP_DB_TO_ENUM.get(dbValue);
		if(c==null) throw new RuntimeException("No Category exists for DB value '"+dbValue+"'.");
		return c;
	}
	private static HashMap<Integer,Category> MAP_DB_TO_ENUM = null;

}
