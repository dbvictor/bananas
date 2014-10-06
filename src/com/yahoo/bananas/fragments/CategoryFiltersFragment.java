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
import com.yahoo.bananas.models.Settings;

public class CategoryFiltersFragment extends Fragment {
	// Static Constants
	// Member Variables
	private List<Category>             categories;
	private CategoryFilterArrayAdapter aCategories;
	private ListView		           lvCategoryFilters;
	private Settings                   settings;

	/** Non-view / non-UI related initialization. (fires before onCreateView()) */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Non-view initialization
		categories  = new ArrayList<Category>();
		aCategories = new CategoryFilterArrayAdapter(getActivity(), categories, settings); // WARNING: RARELY USE getActivity().  Other usage is likely improper.
		settings = (Settings) getArguments().getSerializable(Settings.class.getSimpleName());
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
    public static CategoryFiltersFragment newInstance(Settings settings) {
    	CategoryFiltersFragment f = new CategoryFiltersFragment();
        Bundle args = new Bundle();
        if(settings!=null) args.putSerializable(Settings.class.getSimpleName(), settings);
        f.setArguments(args);
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
