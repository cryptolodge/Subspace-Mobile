package com.subspace.network.messages;

import java.nio.ByteBuffer;

import com.subspace.network.Util;


/*Offset      Length      Description
            0           1           Type Byte 0x18
            1           4           Prize Seed Value *1
            5           4           Door Seed Value *1
            9           4           Timestamp
            13          4           Checksum Key *2
 
            *1 - Seed values are used in Random Number Generators to
                  determine the location of Prizes and the timing for
                  door switching.
 
            *2 - When this packet is received, a Security Checksum packet
                  (0x1A) should be returned using the Server Checksum Key.
                  */
public class SynchronizationRequest {
	public final int PrizeSeed;
	public final int DoorSeed;
	public final int Timestamp;
	public final int ChecksumKey;
	
    public SynchronizationRequest(ByteBuffer buffer)
    {
    	PrizeSeed = buffer.getInt(1);
    	DoorSeed = buffer.getInt(5);
    	Timestamp = buffer.getInt(9);
    	ChecksumKey = buffer.getInt(13);    	
    }
}
