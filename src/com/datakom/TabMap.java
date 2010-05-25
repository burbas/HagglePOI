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


	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	    gpsLocation = GpsLocation.getInstance(null);
	    mv = (MapView) findViewById(R.id.mapview);
	    mc = mv.getController();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    mc.setZoom(20);
//	    GeoPoint p = gpsLocation.getCurrentPoint();
//	    if (p != null) {
//	    	mc.setCenter(p);
//		    
//	    } else {
//	    	Log.e(getClass().getSimpleName(), "Current point is null");
//	    }
	    new Thread(new UpdateCenter(gpsLocation, mc, drawable)).start();

	 }

	@Override
	protected boolean isRouteDisplayed() {
	  return false;
	}
	
	/* Prints out center point continously from gps with image */
	class UpdateCenter implements Runnable {
		private GpsLocation gpsLocation;
		private MapController mc;
		private List<Overlay> mapOverlays;
		private Drawable drawable; 
		private GeoPoint p = null;
		public UpdateCenter(GpsLocation gpsLocation, MapController mc, Drawable drawable) {
			this.gpsLocation = gpsLocation;
			this.mc = mc;
			this.drawable = drawable;
		}
		@Override
		public void run() {
			while (true) {
				Log.d(getClass().getSimpleName(), "Running!");
				p = gpsLocation.getCurrentPoint();
				if (p != null) {
					mc.setCenter(p);
					mapOverlays = mv.getOverlays();
					MapOverlay itemizedOverlay = new MapOverlay(drawable);
					itemizedOverlay.addOverlay(new OverlayItem(p, "", ""));
					mapOverlays.add(itemizedOverlay);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Log.e(getClass().getSimpleName(), "Error occured: " + e.getMessage());
					}
				}
			}
		}
	}
}