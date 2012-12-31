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
import java.util.HashMap;

import android.util.Log;
import android.util.SparseArray;

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
public class NetworkSubspace extends Network implements INetworkCallback {	
	//these can be set via preferences
	public static boolean LOG_CORE_PACKETS = false;
	public static boolean LOG_CONNECTION = true;
	
	private static final String TAG = "Subspace";
    public static final String CHAR_ENCODING = "ISO-8859-1";
    public static final int MAX_RETRY = 5;
    public static final int CONNECT_WAIT_TIME = 4000; //4 second
    
    int sentNumber = 0;
    int playerCount = -1;
    int retryCount = 0;

    INetworkEncryption enc;
    boolean connected = false;
    //reliable packet handling
    int reliableNextOutbound;
    int reliableNextExpected;
    SparseArray<byte[]> reliableIncoming;
    SparseArray<byte[]> reliableOutgoing;
    //chunked message
    byte[] chunkArray = null;
    //stream
    int streamCount = 0;    // Handles chunk pkt sizes
    ByteBuffer streamArray = null;    // Lg chunk packet data
    IDownloadUpdateCallback streamCallback;

	public boolean isConnected() {
		return connected;
	}
    
    public NetworkSubspace() {
        super(null);
        //set call back
        setCallback(this);
        enc = new NetworkEncryptionVIE();
        //setup reliable
        reliableNextOutbound = 0;
        reliableNextExpected = 0;
        reliableIncoming = new SparseArray<byte[]>();
        reliableOutgoing = new SparseArray<byte[]>();
    }

    public final void setDownloadUpdateCallback(IDownloadUpdateCallback callback) {
        this.streamCallback = callback;
    }

