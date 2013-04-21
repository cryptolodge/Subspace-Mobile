package com.subspace.snrrrub;

import java.nio.ByteBuffer;

//Thanks to Snrrrub for providing these Checksum routines
//Taken from the brilliant SubChat Project and coverted into java (ick)
public  class Checksum {

	public static int EXEChecksum(int key)
	{
		int part, csum = 0;

		part = 0xc98ed41f;
		part += 0x3e1bc | key;
		part ^= 0x42435942 ^ key;
		part += 0x1d895300 | key;
		part ^= 0x6b5c4032 ^ key;
		part += 0x467e44 | key;
		part ^= 0x516c7eda ^ key;
		part += 0x8b0c708b | key;
		part ^= 0x6b3e3429 ^ key;
		part += 0x560674c9 | key;
		part ^= 0xf4e6b721 ^ key;
		part += 0xe90cc483 | key;
		part ^= 0x80ece15a ^ key;
		part += 0x728bce33 | key;
		part ^= 0x1fc5d1e6 ^ key;
		part += 0x8b0c518b | key;
		part ^= 0x24f1a96e ^ key;
		part += 0x30ae0c1 | key;
		part ^= 0x8858741b ^ key;
		csum += part;

		part = 0x9c15857d;
		part += 0x424448b | key;
		part ^= 0xcd0455ee ^ key;
		part += 0x727 | key;
		part ^= 0x8d7f29cd ^ key;
		csum += part;

		part = 0x824b9278;
		part += 0x6590 | key;
		part ^= 0x8e16169a ^ key;
		part += 0x8b524914 | key;
		part ^= 0x82dce03a ^ key;
		part += 0xfa83d733 | key;
		part ^= 0xb0955349 ^ key;
		part += 0xe8000003 | key;
		part ^= 0x7cfe3604 ^ key;
		csum += part;

		part = 0xe3f8d2af;
		part += 0x2de85024 | key;
		part ^= 0xbed0296b ^ key;
		part += 0x587501f8 | key;
		part ^= 0xada70f65 ^ key;
		csum += part;

		part = 0xcb54d8a0;
		part += 0xf000001 | key;
		part ^= 0x330f19ff ^ key;
		part += 0x909090c3 | key;
		part ^= 0xd20f9f9f ^ key;
		part += 0x53004add | key;
		part ^= 0x5d81256b ^ key;
		part += 0x8b004b65 | key;
		part ^= 0xa5312749 ^ key;
		part += 0xb8004b67 | key;
		part ^= 0x8adf8fb1 ^ key;
		part += 0x8901e283 | key;
		part ^= 0x8ec94507 ^ key;
		part += 0x89d23300 | key;
		part ^= 0x1ff8e1dc ^ key;
		part += 0x108a004a | key;
		part ^= 0xc73d6304 ^ key;
		part += 0x43d2d3 | key;
		part ^= 0x6f78e4ff ^ key;
		csum += part;

		part = 0x45c23f9;
		part += 0x47d86097 | key;
		part ^= 0x7cb588bd ^ key;
		part += 0x9286 | key;
		part ^= 0x21d700f8 ^ key;
		part += 0xdf8e0fd9 | key;
		part ^= 0x42796c9e ^ key;
		part += 0x8b000003 | key;
		part ^= 0x3ad32a21 ^ key;
		csum += part;

		part = 0xb229a3d0;
		part += 0x47d708 | key;
		part ^= 0x10b0a91 ^ key;
		csum += part;

		part = 0x466e55a7;
		part += 0xc7880d8b | key;
		part ^= 0x44ce7067 ^ key;
		part += 0xe4 | key;
		part ^= 0x923a6d44 ^ key;
		part += 0x640047d6 | key;
		part ^= 0xa62d606c ^ key;
		part += 0x2bd1f7ae | key;
		part ^= 0x2f5621fb ^ key;
		part += 0x8b0f74ff | key;
		part ^= 0x2928b332;
		csum += part;

		part = 0x62cf369a;
		csum += part;

		return csum;
	}
	
	public static int MapChecksum(int key, ByteBuffer data)
	{
		int EAX, ECX, ESI, EDX;
		int HighBit = 0;
		int cnt;
		int original_key = key;

		if(data == null)
			return 0;
		
		data.rewind();

		int index = 0;
		
		
		EAX = key;

		if((EAX & 0x80000000) != 0)
		{
			HighBit = 0xFFFFFFFF;
		}

		EAX ^= HighBit;
		EAX -= HighBit;
		EAX &= 0x1F;
		EAX ^= HighBit;
		ECX = EAX - HighBit;
		if(ECX >= 0x400)
		{
			return key;
		}

		EDX = key % 0x1F;

		ESI = (ECX << 0x0A) + data.get(index) + EDX;
		EAX = 0x400 - EDX;
		cnt = (0x41F - ECX) >> 5;
		index  = EAX;

		for(; cnt > 0; cnt--, ESI += 0x8000)
		{
			EDX = EAX + ESI;
			ECX = ESI;
			if(!(ESI < EDX))
				continue;
			while(ECX < EDX)
			{
				byte byt = data.get(ECX);
				if((byt < 0xA1 || byt == 0xAB) && byt != 0)
					key += original_key ^ byt;
				ECX += 0x1F;
			}
			EAX = data.getInt(index);
		}
		return key;
		
	}
	
	public static int SettingsChecksum(int key,ByteBuffer settingsBuffer)
	{	
		int i;
		long checksum = 0;
		
		for(i = 0; i < 0x165; i++)
			checksum += settingsBuffer.getInt(i*4) ^ key & 0xffffffff;
		return (int)checksum;
	}
}
