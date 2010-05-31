package com.datakom;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsLocation implements LocationListener {
	private int longitude = 0;
	private int latitude = 0;
	private float accuracy = 0;

	private static GpsLocation uniqueInstance;
	
	public static synchronized GpsLocation getInstance(Context context) {
		if (uniqueInstance == null) {
			if (context != null) {
				uniqueInstance = new GpsLocation(context);
			}
		}
		return uniqueInstance;
	}
	
	public GpsLocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(lm.GPS_PROVIDER, 5000, 0, this); //set higher values for min-values on time and movement
		Log.d(getClass().getSimpleName(), "requesting Location Updates");
	}

	@Override
	public void onLocationChanged(Location loc) {
		// Pick out the latitude resp longitude
		loc.getLatitude();
		loc.getLongitude();
		// Pick out the GPS accuracy 
		loc.getAccuracy();
		
		this.latitude = (int) (loc.getLatitude() * 1E6);
		this.longitude = (int) (loc.getLongitude() * 1E6);
		this.accuracy = loc.getAccuracy();

		Log.d(getClass().getSimpleName(), "Lat:" + this.latitude + ", Lon: " + this.longitude);
	}
	
	public float getAccuracy(){
		return this.accuracy;
	}
	
	public boolean accurateEnough(){
		if (getAccuracy() < 30.0){
			return true;
		}
		return false;
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.e(getClass().getSimpleName(), "GPS disabled. Cannot get coordinates.");		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), "GPS enabled.");
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	public GeoPoint getCurrentPoint() {
		if (latitude != 0 && longitude != 0)
			return new GeoPoint(latitude, longitude);
		else	
			return null;
	}
}
