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
	public byte[] Data;
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
		

		if (Compressed) {
			try {
				Inflater ifl = new Inflater(); // mainly generate the extraction
				// df.setLevel(Deflater.BEST_COMPRESSION);
				byte[] input = new byte[bb.limit() - 17];
				bb.position(17);
				bb.get(input);
				ifl.setInput(input);
				ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
				byte[] buff = new byte[1024];
				while (!ifl.finished()) {
					int count = ifl.inflate(buff);
					baos.write(buff, 0, count);
				}

				baos.close();
				Data = baos.toByteArray();
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
				Data = new byte[0];
			}
		} else {
			Data = new byte[bb.limit() - 17];
			bb.position(17);
			bb.get(Data);
		}
	}
}
