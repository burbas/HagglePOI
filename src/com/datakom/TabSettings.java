package com.datakom;

import com.datakom.R;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListActivity;

public class TabSettings extends ListActivity {

	static final String[] SETTINGS = new String[] {
	    "Map", 
	    "Something", 
	    "Something more", 
	    "Foodz", 
	    "About this program"
	  };

	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, SETTINGS));
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	}


}
