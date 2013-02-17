/*  Subspace Mobile - A Android Subspace Client
    Copyright (C) 2012 Kingsley Masters. All Rights Reserved.
    
    kingsley dot masters at gmail dot com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.subspace.redemption;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.subspace.android.Information;
import com.subspace.android.LVL;
import com.subspace.android.NetworkService;
import com.subspace.android.News;
import com.subspace.network.IGameCallback;
import com.subspace.network.ISubspaceCallback;
import com.subspace.network.NetworkGame;
import com.subspace.network.messages.LvlSettings;
import com.subspace.network.messages.Chat;
import com.subspace.network.messages.LoginResponse;
import com.subspace.network.messages.MapInformation;
import com.subspace.network.messages.PlayerEnter;
import com.subspace.network.messages.PlayerLeave;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

public class ConnectActivity extends Activity implements ISubspaceCallback,
		IGameCallback {

	static String TAG = "Subspace";

	TextView messageView;
	NetworkService subspaceService;

	Zone zone;
	boolean networkIsBound;
	DataHelper db;

	ProgressDialog _dialog;

	SharedPreferences prefs;

	ServiceConnection networkServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			subspaceService = ((NetworkService.LocalBinder) service)
					.getService();

			SubspaceConnect();

		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			subspaceService = null;
			UpdateChat("<font color='green'>Network Service Disconnected.</font><br/>");
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

		setContentView(R.layout.connect_activity);

		messageView = (TextView) this.findViewById(R.id.messageView);
		db = new DataHelper(this);

		int selectedZoneId = getIntent().getIntExtra("SelectedZone", -1);

		if (selectedZoneId < 0) {
			Log.e(TAG, "selectedZoneId has not been passed though: "
					+ selectedZoneId);
			return;
		}

		zone = db.getZone(selectedZoneId);

		// now load information

		Display display = getWindowManager().getDefaultDisplay();

		Information.ScreenWidth = (short) display.getWidth();
		Information.ScreenHeight = (short) display.getHeight();

		_dialog = new ProgressDialog(this);

		// hook up chatbox
		EditText editText = (EditText) findViewById(R.id.chatBox);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					sendMessage(arg0);
					handled = true;
				}
				return handled;
			}
		});

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

	}

	protected void sendMessage(TextView textView) {
		final String message = textView.getText().toString();
		textView.setText("");

		// write to log
		messageView.append(Html.fromHtml("<font color='white'>"
				+ prefs.getString("pref_username", "") + ">" + message
				+ "</font><br/>"));

		// send on background thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				subspaceService.SendChat(message);
			}
		}).start();
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

		UpdateChat("<font color='green'>Initialising</font><br/>");

		// start
		startService(new Intent(this, NetworkService.class));
		// bind
		doBindService();
	}

	public void SubspaceConnect() {
		UpdateChat("<font color='green'>Network Service Connected.</font><br/>");
		// lets begin our work now
		UpdateChat("<font color='green'>Connecting to " + zone.Name + " > "
				+ zone.Ip + ":" + zone.Port + "</font><br/>");
		// do a subspace connect please :)

		subspaceService.Connect(zone.Name, zone.Ip, zone.Port);

		UpdateChat("<font color='green'>Connected</font><br/>");
		// now load subspace connection
		subspaceService.setDownloadCallback(this);
		subspaceService.setGameCallback(this);
		//

		// lets begin our work now
		UpdateChat("<font color='green'>Logging in...</font><br/>");

		final String username = prefs.getString("pref_username", "");
		final String password = prefs.getString("pref_password", "");

		// send on background thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				LoginResponse response = subspaceService.Login(false, username,
						password);
				if (response != null) {
					UpdateChat("<font color='green'>Login Success</font><br/>");
					subspaceService.EnterArena();
				} else {
					UpdateChat("<font color='green'>Login Failed</font><br/>");
				}

			}
		}).start();

	}

	@Override
	public void DownloadStarted() {
		Message myMessage = new Message();
		myMessage.arg1 = 10;
		handler.sendMessage(myMessage);
	}

	@Override
	public void DownloadProgressUpdate(int bytesProgress, int bytesTotal) {
		Message myMessage = new Message();
		Bundle resBundle = new Bundle();
		float percentage = (float) bytesProgress / (float) bytesTotal * 100f;
		resBundle.putString("message",
				"Download Progress " + Math.round(percentage) + "%  ("
						+ bytesProgress + "/" + bytesTotal + ")");
		myMessage.arg1 = 11;
		myMessage.setData(resBundle);

		handler.sendMessage(myMessage);
	}

	@Override
	public void DownloadComplete() {
		Message myMessage = new Message();
		myMessage.arg1 = 12;
		handler.sendMessage(myMessage);

	}

	@Override
	public void ChatMessageReceived(Chat message) {
		UpdateChat(message.getFormattedMessage() + "<br/>");
	}

	@Override
	public void ConsoleMessageReceived(String consoleMessage) {
		UpdateChat("<font color='red'>" + consoleMessage + "</font><br/>");
	}

	@Override
	public void NowInGameRecieved() {
		UpdateChat("<font color='green'>Now in game</font><br/>");
	}

	@Override
	public void PlayerIdRecieved(int id) {
		UpdateChat("<font color='green'>PlayerIdRecieved  " + id
				+ "</font><br/>");
	}

	@Override
	public void MapInformationRecieved(MapInformation mapInformation) {
		UpdateChat("<font color='green'>PlayerIdRecieved  "
				+ mapInformation.Filename + " " + mapInformation.CRC32
				+ "</font><br/>");

	}

	@Override
	public void NewsReceieved(News news) {
		UpdateChat("<font color='green'>News Received</font><br/>"
				+ news.getDocument() + "<br/>");

	}

	@Override
	public void MapReceived(LVL currentLVL) {
		UpdateChat("<font color='green'>Map Received + " + currentLVL.Filename
				+ "</font><br/>");
	}

	private void UpdateChat(String msg) {
		// update chat
		Message myMessage = new Message();
		Bundle resBundle = new Bundle();
		resBundle.putString("message", msg);
		myMessage.arg1 = 0;
		myMessage.setData(resBundle);
		handler.sendMessage(myMessage);

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				messageView.append(Html.fromHtml(
						msg.getData().getString("message"), null, null));
			} else if (msg.arg1 == 10) {
				// TODO Auto-generated method stub
				_dialog.setMessage("Download Started");
				_dialog.show();
			} else if (msg.arg1 == 11) {
				_dialog.setMessage(msg.getData().getString("message"));
			} else if (msg.arg1 == 12) {
				// TODO Auto-generated method stub
				if (_dialog.isShowing()) {
					_dialog.dismiss();
				}

			}
		}

	};

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	public void PlayerEntering(PlayerEnter playerEntering) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PlayerLeaving(PlayerLeave playerLeaving) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void LvlSettingsReceived(LvlSettings arenaSettings) {
		// TODO Auto-generated method stub
		
	}

}
