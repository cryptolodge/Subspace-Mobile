package com.subspace.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.subspace.network.Util;

import android.content.Context;

public class News extends ZoneFile {
	

	public News(Context context, String zoneName)
	{
		super(context,zoneName,"news.txt");		
	}
	public String getDocument()
	{
		return Util.GetString(Data);		
	}
}
