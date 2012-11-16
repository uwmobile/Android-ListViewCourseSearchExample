package com.example.uwapicourselistdemo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uwapi.UWAPIWrapper;
import uwapi.UWAPIWrapper.UWAPIWrapperListener;

import com.example.uwapicourselistdemo.CourseAdapter;
import com.example.uwapicourselistdemo.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements UWAPIWrapperListener {

	// Paste your API key here
	// Get API key from: http://api.uwaterloo.ca/#!/keygen
	private static final String API_KEY = "YOUR API KEY HERE";

	// UI elements
	private Button button1;
	private EditText editText1;
	private ListView listView1;
	private TextView textView1;

	private UWAPIWrapper apiWrapper;

	private CourseAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("Course Search API Demo");

		// Link up UI elements:
		button1 = (Button) findViewById(R.id.button1);
		editText1 = (EditText) findViewById(R.id.editText1);
		listView1 = (ListView) findViewById(R.id.listView1);
		textView1 = (TextView) findViewById(R.id.textView1);

		// Initialize an API wrapper object using the API key and this activity
		// as the listener.
		apiWrapper = new UWAPIWrapper(API_KEY, this);

		listAdapter = new CourseAdapter(this);
		listView1.setAdapter(listAdapter);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					textView1.setText(listAdapter.getItem(position).getString(
							"Description"));
				} catch (JSONException e) {
					textView1.setText("No Description");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void buttonClicked(View view) {
		// Call the CourseSearch service using the given course code.
		apiWrapper.callService("CourseSearch", editText1.getText().toString());
		
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
	}

	@Override
	public void onUWAPIRequestComplete(JSONObject jsonObject, boolean success) {
		if (success) {
			// Got result JSON
			// See output format at: http://api.uwaterloo.ca/#!/coursesearch
			try {
				JSONObject response = jsonObject.getJSONObject("response");
				JSONObject data = response.getJSONObject("data");
				Object result = data.get("result");

				ArrayList<JSONObject> courses = new ArrayList<JSONObject>();
				if (result instanceof JSONArray) {
					// There are multiple results
					// Add each of them into our list
					JSONArray resultArray = (JSONArray) result;
					for (int i = 0; i < resultArray.length(); i++) {
						courses.add(resultArray.getJSONObject(i));
					}
				} else if (result instanceof JSONObject) {
					// Only one result
					courses.add((JSONObject) result);
				}

				listAdapter.setCourses(courses);
				listAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// There was an error in JSON parsing
				// (maybe the result is not be in the expected format).
				Log.d("MainActivity", "JSON parsing failed! \n" + e.toString());
			}
		} else {
			// Request failed (most likely network issue).
			Log.d("MainActivity", "Request Failed! Check your network.");
		}
	}
}
