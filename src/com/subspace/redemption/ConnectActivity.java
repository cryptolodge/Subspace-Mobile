package com.subspace.redemption;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.subspace.android.Information;
import com.subspace.android.LVL;
import com.subspace.android.NetworkService;
import com.subspace.android.News;
import com.subspace.network.IGameCallback;
import com.subspace.network.NetworkGame;
import com.subspace.network.messages.Chat;
import com.subspace.network.messages.LoginResponse;
import com.subspace.network.messages.MapInformation;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

public class ConnectActivity extends Activity implements IGameCallback{
	
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
	    	
	    	SubspaceConnect();
	    	
	   }

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
   		
   		//now load information
   		
   		Display display = getWindowManager().getDefaultDisplay();
   		
   		Information.ScreenWidth = (short) display.getWidth();
   		Information.ScreenHeight = (short) display.getHeight();
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
	
	public void SubspaceConnect()
	{
		messageView.append(Html.fromHtml("<font color='green'>Network Service Connected.</font><br/>",null,null));
    	//lets begin our work now
		messageView.append(Html.fromHtml("<font color='green'>Connecting to " 
				+ zone.Name + " > " + zone.Ip + ":" + zone.Port + "</font><br/>",null,null));
		//do a subspace connect please :)
		networkService.Connect(zone.Name, zone.Ip, zone.Port);
		messageView.append(Html.fromHtml("<font color='green'>Connected</font><br/>",null,null));
		//now load subspace connection
		subspace = networkService.getSubspace();
		subspace.setGameCallback(this);
		//		
		try {
			//lets begin our work now
			messageView.append(Html.fromHtml("<font color='green'>Logging in...</font><br/>",null,null));
			LoginResponse response = subspace.Login(false,"SubspaceMobile","SubspaceMobile");
			if(response!=null)
			{
				messageView.append(Html.fromHtml("<font color='green'>Login Success</font><br/>",null,null));	
				subspace.EnterArena();
			}
		} 
		catch(Exception e)
		{
			Log.e(TAG,Log.getStackTraceString(e));
		}	 	
		
	}
	
	@Override
	public void ChatMessageReceived(Chat message) {
		UpdateChat("<font color='green'>" + message.Type + " " + message.Message + "</font><br/>");
	}
	
	@Override
	public void NowInGameRecieved() {
		UpdateChat("<font color='green'>Now in game</font><br/>");		
	}

	@Override
	public void PlayerIdRecieved(int id) {
		UpdateChat("<font color='green'>PlayerIdRecieved  " + id + "</font><br/>");		
	}
	
	@Override
	public void MapInformationRecieved(MapInformation mapInformation) {
		UpdateChat("<font color='green'>PlayerIdRecieved  "
	+ mapInformation.Filename + " " 
				+ mapInformation.CRC32 + "</font><br/>");		
		
	}
	
	@Override
	public void NewsReceieved(News news) {
		UpdateChat("<font color='green'>News Received</font><br/>" + news.getDocument() + "<br/>");		
		
	}


	@Override
	public void MapReceived(LVL currentLVL) {
		UpdateChat("<font color='green'>Map Received + " + currentLVL.Filename + "</font><br/>");	
	}


	private void UpdateChat(String msg)
	{
		//update chat
				Message myMessage=new Message();
				Bundle resBundle = new Bundle();
				resBundle.putString("message", msg);
						
				myMessage.setData(resBundle);
				handler.sendMessage(myMessage);
	
	}
	
	private Handler handler = new Handler() {
		@Override
		
			public void handleMessage(Message msg) {			
				messageView.append(Html.fromHtml(msg.getData().getString("message"),null,null));
			}
		
		};
    
    
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
