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

import java.nio.ByteBuffer;

import com.subspace.network.Util;

public class Chat {
	
	/*
		Offset	 Length	 Description
		0	 1	 Type Byte 0x07
		1	 1	 Chat Type 1
		2	 1	 Sound Byte
		3	 2	 Originator ID 2
		5	 *	 Chat Message
		1 - Chat types:
		
		0x00 - Message in green text [*arena, *zone, ...]
		0x01 - Public macro
		0x02 - Public message
		0x03 - Team message [// or ']
		0x04 - Player to all members of another team ["Whatever]
		0x05 - Private message [/Whatever or :playername:Whatever]
		0x06 - Red warning message [MODERATOR WARNING: Whatever -Whoever]
		0x07 - Remote private message [(Whoever)> Whatever]
		0x08 - Red server errors, without a name tag (S2C only)
		0x09 - Channel message [;X;Whatever]
		2 - Target player's player ID is 0 for public messages (0x02)
	*/
	
	public final byte Type;
	public final byte Sound;
	public final short PlayerId;
	public final String Message;
	
	public Chat(ByteBuffer data) {
		Type = data.get(1);
		Sound = data.get(2);
		PlayerId = data.getShort(3);
		Message = Util.GetString(data, 5, data.limit() - 6, "ISO-8859-1");
	}
	
		
	public String getFormattedMessage()
	{
		
		
		return "";
	}
	

}
