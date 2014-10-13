package com.yahoo.bananas.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/** Categories of jokes. */
public enum Theme {
	White		(0,"White"),
	Grey  		(1,"Grewy");
	
	// Members
	private Integer dbValue     = -1;
	private String  displayName = null;
	// Custom CTOR
	private Theme(Integer dbValue, String displayName){
		this.dbValue     = dbValue;
		this.displayName = displayName;
	}
	// Methods
	public String  getDisplayName(){ return displayName; }
	public static List<Integer> toDbValue(Collection<Theme> enums){
		List<Integer> categoryDbValues = new ArrayList<Integer>(enums.size());
		for(Theme c : enums) categoryDbValues.add(c.toDbValue());
		return categoryDbValues;
	}
	public Integer toDbValue(){ return dbValue; }
	/**
	 * Get the enum from the DB value int.
	 * @return Non-null enum for the specified DB value.
	 * @throws RuntimeException if no enum exists with that DB value.
	 */
	public static Theme fromDbValue(Integer dbValue){
		// If map not yet built, build it once now.
		if(MAP_DB_TO_ENUM==null){ // Okay if multiple threads do at the same time, only point is one will be reused after the brief moment when not exist.
			MAP_DB_TO_ENUM = new HashMap<Integer,Theme>(Theme.values().length);
			for(Theme c : Theme.values()) MAP_DB_TO_ENUM.put(c.toDbValue(), c);
		}
		Theme c = MAP_DB_TO_ENUM.get(dbValue);
		if(c==null) throw new RuntimeException("No Category exists for DB value '"+dbValue+"'.");
		return c;
	}
	private static HashMap<Integer,Theme> MAP_DB_TO_ENUM = null;
	
	public static List<Theme> getList() {
		Theme[] array = Theme.values();
		List<Theme> list = new ArrayList<Theme>(array.length);
		for (Theme t : array) list.add(t);
		return list;
	}

}
