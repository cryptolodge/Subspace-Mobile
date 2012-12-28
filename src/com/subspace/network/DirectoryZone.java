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

/**
 *
 * @author Kingsley
 */
public class DirectoryZone {


    public final String IPAddress;
    public final int Port;
    public final short PlayerCount;
    public final boolean Billing;
    public final byte HopCount;
    public final int Version;
    public final String Name;
    public final String Description;

    public DirectoryZone(String ip, int port, short playercount, boolean billing, byte hopcount, int version, String name, String description)
    {
        this.Name = name.trim();
        this.Description = description.trim();
        this.IPAddress = ip;
        this.Port = port;
        this.PlayerCount = playercount;
        this.Billing = billing;
        this.HopCount = hopcount;
        this.Version = version;
    }
    public String toString() {
        return Name + " " + IPAddress + ":" + Port + " " + PlayerCount + " > " + this.Description;
    }
}
