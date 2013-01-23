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
