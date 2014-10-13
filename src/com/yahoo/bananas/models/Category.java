package com.yahoo.bananas.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import com.yahoo.bananas.R;

/** Categories of jokes. */
public enum Category {
	Animal		(0,"Animal", R.drawable.ic_animal),
	Bar   		(1,"Bar", R.drawable.ic_bar),
	Food   		(2,"Food", R.drawable.ic_food),
	Blonde   	(3,"Blonde", R.drawable.ic_blonde),
	Yo_Mama  	(4,"Yo Mama", R.drawable.ic_yomama),
	Lawyer   	(5,"Lawyer", R.drawable.ic_lawyer),
	Doctor   	(6,"Doctor", R.drawable.ic_doctor),
	KnockKnock  (8, "Knock Knock", R.drawable.ic_knockknock),
	Other 		(7,"Other", R.drawable.ic_others);
	
	// Members
	private Integer dbValue     = -1;
	private String  displayName = null;
	private int imageId;
	// Custom CTOR
	private Category(Integer dbValue, String displayName, Integer imageId){
		this.dbValue     = dbValue;
		this.displayName = displayName;
		this.imageId = imageId;
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
	
	public static List<Category> getList() {
		Category[] array = Category.values();
		List<Category> list = new ArrayList<Category>(array.length);
		for (Category c : array) list.add(c);
		return list;
	}
	
	public int getImageResourceId() {
		return this.imageId;
	}

}
