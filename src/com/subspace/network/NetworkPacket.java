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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Kingsley
 */
public class NetworkPacket {
    //core

    public static final String CHARSET = "ISO-8859-1";
    public static final byte CORE = 0x00;
    public static final byte CORE_CONNECTION = 0x01;
    public static final byte CORE_CONNECTIONACK = 0x02;
    public static final byte CORE_RELIABLE = 0x03;
    public static final byte CORE_RELIABLEACK = 0x04;
    public static final byte CORE_SYNC = 0x05;
    public static final byte CORE_SYNCACK = 0x06;
    public static final byte CORE_DISCONNECT = 0x07;
    public static final byte CORE_CHUNK = 0x08;
    public static final byte CORE_CHUNKEND = 0x09;
    public static final byte CORE_STREAM = 0x0A;
    public static final byte CORE_STREAMCANCEL = 0x0B;
    public static final byte CORE_STREAMCANCELACK = 0x0C;
    public static final byte CORE_CLUSTER = 0x0E;
    public static final byte CORE_CTMCONNECTION = 0x10;
    public static final byte CORE_CTMCONNECTIONACK = 0x11;
    //directory
    public static final byte DIRECTORY_REQUEST = 0x01;
    public static final byte DIRECTORY_RESPONSE = 0x01;
    //game c2s
    public static final byte C2S_ARENALOGIN = 0x01;
    public static final byte C2S_PASSWORD = 0x09;
    public static final byte C2S_MAPREQUEST = 0x0C;
    //game s2c
    public static final byte S2C_MY_UID = 0x01;
    public static final byte S2C_NOW_IN_GAME = 0x02;
    public static final byte S2C_PlayerEntering = 0x03;
    public static final byte S2C_PlayerLeaving = 0x04;
    public static final byte S2C_LargePosition = 0x05;
    public static final byte S2C_PlayerDeath = 0x06;
    public static final byte S2C_ChatMessage = 0x07;
    public static final byte S2C_PlayerGotPrize = 0x08;
    public static final byte S2C_PlayerScoreUpdate = 0x09;
    public static final byte S2C_PASSWORDACK = 0x0A;
    public static final byte S2C_SoccerGoal = 0x0B;
     public static final byte S2C_PlayerVoice = 0x0C;
     public static final byte S2C_PlayerChangedFreq = 0x0D;
     public static final byte S2C_TurretCreate = 0x0E;
     public static final byte S2C_ArenaSettings = 0x0F;
     public static final byte S2C_FileTransfer = 0x10;
     public static final byte S2C_FlagPosition = 0x12;
     public static final byte S2C_FlagClaim= 0x13;
     public static final byte S2C_FlagVictory= 0x14;
     public static final byte S2C_DestroyTurret= 0x15;
     public static final byte S2C_FlagDrop= 0x16;
     public static final byte S2C_ChecksumRecv= 0x18;
     public static final byte S2C_FileRequest= 0x19;
     public static final byte S2C_ScoreReset= 0x1A;
     public static final byte S2C_YourShipReset= 0x1B;
     public static final byte S2C_ShipSpec= 0x1C;
     public static final byte S2C_ShipAndFreqChange= 0x1D;
     public static final byte S2C_YourBannerChanged= 0x1E;
     public static final byte S2C_PlayerBannerChanged= 0x1F;
     public static final byte S2C_CollectedPrize= 0x20;
     public static final byte S2C_BrickDropped= 0x21;
     public static final byte S2C_TurfFlagUpdate= 0x22;
     public static final byte S2C_FlagRewardGranted= 0x23;
     public static final byte S2C_SpeedGameEnded= 0x24;
     public static final byte S2C_ToggleUFO= 0x25;
     public static final byte S2C_KeepAlive= 0x27;
     public static final byte S2C_SmallPosition= 0x28;
     public static final byte S2C_MapInformation= 0x29;
     public static final byte S2C_CompressedMapFile= 0x2A;
     public static final byte S2C_SetYourKotHTime= 0x2B;
     public static final byte S2C_KotHTimerReset= 0x2C;
     public static final byte S2C_PowerBallPosition= 0x2E;
     public static final byte S2C_ArenaListing= 0x2F;
     public static final byte S2C_ZoneBannerAds= 0x30;
     public static final byte S2C_PastLogin= 0x31;
     public static final byte S2C_ChangeShipPosition= 0x32;

