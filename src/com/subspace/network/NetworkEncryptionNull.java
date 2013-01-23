/*  Subspace Mobile - A Android Subspace Client
    Copyright (C) 2012 Kingsley Masters. All Rights Reserved.
    
    kingsley dot masters at gmail dot com

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
*/

package com.subspace.network;

import java.nio.ByteBuffer;
import android.util.Log;

public class NetworkEncryptionNull implements INetworkEncryption{
	static final String TAG = "Subspace";
	@Override
	public ByteBuffer CreateLogin() {
		return NetworkPacket.CreateConnection(0, (short) 1);
	}

	@Override
	public void Encrypt(ByteBuffer data) {
		return;
	}

	@Override
	public boolean HandleLoginAck(ByteBuffer bb) {
		if (bb.limit() >= 6) 
		{
            int gotkey = bb.getInt(2);

            if (gotkey == 0) /* signal for no encryption */ 
            {
            	Log.d(TAG, "Init Null Encryption\n");
                return true;
                //encryption is not turned on
            } 
            else 
            {
                Log.d(TAG, "Invalid Key: " + gotkey);                
            }
        }
        return false;
	}

	@Override
	public void Decrypt(ByteBuffer data) {
		return;
		
	}

}
