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
import java.util.Timer;

import android.content.Context;
import android.util.Log;

import com.subspace.android.Arena;
import com.subspace.android.Information;
import com.subspace.android.LVL;
import com.subspace.android.News;
import com.subspace.android.Player;
import com.subspace.network.messages.LvlSettings;
import com.subspace.network.messages.Chat;
import com.subspace.network.messages.FileTransfer;
import com.subspace.network.messages.LoginResponse;
import com.subspace.network.messages.MapInformation;
import com.subspace.network.messages.PlayerEnter;
import com.subspace.network.messages.PlayerLeave;
import com.subspace.network.messages.SynchronizationRequest;
import com.subspace.snrrrub.Checksum;

/**
 * 
 * @author Kingsley
 */
public class NetworkGame extends NetworkSubspace implements INetworkCallback {
	static final String TAG = "Subspace";
	public static boolean LOG_GAME_PACKETS = true;

	public final String ZoneName;
	
	boolean loginResponseReceived = false;
	LoginResponse loginResponse = null;

	IGameCallback gameCallback;
	News news;	
	Arena currentArena;
	
	Timer positionTimer = new Timer();

	public NetworkGame(Context context,String zonename) {
		super(context);
		
		this.ZoneName = zonename;
		
		news = new News(_context,this.ZoneName);
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
					(short)1024, (short)800, name, (byte)0));
			//create new arena
			currentArena = new Arena();
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
				if(data.limit() > 520)
            	{
            		Log.v(TAG,"Game: packet in excess of 520 recieived, cannot log at the moment");
            	} else {
            		Log.v(TAG, "Game: " + Util.ToHex(data));
            	}
			}
			try {
				if (data.get(0) == NetworkPacket.S2C_PASSWORDACK) {
					Log.d(TAG, "S2C_PASSWORDACK");
					// its saved in the array file

					loginResponse = new LoginResponse(data);
					
					if(!loginResponse.isLoginOK())
					{						
						//login failed
						Log.e(TAG,loginResponse.getLoginMessage());						
						if(gameCallback!=null)
						{
							gameCallback.ConsoleMessageReceived(loginResponse.getLoginMessage());
						}
						SSDisconnect();
					} 
					else 
					{					
						//send sync
						SSSync();
						//now ask for news
						if(news.getCRC32()!=loginResponse.NewsChecksum)
						{
							Log.d(TAG, "Downloading News");
							SSSendReliable(
									NetworkPacket.CreateNewsTxtRequest()
									);
						} else {
							// notify completion of task
							synchronized (this) {
								loginResponseReceived = true;
								this.notify();
							}			
							Log.d(TAG, "No News Changes");
						}
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
					currentArena.SetMyId(data.getShort(1));
					if (gameCallback != null) {
						gameCallback.PlayerIdRecieved(data.getShort(1));						
					}
				} else if (data.get(0) == NetworkPacket.S2C_ArenaSettings) {
					Log.d(TAG, "S2C_ArenaSettings");
					currentArena.LoadSettings(data);
					//notify for ui
					if (gameCallback != null) {
						gameCallback.LvlSettingsReceived(currentArena.Settings);						
					}
					
				} else if (data.get(0) == NetworkPacket.S2C_PlayerEntering) {
					Log.d(TAG, "S2C_PlayerEntering");
					PlayerEnter playerEntering = new PlayerEnter(data);
					currentArena.Add(playerEntering);
					//notify for ui
					if (gameCallback != null) {
						gameCallback.PlayerEntering(playerEntering);						
					}
				} else if (data.get(0) == NetworkPacket.S2C_PlayerLeaving) {
					Log.d(TAG, "S2C_PlayerLeaving");		
					PlayerLeave playerLeaving = new PlayerLeave(data);
					currentArena.Remove(playerLeaving);
					//notify for ui
					if (gameCallback != null) {
						gameCallback.PlayerLeaving(playerLeaving);						
					}
				} else if (data.get(0) == NetworkPacket.S2C_LargePosition) {
					Log.d(TAG, "S2C_LargePosition");		
				} else if (data.get(0) == NetworkPacket.S2C_PlayerDeath) {
					Log.d(TAG, "S2C_PlayerDeath");		
				} else if (data.get(0) == NetworkPacket.S2C_ChatMessage) {
					Log.d(TAG, "S2C_ChatMessage");
					Chat chatMessage = new Chat(data);
					Player player = currentArena.Get(chatMessage.PlayerId);
					if(player!=null)
					{
						chatMessage.PlayerName = player.Name;
					}
					if (gameCallback != null) {
						gameCallback.ChatMessageReceived(chatMessage);
					}
				} else if (data.get(0) == NetworkPacket.S2C_FlagPosition) {
					Log.d(TAG, "S2C_FlagPosition");
				} else if (data.get(0) == NetworkPacket.S2C_FileTransfer) {
					Log.d(TAG, "S2C_FileTransfer");
					
					FileTransfer ft = new FileTransfer(data);
					Log.d(TAG, "S2C_FileTransfer Received: " + ft.Filename);
					if(ft.Filename=="news.txt")
					{
						news.Save(ft.Data,ft.Compressed);
						
						if (gameCallback != null) {
							gameCallback.NewsReceieved(news);
						}
						
						// notify login completed
						synchronized (this) {
							loginResponseReceived = true;
							this.notify();
						}			
					}
				} else if (data.get(0) == NetworkPacket.S2C_CompressedMapFile) {
					Log.d(TAG, "S2C_CompressedMapFile");
					
					FileTransfer ft = new FileTransfer(data);
					Log.d(TAG, "S2C_CompressedMapFile Received: " + ft.Filename);
					
					currentArena.Lvl.Save(ft.Data,ft.Compressed);
					
					if (gameCallback != null) {
						gameCallback.MapReceived(currentArena.Lvl);
					}
					
				} else if (data.get(0) == NetworkPacket.S2C_ChecksumRecv) {
					Log.d(TAG, "S2C_ChecksumRecv");							
					SynchronizationRequest syncRequest = new SynchronizationRequest(data);					
					SSSend(NetworkPacket.CreateSecurityChecksum(
							currentArena.SettingsCheckSum(syncRequest.ChecksumKey), 
							Checksum.EXEChecksum(syncRequest.ChecksumKey),
							currentArena.LvlCheckSum(syncRequest.ChecksumKey)
							));					
				} else if (data.get(0) == NetworkPacket.S2C_KeepAlive) {
					Log.d(TAG, "S2C_KeepAlive");					
					//send postion
					SSSend(NetworkPacket.CreatePosition(
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
					
					currentArena.Lvl = new LVL(_context,ZoneName,mapInfo.Filename);
					
					//we dont have this map so request it
					if(mapInfo.CRC32!= currentArena.Lvl.getCRC32())
					{
						Log.d(TAG, "Request Map " + mapInfo.Filename);	
						SSSendReliable(
								NetworkPacket.CreateMapRequest()
								);						
					}
					
					if (gameCallback != null) {
						gameCallback.MapInformationRecieved(mapInfo);
					}
					
				} else {
					Log.i(TAG, String.format("Unhandled Packet %d H%x",data.get(0),data.get(0)));
				}
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		return null;
	}

	public void SendChat(String message) {
		// send reliable
		try {
			SSSendReliable(
					NetworkPacket.CreateChat((byte)2,(byte)0,(short)0,message)
					);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public Arena getArena() {
		return currentArena;
	}

}
