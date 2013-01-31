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

package com.subspace.redemption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import com.subspace.android.ZoneAdapter;
import com.subspace.network.*;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;


import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.View;

import android.widget.*;

public class ZonesActivity extends ListActivity  {		
	static final String TAG = "Subspace";	
	
	ArrayList<Zone> zones;
	ZoneAdapter adapter;
	DataHelper db;
	
	private Context context = this;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    	
    	  super.onCreate(savedInstanceState);
    	  setContentView(R.layout.zones_activity);
    	  
    	  zones = new ArrayList<Zone>();
    	  adapter = new ZoneAdapter(this, R.layout.zone_item, zones);
    	  db = new DataHelper(this);
    	  
          setListAdapter(adapter);
    }
    
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {		 
		super.onListItemClick(l, v, position, id);
		
		
	}



	@Override
	protected void onStart() 
    {		
		super.onStart();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);		
		String directoryServer =  prefs.getString("pref_directoryServer", "");		
		new DownloadZonesTask(this).execute(directoryServer);
    }  
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    
    private class DownloadZonesTask extends AsyncTask<String, String,ArrayList<DirectoryZone>> implements ISubspaceCallback {
    	private ProgressDialog _dialog;
    	private Activity _activity;
    	public DownloadZonesTask(Activity activity)
    	{
    		_activity = activity;
    		_dialog = new ProgressDialog(_activity);

    	}
    	
        protected void onPreExecute() {
            _dialog.setMessage("Starting Download of Zone");
            _dialog.show();
        }
        
		@Override
		public void DownloadProgressUpdate(int bytesProgress, int bytesTotal) {			
			float percentage = (float)bytesProgress / (float)bytesTotal * 100f;
			this.publishProgress("Download Progress " + Math.round(percentage) + "%  ("+ bytesProgress + "/" +bytesTotal+")" );
		}
		
		@Override
		public void DownloadStarted() {
			this.publishProgress("Download Started");
		}

		@Override
		public void DownloadComplete() {
			this.publishProgress("Download Complete");			
		}

        
        @Override
		protected void onProgressUpdate(String... progress) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(progress);
			_dialog.setMessage(progress[0]);
			Log.d(TAG, progress[0]);
		}

		protected ArrayList<DirectoryZone> doInBackground(String... server) 
        {       	
        	
        	ArrayList<DirectoryZone> zones =null;
        	try {
        		
            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            	boolean logConnection =  prefs.getBoolean("pref_logConnection", true);
        		boolean logCorePackets =  prefs.getBoolean("pref_logCorePackets", true);	
        		
        		NetworkDirectory nd = new NetworkDirectory(_activity);
        		NetworkDirectory.LOG_CONNECTION = logConnection;    		
        		NetworkDirectory.LOG_CORE_PACKETS = logCorePackets;
        		nd.setDownloadCallback(this);
        		zones = nd.Download(server[0]);
        		if(zones!=null)
        		{        			
	        		//sort by player count
	        		Collections.sort(zones, new Comparator<DirectoryZone>(){
	        			 
	        	            public int compare(DirectoryZone o1, DirectoryZone o2) {
	        	            	return (o1.PlayerCount>o2.PlayerCount ? -1 : (o1.PlayerCount==o2.PlayerCount ? 0 : 1));
	        	            }
	        	 
	        	        });
        		}
        	} 
    		catch (Exception e)
    		{    			
    			Log.e(TAG, Log.getStackTraceString(e));    			
    		}	
        	return zones;        	
        }
        
        protected void onPostExecute(ArrayList<DirectoryZone> result) {
        	//only replace if results returned
        	if(result!=null && result.size() > 0)
        	{
	        	List<Zone> zoneList = new ArrayList<Zone>();
	        	//delete all zones
	        	db.clearZones();
	        	//referesh
	        	for(DirectoryZone dz : result)
	        	{
	        		Zone zone = new Zone(dz);
	        		//add to db        		
	        		db.addZone(zone);
	        		zoneList.add(zone);
	        	}        	
	        	
	        	adapter.notifyDataSetChanged();        	
	        	adapter.clear();     
	        	
	        	for(Zone z : zoneList)
	        	{
	        		adapter.add(z);
	        	}
	        	adapter.notifyDataSetChanged();
        	}
			
        	if (_dialog.isShowing()) {
                _dialog.dismiss();
            }
        	
        	if(result==null)
        	{
        		Toast.makeText(context, "Unable to contact zone directory server", 3000).show();
        	}
        }





    }
    
   

}