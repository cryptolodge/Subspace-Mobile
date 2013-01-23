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

import java.nio.ByteBuffer;

import com.subspace.redemption.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class LVL extends ZoneFile {

	public static short BMPHeader = 0x4D42;

	public Bitmap Tileset;

	public LVLTile[][] Tiles;

	public LVL(Context context, String zoneName, String filename) {
		super(context, zoneName, filename);

	}

	public synchronized  int CheckSum(int key) {
		int savekey = (int) key;
		
		if(Tiles!=null)
		{
			for (int y = savekey % 32; y < 1024; y += 32)
				for (int x = savekey % 31; x < 1024; x += 31) {
					short tile = 0;
					LVLTile lvltile = Tiles[x][y];
					if (lvltile != null) {
						tile = Tiles[x][y].Type;
						if ((tile >= LVLTile.TILE_START && tile <= LVLTile.TILE_END)
								|| tile == LVLTile.SAFETY) {
							key += savekey ^ tile;
						}
					}
				}
		}
		return key;
	}

	@Override
	public synchronized void AfterLoad() {
		Tiles = new LVLTile[1024][1024];
		// process file
		short bfType = Data.getShort(0); // == 0x4D42 ("BM")
		int bfSize = Data.getInt(2); // the total size of the file, which is
										// also
		// the offset of the tile data
		short bfReserved1 = Data.getShort(6);// previously reserved, but we're
												// going to
		// use this for our format
		short bfReserved2 = Data.getShort(8); // this one is still unused,
												// should be 0
		int bfOffBits = Data.getInt(10); // the offset to the bitmap data

		int tileOffset = 0;

		Log.i(TAG, "Loading LVL");
		byte[] lvlData = new byte[Data.limit()];
		Data.rewind();
		Data.position(0);
		Data.get(lvlData);

		// embedded bitmap
		if (bfType == 0x4D42) {
			Log.i(TAG, "Loading LVL tileset");
			tileOffset = bfSize;
			// read in
			Tileset = BitmapFactory.decodeByteArray(lvlData, 0, bfSize);
		} else {
			Log.i(TAG, "Using Default Tileset");
			Tileset = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.tiles);
		}

		// now load tiles
		// might need to rethink this to improve performance
		Log.i(TAG, "Loading LVL Tiles");

		ByteBuffer bb = ByteBuffer.wrap(lvlData);

		for (int i = tileOffset; i < lvlData.length; i += 4) {
			int tileData = bb.getInt(i);

			short tileType = (short) ((tileData >> 24) & 0xFF);
			short y = (short) ((tileData >> 12) & 0x03FF);
			short x = (short) (tileData & 0x03FF);

			LVLTile tile = new LVLTile(x, y, tileType);
			Tiles[x][y] = tile;
		}
		Log.i(TAG, "Completed loading LVL Tiles");

	}

}
