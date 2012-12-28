
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.subspace.random.*;

/**
 *
 * @author Kingsley
 */
public final class NetworkEncryptionVIE  implements INetworkEncryption {
	static final String TAG = "Subspace";
    static final int BAD_KEY = -1;
    static final int MAX_PACKET_SIZE = 520;
    int key;
    ByteBuffer keyStream;
    boolean encrypt = false;
    SubspaceHeavyLCG prng = new SubspaceHeavyLCG();

    public boolean isConnected() {
        return encrypt;
    }

    public NetworkEncryptionVIE() {
    	keyStream = ByteBuffer.allocate(MAX_PACKET_SIZE);
    	keyStream.order(ByteOrder.LITTLE_ENDIAN);
    	
        key = BAD_KEY;
    }

    public ByteBuffer CreateLogin() {
        encrypt = false;
        key = Util.GetRandomInt();
        if (key > 0) {
            key = -key;
        }
        Log.d(TAG, "Create Key: " + key);
        return NetworkPacket.CreateConnection(key, (short) 1);
    }

    public boolean HandleLoginAck(ByteBuffer bb) {
        if (bb.limit() >= 6) {
            int gotkey = bb.getInt(2);

            if (gotkey == key) /* signal for no encryption */ {
                key = BAD_KEY;
                return true;
                //encryption is not turned on
            } else {
                if (key == -gotkey) {
                	Log.d(TAG, "Init VIE Encryption\n");
                    key = gotkey;
                    DoInit();
                    return true;
                } else {
                    Log.d(TAG, "Invalid Key: " + gotkey);
                }
            }
        }
        return false;
    }

    void DoInit() {
        int k = key;
        if (k == 0) {
            return;
        }
        prng.seed(k);
        
        for (int i = 0; i < MAX_PACKET_SIZE; i += 2) {
        	keyStream.putShort(i, prng.getNextE());
        }
        //turn encryption on now
        encrypt = true;
    }

    public void Encrypt(ByteBuffer message) {
    	message.rewind();
    	int length = message.limit();
        if (length > MAX_PACKET_SIZE) {
            throw new IndexOutOfBoundsException("Max Packet Length for VIE Encrption is 520");
        }
        if (!encrypt) {
            return; //if encryption isnt on, dont do it!
        }            
        message.order(ByteOrder.LITTLE_ENDIAN);
        //if encryption isnt need
        if (this.key == 0 || keyStream.getInt(0) == 0) {
            return;
        }

        int work = key;

        int mytable = 0; 
        int mydata = 0;

        int loop, until, lengthWithoutHeader;

        if (work == 0 || keyStream.getInt(0) == 0) {
            return;
        }

        if (message.get(0) == 0) {
            mydata = 2;
            lengthWithoutHeader =  (length - 2);
            until = lengthWithoutHeader / 4;

        } else {
            mydata = 1;
            lengthWithoutHeader =  (length - 1);
            until = lengthWithoutHeader / 4;
        }
        if(lengthWithoutHeader % 4 !=0) until++;

        for (loop = 0; loop < until; loop++) {
            work = Util.safeGetInt(message,mydata + loop * 4)
                    ^ keyStream.getInt(mytable + loop * 4) ^ work;

            Util.safePutInt(message,mydata + loop * 4, work);
        }        
        message.rewind();
    }

    public void Decrypt(ByteBuffer message) {
    	message.rewind();
    	int length = message.limit();
        if (length > MAX_PACKET_SIZE) {
            throw new IndexOutOfBoundsException("Max Packet Length for VIE Encrption is 520");
        }
        if (!encrypt) {
            return; //if encryption isnt on, dont do it!
        }

        //if encryption isnt need
        if (this.key == 0 || keyStream.getInt(0) == 0) {
            return;
        }

        int work = key;

        int mytable = 0; //keyStreamMessage(int);
        int mydata = 0;//msg(int)

        int loop, until;
        int lengthWithoutHeader = 0;

        if (message.get(0) == 0) {
            mydata = 2;
            lengthWithoutHeader =  (length - 2);
            until = lengthWithoutHeader / 4;

        } else {
            mydata = 1;
            lengthWithoutHeader =  (length - 1);
            until = lengthWithoutHeader / 4;
        }
        if(lengthWithoutHeader % 4 !=0) until++;

        for (loop = 0; loop < until; loop++) {
            int tmp = Util.safeGetInt(message, mydata + loop * 4);

            Util.safePutInt(message, 
            		mydata + loop * 4,
            		keyStream.getInt(mytable + loop * 4)
                    ^ work ^ tmp);
            work = tmp;
        }
        message.rewind();
    }
}
