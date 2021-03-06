package com.datakom.POIObjects;

import java.util.ArrayList;

import android.util.Log;
import android.graphics.Bitmap;
import com.google.android.maps.GeoPoint;

/**
 * @author aa a
 * The POI Object class which represents our POIs
 */
public class POIObject {
	
	private int type; 
	/* Pictures are stored in path: HaggleConnector.STORAGE_PATH */
	private String picPath;
	private double rating;
	private String name;
	private String description;
	private String md5;
	private GeoPoint point;
	private ArrayList<GeoPoint> coordsExchange;
	
	public POIObject(int type, String picPath, double rating, String name, String description, GeoPoint p) {
		Log.d(getClass().getSimpleName(), "Creating a POI Object");
		
		this.setType(type);
		this.setPicPath(picPath);
		this.setRating(rating);
		this.setName(name);
		this.setDescription(description);
		this.setPoint(p);
		coordsExchange = new ArrayList<GeoPoint>();
		md5 = Helper.createMD5(String.valueOf(System.currentTimeMillis()));
	}

	public POIObject(int type, String picPath, double rating, String name, String desc, int latitude, int longitude, String md5, ArrayList<GeoPoint> coordsEx) {
		this(type, picPath, rating, name, desc, new GeoPoint(latitude, longitude));
		this.setMd5(md5);
		this.setCoordsExchange(coordsEx);
	}

	//measured in microdegrees (degrees * 1E6).
	public void setPoint(int latitude, int longitude) {
		Log.d(getClass().getSimpleName(), "Creating a GeoPoint in setPoint");
		
		GeoPoint g = new GeoPoint(latitude, longitude);
		this.point = g;
	}
	public void setPoint(GeoPoint p) {
		this.point = p;
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
	
	public void setMd5(String s) {
		this.md5 = s;
	}
	public String getMd5() {
		return md5;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setPicPath(String p) {
		this.picPath = p;
	}

	public String getPicPath() {
		return picPath;
	}

	public ArrayList<GeoPoint> getCoordsExchange() {
		return coordsExchange;
	}
	public void setCoordsExchange(ArrayList<GeoPoint> arr) {
		this.coordsExchange = arr;
	}
	public void addExchangeCoords(GeoPoint p) {
		coordsExchange.add(p);
	}
}

