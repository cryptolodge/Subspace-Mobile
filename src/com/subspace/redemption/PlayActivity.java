package com.subspace.redemption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.subspace.android.ZoneAdapter;
import com.subspace.network.NetworkPing;
import com.subspace.network.PingInfo;
import com.subspace.redemption.database.DataHelper;
import com.subspace.redemption.dataobjects.Zone;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PlayActivity extends ListActivity {

	private static final String TAG = "Subspace";

	List<Zone> zones;
	ZoneAdapter adapter;
	DataHelper db;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);

		zones = new CopyOnWriteArrayList<Zone>();
		adapter = new ZoneAdapter(this, R.layout.zone_item, zones);
		db = new DataHelper(this);

		setListAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		zones.clear();
		zones.addAll(db.getAllZones());
		adapter.notifyDataSetChanged();
		// now ping all

		for (Zone zone : zones) {
			PingTask pingTask = new PingTask();
			pingTask.execute(zone);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Zone selectedValue = (Zone) getListAdapter().getItem(position);
		Toast.makeText(this, selectedValue.Name, Toast.LENGTH_SHORT).show();

		Intent moreDetailsIntent = new Intent(this, ConnectActivity.class);

		Bundle dataBundle = new Bundle();
		dataBundle.putInt("SelectedZone", selectedValue.Id);
		moreDetailsIntent.putExtras(dataBundle);
		startActivity(moreDetailsIntent);
	}

	private class PingTask extends AsyncTask<Zone, Void, Void> {

		@Override
		protected Void doInBackground(Zone... params) {

			Zone zone = params[0];

			PingInfo result = null;
			Log.i(TAG, "Pinging " + zone.Name);

			NetworkPing netping = new NetworkPing();
			try {
				result = netping.Ping(zone.Ip, zone.Port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG,Log.getStackTraceString(e));;
			}

			if (result != null) {
				Log.i(TAG, "Players: " + result.Count + "  Ping: "
						+ result.Ping);
				zone.Population = result.Count;
				zone.Ping = result.Ping;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			adapter.notifyDataSetChanged();

		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
}
