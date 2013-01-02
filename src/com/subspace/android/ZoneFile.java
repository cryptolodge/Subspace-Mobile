package com.subspace.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.subspace.network.Util;

public abstract class ZoneFile {
	
	protected static final String TAG = "Subspace";
	
	public boolean Exists;
	public final String Filename;
	public byte[] RawData;
	public int CRC32;
	
	protected Context context;
		
	public ZoneFile(Context context, String zoneName, String filename)
	{
		this.context = context;
		Exists = false;
		
		Filename = Util.GetSafeFilename(zoneName + "_" + filename);
		
		File file = context.getFileStreamPath(Filename);		
		Exists = file.exists();
		if(Exists)
		{
			Reload();
		}
	}
		
	
	public void Save(byte[] data)
	{
		Log.i(TAG,"Writing " + Filename + " " + data.length + " bytes");
		RawData = data;
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(Filename, Context.MODE_PRIVATE);		
			fos.write(RawData);
			fos.close();
		} catch (IOException e) {
			Log.e(TAG,Log.getStackTraceString(e));
		}		
		UpdateCRC();
	}
	
	public void Reload()
	{
		try {
			Log.i(TAG,"Loading " + Filename);
			//get file length first
			File file = context.getFileStreamPath(Filename);
			RawData = new byte[(int) file.length()];
			//read EVERYTHING (this code probably sucks and should be done properly..nvm)
			FileInputStream fis = context.openFileInput(Filename);
			fis.read(RawData);		
			fis.close();			
		} catch (IOException e) {
			Log.e(TAG,Log.getStackTraceString(e));
		}
		UpdateCRC();
	}

	private void UpdateCRC() {
		if(RawData!=null)
		{
			CRC32 = Util.CRC32(RawData);
		}
	}
	
	
}
