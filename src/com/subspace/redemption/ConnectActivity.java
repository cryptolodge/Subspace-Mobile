package com.subspace.redemption;

import com.subspace.android.NetworkService;
import com.subspace.network.NetworkGame;
import com.subspace.network.NetworkPacket;
import com.subspace.network.NetworkSubspace;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ConnectActivity extends Activity{
	
	static String TAG = "Subspace";
	
	TextView messageView;
	NetworkService networkService;
	NetworkGame subspace;
	Zone zone;
	boolean networkIsBound;
	DataHelper db;
	
	ServiceConnection networkServiceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	networkService = ((NetworkService.LocalBinder)service).getService();
	    	messageView.append(Html.fromHtml("<font color='green'>Network Service Connected.</font><br/>",null,null));
	    	//lets begin our work now
			messageView.append(Html.fromHtml("<font color='green'>Connecting to " 
					+ zone.Name + " > " + zone.Ip + ":" + zone.Port + "</font><br/>",null,null));
			//do a subspace connect please :)
			networkService.Connect(zone.Ip, zone.Port);
			messageView.append(Html.fromHtml("<font color='green'>Connected</font><br/>",null,null));
			//now load subspace connection
			subspace = networkService.getSubspace();
			//		
			try {
				//lets begin our work now
				messageView.append(Html.fromHtml("<font color='green'>Logging in...</font><br/>",null,null));
				subspace.Login(false,"SubspaceMobile","SubspaceMobile");
			} 
			catch(Exception e)
			{
				Log.e(TAG,Log.getStackTraceString(e));
			}	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	networkService = null;
	        messageView.append(Html.fromHtml("<font color='green'>Network Service Disconnected.</font><br/>",null,null));
	    }
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)        	
   		
   		setContentView(R.layout.connect_activity);
   		
   		messageView = (TextView)this.findViewById(R.id.messageView);
   		db = new DataHelper(this);
   		
   		int selectedZoneId = getIntent().getIntExtra("SelectedZone",-1);
   		
   		if(selectedZoneId < 0)
   		{
   			Log.e(TAG, "selectedZoneId has not been passed though: " + selectedZoneId);
   			return;
   		}
   		
   		zone = db.getZone(selectedZoneId);
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
		
		messageView.append(Html.fromHtml("<font color='green'>Initialising</font><br/>",null,null));
		
		//start
		startService(new Intent(this, NetworkService.class));
		//bind
		doBindService();
	}
    
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(this, 
	    		NetworkService.class), networkServiceConnection, Context.BIND_AUTO_CREATE);
	    networkIsBound = true;
	}

	void doUnbindService() {
	    if (networkIsBound) {
	        // Detach our existing connection.
	        unbindService(networkServiceConnection);
	        networkIsBound = false;
	    }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
    
}
