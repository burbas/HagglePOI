package com.datakom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.datakom.POIObjects.DatabaseHelper;
import com.datakom.POIObjects.HaggleConnector;

public class TabSearch extends Activity {
	HaggleConnector conn = HaggleConnector.getInstance();
	protected ArrayList<String> allNames = conn.getAllObjectNames();

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.search_main);
	    
	    final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoComp);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.search_results, allNames);
	    textView.setAdapter(adapter);
	    
	    final Button button = (Button) findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	DatabaseHelper a = new DatabaseHelper(getApplicationContext());
            	a.openDatabase();
            	a.getXmlFromName("e");
            	a.closeDatabase();
            	
            }
//                if(allNames.contains(textView.getText().toString())){
//                	Intent infoView = new Intent(TabSearch.this, TabInfo.class);
//                	startActivity(infoView);
//                }
//                else{
//                	Toast.makeText(TabSearch.this, "No match", Toast.LENGTH_SHORT).show();
//                }
//            }
        });

	}

}