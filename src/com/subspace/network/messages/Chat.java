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

}
