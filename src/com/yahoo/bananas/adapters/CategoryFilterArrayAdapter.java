package com.yahoo.bananas.adapters;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.bananas.R;
import com.yahoo.bananas.activities.DetailActivity;
import com.yahoo.bananas.models.Category;
import com.yahoo.bananas.models.Joke;
import com.yahoo.bananas.models.User;
import com.yahoo.bananas.util.Util;

public class CategoryFilterArrayAdapter extends ArrayAdapter<Category> {

	public CategoryFilterArrayAdapter(Context context, List<Category> objects){
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Category category = getItem(position);
		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.item_category_filter, parent, false);
		} else {
			v = convertView;
		}
		// Find views within template
		CheckBox cbCategory = (CheckBox) v.findViewById(R.id.cbCategory);
		// Initialize to correct initial state.
		// TODO
		// Set Name
		cbCategory.setText(category.getDisplayName());
		// Store the category into the checkbox to make it easy to use later.
		cbCategory.setTag(category);
		// Setup click listener
		cbCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				/*
				Intent intent = new Intent(v.getContext(),DetailActivity.class);
				intent.putExtra("joke", joke);
				v.getContext().startActivity(intent);
				*/
			}
		});
		return v;
	}
	
}
