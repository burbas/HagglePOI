package com.datakom;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.widget.LinearLayout;

public class TabMap extends MapActivity {
	LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	MapOverlay itemizedOverlay;
	GeoPoint point = new GeoPoint(19240000,-99120000);
	OverlayItem overlayitem = new OverlayItem(point, "", "");
	
	private LocationManager lm;
    private LocationListener locationListener;
    private MapController mc;
    
    
	
	@Override
	protected void onCreate(Bundle icicle) {
	   super.onCreate(icicle);
	   setContentView(R.layout.maptabview);
	   mapView = (MapView) findViewById(R.id.mapview);
	   mapView.setBuiltInZoomControls(true);
	   	   
	 //---use the LocationManager class to obtain GPS locations---
       lm = (LocationManager) 
           getSystemService(Context.LOCATION_SERVICE);    
       
       locationListener = new MyLocationListener();
       
       lm.requestLocationUpdates(
           LocationManager.GPS_PROVIDER, 
           0, 
           0, 
           locationListener);
       mc = mapView.getController();
       
       mapOverlays = mapView.getOverlays();
	   drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	   itemizedOverlay = new MapOverlay(drawable);
	   itemizedOverlay.addOverlay(overlayitem);
	   mapOverlays.add(itemizedOverlay);
	}
	 
	@Override
	protected boolean isRouteDisplayed() {
	  return false;
	}

	private class MyLocationListener implements LocationListener 
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {                
                /*Toast.makeText(getBaseContext(), 
                    "Location changed : Lat: " + loc.getLatitude() + 
                    " Lng: " + loc.getLongitude(), 
                    Toast.LENGTH_SHORT).show();*/
                
                GeoPoint p = new GeoPoint(
                        (int) (loc.getLatitude() * 1E6), 
                        (int) (loc.getLongitude() * 1E6));
                mc.animateTo(point);
                mc.setZoom(16);                
                mapView.invalidate();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
            // TODO Auto-generated method stub
        }
    }    
}