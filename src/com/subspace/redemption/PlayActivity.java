package com.subspace.redemption;

import java.util.ArrayList;

import com.subspace.android.ZoneAdapter;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PlayActivity  extends ListActivity {
	
	ArrayList<Zone> zones;
	ZoneAdapter adapter;
	DataHelper db;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        
  	  zones = new ArrayList<Zone>();
  	  adapter = new ZoneAdapter(this, R.layout.zone_item, zones);
  	  db = new DataHelper(this);
  	  
        setListAdapter(adapter);
    }
    
    @Override
	protected void onStart() 
    {		
		super.onStart();	
    }
    
   
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		zones.clear();
		zones.addAll(db.getAllZones());
		adapter.notifyDataSetChanged();	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {		 
		super.onListItemClick(l, v, position, id);
		
		Zone selectedValue = (Zone) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue.Name, Toast.LENGTH_SHORT).show();

        Intent moreDetailsIntent = new Intent(this,ConnectActivity.class);

        Bundle dataBundle = new Bundle();
        dataBundle.putInt("SelectedZone", selectedValue.Id);
        moreDetailsIntent.putExtras(dataBundle);
        startActivity(moreDetailsIntent);
	}
}
