package com.subspace.network;

import java.nio.ByteBuffer;

public interface INetworkEncryption {

	ByteBuffer CreateLogin();

	void Encrypt(ByteBuffer bytes);

	boolean HandleLoginAck(ByteBuffer data);

	void Decrypt(ByteBuffer data);

}
