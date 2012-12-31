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

import android.util.Log;

/**
 *
 * @author Kingsley
 */
public class NetworkGame extends NetworkSubspace implements INetworkCallback {
    static final String TAG = "Subspace";  
    public static boolean LOG_GAME_PACKETS = true;
    
    boolean loginResponseReceived = false;
    NetworkLoginResponse loginResponse = null;

    public NetworkGame() {
        super();
        this.setCallback(this);
    }

    public NetworkLoginResponse Login(Boolean isNewUser, String username, String password) throws IOException {
    	loginResponseReceived = false;
    	loginResponse = null;
        try {
            
        	//TODO: create mac id
        	
            this.SSSendReliable(
            		NetworkPacket.CreatePassword(isNewUser, username, password, 12346789)
            		);

            synchronized (this) {
                this.wait();
                // this.wait(RESPONSE_TIMEOUT);
            }
          
            //if no response received return null
            if (!loginResponseReceived) {
                return null;
            }           
        
        } catch (InterruptedException ie) {
        	Log.d(TAG, Log.getStackTraceString(ie));            
        } 
        return loginResponse;
    }

    public ByteBuffer Recv(ByteBuffer data, boolean decrypt) {
        //decrypt
        data = super.Recv(data, decrypt);
        //analyse
        if (data != null) {        	
        	if(LOG_GAME_PACKETS)
        	{
        		Log.v(TAG,"Game: " + Util.ToHex(data));
        	}
            try {
                if (data.get(0) == NetworkPacket.S2C_PASSWORDACK) {
                	Log.d(TAG, "Passwork ACK");
                    //its saved in the array file                    
                    
                	loginResponse = new NetworkLoginResponse(data);
                                        
                    //notify completion of task
                    synchronized (this) {
                    	loginResponseReceived = true;
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

