package com.datakom;

import org.haggle.Attribute;

import com.datakom.POIObjects.HaggleConnector;
import com.datakom.POIObjects.ObjectTypes;
import com.datakom.POIObjects.POIObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TabInfo extends Activity {
	HaggleConnector conn;
	private static final int MENU_QUIT = -1000;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);
		
        Button shutdown = (Button)findViewById(R.id.shutdownHaggle);
        Button create = (Button)findViewById(R.id.createObj);
        Button register = (Button) findViewById(R.id.registerInterest);
        Button killApp = (Button) findViewById(R.id.killApp);
        
        conn = HaggleConnector.getInstance();
        shutdown.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
                int status = conn.shutdownHaggle();
                
                if (status == 1) {
                	Toast.makeText(TabInfo.this, "Haggle was already shutdown", Toast.LENGTH_SHORT).show();
                }
                else {
                	Toast.makeText(TabInfo.this, "Status is: " + status, Toast.LENGTH_SHORT).show();
                }
        	}
        });
        
        create.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		POIObject o = new POIObject(ObjectTypes.RESTURANT, "", 5.0, "Apans resturang", "monkey food!", 1, 2);
        		int status = conn.pushPOIObject(o);
        		
        		if (status == 0) {
        			Toast.makeText(TabInfo.this, "Pushed!", Toast.LENGTH_SHORT).show();
        		} else {
        			Toast.makeText(TabInfo.this, "Something went wrong, status: " + status, Toast.LENGTH_SHORT).show();
        		}
        	}
        });
	
        register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Attribute a = new Attribute("Type", Integer.toString(ObjectTypes.RESTURANT), 1);
				int status = conn.getHaggleHandle().registerInterest(a);
				if (status == 0) {
					Log.e(getClass().getSimpleName(), "Registered Interest!");
				} else {
					Log.e(getClass().getSimpleName(), "Failed to register Interest! statuS: " + status);
				}
			}
		});
        killApp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 
			}
		});
	
	}
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_QUIT, 0, "Quit");
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_QUIT:
	    	finish();
	        return true;
	    }
	    return false;
	}
	
}