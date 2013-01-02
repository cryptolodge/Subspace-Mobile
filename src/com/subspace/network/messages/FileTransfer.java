package com.subspace.network.messages;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Inflater;

import android.util.Log;

import com.subspace.network.NetworkPacket;
import com.subspace.network.Util;

/*
 *  0x10  File Transfer

 Offset      Length      Description
 0           1           Type Byte 0x10
 1           16          File Name *1
 17          *           File Data... *2

 *1 - If no File Name is specified, then it's the News.txt

 *2 - If the file is the News.txt it must be Uncompressed first.
 According to Snrrrub, all other files are sent Uncompressed.
 */

public class FileTransfer {

	protected static final String TAG = "Subspace";

	public String Filename;
	public ByteBuffer Data;
	public final boolean Compressed;

	public FileTransfer(ByteBuffer bb) {

		Filename = Util.GetString(bb, 1, 16);

		//this is reused for this packed also
		if(bb.get(0) == NetworkPacket.S2C_CompressedMapFile)
		{
			Compressed = true;
		} else {

			if (bb.get(1) == 0 || Filename.length() == 0) {
				Filename = "news.txt";
				Compressed = true;
			} else {
				Compressed = false;
			}
			
		}
		
		bb.position(17);
		Data= bb.slice();
	}
}
