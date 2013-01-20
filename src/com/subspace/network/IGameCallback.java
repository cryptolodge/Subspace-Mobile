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
}
