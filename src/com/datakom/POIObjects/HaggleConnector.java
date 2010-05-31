package com.datakom.POIObjects;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.haggle.Attribute;
import org.haggle.DataObject;
import org.haggle.EventHandler;
import org.haggle.Node;
import org.haggle.Handle;
import org.haggle.DataObject.DataObjectException;

import com.google.android.maps.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;


/**
 * @author aa a
 * 
 * Haggle controller implementing interface
 */
public class HaggleConnector implements EventHandler {
	private static final String HAGGLE_TAG = "HagglePOI"; 
	public static final String STORAGE_PATH = "/sdcard/HagglePOI";
	public static final String SEARCH_TITLE = "SEARCH_TITLE_KEY";
	
	/* used for fetching from DataObject in haggle*/
	public static final String MD5 = "md5";
	public static final String TYPE = "Type";
	public static final String NAME = "Name";
	public static final String DESC = "Desc";
	public static final String RATING = "Rating";
	public static final String C_LAT = "Create_Latitude";
	public static final String C_LON = "Create_Longitude";
	public static final String EX_COORD = "Exchange_Coordinates";
	/* Format for exchange coordinates are lat#Lon*/

	
	private Handle hh;
	
	private static final int STATUS_OK = 0;
	private static final int STATUS_REG_FAILED = -2;
	private static final int STATUS_SPAWN_DAEMON_FAILED = -3;
	
	private static  final int NUM_RETRIES = 2;
	

	/* keeping track of what neighbors exist*/
	private Node[] neighbors = null;

	/* collection for keeping  track of POIObjects recieved from haggle
	 * this could be avoided if we traverse the haggle.db and generate XML-objects from
	 * xmlhdr and parse values into a POIObject, though due to time limimations this design was choosen.*/
	private HaggleContainer haggleContainer =  HaggleContainer.getInstance(); 
	
	//Singleton instance
	private static HaggleConnector uniqueInstance;
	
