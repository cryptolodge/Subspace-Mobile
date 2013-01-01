package com.subspace.network.messages;

import java.nio.ByteBuffer;

import com.subspace.network.Util;

public class MapInformation {
	
	/*
	Map Information
	 
    Offset      Length      Description
    0           1           Type Byte 0x29
    1           16          Map File Name
    17          4           Map Checksum *1*/
	
	public final String Filename;
	public final int CRC32;
    
       
    public MapInformation(ByteBuffer buffer)
    {
    	Filename = Util.GetString(buffer, 1,16);    	   	
    	CRC32 = buffer.getInt(17);    	
    }
}
