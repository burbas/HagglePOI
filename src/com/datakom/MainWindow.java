package com.datakom;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainWindow extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create the first tab
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        // Creates an intent that can be reusable
        intent = new Intent().setClass(this, TabMap.class);
        
        spec = tabHost.newTabSpec("map").setIndicator("Map", 
        		res.getDrawable(R.drawable.ic_tab_map))
        		.setContent(intent);
       tabHost.addTab(spec);
       
       intent.setClass(this, TabInfo.class);
       spec = tabHost.newTabSpec("info").setIndicator("Info", 
       		res.getDrawable(R.drawable.ic_tab_info))
       		.setContent(intent);
      tabHost.addTab(spec);
      
      intent.setClass(this, TabSearch.class);
      spec = tabHost.newTabSpec("search").setIndicator("Search", 
      		res.getDrawable(R.drawable.ic_tab_search))
      		.setContent(intent);
     tabHost.addTab(spec);
        
     intent.setClass(this, TabSettings.class);
        spec = tabHost.newTabSpec("settings").setIndicator("Settings", 
        		res.getDrawable(R.drawable.ic_tab_settings))
        		.setContent(intent);
       tabHost.addTab(spec);
        	
       tabHost.setCurrentTabByTag("map");

    }
}