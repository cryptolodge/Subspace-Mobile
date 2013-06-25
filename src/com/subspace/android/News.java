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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.subspace.network.Util;

import android.content.Context;

public class News extends ZoneFile {
	
	String newsContent;

	public News(Context context, String zoneName)
	{
		super(context,zoneName,"news.txt");		
	}
	public String getDocument()
	{
		return newsContent; //Util.GetString(Data);		
	}
	
	@Override
	public synchronized void AfterLoad(LittleEndianDataInputStream inputStream) throws IOException {
		newsContent = "";
		StringBuffer inputLine = new StringBuffer();
            String tmp; 
            while ((tmp = inputStream.readLine()) != null) {
                inputLine.append(tmp);
            }
       newsContent = inputLine.toString();
	}
	
	
}
