/*
SubspaceMobile - A subspace/continuum client for mobile phones
Copyright (C) 2012 Kingsley Masters
Email: kshade2001 at users.sourceforge.net

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


REVISIONS:
 */

package com.subspace.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.subspace.android.Information;
import com.subspace.network.messages.*;

import android.util.Log;

/**
 * 
 * @author Kingsley
 */
public class NetworkGame extends NetworkSubspace implements INetworkCallback {
	static final String TAG = "Subspace";
	public static boolean LOG_GAME_PACKETS = true;

	boolean loginResponseReceived = false;
	LoginResponse loginResponse = null;

	IGameCallback gameCallback;
	
	Timer positionTimer = new Timer();

	public NetworkGame() {
		super();
		this.setCallback(this);
	}

	public final void setGameCallback(IGameCallback callback) {
		this.gameCallback = callback;
	}

	public LoginResponse Login(Boolean isNewUser, String username,
			String password) throws IOException {
		loginResponseReceived = false;
		loginResponse = null;
		try {

			// TODO: create mac id
			Log.d(TAG, "C2S_PASSWORD");
			this.SSSendReliable(NetworkPacket.CreatePassword(isNewUser,
					username, password, 12346789));
			//now send sync
			this.SSSync();
			//and wait
			synchronized (this) {
				this.wait();
				// this.wait(RESPONSE_TIMEOUT);
			}

			// if no response received return null
			if (!loginResponseReceived) {
				return null;
			}

		} catch (InterruptedException ie) {
			Log.d(TAG, Log.getStackTraceString(ie));
		}
		return loginResponse;
	}

	public void EnterArena() {
		EnterArena(null);
	}

	public void EnterArena(String name) {
		try {
			Log.d(TAG, "C2S_ARENALOGIN");
			this.SSSendReliable(NetworkPacket.CreateArenaLogin((byte) 7,
					Information.ScreenWidth, Information.ScreenHeight, name));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public ByteBuffer Recv(ByteBuffer data, boolean decrypt) {
		// decrypt
		data = super.Recv(data, decrypt);
		// analyse
		if (data != null) {
			if (LOG_GAME_PACKETS) {
				Log.v(TAG, "Game: " + Util.ToHex(data));
			}
			try {
				if (data.get(0) == NetworkPacket.S2C_PASSWORDACK) {
					Log.d(TAG, "S2C_PASSWORDACK");
					// its saved in the array file

					loginResponse = new LoginResponse(data);

					// notify completion of task
					synchronized (this) {
						loginResponseReceived = true;
						this.notify();
					}
				} else if (data.get(0) == NetworkPacket.S2C_NOW_IN_GAME) {
					Log.d(TAG, "S2C_NOW_IN_GAME");
					//start sending position
				/*	positionTimer.scheduleAtFixedRate(new TimerTask()
					{
						@Override
						public void run() {
							try {
								//TODO finish this
								SSSend(NetworkPacket.Position(
										(short)0,
										(short)0,
										(byte)0,
										(short)0,
										(short)0,
										(short)0,
										(short)0,
										(short)0,
										(byte)0
										));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Log.e(TAG,Log.getStackTraceString(e));
							}
							
						}
					}, 200, 200);	*/					
					
					if (gameCallback != null) {
						gameCallback.NowInGameRecieved();
					}
				} else if (data.get(0) == NetworkPacket.S2C_MY_UID) {
					Log.d(TAG, "S2C_MY_UID");
					if (gameCallback != null) {
						gameCallback.PlayerIdRecieved(data.getShort(1));						
					}
				} else if (data.get(0) == NetworkPacket.S2C_PlayerEntering) {
					Log.d(TAG, "S2C_PlayerEntering");	
				} else if (data.get(0) == NetworkPacket.S2C_PlayerLeaving) {
					Log.d(TAG, "S2C_PlayerLeaving");		
				} else if (data.get(0) == NetworkPacket.S2C_LargePosition) {
					Log.d(TAG, "S2C_LargePosition");		
				} else if (data.get(0) == NetworkPacket.S2C_PlayerDeath) {
					Log.d(TAG, "S2C_PlayerDeath");		
				} else if (data.get(0) == NetworkPacket.S2C_ChatMessage) {
					Log.d(TAG, "S2C_ChatMessage");
					Chat chatMessage = new Chat(data);
					if (gameCallback != null) {
						gameCallback.ChatMessageReceived(chatMessage);
					}
				} else if (data.get(0) == NetworkPacket.S2C_FlagPosition) {
					Log.d(TAG, "S2C_FlagPosition");
				} else if (data.get(0) == NetworkPacket.S2C_ChecksumRecv) {
					Log.d(TAG, "S2C_ChecksumRecv");							
					SynchronizationRequest syncRequest = new SynchronizationRequest(data);
					
				} else if (data.get(0) == NetworkPacket.S2C_KeepAlive) {
					Log.d(TAG, "S2C_KeepAlive");					
					//send postion
					SSSend(NetworkPacket.Position(
							(short)0,
							(short)0,
							(byte)0,
							(short)0,
							(short)0,
							(short)0,
							(short)0,
							(short)0,
							(byte)0
							));
				} else if (data.get(0) == NetworkPacket.S2C_BrickDropped) {
					Log.d(TAG, "S2C_BrickDropped");	
				} else if (data.get(0) == NetworkPacket.S2C_MapInformation)	{
					Log.d(TAG, "S2C_MapInformation");	
					MapInformation mapInfo = new MapInformation(data);
					if (gameCallback != null) {
						gameCallback.MapInformationRecieved(mapInfo);
					}
					
				} else {
					Log.i(TAG, "Unhandled Packet " + data.get(0));
				}
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		return null;
	}

}
