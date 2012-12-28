package com.subspace.redemption;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;



public class MainMenuActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
    }

	@Override
	public void onClick(View view) {
		switch(view.getId()){
	       case R.id.btn_settings:
	    	   startActivity(new Intent(this, SettingsActivity.class));
	    	   break;
	       case R.id.btn_zones:
	    	   startActivity(new Intent(this, ZonesActivity.class));
	    	   break;
	       default:
	    	   break;
		}
	}
	
	protected void alertbox(String title, String mymessage)
	   {
	   new AlertDialog.Builder(this)
	      .setMessage(mymessage)
	      .setTitle(title)
	      .setCancelable(true)
	      .setNeutralButton(android.R.string.cancel,
	         new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){}
	         })
	      .show();
	   }
}