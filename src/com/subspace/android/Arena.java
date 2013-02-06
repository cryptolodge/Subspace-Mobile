package com.subspace.android;

import java.util.HashMap;

import com.subspace.network.messages.PlayerEnter;
import com.subspace.network.messages.PlayerLeave;

import android.util.SparseArray;

public class Arena {
	
	private SparseArray<Player> _players = new SparseArray<Player>();
	
	public LVL Lvl;
	
	public Arena()
	{
		
	}
	
	public void Add(PlayerEnter entering){
		Player p = new Player(entering);
		_players.append(p.Id,p);
	}
	
	public void Remove(PlayerLeave leaving)
	{
		_players.remove(leaving.Id);
	}

	public Player get(short playerId) {
		// TODO Auto-generated method stub
		return _players.get(playerId, null);
	}
	
	
}
