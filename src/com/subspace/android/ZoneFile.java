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

package com.subspace.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

import org.apache.hadoop.util.PureJavaCrc32;

import android.content.Context;
import android.util.Log;

import com.subspace.network.Util;

public abstract class ZoneFile {

	protected static final String TAG = "Subspace";

	public boolean Exists;
	public final String Filename;
	public int CRC;
	public ByteBuffer Data;

	protected Context context;

	public ZoneFile(Context context, String zoneName, String filename) {
		this.context = context;
		Exists = false;

		Filename = Util.GetSafeFilename(zoneName + "_" + filename);

		File file = context.getFileStreamPath(Filename);
		Exists = file.exists();
		if (Exists) {
			Reload();
		}
	}

	public void Save(ByteBuffer data, boolean requiresDecompressing) {
		Log.i(TAG, "Writing " + Filename + " " + data.limit() + " bytes");
		OutputStream fos;
		try {
			if (requiresDecompressing) {
				fos = new InflaterOutputStream(context.openFileOutput(Filename,
						Context.MODE_PRIVATE));
			} else {
				fos = context.openFileOutput(Filename, Context.MODE_PRIVATE);
			}
			writeBuffer(data, fos);
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		Reload();
	}

	public void Reload() {
		Log.i(TAG, "Loading " + Filename);
		// memory map file
		Data = byteBufferForFile(context.getFileStreamPath(Filename));
		Data.order(ByteOrder.LITTLE_ENDIAN);
		
		UpdateCRC();
		
		AfterLoad();
	}
	
	protected void AfterLoad() { }

	private void UpdateCRC() {
		if (Data != null) {
			Log.d(TAG, "Init CRC32");
			
			byte[] data = new byte[Data.limit()];
			Data.rewind();
			Data.position(0);
			Data.get(data);		
			Data.rewind();
			Log.d(TAG, "Calculating CRC32");
			
			CRC = (int)Util.CRC32(data);

			Log.d(TAG, "Completed CRC32: " + CRC);
		}
	}

	public void writeBuffer(ByteBuffer buffer, OutputStream stream) {
		WritableByteChannel channel = Channels.newChannel(stream);

		try {
			channel.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static ByteBuffer byteBufferForFile(File file) {

		FileChannel vectorChannel;
		ByteBuffer vector;
		try {
			vectorChannel = new FileInputStream(file).getChannel();
		} catch (FileNotFoundException e1) {
			Log.e(TAG, Log.getStackTraceString(e1));
			return null;
		}
		try {
			vector = vectorChannel.map(MapMode.READ_ONLY, 0,
					vectorChannel.size());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return null;
		}
		return vector;

	}

}
