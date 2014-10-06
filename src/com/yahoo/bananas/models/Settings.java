package com.yahoo.bananas.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings implements Serializable{
	// Static Constants
	private static final long   serialVersionUID    = -7811227143349252788L;
	private static final int    CATEGORIES_MAXCOUNT = Category.values().length;
	// Member Variables
	private final HashSet<Category> categoriesSelected = new HashSet<Category>(CATEGORIES_MAXCOUNT);
	// Access Methods
	public Set<Category> getCategoriesSelected(){ return categoriesSelected; }
	
	// ----- PERSISTENCE -----
	public static Settings load(Activity anyActivity){
		Settings settings = new Settings();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(anyActivity);
		// Go through all categories by name, and see if they are selected
		// - Default to enabled, so that if more are added over time that we show them until the user disables them.
		for(Category c : Category.values()){
			if(pref.getBoolean(Category.class.getSimpleName()+"."+c.name(), true)){
				settings.categoriesSelected.add(c);
			}
		}
		return settings;
	}
	public void save(Activity anyActivity){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(anyActivity);
		Editor prefEdit = pref.edit();
		// Go through all categories by name, and store whether they are selected here or not.
		for(Category c : Category.values()){
			prefEdit.putBoolean(Category.class.getSimpleName()+"."+c.name(), categoriesSelected.contains(c));
		}
		prefEdit.commit();
	}
	
}
