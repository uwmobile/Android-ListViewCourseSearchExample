package com.example.uwapicourselistdemo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CourseAdapter extends BaseAdapter {
	ArrayList<JSONObject> courses;
	Context context;

	public CourseAdapter(Context context) {
		super();
		this.context = context;
		this.courses = new ArrayList<JSONObject>();
	}

	public void setCourses(List<JSONObject> newCourses) {
		courses.clear();
		courses.addAll(newCourses);
	}
	
	// Defines how many items the list view displays.
	@Override
	public int getCount() {
		if (courses == null) {
			return 0;
		}
		return courses.size();
	}

	// Defines how the list view displays each list item.
	@Override
	public JSONObject getItem(int index) {
		return courses.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		JSONObject course = courses.get(index);
		TextView textView = new TextView(context);
		
		try {
			textView.setText("");
			textView.append(course.getString("DeptAcronym") + " ");
			textView.append(course.getString("Number") + " - ");
			textView.append(course.getString("Title") + "\n");
		} catch (JSONException e) {
			textView.setText("JSON parsing failed.");
		}
		
		return textView;
	}

}
