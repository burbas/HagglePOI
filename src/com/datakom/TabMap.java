package com.datakom;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TabMap extends MapActivity {
	GpsLocation gpsLocation;
	private MapView mv;
	private MapController mc;
	private List<Overlay> mapOverlays;

	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	    gpsLocation = GpsLocation.getInstance(null);
	    mv = (MapView) findViewById(R.id.mapview);
	    mc = mv.getController();

	    GeoPoint p = gpsLocation.getCurrentPoint();
	    if (p != null) {
	    	mc.setCenter(p);
		    mapOverlays = mv.getOverlays();
		    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		    MapOverlay itemizedOverlay = new MapOverlay(drawable);
			itemizedOverlay.addOverlay(new OverlayItem(p, "", ""));
		    mapOverlays.add(itemizedOverlay);
	    } else {
	    	Log.e(getClass().getSimpleName(), "Current point is null");
	    }
	    mc.setZoom(20);
	 }

	 
	@Override
	protected boolean isRouteDisplayed() {
	  return false;
	}    
}