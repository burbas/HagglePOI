package com.datakom.POIObjects;

import java.util.ArrayList;

import org.haggle.Attribute;
import org.haggle.DataObject;
import org.haggle.EventHandler;
import org.haggle.Node;
import org.haggle.Handle;

import android.util.Log;


/**
 * @author aa a
 * 
 * Haggle controller implementing interface
 */
public class HaggleConnector implements HaggleInterface, EventHandler {
	private static final String HAGGLE_TAG = "HagglePOI"; 
	private static final String STORAGE_PATH = "/sdcard/HagglePOI";
	
	private Handle hh;
	
	private static final int STATUS_OK = 0;
	private static final int STATUS_REG_FAILED = -2;
	private static final int STATUS_SPAWN_DAEMON_FAILED = -3;
	
	private static  final int NUM_RETRIES = 2;
	
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
			return hh.shutdown();
		}
		return 1;
	}
	
	@Override
	public void onInterestListUpdate(Attribute[] arg0) {
		Log.d(getClass().getSimpleName(), "got new interests");
	}

	@Override
	public void onNeighborUpdate(Node[] neighbors) {
		Log.d(getClass().getSimpleName(), "");
		for (Node n : neighbors) {
			Log.d(getClass().getSimpleName(), "NeighborUpdate: " + n.getName());
		}
	}

	@Override
	public void onNewDataObject(DataObject dObj) {
		Log.d(getClass().getSimpleName() + ":onNewDataObject", "Got new data!");
		
		if (dObj.getAttribute("Picture", 0) == null) {
			Log.d(getClass().getClass().getSimpleName() + ":onNewDataObject", "no picture!");
		}
	}

	@Override
	public void onShutdown(int reason) {
		Log.e(getClass().getSimpleName(), "Haggle Shutdown - panic!");
	}

	@Override
	public ArrayList<POIObject> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public POIObject getPOIObject(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int pushPOIObject(POIObject o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
