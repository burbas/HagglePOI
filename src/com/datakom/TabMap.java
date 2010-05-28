package com.datakom;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
	private static final int MENU_QUIT = -1000;

	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	    gpsLocation = GpsLocation.getInstance(null);
	    mv = (MapView) findViewById(R.id.mapview);
	    mv.setBuiltInZoomControls(true);
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
	    new Thread(new UpdateCenter(gpsLocation, mv,  mc, drawable)).start();

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
	@Override
	protected boolean isRouteDisplayed() {
	  return false;
	}
	
	/* waits until GPS gives valid points, sets current position to center of the map with an icon*/
	class UpdateCenter implements Runnable {
		private GpsLocation gpsLocation;
		private MapController mc;
		private MapView mv;
		private List<Overlay> mapOverlays;
		private Drawable drawable; 
		private GeoPoint p = null;
		private boolean running = true;
		
		public UpdateCenter(GpsLocation gpsLocation, MapView mv, MapController mc, Drawable drawable) {
			this.gpsLocation = gpsLocation;
			this.mc = mc;
			this.drawable = drawable;
			this.mv = mv;
		}
		@Override
		public void run() {
			while (running) {
				Log.d(getClass().getSimpleName(), "Running!");
				
				p = gpsLocation.getCurrentPoint();
				if (p != null) {
					mc.setCenter(p);
					mapOverlays = mv.getOverlays();
					MapOverlay itemizedOverlay = new MapOverlay(drawable);
					itemizedOverlay.addOverlay(new OverlayItem(p, "", ""));
					mapOverlays.add(itemizedOverlay);
					running = false;
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Log.e(getClass().getSimpleName(), "Sleep failed!");
				}
			}
		}
	}
}