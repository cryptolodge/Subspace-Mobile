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

package com.subspace.network;

import com.subspace.android.LVL;
import com.subspace.android.News;
import com.subspace.network.messages.*;

public interface IGameCallback {
	 void ChatMessageReceived(Chat message) ;
	 void NowInGameRecieved();
	 void PlayerIdRecieved(int id);
	 void MapInformationRecieved(MapInformation mapInformation);
	 void NewsReceieved(News news);
	void MapReceived(LVL currentLVL);
	void ConsoleMessageReceived(String consoleMessage);
	void PlayerEntering(PlayerEnter playerEntering);
	void PlayerLeaving(PlayerLeave playerLeaving);
	void LvlSettingsReceived(LvlSettings arenaSettings);
}
