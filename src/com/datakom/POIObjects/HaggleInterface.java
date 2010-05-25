package com.datakom.POIObjects;

import java.util.ArrayList;


/**
 * @author aa a
 *
 * methods for interaction with haggle system from Application
 */
public interface HaggleInterface {
	
	public ArrayList<POIObject> getAllObjects();
	 
	public POIObject getPOIObject(int id);
	
	public int pushPOIObject(POIObject o); 
}
