package com.subspace.network;

import com.subspace.network.messages.*;

public interface IGameCallback {
	 void ChatMessageReceived(Chat message) ;
	 void NowInGameRecieved();
	 void PlayerIdRecieved(int id);
	 void MapInformationRecieved(MapInformation mapInformation);
}