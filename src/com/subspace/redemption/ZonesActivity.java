package com.subspace.redemption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import com.subspace.network.*;


import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class ZonesActivity extends ListActivity  {
	
	static class ViewHolder
	{	    
	    TextView topText;
	    TextView bottomText;
	}
	
	static final String TAG = "Subspace";	
	
	ArrayList<DirectoryZone> zones;
	DirectoryZoneAdapter adapter;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    	
    	  super.onCreate(savedInstanceState);
    	  setContentView(R.layout.zones_activity);
    	  
    	  zones = new ArrayList<DirectoryZone>();
    	  adapter = new DirectoryZoneAdapter(this, R.layout.zone_item, zones);
    	  
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

    
    private class DownloadZonesTask extends AsyncTask<String, Void,ArrayList<DirectoryZone>> {
    	private ProgressDialog _dialog;
    	private Activity _activity;
    	public DownloadZonesTask(Activity activity)
    	{
    		_activity = activity;
    		_dialog = new ProgressDialog(_activity);

    	}
    	
        protected void onPreExecute() {
            _dialog.setMessage("Progress start");
            _dialog.show();
        }

    	
        protected ArrayList<DirectoryZone> doInBackground(String... server) 
        {        	
        	ArrayList<DirectoryZone> zones =null;
        	try {		
        		NetworkDirectory nd = new NetworkDirectory();
        		zones = nd.Download(server[0]);
        		//sort by player count
        		Collections.sort(zones, new Comparator<DirectoryZone>(){
        			 
        	            public int compare(DirectoryZone o1, DirectoryZone o2) {
        	            	return (o1.PlayerCount>o2.PlayerCount ? -1 : (o1.PlayerCount==o2.PlayerCount ? 0 : 1));
        	            }
        	 
        	        });
        	} 
    		catch (IOException e)
    		{    			
    			Log.e(TAG, Log.getStackTraceString(e));    			
    		}	
        	return zones;        	
        }
        
        protected void onPostExecute(ArrayList<DirectoryZone> result) { 
        	adapter.notifyDataSetChanged();        	
        	adapter.clear();     
        	
        	for(DirectoryZone dz : result)
        	{
        		adapter.add(dz);
        	}
        	adapter.notifyDataSetChanged();
			
            if (_dialog.isShowing()) {
                _dialog.dismiss();
            }
        }


    }
    
    private class DirectoryZoneAdapter extends ArrayAdapter<DirectoryZone> {
        private ArrayList<DirectoryZone> items;        

        public DirectoryZoneAdapter(Context context, int textViewResourceId, ArrayList<DirectoryZone> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder;
        	DirectoryZone o = items.get(position);
        	if(convertView==null)
        	{
        		convertView = View.inflate(getContext(), R.layout.zone_item, null);
        		holder = new ViewHolder();
        		holder.topText = (TextView)convertView.findViewById(R.id.toptext);
        		holder.bottomText = (TextView)convertView.findViewById(R.id.bottomtext);
        		convertView.setTag(holder);
        	}
        	else {
        		holder = (ViewHolder)convertView.getTag();
        	}
        	
        	if(o!=null)
        	{
        		holder.topText.setText(o.Name + " : " + o.PlayerCount + " Players");
        	}
            return convertView;
        }
    }

}