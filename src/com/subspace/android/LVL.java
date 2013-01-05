package com.subspace.android;

import android.content.Context;

public class LVL extends ZoneFile {

	public static short BMPHeader = 0x4D42;
	
	public LVLTile[][] Tiles = new LVLTile[1024][1024];
	
	public LVL(Context context, String zoneName, String filename)
	{
		super(context,zoneName,filename);
		
		if(Exists)
		{
			
			//process file
			short bfType = Data.getShort(0); // == 0x4D42 ("BM")
			int bfSize = Data.getInt(2); // the total size of the file, which is also
            // the offset of the tile data
			short bfReserved1 = Data.getShort(6);// previously reserved, but we're going to
            // use this for our format
			short bfReserved2 = Data.getShort(8); // this one is still unused, should be 0
			int bfOffBits = Data.getInt(10); // the offset to the bitmap data
			
			int tileOffset = 0;
			
			
			//embedded bitmap
			if(bfType==0x4D42)
			{
				tileOffset = bfSize;
				//read in 
				
			} else {
				//use resource
			}
			
		        
		}
	}

	
}
