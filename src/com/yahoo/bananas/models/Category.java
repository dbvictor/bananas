package com.yahoo.bananas.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum Category {
	Animal		(0,"Animal"		),
	Bar   		(1,"Bar"   		),
	Food   		(2,"Food"   	),
	Blonde   	(3,"Blonde"   	),
	Yo_Mama  	(4,"Yo Mama" ),
	Lawyer   	(5,"Lawyer"   	),
	Doctor   	(6,"Doctor"   	),
	Other 		(7,"Other" 		);
	
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
	public static List<Integer> toDbValue(Collection<Category> enums){
		List<Integer> categoryDbValues = new ArrayList<Integer>(enums.size());
		for(Category c : enums) categoryDbValues.add(c.toDbValue());
		return categoryDbValues;
	}
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
	
	public static List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();
		for (Category cat : Category.values()) {
			categories.add(cat);
		}
		return categories;
	}

}
