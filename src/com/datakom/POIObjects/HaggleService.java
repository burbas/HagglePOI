package com.datakom.POIObjects;

import org.haggle.Attribute;
import org.haggle.DataObject;
import org.haggle.EventHandler;
import org.haggle.Node;
import org.haggle.Handle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * @author aa a
 * 
 * Haggle controller.
 */
public class HaggleService extends Service implements EventHandler {
	private static final String LOG_TAG = "HaggleController";
	private static final String HAGGLE_TAG = "HagglePOI"; 
	
	private Handle hh;
	
	private static final int STATUS_OK = 0;
	private static final int STATUS_REG_FAILED = -2;
	private static final int STATUS_SPAWN_DAEMON_FAILED = -3;
	
	private static  final int NUM_RETRIES = 2;
	
	public HaggleService() {
	
	}

	public int initHaggle() {
		if (hh != null)
			return STATUS_OK;

		Log.d(HaggleService.LOG_TAG, "Trying to Spawn haggle daemon");
		
		if (!Handle.spawnDaemon()) {
			Log.d(HaggleService.LOG_TAG, "Spawning failed");
			return STATUS_SPAWN_DAEMON_FAILED;
		}
		
		long pid = Handle.getDaemonPid();
		Log.d(HaggleService.LOG_TAG, "Haggle daemon pid is: " + pid);

		
		for (int tries = 0; tries < NUM_RETRIES; tries++) {
			try {
				hh = new Handle(HAGGLE_TAG);
				
				hh.registerEventInterest(EVENT_NEIGHBOR_UPDATE, this);
				hh.registerEventInterest(EVENT_NEW_DATAOBJECT, this);
				hh.registerEventInterest(EVENT_INTEREST_LIST_UPDATE, this);
				hh.registerEventInterest(EVENT_HAGGLE_SHUTDOWN, this);
				
				hh.eventLoopRunAsync();
				hh.getApplicationInterestsAsync();
				
				Log.d(HaggleService.LOG_TAG, "Haggle event loop started");
				
				return STATUS_OK;
				
			} catch (Handle.RegistrationFailedException e) {
				Log.e(HaggleService.LOG_TAG, "Registration failed: " + e.getMessage());
				
				Handle.unregister(HAGGLE_TAG);
			}
		}

		Log.e(HaggleService.LOG_TAG, "Registration failed, after retries");
		return STATUS_REG_FAILED;
	}
	
	public synchronized void finiHaggle() {
		if (hh != null) {
			hh.eventLoopStop();
			hh.dispose();
			hh = null;
		}
	}
	
	public void shutdownHaggle() {
		if (hh != null) {
			hh.shutdown();
		}
	}
	
	@Override
	public void onInterestListUpdate(Attribute[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNeighborUpdate(Node[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewDataObject(DataObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdown(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		initHaggle();
	
	}
}
