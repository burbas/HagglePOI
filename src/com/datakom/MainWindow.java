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
        intent = new Intent().setClass(this, TabSettings.class);
        
        
        spec = tabHost.newTabSpec("settings").setIndicator("Settings", 
        		res.getDrawable(R.drawable.ic_tab_settings))
        		.setContent(intent);
       tabHost.addTab(spec);
        	
        

    }
}