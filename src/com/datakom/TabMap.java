package com.datakom;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.datakom.POIObjects.HaggleConnector;
import com.datakom.R.string;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TabMap extends MapActivity {
	GpsLocation gpsLocation;
	private boolean showingTraces = false;
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
	    
	    Bundle extras = getIntent().getExtras();
	    if(extras != null){
    		String name = extras.getString(com.datakom.POIObjects.HaggleConnector.SEARCH_TITLE);
    		String bool = extras.getString(com.datakom.POIObjects.HaggleConnector.SHOW_TRACE);
    		if (bool == null) {
    			Log.e(getClass().getSimpleName(), "show_trace intent null");
    		}
	    	if(extras.getString(com.datakom.POIObjects.HaggleConnector.SHOW_TRACE).compareTo("true")==0){
	    		showingTraces=true;
	    		ArrayList<GeoPoint> points = HaggleConnector.getInstance().getAllPOITraces(name);
	    		if(points != null){
	    			plotPoints(points);
	    			mc.animateTo(points.get(0));
	    		}
	    	}
	    	else{
	    		showingTraces=false;
	    		GeoPoint point = HaggleConnector.getInstance().getPoint(name);
	    		plotPoint(point);
	    		mc.animateTo(point);
	    	}
	    }
	    
	    new Thread(new UpdateCenter(gpsLocation, mv,  mc, drawable)).start();

	 }
	 /* Creates the menu items */
	 public boolean onCreateOptionsMenu(Menu menu) {
		 boolean result = super.onCreateOptionsMenu(menu);
		 if(!showingTraces){
			 menu.add(0, MENU_MY_LOCATION, 0, "My location");
			 menu.add(0, MENU_PLOT, 0, "Plot POIs");
			 menu.add(0, MENU_QUIT, 0, "Quit");
		 }
		 return result;
	 }

	 /* Handles item selections */
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case MENU_MY_LOCATION:
			 centerMyPosition();
			 return true;
		 case MENU_QUIT:
			 Log.d(getClass().getSimpleName(), "calling shutdownHaggle");
			 HaggleConnector.getInstance().shutdownHaggle();
			 finish();
			 return true;
		 case MENU_PLOT:
			 centerMyPosition();
			 plotPoints(HaggleConnector.getInstance().getAllObjectPoints());
			 return true;
		 }
		 return false;
	 }
	 
	 /* Animates view to GeoPoint and show Latitude, Longitude, accuracy and if the accuracy is within allowed radius 
	  * also removes all other plotted items, they will need to be readded to the vm.getOverlays().
	  */
	public void centerMyPosition(){
        Drawable drawable = this.getResources().getDrawable(R.drawable.location_point_blue);
		mv.getOverlays().clear();
		GeoPoint current = gpsLocation.getCurrentPoint();
		mc = mv.getController();
		if (current != null){
            MapOverlay itemizedOverlay = new MapOverlay(drawable);
            itemizedOverlay.addOverlay(new OverlayItem(current, "", ""));
            mv.getOverlays().add(itemizedOverlay);
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
	/* adds all argumented points to the mapViews overlays. */
	 public void plotPoints(List<GeoPoint> points){
		 if(points == null){
			 Toast.makeText(this, "plotPoints were called with null.", Toast.LENGTH_SHORT).show();
			 return;
		 }
         Drawable drawable = this.getResources().getDrawable(R.drawable.location_point_red);
         for(GeoPoint o : points){
             MapOverlay itemizedOverlay = new MapOverlay(drawable);
             itemizedOverlay.addOverlay(new OverlayItem(o, "", ""));
             mv.getOverlays().add(itemizedOverlay);
         }
         Toast.makeText(this, HaggleConnector.getInstance().countObjects()+ " objects plotted.", Toast.LENGTH_SHORT).show();
	 }
	 public void plotPoint(GeoPoint point){
		 Drawable drawable = this.getResources().getDrawable(R.drawable.location_point_red);
         MapOverlay itemizedOverlay = new MapOverlay(drawable);
         itemizedOverlay.addOverlay(new OverlayItem(point, "", ""));
         mv.getOverlays().add(itemizedOverlay);
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