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

package com.subspace.redemption.dataobjects;

import android.database.Cursor;

import com.subspace.network.DirectoryZone;

public class Zone {

	public int Id;
	public String Name;
	public String Ip;
	public int Port;
	public String Description;
	public int Population;
	public int Ping;
	public int IsCustom;
	
	public Zone(DirectoryZone dz) {
		Name = dz.Name;
		Ip  = dz.IPAddress;
		Port = dz.Port;
		Description = dz.Description;
		Population = dz.PlayerCount;
		IsCustom = 0;
	}

	public Zone() {
		// TODO Auto-generated constructor stub
	}

	public Zone(Cursor cursor) {
        this.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
        this.Name = cursor.getString(cursor.getColumnIndex("name"));
        this.Description = cursor.getString(cursor.getColumnIndex("description"));
        this.Ip = cursor.getString(cursor.getColumnIndex("ip"));
        this.Port = Integer.parseInt(cursor.getString(cursor.getColumnIndex("port")));
        this.IsCustom = Integer.parseInt(cursor.getString(cursor.getColumnIndex("iscustom")));
	}
}
