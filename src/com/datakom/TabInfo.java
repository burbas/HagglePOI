package com.datakom;

import com.datakom.POIObjects.IHaggleInterface;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class TabInfo extends Activity {
	private boolean bound = false;
	private IHaggleInterface haggleService; 
	private TestServiceConnection conn = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabinfoview);
		
        Button bind = (Button)findViewById(R.id.bindButton);
        Button unBind = (Button)findViewById(R.id.unBindButton);
        Button invoke = (Button)findViewById(R.id.invokeButton);
        Button invoke2 = (Button)findViewById(R.id.invokeButton2);
              
        
        bind.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
                getApplicationContext().bindService(new Intent(IHaggleInterface.class.getName()),
                        conn, Context.BIND_AUTO_CREATE);
                bound = true;
                updateServiceStatus();

        	}
        });  
        
        unBind.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		if (bound == true) {
        			if  (haggleService != null) {
        				getApplicationContext().unbindService(conn);
        				conn = null;
        				updateServiceStatus();
        			}
        		}
        	}
        });          
        
        //testa skapa
        invoke.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){

        	}
        });
        //testa fråga
        invoke2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        	}
        });
	}
	
	private void updateServiceStatus() {
		String bindStatus = (conn == null) ? "unbound" : "bound";
		String statusText = "Service status: " + bindStatus;
		
		TextView t = (TextView) findViewById(R.id.serviceStatus);
		t.setText(statusText);
	}

	private class TestServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			haggleService = IHaggleInterface.Stub.asInterface((IBinder) boundService);
			Log.d(getClass().getSimpleName(), "bound service! onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			haggleService = null;
			updateServiceStatus();
			Log.d(getClass().getSimpleName(), "unbound!");
		}
		
	}
}