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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.subspace.android.ZoneAdapter;
import com.subspace.network.*;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.*;

public class ZonesActivity extends ListActivity {
	static final String TAG = "Subspace";

	ArrayList<Zone> zones;
	ZoneAdapter adapter;
	DataHelper db;
	TextView lastRefreshTextView;

	private Activity self = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zones_activity);

		zones = new ArrayList<Zone>();
		adapter = new ZoneAdapter(this, R.layout.zone_item, zones,false);
		db = new DataHelper(this);

		setListAdapter(adapter);

		// setup event handlers
		lastRefreshTextView = (TextView) findViewById(R.id.LastRefreshTextView);
		Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(self);
				String directoryServer = prefs.getString(
						"pref_directoryServer", "");
				new DownloadZonesTask(self).execute(directoryServer);
			}
		});

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

	}

	@Override
	protected void onStart() {
		super.onStart();
		adapter.addAll(db.getAllZones());
		adapter.notifyDataSetChanged();
		UpdateLastRefreshTextView();
	}
	
	private void UpdateLastRefreshTextView() {
		//load last refresh
		String lastRefreshDateTime = self.getPreferences(MODE_PRIVATE).getString("lastRefresh", "Never");
		lastRefreshTextView.setText("Last Updated : " + lastRefreshDateTime);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	protected class DownloadZonesTask extends
			AsyncTask<String, String, ArrayList<DirectoryZone>> implements
			ISubspaceCallback {
		private ProgressDialog _dialog;
		private Activity _activity;

		public DownloadZonesTask(Activity activity) {
			_activity = activity;
			_dialog = new ProgressDialog(_activity);

		}

		protected void onPreExecute() {
			_dialog.setMessage("Starting Download of Zones from Directory Server");
			_dialog.show();
		}

		@Override
		public void DownloadProgressUpdate(int bytesProgress, int bytesTotal) {
			float percentage = (float) bytesProgress / (float) bytesTotal
					* 100f;
			this.publishProgress("Download Progress " + Math.round(percentage)
					+ "%  (" + bytesProgress + "/" + bytesTotal + ")");
		}

		@Override
		public void DownloadStarted() {
			this.publishProgress("Download Started");
		}

		@Override
		public void DownloadComplete() {
			this.publishProgress("Download Complete");
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(progress);
			_dialog.setMessage(progress[0]);
			Log.d(TAG, progress[0]);
		}

		protected ArrayList<DirectoryZone> doInBackground(String... server) {

			ArrayList<DirectoryZone> zones = null;
			try {

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(self);
				boolean logConnection = prefs.getBoolean("pref_logConnection",
						true);
				boolean logCorePackets = prefs.getBoolean(
						"pref_logCorePackets", true);
				boolean logGamePackets = prefs.getBoolean(
						"pref_logGamePackets", true);

				NetworkDirectory nd = new NetworkDirectory(_activity);

				// setup logging as set in settings
				NetworkGame.LOG_CONNECTION = logConnection;
				NetworkGame.LOG_CORE_PACKETS = logCorePackets;
				NetworkGame.LOG_GAME_PACKETS = logGamePackets;

				nd.setDownloadCallback(this);
				zones = nd.Download(server[0]);
				if (zones != null) {
					// sort by player count
					Collections.sort(zones, new Comparator<DirectoryZone>() {

						public int compare(DirectoryZone o1, DirectoryZone o2) {
							return (o1.PlayerCount > o2.PlayerCount ? -1
									: (o1.PlayerCount == o2.PlayerCount ? 0 : 1));
						}

					});
				}
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			return zones;
		}

		protected void onPostExecute(ArrayList<DirectoryZone> result) {
			// only replace if results returned
			if (result != null && result.size() > 0) {
				List<Zone> zoneList = new ArrayList<Zone>();
				// delete all zones
				db.clearZones();
				// referesh
				for (DirectoryZone dz : result) {
					Zone zone = new Zone(dz);
					// add to db
					db.addZone(zone);
					zoneList.add(zone);
				}

				adapter.notifyDataSetChanged();
				adapter.clear();

				for (Zone z : zoneList) {
					adapter.add(z);
				}
				adapter.notifyDataSetChanged();
			}

			if (_dialog.isShowing()) {
				_dialog.dismiss();
			}

			if (result == null) {
				Toast.makeText(self, "Unable to contact zone directory server",
						3000).show();
			} else {
			      // We need an Editor object to make preference changes.
			      // All objects are from android.context.Context
			      SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
			      SharedPreferences.Editor editor = settings.edit();
			      Time time = new Time();
			      time.setToNow();
			      editor.putString("lastRefresh", time.format("%d/%m/%Y %H:%M:%S"));

			      // Commit the edits!
			      editor.commit();			
			      
			      UpdateLastRefreshTextView();
			}
		}

	}

}