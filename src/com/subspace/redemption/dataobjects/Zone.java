package com.subspace.redemption.dataobjects;

import android.database.Cursor;

import com.subspace.network.DirectoryZone;

public class Zone {

	public int Id;
	public String Name;
	public String Ip;
	public int Port;
	public String Description;
	public int Population;
	public int Ping;
	public int IsCustom;
	
	public Zone(DirectoryZone dz) {
		Name = dz.Name;
		Ip  = dz.IPAddress;
		Port = dz.Port;
		Description = dz.Description;
		Population = dz.PlayerCount;
		IsCustom = 0;
	}

	public Zone() {
		// TODO Auto-generated constructor stub
	}

	public Zone(Cursor cursor) {
        this.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
        this.Name = cursor.getString(cursor.getColumnIndex("name"));
        this.Description = cursor.getString(cursor.getColumnIndex("description"));
        this.Ip = cursor.getString(cursor.getColumnIndex("ip"));
        this.Port = Integer.parseInt(cursor.getString(cursor.getColumnIndex("port")));
        this.IsCustom = Integer.parseInt(cursor.getString(cursor.getColumnIndex("iscustom")));
	}
}
