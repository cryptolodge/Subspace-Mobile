/*  Subspace Mobile - A Android Subspace Client
    Copyright (C) 2012 Kingsley Masters. All Rights Reserved.
    
    kingsley dot masters at gmail dot com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.subspace.android;

import java.io.IOException;

import com.subspace.network.NetworkGame;
import com.subspace.network.NetworkSubspace;
import com.subspace.redemption.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends Service {

     static final String TAG = "Subspace";
     NetworkServiceNotification notification;

	 private NetworkGame subspace;
	

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	public NetworkService getService() {
            return NetworkService.this;
        }
    }
    
	public NetworkGame getSubspace()
    {
    	return subspace;
    }
	 	
	@Override
    public void onCreate() {
		notification = new NetworkServiceNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);               
        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	
    	//disconnect if we are connected
    	if(subspace.isConnected())
    	{
    		try {
    			subspace.SSDisconnect();
    		} catch(IOException ioe)
    		{
    			Log.e(TAG, Log.getStackTraceString(ioe));
    		}
    	}
    	
    	notification.cancel();

        // Tell the user we stopped.
        Toast.makeText(this, R.string.network_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();


    
    public void Connect(String zoneName,String ipAddress, int port)
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean logConnection =  prefs.getBoolean("pref_logConnection", true);
		boolean logCorePackets =  prefs.getBoolean("pref_logCorePackets", true);
		boolean logGamePackets =  prefs.getBoolean("pref_logGamePackets", true);	
    	
    	try {
    		subspace = new NetworkGame(getApplicationContext(),zoneName);
    		//setup logging as set in settings
    		NetworkGame.LOG_CONNECTION = logConnection;    		
    		NetworkGame.LOG_CORE_PACKETS = logCorePackets;
    		NetworkGame.LOG_GAME_PACKETS = logGamePackets;
    		
    		//temp local test
    		ipAddress= "ssmobile.subspace2.net";
    		port = 2000;
    		
			subspace.SSConnect(ipAddress,port);
			
			//change notify we are connected
			notification.connected(zoneName);			
		} catch (Exception e) {
			Log.e(TAG,Log.getStackTraceString(e)); 
		}
    }   
    
    


    	
}
