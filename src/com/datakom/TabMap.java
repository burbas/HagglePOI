package com.datakom;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class TabMap extends MapActivity {
	GpsLocation gpsLocation;
	
	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	    gpsLocation = GpsLocation.getInstance(null);
	    MapView mv = (MapView) findViewById(R.id.mapview);
	    MapController mc = mv.getController();

	    GeoPoint p = gpsLocation.getCurrentPoint();
	    if (p != null) {
	    	mc.setCenter(p);
	    } else {
	    	Log.e(getClass().getSimpleName(), "CUrrent point is null");
	    }
	    
	    mc.setZoom(20);
	 }
	 
	 @Override
	 protected boolean isRouteDisplayed() {
	   return false;
	 }
}