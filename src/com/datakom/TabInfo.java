package com.datakom;

import com.datakom.POIObjects.HaggleConnector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TabInfo extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);
		
        Button shutdown = (Button)findViewById(R.id.shutdownHaggle);
        
        shutdown.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
                HaggleConnector con = HaggleConnector.getInstance();
                int status = con.shutdownHaggle();
                
                if (status == 1) {
                	Toast.makeText(TabInfo.this, "Haggle was already shutdown", Toast.LENGTH_SHORT).show();
                }
                else {
                	Toast.makeText(TabInfo.this, "Status is: " + status, Toast.LENGTH_SHORT).show();
                }
        	}
        });  
	}
}