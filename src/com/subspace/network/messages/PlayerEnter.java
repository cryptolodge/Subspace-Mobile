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

package com.subspace.network.messages;

import java.nio.ByteBuffer;

import com.subspace.network.Util;

public class PlayerEnter {
	
	/*
    0           1           Type Byte 0x03
    1           1           Ship Type
    2           1           ? Unknown (0x00)
    3           20          Name
    23          20          Squad
    43          4           Flag Points
    47          4           Kill Points
    51          2           User ID
    53          2           Wins
    55          2           Losses
    57          6           Audio and Other Stuff*/
	
	public final byte ShipType;
	public final byte Unknown; //todo
	public final String Name;
	public final String Squad;	
	public final int FlagPoints;
	public final int KillPoints;
	public final short Id;
	public final short Wins;
	public final short Losses;
	public final short Audio; //todo fix me
	public final int OtherStuff; //todo fix me
	
	public PlayerEnter(ByteBuffer bb)
	{
		ShipType = bb.get(1);
		Unknown = bb.get(2);
		Name = Util.GetString(bb, 3,20);
		Squad = Util.GetString(bb, 23,20);
		FlagPoints = bb.getInt(43);
		KillPoints = bb.getInt(47);
		Id = bb.getShort(51);
		Wins = bb.getShort(53);
		Losses = bb.getShort(55);
		Audio = bb.getShort(57);
		OtherStuff = bb.getInt(59);
	}
}
