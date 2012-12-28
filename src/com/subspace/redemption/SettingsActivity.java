package com.subspace.redemption;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
		 @Override
		 protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  addPreferencesFromResource(R.xml.application_preferences_screen);		   
		 }
}
