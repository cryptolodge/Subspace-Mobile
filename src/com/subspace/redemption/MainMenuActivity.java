package com.subspace.redemption;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;



public class MainMenuActivity extends TabActivity {
    
    private static final String PLAY_SPEC = "Play";
    private static final String ZONES_SPEC = "Zones";
    private static final String SETTINGS_SPEC = "Settings";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        
        
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
        
    }	
}