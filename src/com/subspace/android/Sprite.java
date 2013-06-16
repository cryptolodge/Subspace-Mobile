/*  Subspace Mobile - A Android Subspace Client
    Copyright (C) 2013 Kingsley Masters. All Rights Reserved.
    
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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Sprite {
	
	Bitmap bitmap;
	int frames;
	int currentFrame = 0;
	Rect frameRect = new Rect();
	int frameSizeX;
	int frameSizeY;
	
	public Sprite(Bitmap bitmap, int frameSizeX, int frameSizeY)
	{
		this.bitmap = bitmap;
		this.frameSizeX= frameSizeX;
		this.frameSizeY= frameSizeY;
		
		frames = 
				bitmap.getWidth() / frameSizeX
				+
				bitmap.getHeight() / frameSizeY; 
		
		frameRect.set(0, 0, frameSizeX, frameSizeY);		
	}
	
	public void Draw(Canvas canvas, Rect dest, Paint paint)
	{
		canvas.drawBitmap(bitmap, frameRect, dest, paint);
		nextFrame();
	}
	
	public void Draw(Canvas canvas, Rect dest, Paint paint, int frame)
	{
		SetFrame(frame);
		Draw(canvas,dest,paint);
	}
	
	public void SetFrame(int frame)
	{	
		int tileY = (frame / (bitmap.getWidth() / frameSizeX));
        int tileX = frame - ((bitmap.getWidth() / frameSizeX) * tileY);        
        frameRect.set(
        		tileX*frameSizeX, 
        		tileY*frameSizeY, 
        		tileX*frameSizeX+frameSizeX, 
        		tileY*frameSizeY+frameSizeY);
	}	
	
	public void nextFrame()
	{
		currentFrame++;	
		if(currentFrame>frames)
		{
			currentFrame = 0;
		}
		SetFrame(currentFrame);
	}
}
