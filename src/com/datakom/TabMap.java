package com.datakom;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.datakom.POIObjects.HaggleConnector;
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
	private static final int MENU_QUIT = 1000;
	private static final int MENU_MY_LOCATION = 1001;
	private static final int MENU_PLOT = 1002;

	 @Override
	 protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    setContentView(R.layout.maptabview);
	    gpsLocation = GpsLocation.getInstance(null);
	    mv = (MapView) findViewById(R.id.mapview);
	    mv.setBuiltInZoomControls(true);
	    mc = mv.getController();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.location_point_blue);
	    mc.setZoom(18);
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
		 menu.add(0, MENU_MY_LOCATION, 0, "My location");
		 
		 
		// return true;
	 //}
	 //public boolean onCreateOptionsMenu(Menu menu) {
		  boolean result = super.onCreateOptionsMenu(menu);
		  menu.add(0, MENU_PLOT, 0, "Plot POIs");
/*		  SubMenu myInterests = menu.addSubMenu("My interests");
		  myInterests.add("Resturant"); 
		  myInterests.add("Pub");
		  myInterests.add("Other");
*/		  
		  menu.add(0, MENU_QUIT, 0, "Quit");
		  
		  return result;
		}

	 /* Handles item selections */
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case MENU_MY_LOCATION:
			 centerMyPosition();
			 return true;
		 case MENU_QUIT:
			 finish();
			 return true;
		 case MENU_PLOT:
		 	plotPoints(HaggleConnector.getInstance().getAllObjectPoints());
		 	Toast.makeText(this, HaggleConnector.getInstance().countObjects()+ " objects plotted.", Toast.LENGTH_SHORT).show();
		 	return true;
		 }
		 return false;
	 }
	 
	 /* Animates view to GeoPoint and show Latitude, Longitude, accuracy and if the accuracy is within allowed radius */
	 public void centerMyPosition(){
		GeoPoint current = gpsLocation.getCurrentPoint();
		mc = mv.getController();
		if (current != null){
			mc.animateTo(current);
			Toast.makeText(getBaseContext(), 
				"Lat: " + gpsLocation.getCurrentPoint().getLatitudeE6() + 
				"\nLng: " + gpsLocation.getCurrentPoint().getLongitudeE6()+ 
				"\nAccuracy: "+ gpsLocation.getAccuracy() +
				"\nAccurate Enough: "+ gpsLocation.accurateEnough(),
				Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(TabMap.this, "GPS hasn't been initilized yet, try again later", Toast.LENGTH_SHORT).show();
		}
	 }
	 public void plotPoints(List<GeoPoint> points){
         Drawable drawable = this.getResources().getDrawable(R.drawable.location_point_red);
         for(GeoPoint o : points){
                 MapOverlay itemizedOverlay = new MapOverlay(drawable);
                 itemizedOverlay.addOverlay(new OverlayItem(o, "", ""));
                 mv.getOverlays().add(itemizedOverlay);
         }
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