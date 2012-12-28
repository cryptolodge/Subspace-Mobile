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

/**
struct C2SSimplePing {
u32 timestamp;
};

the server replies with 8 bytes:

struct S2CSimplePing {
u32 total;
u32 timestamp;
};
 */
public class NetworkPing extends Network implements INetworkCallback {

    int sentNumber = 0;
    int playerCount = -1;
    int retryCount = 0;
    static int MAX_RETRY = 5;
    static int PING_WAIT_TIME = 1000; //1 second

    public NetworkPing() {
        super(null);
        //set call back
        setCallback(this);
    }

    public PingInfo Ping(String host, int port) throws IOException {
        //incrememnt port by 1 to access ping port
        port++;

        //connct
        long sendTime;
        int pingTime = -1;
        this.Connect(host, port);
        synchronized (this) {
            //block for wait
            try {
                retryCount = 0;
                while ((playerCount < 0) && (retryCount < MAX_RETRY)) {
                    //gen random number
                    sentNumber = Util.GetRandomInt();
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.putInt(0, sentNumber);
                    retryCount++;
                    //send
                    sendTime = System.currentTimeMillis();
                    this.Send(bb);
                    this.wait(PING_WAIT_TIME);
                    pingTime = (int) (System.currentTimeMillis() - sendTime);
                }
                //close connection
                this.Close();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        if (playerCount < 0) {
            pingTime = -1;
        }
        return new PingInfo(pingTime, playerCount);
    }

    public ByteBuffer Recv(ByteBuffer bb, boolean decrypt) {
        synchronized (this) {
            if (bb.limit() == 8) {      
            	
                int total = bb.getInt(0);
                int random = bb.getInt(4);
                if (random == this.sentNumber) {
                    playerCount = total;
                    this.notify();
                } else {
                    playerCount = -1;
                }
            }            
        }
        return null;
    }
}
