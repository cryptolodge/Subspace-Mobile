package com.subspace.network.messages;

import java.nio.ByteBuffer;

public class ShipSetting {
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
	*/
	public ShipSetting(ByteBuffer byteBuffer)
	{
		
	}
}
