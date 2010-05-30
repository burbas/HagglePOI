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
	
	private Handle hh;
	
	private static final int STATUS_OK = 0;
	private static final int STATUS_REG_FAILED = -2;
	private static final int STATUS_SPAWN_DAEMON_FAILED = -3;
	
	private static  final int NUM_RETRIES = 2;
	
	/* keeping track of every pushed dObj during latest runtime */
	List<String> md5ObjectList = new ArrayList<String>();
	/* keeping track of what neighbors exist*/
	private Node[] neighbors = null;
	
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
		for (String s : md5ObjectList) {
			/* doesn't work for some haggleissues reason*/
//			Log.d("BAJS", s);
//			Log.d("BAJS", dObj.getAttribute("md5", 1).getValue());
//			if (dObj.getAttribute("md5", 1).getValue().compareTo(s) == 0) {
//				Log.d(getClass().getSimpleName() + ":onNewDataObject", "got newly pushed object");
//				return;
//			}
			for (Attribute a : all) {
				if (a.getName().compareTo("md5") == 0 && a.getValue().compareTo(s) == 0) {
					Log.d(getClass().getSimpleName() + ":onNewDataObject", "got newly pushed object");
					return;
				}
			}
			
		}

		if (neighbors != null && neighbors.length > 0) {
			//case 3 - neighbours published data
			//remove dObj from haggle, create POIObject(?), remove md5-hash from array(?), 
			//add tracing data and publish it again.

			Log.d(getClass().getSimpleName() + ":onNewDataObject", "Got new data from neighbor!");
			/* get all attributes */
			for (Attribute a : all) {			
				Log.d(getClass().getSimpleName() + ":onNewDataObject", a.getName() + ", " + a.getValue());
			}
		} else {
			//case 1 - published material from previous run is re-presented again
			//don't modify any of this data
			Log.d(getClass().getSimpleName() + ":onNewDataObject", "Got old data from haggle on start!");
			for (Attribute a : all) {			
				Log.d(getClass().getSimpleName() + ":onNewDataObject", a.getName() + ", " + a.getValue());
			}
		}
	}

	@Override
	public void onShutdown(int reason) {
		Log.e(getClass().getSimpleName(), "Haggle Shutdown, reason: " + reason);
		
	}

	//@Override
	public ArrayList<POIObject> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public POIObject getPOIObject(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<String> getAllObjectNames(){
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<POIObject> collection = getAllObjects();
		
		if (getAllObjects() == null) {
			Log.d(getClass().getSimpleName(), "getAllObjects returned null");
			return null;
		} 
		for (POIObject p : collection) {
			result.add(p.getName());
		}
		return result;
	}
	
	public ArrayList<POIObject> getPOIObjectsByName(String name) {
		return null;
	}
	public POIObject getPOIObjectByName(String name){
		return null;
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
		//for the uniqueness
		String md5 = Helper.createMD5(String.valueOf(System.currentTimeMillis()));
		
		try {
			DataObject dObj = new DataObject(STORAGE_PATH + "/" + o.getPicPath());
			
			dObj.addAttribute("md5", md5, 1); 
			dObj.addAttribute("Type", Integer.toString(o.getType()), 1);
			dObj.addAttribute("Name", o.getName(), 1);
			dObj.addAttribute("Desc", o.getDescription(), 1);
			dObj.addAttribute("Rating", Double.toString(o.getRating()), 1);
			dObj.addAttribute("Create_Latitude", Integer.toString(o.getPoint().getLatitudeE6()), 1);
			dObj.addAttribute("Create_Longitude", Integer.toString(o.getPoint().getLongitudeE6()), 1);

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
		//on successful publish we add the md5
		md5ObjectList.add(md5);
		return 0;
	}
}