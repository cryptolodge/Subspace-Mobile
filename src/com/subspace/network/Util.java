/*
SubspaceMobile - A subspace/continuum client for mobile phones
Copyright (C) 2010 Kingsley Masters
Email: kshade2001 at users.sourceforge.net

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


REVISIONS:
 */


package com.subspace.network;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import org.apache.hadoop.util.PureJavaCrc32;



public final class Util {
	
	static Random r = new Random();    
	
	
	
	//static constructor
	static {
	
	}
	  
    
    public static final int GetRandomInt() {
        return r.nextInt();
    }    

    public static int GetTickCount() {
        return (int) (System.currentTimeMillis() / 10);
    }    

    public static String ToHex(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;

    }
        
    public static String ToHex(ByteBuffer bb) {
 	   byte[] data = new byte[bb.limit()];
 	   bb.rewind();
	   bb.get(data); 
	   bb.rewind();
	   return ToHex(data);
    }
    
	public static String GetString(ByteBuffer data) {
		return  GetString(data,0,data.limit()-1);
	}

    
	public static String GetString(ByteBuffer buffer, int index, int length) {		
		return  GetString(buffer, index, length, "ISO-8859-1");
	}
    
    public static String GetString(ByteBuffer buffer, int index, int length, String encoding)
    {
    	try{
    		buffer.position(index);	    	
	    	int realLength = 0;
	    	
	    	//this is so we stop at any "null" characters
	    	while(buffer.get()!=0 && length> realLength)
	    	{
	    		realLength++;
	    	}
	    	if(realLength > 0)
	    	{
	    		byte[] bytearr = new byte[realLength];
	    		buffer.position(index);
	    		buffer.get(bytearr,0,realLength);	    		
	    		return new String(bytearr,encoding);
	    	} else {
	    		return "";	    		
	    	}
    	} catch(UnsupportedEncodingException e)
    	{
    		return "Error Descoding string " + encoding;
    	}
    }
	
	public static int CRC32(byte[] data)
	{		
		PureJavaCrc32 crc32Fast = new PureJavaCrc32();
		crc32Fast.update(data, 0, data.length);
		
		return (int)crc32Fast.getValue();
	}
    
    public static int safeGetInt(ByteBuffer buffer, int index) {
    	int result;
        int length = buffer.limit();
        if (buffer.order() == ByteOrder.LITTLE_ENDIAN) {
            result = (buffer.get(index) & 0xff);
            index++;
            if (index < length) {
                result += ((buffer.get(index) & 0xff) << 8);
                index++;
            }
            if (index < length) {
                result += ((buffer.get(index) & 0xff) << 16);
                index++;
            }
            if (index < length) {
                result += ((buffer.get(index) & 0xff) << 24);
            }
        } else {
            result = (buffer.get(index) << 24);
            index++;
            if (index < length) {
                result += ((buffer.get(++index) & 0xff) << 16);
                index++;
            }
            if (index < length) {
                result += ((buffer.get(++index) & 0xff) << 8);
                index++;
            }
            if (index < length) {
                result += ((buffer.get(++index) & 0xff));
            }
        }
        return result;
    }

    public static void safePutInt(ByteBuffer buffer, int index, int value) {
    	int length = buffer.limit();
        if (buffer.order() == ByteOrder.LITTLE_ENDIAN) {
            buffer.put(index,(byte) value);
            index++;
            if (index < length) {
            	buffer.put(index,(byte) (value >> 8));
                index++;
            }
            if (index < length) {
            	buffer.put(index,(byte) (value >> 16));
                index++;
            }
            if (index < length) {
            	buffer.put(index,(byte) (value >> 24));
                index++;
            }
        } else {
        	buffer.put(index,(byte) (value >> 24));
            index++;
            if (index < length) {
            	buffer.put(index,(byte) (value >> 16));
                index++;
            }
            if (index < length) {
            	buffer.put(index,(byte) (value >> 8));
                index++;
            }
            if (index < length) {
            	buffer.put(index,(byte) value);
                index++;
            }
        }
    }


    public static String GetSafeFilename(String Filename)
    {    
    	return Filename.replaceAll("[^\\w\\.]","_");
    }



}