	/* Such that several Activities in Front end can share the same HaggleConnector object*/
	public static synchronized HaggleConnector getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HaggleConnector();
			uniqueInstance.initHaggle();
		}
		return uniqueInstance;
	}
	/* this could be private later on */
	public Handle getHaggleHandle() {
		if (hh == null) {
			Log.e(getClass().getSimpleName(), "Haggle Handle is null!");
		}
		
		return hh;
	}
	
	private int initHaggle() {
		if (hh != null)
			return STATUS_OK; 

		Log.d(getClass().getSimpleName(), "Trying to Spawn haggle daemon");
		
		if (!Handle.spawnDaemon()) {
			Log.d(getClass().getSimpleName(), "Spawning failed");
			return STATUS_SPAWN_DAEMON_FAILED;
		}
		
		long pid = Handle.getDaemonPid(); 
		Log.d(getClass().getSimpleName(), "Haggle daemon pid is: " + pid);

		
		for (int tries = 0; tries < NUM_RETRIES; tries++) {
			try {
				hh = new Handle(HAGGLE_TAG);
				
				hh.registerEventInterest(EVENT_NEIGHBOR_UPDATE, this);
				hh.registerEventInterest(EVENT_NEW_DATAOBJECT, this);
				hh.registerEventInterest(EVENT_INTEREST_LIST_UPDATE, this);
				hh.registerEventInterest(EVENT_HAGGLE_SHUTDOWN, this);
				
				hh.eventLoopRunAsync();
				hh.getApplicationInterestsAsync();
				
				Log.d(getClass().getSimpleName(), "Haggle event loop started");
				registerInterests();
				
				return STATUS_OK;
				 
			} catch (Handle.RegistrationFailedException e) {
				Log.e(getClass().getSimpleName(), "Registration failed: " + e.getMessage());
				
				Handle.unregister(HAGGLE_TAG);
			}
		}

		Log.e(getClass().getSimpleName(), "Registration failed, after retries");
		return STATUS_REG_FAILED;
	}
	
	/* drop current haggle connection? */
	public synchronized void finiHaggle() {
		if (hh != null) {
			hh.eventLoopStop();
			hh.dispose();
			hh = null;
		}
	}
	
	public int shutdownHaggle() {
		if (hh != null) {
			unregisterInterests();
			return hh.shutdown();
		}
		return 1;
	}
	
	@Override
	public void onInterestListUpdate(Attribute[] arr) {
		Log.d(getClass().getSimpleName() + ":onInterestListUpdate", "got new interests, size: " +arr.length);
		for (Attribute a : arr) {
			Log.d(getClass().getSimpleName(), "Attr: " + a.getName() + ", value:" + a.getValue());
		}
	}

	@Override
	public void onNeighborUpdate(Node[] newNeighbors) {
		Log.d(getClass().getSimpleName(), "onNewNeighbours");
		
		if (newNeighbors == null) {
			Log.e(getClass().getSimpleName() + ":NeighborUpdate", "Neighbors null");
		}
		
		/* updating neighborArray */
		if (newNeighbors.length == 0) {
			neighbors = null;
			return;
		}
		neighbors = newNeighbors;
		
		for (Node n : newNeighbors) {
			Log.d(getClass().getSimpleName() + ":NeighborUpdate", n.getName());
		}
	}
	
	@Override
	public void onNewDataObject(DataObject dObj) { 
		Log.d(getClass().getSimpleName() + ":onNewDataObject", "callback occured");
		
		String filepath = dObj.getFilePath();
		/* if there is a random Haggle callback, we disregard :-) */
		if (filepath == null || filepath.length() == 0) {
			Log.d(getClass().getSimpleName() + ": onNewDataObject", "Filepath is empty, disregarding dObj");
			return;
		}
		
		Attribute[] all = dObj.getAttributes();
		
		//case 2 - newly published dObj is re-presented again
		//don't modify this data		
		for (POIObject poi : haggleContainer.getAllPOIObjects()) {
			for (Attribute a : all) {
				if (a.getName().compareTo("md5") == 0 && a.getValue().compareTo(poi.getMd5()) == 0) {
					Log.d(getClass().getSimpleName() + ":onNewDataObject", "got newly pushed object");
					return; 
				}
			}
		}
		
		if (neighbors != null && neighbors.length > 0) {
			Log.d(getClass().getSimpleName() + ":onNewDataObject", "Got new data from neighbor!");
			
			//case 3 - neighbours published data
			//remove dObj from haggle, create POIObject(?), remove md5-hash from array(?), 
			//add tracing data and publish it again.
			//parse out thumbnail and store to disk
			 
			/* pushing current coordinates for data tracing */
//			GeoPoint p = new GeoPoint(10, 10);
//			o.addExchangeCoords(p);
//			
//			for (GeoPoint point : o.getCoordsExchange()) {
//				dObj.addAttribute(EX_COORD, point.getLatitudeE6() + "#" + point.getLongitudeE6(), 1);
//			}
//			
			/* get all attributes */
			for (Attribute a : all) {			
				Log.d(getClass().getSimpleName() + ":onNewDataObject", a.getName() + ", " + a.getValue());
			}
		} else {
			//case 1 - published material from previous run is re-presented again
			//don't modify any of this data
			haggleContainer.add(parseDataObject(dObj));
			
//			Log.d(getClass().getSimpleName() + ":onNewDataObject", "Got old data from haggle on start!");
//			POIObject poi = parseDataObject(dObj);
//			Log.d("test", poi.getName());
//			Log.d("test", poi.getPicPath());
//			Log.d("test", poi.getDescription());
//			Log.d("test", poi.getMd5());
//			Log.d("test", "" + poi.getType());
//			Log.d("test", "lat: " + poi.getPoint().getLatitudeE6() + ", lon: " + poi.getPoint().getLongitudeE6());
//			Log.d("test", "" + poi.getRating());
//			Log.d("test", "num exPoint: " + poi.getCoordsExchange().size());
//			for (GeoPoint p : poi.getCoordsExchange()) {
//				Log.d("test", "exPoint: " + p.getLatitudeE6() + ", lon: " + p.getLongitudeE6());
//			}
		}
	}

	private POIObject parseDataObject(DataObject dObj) {
		Log.d(getClass().getSimpleName() + ":parseDataObject", "starting to parse");
		if (dObj == null || dObj.getFilePath() == null || dObj.getFilePath().length() == 0) {
			Log.e(getClass().getSimpleName() + ":parseDataObject", "dObj is null or filePath is not right");
			return null;
		}
		
		if (dObj.getAttributes() == null || dObj.getAttributes().length == 0) {
			Log.e(getClass().getSimpleName() + ":parseDataObject", "dObj lacks attributes");
			return null;
		}
		
		int type = Integer.parseInt(dObj.getAttribute(TYPE, 0).getValue());
		String picPath = dObj.getFilePath();
		double rating = Double.parseDouble(dObj.getAttribute(RATING, 0).getValue());
		String name = dObj.getAttribute(NAME, 0).getValue();
		String desc = dObj.getAttribute(DESC, 0).getValue();
		int create_latitude = Integer.parseInt(dObj.getAttribute(C_LAT, 0).getValue());
		int create_longitude = Integer.parseInt(dObj.getAttribute(C_LON, 0).getValue());
		String md5 = dObj.getAttribute(MD5, 0).getValue();
		ArrayList<GeoPoint> coordsExchange = new ArrayList<GeoPoint>();
		
		Attribute[] attributes = dObj.getAttributes();
		/* due to the reason haggle doesn't put attributes in order we have to 
		 * combine for exchange coordinates due to the fact that they can be arbitrary many */
		for (Attribute a : attributes) {
			if (a.getName().compareTo(EX_COORD) == 0) {
				String both = a.getValue();
				int separator = both.indexOf("#");
				int latitude = Integer.parseInt(both.substring(0, separator));
				int longitude = Integer.parseInt(both.substring(separator + 1, both.length()));
				GeoPoint p = new GeoPoint(latitude, longitude);
				coordsExchange.add(p);
			}
		}
		return new POIObject(type, picPath, rating, name, desc, create_latitude, create_longitude, md5, coordsExchange);
	}
	
	@Override
	public void onShutdown(int reason) {
		Log.e(getClass().getSimpleName(), "Haggle Shutdown, reason: " + reason);
	}

	//@Override
	public ArrayList<POIObject> getAllObjects() {
		return haggleContainer.getAllPOIObjects();
	}
	
	public ArrayList<String> getAllObjectNames(){
		return haggleContainer.getAllPOIObjectNames();
	}
	
	public ArrayList<POIObject> getPOIObjectsByName(String name) {
		return haggleContainer.search(name);
	}

	private void registerInterests() {
		List<String> col = ObjectTypes.getAsList();
		for (String c : col) {
			Attribute a = new Attribute("Type", c, 1);
			int status = getHaggleHandle().registerInterest(a);
			if (status != 0)
				Log.d(getClass().getSimpleName(), "Registered Interest!: " + c);
			else
				Log.e(getClass().getSimpleName(), "Failed to register Interest! status: " + status);
		}
	}
	
	private void unregisterInterests() {
		List<String> col = ObjectTypes.getAsList();
		for (String c : col) {
			Attribute a = new Attribute("Type", c, 1);
			int status = getHaggleHandle().unregisterInterest(a);
			if (status != 0)
				Log.d(getClass().getSimpleName(), "Unregistered Interest!: " + c);
			else
				Log.e(getClass().getSimpleName(), "Failed to unregister Interest! status: " + status);
		}
	}
	
	public Bitmap scaleImage(String filepath, int width) {
		BitmapFactory.Options opts = new BitmapFactory.Options();

    	opts.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(filepath, opts); 

    	double ratio = opts.outWidth / width;
    	
    	opts.inSampleSize = (int)ratio;
    	opts.inJustDecodeBounds = false;
    	
    	return BitmapFactory.decodeFile(filepath, opts);
	}
	//@Override
	public int pushPOIObject(POIObject o) {

		try {
			DataObject dObj;
			/* if it's a re-publish it already contains the storage path*/
			if (o.getPicPath().substring(0, STORAGE_PATH.length()).compareTo(STORAGE_PATH) == 0)
				dObj = new DataObject(o.getPicPath());
			else
				dObj = new DataObject(STORAGE_PATH + "/" + o.getPicPath());
			
			dObj.addAttribute(MD5, o.getMd5(), 1); 
			dObj.addAttribute(TYPE, Integer.toString(o.getType()), 1);
			dObj.addAttribute(NAME, o.getName(), 1);
			dObj.addAttribute(DESC, o.getDescription(), 1);
			dObj.addAttribute(RATING, Double.toString(o.getRating()), 1);
			dObj.addAttribute(C_LAT, Integer.toString(o.getPoint().getLatitudeE6()), 1);
			dObj.addAttribute(C_LON, Integer.toString(o.getPoint().getLongitudeE6()), 1);

			Bitmap bmp = scaleImage(dObj.getFilePath(), 32);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 75, os);
			dObj.setThumbnail(os.toByteArray());

			getHaggleHandle().publishDataObject(dObj);
		} catch (DataObjectException e) {
			Log.e(getClass().getSimpleName(), "Could not create object for: " + o.getName());
			Log.e(getClass().getSimpleName(), e.getMessage());
			
			return -1;
		}
		//on successful publish we store the POIObject
		haggleContainer.add(o);
		return 0;
	}
}