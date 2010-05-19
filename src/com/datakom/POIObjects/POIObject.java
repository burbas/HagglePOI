package com.datakom.POIObjects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import android.graphics.Bitmap;
import com.google.android.maps.GeoPoint;

/**
 * @author aa a
 * The POI Object class which represents our POIs
 */
public class POIObject implements Parcelable {
	//prettier to subclass instead of type, though this is a prototype
	private static String LOG_TAG = "POIObject";
	
	private int type; 
	private Bitmap picture;
	private double rating;
	private String name;
	private String description;
	private GeoPoint point;
	
	public POIObject(int type, Bitmap picture, double rating, String name, String description) {
		Log.d(POIObject.LOG_TAG, "Creating a POI Object");
		
		this.setType(type);
		this.setPicture(picture);
		this.setRating(rating);
		this.setName(name);
		this.setDescription(description);
	}
	
	public POIObject(Parcel in) {
		readFromParcel(in);
	}

	//measured in microdegrees (degrees * 1E6).
	public void setPoint(int latitude, int longitude) {
		Log.d(POIObject.LOG_TAG, "Creating a GeoPoint in setPoint");
		
		GeoPoint g = new GeoPoint(latitude, longitude);
		this.point = g;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getRating() {
		return rating;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	public Bitmap getPicture() {
		return picture;
	}
	
	//pullers for rest of system
	public ArrayList<POIObject> getAllObjects() {
		Log.d(POIObject.LOG_TAG, "pulling all POIObjects from Haggle");
		return null;
	}
	 
	//pullers for rest of system
	public POIObject getObject(int id) {
		Log.d(POIObject.LOG_TAG, "pulling POIObject with id: " + id + " from Haggle");
		return null;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(getType());
		//out.write
		//type, picture, rating, name, description, point.
		
	}
	private void readFromParcel(Parcel in) {
		type = in.readInt();
	}

}

