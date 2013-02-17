package com.subspace.android;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.subspace.network.messages.LvlSettings;
import com.subspace.network.messages.PlayerEnter;
import com.subspace.network.messages.PlayerLeave;

import android.util.SparseArray;

public class Arena {
			
	private SparseArray<Player> _players = new SparseArray<Player>();
	private Player _me = new Player();
	
	public LVL Lvl;
	public LvlSettings Settings;
	
	public Arena()
	{
		
	}
	
	public void SetMyId(short id)
	{
		_me.Id = id;
	}
	
	public void Add(PlayerEnter entering){
		Player p = new Player(entering);
		//update myself it its me being updated
		if(p.Id == _me.Id)
		{
			_me = p;
		}
		_players.append(p.Id,p);
	}
	
	public void Remove(PlayerLeave leaving)
	{
		_players.remove(leaving.Id);
	}

	public Player Get(short playerId) {
		// TODO Auto-generated method stub
		return _players.get(playerId, null);
	}

	public int SettingsCheckSum(int checksumKey) {
		if(Settings!=null)
		{
			return Settings.CheckSum(checksumKey);
		}
		return 0;
	}

	public int LvlCheckSum(int checksumKey) {
		if(Lvl!=null)
		{
			return Lvl.CheckSum(checksumKey);
		}
		return 0;
	}

	public void LoadSettings(ByteBuffer arenaSettingsRaw) {
		// TODO Auto-generated method stub
		Settings = new LvlSettings(arenaSettingsRaw);
			
	}
	
	
}
