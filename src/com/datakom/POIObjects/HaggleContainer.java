package com.datakom.POIObjects;

import java.util.ArrayList;
import com.google.android.maps.GeoPoint;

public class HaggleContainer {
	private ArrayList<POIObject> collection = null;
	private static HaggleContainer uniqueInstance = null ;
	
	public static synchronized HaggleContainer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HaggleContainer();
		}
		return uniqueInstance;
	}

	public HaggleContainer() {
		collection = new ArrayList<POIObject>();
	}
	
	public synchronized void add(POIObject p) {
		collection.add(p);
	}
	
	public synchronized boolean remove(POIObject s) {
		return collection.remove(s);
	}
	public synchronized void remove(String s) {
		ArrayList<POIObject> poiList = search(s);
		if (poiList != null) {
			for (POIObject o : poiList) {
				collection.remove(o);
			}
		}
	}
	
	/* manipulation over this collection will cause exceptions, due to 
	 * one iterates while other manipulates */
	public synchronized ArrayList<POIObject> getAllPOIObjects() {
		return collection;
	}
	
	public synchronized ArrayList<String> getAllPOIObjectNames() {
		if (collection == null || collection.size() == 0) {
			return null;
		}
		
		ArrayList<String> ret = new ArrayList<String>(); 
		for (POIObject poi : collection) {
			ret.add(poi.getName());
		}
		return ret;
	}

	public synchronized ArrayList<GeoPoint> getAllPOIObjectGeoPoints() {
        if (collection == null || collection.size() == 0) {
                return null;
        }
        
        ArrayList<GeoPoint> ret = new ArrayList<GeoPoint>(); 
        for (POIObject poi : collection) {
                ret.add(poi.getPoint());
        }
        return ret;
	}
	public synchronized ArrayList<GeoPoint> getAllPOITraces(String name) {
        if (collection == null || collection.size() == 0) {
                return null;
        }
        
        ArrayList<GeoPoint> ret = new ArrayList<GeoPoint>(); 
        for (POIObject poi : collection) {
                if(poi.getName().compareTo(name)==0){
                	ret.addAll(poi.getCoordsExchange());
                }
        }
        return ret;
	}
	public synchronized GeoPoint getPoint(String name) {
        if (collection == null || collection.size() == 0) {
                return null;
        }
        
        GeoPoint ret = null; 
        for (POIObject poi : collection) {
                if(poi.getName().compareTo(name)==0){
                	ret = poi.getPoint();
                }
        }
        return ret;
	}
	public int countObject(){
		if (collection == null){
			return 0;
		}
		return collection.size();
	}
	
	public synchronized ArrayList<POIObject> search(String s) {
		ArrayList<POIObject> ret = new ArrayList<POIObject>();
		
		for (POIObject poi : collection) {
			if (poi.getName().compareTo(s) == 0) {
				ret.add(poi);
			}
		}
		if (ret.size() == 0)
			return null;
		else
			return ret;
	}
}
