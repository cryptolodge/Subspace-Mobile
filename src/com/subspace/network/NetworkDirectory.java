/*
SubspaceMobile - A subspace/continuum client for mobile phones
Copyright (C) 2010 Kingsley Masters
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

import android.content.Context;
import android.util.Log;

/**
 *
 * @author Kingsley
 */
public class NetworkDirectory extends NetworkSubspace implements INetworkCallback {
    static final String TAG = "Subspace";
    static int SUBSPACE_DIRECTORY_PORT = 4990;
    static int RESPONSE_TIMEOUT = 30000; // 30s
    boolean responseReceieved;
    ArrayList<DirectoryZone> result;

    public NetworkDirectory(Context context) {
        super(context);
        this.setCallback(this);
    }

    public  ArrayList<DirectoryZone> Download(String host) throws IOException {
        responseReceieved = false;
        result = null;
        try {
            if (this.SSConnect(host, SUBSPACE_DIRECTORY_PORT)) {
                this.SSSendReliable(NetworkPacket.CreateZoneListRequest());

                synchronized (this) {
                    this.wait();
                    // this.wait(RESPONSE_TIMEOUT);
                }
                this.SSDisconnect();
                //if no response received return null
                if (!responseReceieved) {
                    return null;
                }
            }
        } catch (InterruptedException ie) {
        	Log.d(TAG, Log.getStackTraceString(ie));            
        } 
        return result;
    }

    public ByteBuffer Recv(ByteBuffer data, boolean decrypt) {
        //decrypt
        data = super.Recv(data, decrypt);
        //analyse
        if (data != null) {        	
        	if(LOG_CORE_PACKETS)
        	{
        		Log.v(TAG,"Zone Download Stream " + Util.ToHex(data));
        	}
            try {
                if (data.get(0) == NetworkPacket.DIRECTORY_REQUEST) {
                    //its saved in the array file                    
                    result = new ArrayList<DirectoryZone>();
                   
                    int p = 1;
                    while (p < data.limit()) {
                        StringBuffer ipsb = new StringBuffer(15);
                        ipsb.append(data.get(p) & 0xFF);
                        p++;
                        ipsb.append(".");
                        ipsb.append(data.get(p) & 0xFF);
                        p++;
                        ipsb.append(".");
                        ipsb.append(data.get(p) & 0xFF);
                        p++;
                        ipsb.append(".");
                        ipsb.append(data.get(p) & 0xFF);
                        p++;
                        //switch byte order
                        data.order(ByteOrder.LITTLE_ENDIAN);
                        int port = (int)(data.getShort(p) & 0xFFFF);
                        p += 2;
                        short playerCount = (short) (data.getShort(p) & 0xFFFF);
                        p += 2;
                        byte billing = data.get(p);
                        p += 1;
                        byte hopcount = data.get(p);
                        p += 1;
                        int version = data.getInt(p);
                        p += 4;
                        String name = Util.GetString(data, p, 64, NetworkSubspace.CHAR_ENCODING);
                        p += 64;
                        int startPosition = p;
                        while (data.get(p) != 0) {
                            p++;
                        }
                        String description = Util.GetString(data, startPosition, p - startPosition, NetworkSubspace.CHAR_ENCODING);
                        p++;
                        DirectoryZone dzi =
                                new DirectoryZone(
                                ipsb.toString(), port, playerCount, billing != 0, hopcount, version, name, description);
                        
                        Log.v(TAG, "Zone Listing Downloaded: " + dzi.toString());
                        
                        result.add(dzi);
                    }
                    
                    //notify completion of task
                    synchronized (this) {
                        responseReceieved = true;
                        this.notify();
                    }
                }
            } catch (Exception e) {
            	Log.e(TAG, Log.getStackTraceString(e)); 
            }            
        }
        return null;
    }
}