    //core packets
    static ByteBuffer CreateCore(int length, byte type) {
        ByteBuffer bb = ByteBuffer.allocate(length + 2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(CORE);
        bb.put(type);
        bb.mark();
        return bb;
    }

    static ByteBuffer CreatePacket(int length, byte type) {
        ByteBuffer bb = ByteBuffer.allocate(length + 1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(type);
        bb.mark();
        return bb;
    }

    public static ByteBuffer CreateConnection(int key, short version) {
        ByteBuffer bb = CreateCore(6, CORE_CONNECTION);
        bb.putInt(key);
        bb.putShort(version);
        return bb;
    }

    public static ByteBuffer CreateDisconnect() {
        return CreateCore(0, CORE_DISCONNECT);
    }

    public static ByteBuffer CreateReliable(int id, ByteBuffer data) {
    	data.rewind(); //move data pointer to start    	
        ByteBuffer bb = CreateCore(data.limit() + 4, CORE_RELIABLE);
        bb.putInt(id);
        bb.put(data);
        return bb;
    }

    public static ByteBuffer CreateReliableAck(int id) {
        ByteBuffer bb = CreateCore(4, CORE_RELIABLEACK);
        bb.putInt(id);
        return bb;
    }

    public static ByteBuffer CreateSync(int packetInCount, int packetoutCount) {
        ByteBuffer bb = CreateCore(14, CORE_SYNC);
        bb.putInt(Util.GetTickCount());
        bb.putInt(packetoutCount);
        bb.putInt(packetInCount);
        return bb;
    }

    public static ByteBuffer CreateSyncResponse(int syncTime) {
        ByteBuffer bb = CreateCore(8, CORE_SYNCACK);
        bb.putInt(syncTime);
        bb.putInt(Util.GetTickCount());
        return bb;
    }

    //directory server packet
    public static ByteBuffer CreateZoneListRequest() {
        ByteBuffer bb = CreatePacket(4, DIRECTORY_REQUEST);
        bb.putShort((short) 0); //min players
        bb.putShort((short) 0); //max players
        return bb;
    }

    //game
    // <editor-fold defaultstate="collapsed" desc="Packet Description">
    /*
    Offset	Length	Description
    0		1		Type byte
    1		1		Boolean: New user *1
    2		32		Name
    34		32		Password
    66		4		Machine ident *2
    70		1		ConnectType *3
    71		2		Timezone bias
    73		2		?
    75		2		Client version *4
    77		4		? memory checksum, Set to = 444
    81		4		? memory checksum, Set to = 555
    85		4		Permission ident
    89		12		?
    
     *1 - 1 = New, 0 = Not New
    
     *2 - Should be Drive Seriel of C (can be random for bots)
    
     *3 - Type 0x00 is a safe bet.
    
     *4 - 0x24 = Ctm, 0x86 = SS
     *  
     */
    // </editor-fold>    
    public static ByteBuffer CreatePassword(boolean newUser, String name, String password, int macId) {
        try {
            int permissionId = (Util.GetTickCount() ^ 0xAAAAAAAA) * 0x5f346d + 0x5abcdef;
            //create buffer
            ByteBuffer bb = CreatePacket(100, C2S_PASSWORD);
            bb.put((byte) (newUser ? 0x01 : 0x00));
            bb.position(2);  bb.put(name.getBytes(CHARSET));
            bb.position(34);  bb.put(password.getBytes(CHARSET));
            bb.putInt(66, macId);
            bb.put(70, (byte) 0x00); //conection type
            bb.putShort(71, (short) 240); //timezone bias (240=est)
            bb.put(73, (byte) 0x00); //unknown
            bb.putShort(75, (short) 0x86); //type = VIE
            bb.putInt(77, 444); //memory checksum
            bb.putInt(81, 555); //memory checksum
            bb.putInt(85, permissionId); //permissionid
            return bb;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" Packet Description ">
    /*
        Offset	Length	Descriptoin
        0		1		Type Byte 0x01
        1		1		Ship type
        2		2		Allow audio?
        4		2		X resolution
        6		2		Y resolution
        8		2		Main arena number *1

        10		16		Arena name (Optional)

         *1 - Set to 0xFFFF for Random Pub,
        or 0xFFFD for Specific Sub (Must provide name).
         */
    // </editor-fold>
    public static ByteBuffer ArenaLogin(byte shiptype, short xres, short yres, String arena) {
        try {
            ByteBuffer bb;
            if(arena!=null) {
                bb = CreatePacket(25, C2S_ARENALOGIN);
            } else {
                bb = CreatePacket(10, C2S_ARENALOGIN);
            }
            bb.put(1, shiptype);
            bb.putShort(2, (short) 0); //disable sound
            bb.putShort(4, xres);
            bb.putShort(6, yres);
            if (arena == null || arena.length() == 0) {
                bb.putShort(8, (short) 0xFFFF);
            } else {
                bb.putShort(8, (short) 0xFFFD);
            }
            if(arena!=null)
                bb.position(10); bb.put(arena.getBytes(CHARSET));
            return bb;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ByteBuffer MapRequest()
    {
         ByteBuffer bb = CreatePacket(1, C2S_MAPREQUEST);
         return bb;
    }
}
