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
