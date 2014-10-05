package com.yahoo.bananas.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yahoo.bananas.R;
import com.yahoo.bananas.adapters.CategoryFilterArrayAdapter;
import com.yahoo.bananas.models.Category;

public class CategoryFiltersFragment extends Fragment {
	private List<Category>             categories;
	private CategoryFilterArrayAdapter aCategories;
	private ListView		           lvCategoryFilters;

	/** Non-view / non-UI related initialization. (fires before onCreateView()) */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Non-view initialization
		categories  = new ArrayList<Category>();
		aCategories = new CategoryFilterArrayAdapter(getActivity(), categories); // WARNING: RARELY USE getActivity().  Other usage is likely improper.
	}
	
	/** View/UI-related initialization. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate Layout
		View v = inflater.inflate(R.layout.fragment_category_filters, container, false); // false = don't attach to container yet.
		// Assign view preferences
		lvCategoryFilters = (ListView) v.findViewById(R.id.lvCategoryFilters);
		lvCategoryFilters.setAdapter(aCategories);
		populateCategories();
		// Return layout view
		return v;
	}
	
	/** [For dynamic loading] The activity can pass a custom user instead of defaulting to the current user.
	 *  Set to NULL (default) for current user. */
    public static CategoryFiltersFragment newInstance() { // TODO pass settings
    	CategoryFiltersFragment f = new CategoryFiltersFragment();
    	/* TODO
        Bundle args = new Bundle();
        if(user!=null) args.putLong(UID, user.getId());
        f.setArguments(args);
        Log.e(DEBUG,"new fragment: "+user);
        if(user!=null) Log.e(DEBUG,"new fragment: "+user.getId());
        */
        return f;
    }	
	
    /** Populate the list with categories. */
	public void populateCategories(){
		Log.d("trace", "CategoryFiltersFragment.populateCategories()");
		// Get Current List
		categories = Arrays.asList(Category.values());
		// Sort it by Display Name
		Collections.sort(categories, new Comparator<Category>() {
			@Override
			public int compare(Category lhs, Category rhs) {
				return lhs.getDisplayName().compareTo(rhs.getDisplayName());
			}
		});
		aCategories.addAll(categories);
	}
}