    public final boolean SSConnect(String host, int port) throws IOException {
        connected = false;
        this.Connect(host,port);
        //send connection
        synchronized (this) {
            //block for wait
            try {
                retryCount = 0;
                while ((!connected) && (retryCount < MAX_RETRY)) {
                    retryCount++;
                    //send login
                    this.Send(enc.CreateLogin());
                    this.wait(CONNECT_WAIT_TIME);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        return connected;
    }

    public final void SSDisconnect() throws IOException {    	
    	//send 1 disconnect
        this.SSSend(NetworkPacket.CreateDisconnect());
        //signal that we've disconnected
        this.connected = false;
        Log.d(TAG,"Sent Disconnect");
        //send disconnect 2 times to make sure
        this.SSSend(NetworkPacket.CreateDisconnect());
        this.SSSend(NetworkPacket.CreateDisconnect());
    }

    public final void SSSend(ByteBuffer buffer) throws IOException {    	
    	buffer.rewind();
        enc.Encrypt(buffer);
        this.Send(buffer);
    }

    public final void SSSendReliable(ByteBuffer bytes) throws IOException {
        this.SSSend(NetworkPacket.CreateReliable(reliableNextOutbound, bytes));
        reliableNextOutbound++;
    }

    public final void SSSync() throws IOException {
        this.SSSend(NetworkPacket.CreateSync(this.getRecvDatagramCount(), this.getSentDatagramCount()));
    }

    public ByteBuffer Recv(ByteBuffer data, boolean decryptPacket) {
        if (data == null) {
            return null;
        }
        try {
            //if not connected pass to encryption handler
            if (!this.connected && (data.get(0) == NetworkPacket.CORE)) {
            	 //log packets
                if(LOG_CORE_PACKETS)
                {
                	Log.v(TAG,Util.ToHex(data));
                }
                
                if (data.get(1) == NetworkPacket.CORE_CONNECTIONACK) {
                	if(LOG_CONNECTION)
                	{
                		Log.d(TAG,"Received ConnectionAck: " + Util.ToHex(data));
                	}
                    synchronized (this) {
                        if (enc.HandleLoginAck(data)) {
                            this.connected = true;
                            this.notify();
                        } else {
                            Log.d(TAG,Util.ToHex(data));
                        }
                    }
                } 
                else if (data.get(1) == NetworkPacket.CORE_SYNC) //handle subgame flood protection
                {
                	if(LOG_CONNECTION)
                	{
                		Log.d(TAG,"Received Sync: " + Util.ToHex(data));
                	}
                    ByteBuffer syncResponse = NetworkPacket.CreateSyncResponse(data.getInt(2));
                    this.SSSend(syncResponse);         
                	if(LOG_CONNECTION)
                	{
                		Log.d(TAG,"Send back Sync: " + Util.ToHex(syncResponse.array()));
                	}
                } else if (data.get(1) == NetworkPacket.CORE_DISCONNECT) {
                    synchronized (this) {
                        this.connected = false;
                        this.notify();
                        Log.i(TAG,"Disconnected");
                    }
                } else {
                    Log.i(TAG,"Unrecognised Packet: " + Util.ToHex(data));
                }
            }
            else
            {
                //if packet needs decrypting
                //do so            	
                if (decryptPacket) {                    
                    enc.Decrypt(data);
                }
                
                //log packets
                if(LOG_CORE_PACKETS)
                {
                	Log.v(TAG,Util.ToHex(data));
                }
                
                byte packetType = data.get(); 
                if (packetType == NetworkPacket.CORE) {
                	byte corePacketType = data.get();
                    switch (corePacketType) {
                        case NetworkPacket.CORE_RELIABLE: {
                            
                            int id = data.getInt(2);
                            //send ack twice
                            this.SSSend(NetworkPacket.CreateReliableAck(id));   
                            this.SSSend(NetworkPacket.CreateReliableAck(id));
                            Log.d(TAG,"New Reliable Packet Received: " + id);

                            //now handle
                            if (id == this.reliableNextExpected) {
                            	this.reliableNextExpected++;
                                //its expected so send it for processing
                            	data.position(6);       
                            	//copy to new buffer, but remember to update endian
                                ByteBuffer sliceBuffer = data.slice();
                                sliceBuffer.order(ByteOrder.LITTLE_ENDIAN);
                                this.callback.Recv(sliceBuffer, false);                                
                                //now check queue
                                if (this.reliableIncoming.size() > 0) {
                                    while (true) {
                                    	//get stored value
                                        byte[] b = (byte[]) this.reliableIncoming.get(this.reliableNextExpected);
                                        //remove it
                                        this.reliableIncoming.remove(this.reliableNextExpected);
                                        //return null if no more left
                                        if (b == null) {
                                            return null;
                                        }
                                        //Increment next expected
                                        reliableNextExpected++;
                                        //copy to new buffer, but remember to update endian
                                        ByteBuffer buffer = ByteBuffer.wrap(b);
                                        buffer.order(ByteOrder.LITTLE_ENDIAN);
                                        //now process received byte
                                        this.callback.Recv(buffer, false);
                                    }
                                }
                            } else if (id > this.reliableNextExpected) {
                                byte[] msg = new byte[data.limit() - 6];
                                //read into msg
                                data.get(msg,0,msg.length);                                
                                this.reliableIncoming.put(id, msg);
                            } else {
                            	Log.d(TAG,"Already received, resending ack for: " + id);
                            	//we've already had this packet so send a 2 more acks to make sure we don't get it again 
                            	this.SSSend(NetworkPacket.CreateReliableAck(id));
                            	this.SSSend(NetworkPacket.CreateReliableAck(id));
                                return null;
                            }
                        }

                        case NetworkPacket.CORE_RELIABLEACK: {
                            Integer id = Integer.valueOf(data.getInt(2));
                            reliableOutgoing.remove(id);
                            break;
                        }
                        case NetworkPacket.CORE_SYNC: {
                            //send back ack
                            this.SSSend(NetworkPacket.CreateSyncResponse(data.getInt(2)));
                            break;
                        }
                        case NetworkPacket.CORE_SYNCACK: {
                            //do nothing
                            break;
                        }
                        case NetworkPacket.CORE_DISCONNECT:
                            SSDisconnect();
                            return null;
                        case NetworkPacket.CORE_CHUNK: {
                            int oldSize;
                            if (chunkArray == null) {
                                chunkArray = new byte[data.limit() - 2];
                                data.position(2); data.get(chunkArray, 0, data.limit() - 2);
                            } else {
                                oldSize = chunkArray.length;
                                byte[] newArray = new byte[oldSize + data.limit() - 2];
                                System.arraycopy(chunkArray, 0, newArray, 0, chunkArray.length);
                                chunkArray = newArray;
                                newArray = null;
                                data.position(2); data.get(chunkArray, 0, data.limit() - 2);
                            }
                        }
                        break;
                        case NetworkPacket.CORE_CHUNKEND: {
                            int oldSize;
                            oldSize = chunkArray.length;
                            byte[] newArray = new byte[oldSize + data.limit() - 2];
                            System.arraycopy(chunkArray, 0, newArray, 0, chunkArray.length);
                            chunkArray = newArray;
                            newArray = null;
                            data.position(2); data.get(chunkArray, 0, data.limit() - 2);
                            //copy to new buffer, but remember to update endian
                            ByteBuffer buffer = ByteBuffer.wrap(chunkArray);
                            buffer.order(ByteOrder.LITTLE_ENDIAN);                            
                            this.callback.Recv(buffer, false);
                            chunkArray = null;
                        }
                        break;
                        case NetworkPacket.CORE_STREAM: {
                            if (streamCount == 0) {
                                streamCount = data.getInt(2);
                                streamArray = ByteBuffer.allocateDirect(streamCount);
                            }
                            data.position(6); streamArray.put(data);                            
                            streamCount -= (data.limit() - 6);
                            Log.d(TAG,"Stream: " + (streamArray.limit()-streamCount) +"/" + streamArray.limit());
                             if(this.streamCallback!=null)
                                this.streamCallback.Update(streamArray.limit()-streamCount, streamArray.limit());
                            if (streamCount <= 0) {
                            	streamArray.rewind();//move back to start
                                this.callback.Recv(streamArray, false);
                                streamArray = null;
                            }
                        }
                        break;
                        case NetworkPacket.CORE_STREAMCANCEL:
                            break;
                        case NetworkPacket.CORE_STREAMCANCELACK:
                            break;
                        case NetworkPacket.CORE_CLUSTER: {
                            int i = 2;
                            int size;
                            byte[] subMessage;
                            while (i < data.limit()) {
                                size = data.get(i) & 0xff;
                                subMessage = new byte[size];
                                data.position(i+1); data.get(subMessage, 0, size);
                                
                                ByteBuffer buffer = ByteBuffer.wrap(subMessage);
                                buffer.order(ByteOrder.LITTLE_ENDIAN);   
                                
                                this.callback.Recv(buffer, false);
                                i += size + 1;
                            }
                            break;
                        }
                        default:
                        {
                            Log.d(TAG,"Unrecognised Core Packet: " +Util.ToHex(data));
                        }
                    }
                    return null;
                }
                //not a core packet so just return the data
                return data;
            }
        } catch (IOException ioe) {
        	Log.e(TAG,Log.getStackTraceString(ioe));
        }
        return null;
    }


}
