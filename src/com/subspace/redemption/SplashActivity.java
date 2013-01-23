package com.subspace.redemption;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity  extends Activity {
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); // 
	        
	        setContentView(R.layout.splash_activity);
	        
	        if(!Adverts.ON)
	        {
	        	this.findViewById(R.id.adView).setVisibility(View.GONE);
	        }  else {
	        	AdRequest adRequest = new AdRequest();
	        	adRequest.addTestDevice(AdRequest.TEST_EMULATOR);           
	        	adRequest.addTestDevice("B7697111E0B7DFDD28972510CB26CF65");
	        	((AdView)this.findViewById(R.id.adView)).loadAd(adRequest);        	
	        }
	 
	        Handler handler = new Handler();
	 
	        // run a thread after 2 seconds to start the home screen
	        handler.postDelayed(new Runnable() {
	        	
	            @Override
	            public void run() {
	 
	                // make sure we close the splash screen so the user won't come back when it presses back key
	 
	                finish();
	                // start the home screen
	 
	                Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
	                SplashActivity.this.startActivity(intent);
	 
	            }
	 
	        }, Adverts.ON ? 5000 : 1000); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
	 
	    }
}
