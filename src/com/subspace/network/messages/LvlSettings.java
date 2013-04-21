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
import java.nio.ByteOrder;


/* struct WeaponBits  4 bytes 
{
        u32 ShrapnelMax    : 5;
        u32 ShrapnelRate   : 5;
        u32 CloakStatus    : 2;
        u32 StealthStatus  : 2;
        u32 XRadarStatus   : 2;
        u32 AntiWarpStatus : 2;
        u32 InitialGuns    : 2;
        u32 MaxGuns        : 2;
        u32 InitialBombs   : 2;
        u32 MaxBombs       : 2;
        u32 DoubleBarrel   : 1;
        u32 EmpBomb        : 1;
        u32 SeeMines       : 1;
        u32 Unused1        : 3;
};

struct MiscBitfield  2 bytes 
{
        u16 SeeBombLevel   : 2;
        u16 DisableFastShooting : 1;
        u16 Radius         : 8;
        u16 _padding       : 5;
};

struct ShipSettings  144 bytes 
{
        i32 long_set[2];
        i16 short_set[49];
        i8 byte_set[18];
        struct WeaponBits Weapons;
        byte Padding[16];
};


struct ClientSettings
{
        struct  4 bytes 
        {
                u32 type : 8;  0x0F 
                u32 ExactDamage : 1;
                u32 HideFlags : 1;
                u32 NoXRadar : 1;
                u32 SlowFrameRate : 3;
                u32 DisableScreenshot : 1;
                u32 _reserved : 1;
                u32 MaxTimerDrift : 3;
                u32 DisableBallThroughWalls : 1;
                u32 DisableBallKilling : 1;
                u32 _padding : 11;
        } bit_set;
        struct ShipSettings ships[8];
        i32 long_set[20];
        struct  4 bytes 
        {
                u32 x : 10;
                u32 y : 10;
                u32 r : 9;
                u32 pad : 3;
        } spawn_pos[4];
        i16 short_set[58];
        i8 byte_set[32];
        u8 prizeweight_set[28];
};
 
	
	*/
public class LvlSettings {
	
	byte[] _raw;
	
	int type;
	int ExactDamage;
	int HideFlags;
	int NoXRadar;
	int SlowFrameRate;
	int DisableScreenshot;
	int _reserved;
	int MaxTimerDrift;
	int DisableBallThroughWalls;
	int DisableBallKilling;
	int _padding;
	
	ShipSetting[] ShipSettings = new ShipSetting[8];
	
	
	public LvlSettings(ByteBuffer data) {
		_raw = new byte[data.limit()];
		//data.position(1);
		data.get(_raw, 0, _raw.length);
	}
	
	public int CheckSum(int checksumKey) {
		ByteBuffer settingsBuffer = ByteBuffer.wrap(_raw);
		settingsBuffer.order(ByteOrder.LITTLE_ENDIAN);		
		long checksum = 0;
		
		for(int i = 0; i < 0x165; i++)
			checksum += settingsBuffer.getInt(i*4) ^ checksumKey & 0xffffffff;
		return (int)checksum;
		
	}

}
