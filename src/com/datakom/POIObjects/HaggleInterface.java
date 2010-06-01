package com.datakom.POIObjects;

import java.util.ArrayList;


/**
 * @author aa a
 *
 * methods for interaction with haggle system from Application
 * due to time limitations we skip connecting through interface
 */
public interface HaggleInterface {
	
	public ArrayList<POIObject> getAllObjects();
	 
	public POIObject getPOIObject(int id);
	
	public int pushPOIObject(POIObject o); 
}
