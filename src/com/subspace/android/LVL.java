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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.subspace.redemption.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class LVL extends ZoneFile {

	public static short BMPHeader = 0x4D42;

	public static final int FLAG = 170;
	public static final int SAFETY = 171;
	public static final int GOAL = 172;

	// Large objects

	public static final int SMALL_ASTEROID1 = 216;
	public static final int LARGE_ASTEROID = 217;
	public static final int SMALL_ASTEROID2 = 218;
	public static final int STATION = 219;
	public static final int WORMHOLE = 220;

	public static final int FIRST_FLYUNDER = 176;
	public static final int LAST_FLYUNDER = 190;

	BitmapFactory.Options opts = new BitmapFactory.Options();

	public Bitmap Tileset;

	public LVLTile[][] Tiles;

	Paint paint = new Paint();
	Rect r = new Rect();

	Sprite smallAsteroid1;
	Sprite largeAsteroid;
	Sprite smallAsteroid2;

	public LVL(Context context, String zoneName, String filename) {
		super(context, zoneName, filename);

		// prevent scaling
		opts.inScaled = false;

		smallAsteroid1 = new Sprite(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.over1, opts), 16, 16);

		largeAsteroid = new Sprite(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.over2, opts), 32, 32);

		smallAsteroid2 = new Sprite(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.over3, opts), 16, 16);

		paint.setColor(Color.CYAN);
	}

	public synchronized int CheckSum(long key) {
		int savekey = (int) key;

		if (Tiles != null) {
			for (int y = savekey % 32; y < 1024; y += 32)
				for (int x = savekey % 31; x < 1024; x += 31) {
					byte tile = 0;
					LVLTile lvltile = Tiles[x][y];
					if (lvltile != null) {
						tile = (byte) Tiles[x][y].Type;
						if ((tile >= LVLTile.TILE_START && tile <= LVLTile.TILE_END)
								|| tile == LVLTile.SAFETY) {
							key += savekey ^ tile;
						}
					}
				}
		}
		return (int) key;
	}

	@Override
	public synchronized void AfterLoad(LittleEndianDataInputStream inputStream)
			throws IOException {
		Tiles = new LVLTile[1024][1024];
		// process file
		inputStream.mark(500);
		short bfType = inputStream.readShort(); // == 0x4D42 ("BM")
		int bfSize = inputStream.readInt(); // the total size of the file, which
											// is // also
		// the offset of the tile data
		short bfReserved1 = inputStream.readShort();// previously reserved, but
													// we're going to use this
													// for our format
		short bfReserved2 = inputStream.readShort(); // this one is still
														// unused,should be 0
		int bfOffBits = inputStream.readInt(); // the offset to the bitmap data

		
		int tileOffset = 0;

		Log.i(TAG, "Loading LVL");

		// embedded bitmap
		if (bfType == 0x4D42) {
			Log.i(TAG, "Loading LVL tileset");
			//rewind to start
			inputStream.reset();
			//read in entire bitmap
			tileOffset = bfSize;
			byte[] bitmapBuffer = new byte[bfSize];
			inputStream.readFully(bitmapBuffer);
			// read in
			Tileset = BitmapFactory.decodeByteArray(bitmapBuffer, 0, bfSize);
		} else {
			Log.i(TAG, "Using Default Tileset");
			Tileset = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.tiles, opts);
		}

		// now load tiles
		// might need to rethink this to improve performance
		Log.i(TAG, "Loading LVL Tiles");

		while (true) {
			try {
				int intstruct = inputStream.readInt();
				int tileType = (int) ((intstruct >> 24) & 0xFF);
				int y = (int) ((intstruct >> 12) & 0x03FF); // get position in
															// pixels
				int x = (int) (intstruct & 0x03FF); // get position in pixels

				LVLTile tile = new LVLTile((short) x, (short) y,(short) tileType);
				Tiles[x][y] = tile;
			} catch (EOFException eofE) {
				break;
			}
		}
		Log.i(TAG, "Completed loading LVL Tiles");

	}

	public void Draw(Canvas canvas, Rect clipRectangle) {

		int tileOffsetLeft = EnsureWithinBounds(clipRectangle.left / 16);
		int tileOffsetTop = EnsureWithinBounds(clipRectangle.top / 16);
		int tileOffsetRight = EnsureWithinBounds(clipRectangle.right / 16);
		int tileOffsetBottom = EnsureWithinBounds(clipRectangle.bottom / 16);

		for (int i = tileOffsetLeft; i < tileOffsetRight; i++) {
			for (int j = tileOffsetTop; j < tileOffsetBottom; j++) {
				if (Tiles[i][j] != null) {
					int type = Tiles[i][j].Type;
					r.set(i * 16 - clipRectangle.left, j * 16
							- clipRectangle.top, i * 16 + 16
							- clipRectangle.left, j * 16 + 16
							- clipRectangle.top);

					// this is for standard tiles

					if (type == SMALL_ASTEROID1) {
						smallAsteroid1.Draw(canvas, r, null);
						// render small asteroid sprite
					} else if (type == LARGE_ASTEROID) {
						r.set(i * 16 - clipRectangle.left, j * 16
								- clipRectangle.top, i * 16 + 32
								- clipRectangle.left, j * 16 + 32
								- clipRectangle.top);
						largeAsteroid.Draw(canvas, r, null);
					} else if (type == SMALL_ASTEROID2) {
						smallAsteroid2.Draw(canvas, r, null);
					} else {

						canvas.drawBitmap(Tileset, GetTile(type), r, null);
					}

				}
			}
		}

	}

	private Rect GetTile(int type) {
		Rect result = new Rect();
		type = type - 1;
		int tileY = (type / 19);
		int tileX = type - (19 * tileY);
		result.set(tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);
		return result;
	}

	private int EnsureWithinBounds(int i) {
		int result = 0;
		if (i > 0 && i < 1024) {
			result = i;
		}
		return result;
	}

}
