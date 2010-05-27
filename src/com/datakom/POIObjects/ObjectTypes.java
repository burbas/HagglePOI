package com.datakom.POIObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aa a
 * 
 */
public class ObjectTypes {

	//high numbers just to separate them from status codes when adding them as attributes
	public static final int OTHER		= 100001;
	public static final int RESTURANT 	= 100002;
	public static final int PUB 		= 100003;
	
	public static final List<String> getAsList() {
		List<String> col = new ArrayList<String>();
		col.add(Integer.toString(RESTURANT));
		col.add(Integer.toString(PUB));
		col.add(Integer.toString(OTHER));
		
		return col;
	}
}
