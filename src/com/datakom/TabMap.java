package com.datakom;

import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class TabMap extends MapActivity {
	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	 }
	 
	 @Override
	 protected boolean isRouteDisplayed() {
	   return false;
	 }
}