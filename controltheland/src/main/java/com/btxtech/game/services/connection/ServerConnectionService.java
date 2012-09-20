/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.planet.Base;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:07 PM
 */
public interface ServerConnectionService extends com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService {
    void clientLog(String message, Date date);

    boolean hasConnection();

    boolean hasConnection(SimpleBase simpleBase);

    Connection getConnection(String startUuid) throws NoConnectionException;

    void createConnection(Base base, String startUuid);

    void closeConnection(SimpleBase simpleBase, NoConnectionException.Type closedReason);

    void sendPacket(SimpleBase base, Packet packet);

    void sendPacket(Packet packet);

    void sendSyncInfos(Collection<SyncBaseItem> syncItem);

    void sendChatMessage(ChatMessage chatMessage);

    List<ChatMessage> pollChatMessages(Integer lastMessageId);

    Collection<SimpleBase> getOnlineBases();
}
