package com.datakom;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.datakom.POIObjects.HaggleConnector;
import com.datakom.POIObjects.POIObject;

public class TabSearch extends Activity {
	HaggleConnector conn = HaggleConnector.getInstance();
	protected ArrayList<String> allNames = conn.getAllObjectNames();
	protected Toast failSearch = new Toast(this);
    /** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoComp);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.search_results, allNames);
	    textView.setAdapter(adapter);
	    
	    final Button button = (Button) findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(allNames.contains(textView.getText().toString())){
                	loadResult(textView.getText().toString());
                }
                else{
                	failSearch.setText("No match.");
                	failSearch.show();
                }
            }
        });

	}
	
	protected void loadResult(String input){
		POIObject result = conn.getPOIObjectByName(input);
		
	}

}