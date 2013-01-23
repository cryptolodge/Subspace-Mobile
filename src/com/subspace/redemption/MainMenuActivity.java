package com.subspace.redemption;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.subspace.android.NetworkService;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;



public class MainMenuActivity extends TabActivity {
    
	private static String TAG = "Subspace";
	
    private static final String PLAY_SPEC = "Play";
    private static final String ZONES_SPEC = "Zones";
    private static final String SETTINGS_SPEC = "Settings";
	
    boolean networkServiceIsBound;
    NetworkService networkService;    
    Context mainMenuContext = this;
    
    ServiceConnection networkServiceConnection = new ServiceConnection() {
    	
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			Log.w(TAG,"Network Service Connected.");
			networkService = ((NetworkService.LocalBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			networkService = null;
			new AlertDialog.Builder(mainMenuContext).setTitle("Error").setMessage(" Network Service Disconnected.").setNeutralButton("Close", null).show(); 
			
			Log.w(TAG,"Network Service Disconnected.");
		}
	};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        
        //hide adverts
        if(!Adverts.ON)
        {
        	this.findViewById(R.id.adView).setVisibility(View.GONE);
        } else {
        	AdRequest adRequest = new AdRequest();
        	adRequest.addTestDevice(AdRequest.TEST_EMULATOR);           
        	adRequest.addTestDevice("B7697111E0B7DFDD28972510CB26CF65");
        	((AdView)this.findViewById(R.id.adView)).loadAd(adRequest);        	
        }
        
        TabHost tabHost = getTabHost();

        //play
        TabSpec playSpec = tabHost.newTabSpec(PLAY_SPEC);
        playSpec.setIndicator(PLAY_SPEC, getResources().getDrawable(R.drawable.icon_play));
        Intent playIntent = new Intent(this, PlayActivity.class);       
        playSpec.setContent(playIntent);
 
        // zone Tab
        TabSpec zonesSpec = tabHost.newTabSpec(ZONES_SPEC);
        zonesSpec.setIndicator(ZONES_SPEC, getResources().getDrawable(R.drawable.icon_zone));
        Intent zonesIntent = new Intent(this, ZonesActivity.class);
        zonesSpec.setContent(zonesIntent);
 
        // settings Tab
        TabSpec settingsSpec = tabHost.newTabSpec(SETTINGS_SPEC);
        settingsSpec.setIndicator(SETTINGS_SPEC, getResources().getDrawable(R.drawable.icon_settings));
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsSpec.setContent(settingsIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(playSpec); 
        tabHost.addTab(zonesSpec); 
        tabHost.addTab(settingsSpec);
        //start and bind the service if we need to
		startService(new Intent(this, NetworkService.class));
    }	       
    
    void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(this, NetworkService.class),
				networkServiceConnection, Context.BIND_AUTO_CREATE);
		networkServiceIsBound = true;
	}

	void doUnbindService() {
		if (networkServiceIsBound) {
			// Detach our existing connection.
			unbindService(networkServiceConnection);
			networkServiceIsBound = false;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		doBindService();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doUnbindService();
	}

}