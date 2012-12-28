package com.subspace.redemption.dataobjects;

import com.subspace.network.DirectoryZone;

public class Zone {

	public int Id;
	public String Name;
	public String Ip;
	public int Port;
	public String Description;
	public int Population;
	public int Ping;
	public boolean IsCustom;
	
	public Zone(DirectoryZone dz) {
		Name = dz.Name;
		Ip  = dz.IPAddress;
		Port = dz.Port;
		Description = dz.Description;
		Population = dz.PlayerCount;
		IsCustom = false;
	}

	public Zone() {
		// TODO Auto-generated constructor stub
	}
}
