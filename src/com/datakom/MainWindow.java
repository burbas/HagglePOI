package com.datakom;



import com.datakom.POIObjects.HaggleConnector;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class MainWindow extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GpsLocation.getInstance(this.getApplicationContext()); // initalizing GPS
        
        // Create the first tab
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // Creates an intent that can be reusable

        Intent intent = new Intent() .setClass(this, TabMap.class);
        spec = tabHost.newTabSpec("map").setIndicator("Map", 
        		res.getDrawable(R.drawable.ic_tab_map))
        		.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, TabInfo.class);
        spec = tabHost.newTabSpec("info").setIndicator("Info", 
        		res.getDrawable(R.drawable.ic_tab_info))
       		.setContent(intent);
        tabHost.addTab(spec);
        
        
        intent = new Intent().setClass(this, TabSearch.class);
        spec = tabHost.newTabSpec("search").setIndicator("Search", 
      		res.getDrawable(R.drawable.ic_tab_search))
      		.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, TabCreate.class);
        spec = tabHost.newTabSpec("create").setIndicator("Create", 
        		res.getDrawable(R.drawable.ic_tab_settings))
        		.setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTabByTag("map");

        //hagglestuff
        HaggleConnector.getInstance();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
		Log.d(getClass().getSimpleName(), "calling shutdownHaggle");
    	HaggleConnector.getInstance().shutdownHaggle();
    }
}