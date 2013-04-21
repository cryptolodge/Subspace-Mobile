package com.subspace.redemption;

import com.subspace.android.Arena;
import com.subspace.android.GamePanel;
import com.subspace.android.NetworkService;
import com.subspace.redemption.database.DataHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.widget.TextView;

public class GameActivity extends Activity {
	static String TAG = "Subspace";

	SharedPreferences prefs;
	boolean networkIsBound;
	NetworkService subspaceService;
	GamePanel gamePanel;

	ServiceConnection networkServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			subspaceService = ((NetworkService.LocalBinder) service)
					.getService();

			Arena arena = subspaceService.getArena();
			gamePanel.setArena(arena);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			subspaceService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// TODO make full screen a preference
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		 */

		gamePanel = new GamePanel(this);
		setContentView(gamePanel);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// start
		startService(new Intent(this, NetworkService.class));
		// bind
		doBindService();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(this, NetworkService.class),
				networkServiceConnection, Context.BIND_AUTO_CREATE);
		networkIsBound = true;
	}

	void doUnbindService() {
		if (networkIsBound) {
			// Detach our existing connection.
			unbindService(networkServiceConnection);
			networkIsBound = false;
		}
	}

}
