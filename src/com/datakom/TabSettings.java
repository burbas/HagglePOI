package com.datakom;

import com.datakom.R;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ListActivity;

public class TabSettings extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, SETTINGS));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  lv.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
	      // When clicked, show a toast with the TextView text. This is just a test to see if it works
	      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	          Toast.LENGTH_SHORT).show();
	    }
	  });
	}
	
	static final String[] SETTINGS = new String[] {
	    "Map", "Something", "Something more", "Foodz", "About this program"
	  };
}
