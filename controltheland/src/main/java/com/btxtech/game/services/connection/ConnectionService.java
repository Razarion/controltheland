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

import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;

import java.util.Collection;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:07 PM
 */
public interface ConnectionService extends com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService {
    void clientLog(String message, Date date);

    boolean hasConnection();

    boolean hasConnection(SimpleBase simpleBase);

    Connection getConnection() throws NoConnectionException;

    void createConnection(Base base);

    void closeConnection();

    void closeConnection(SimpleBase simpleBase);

    void sendPacket(SimpleBase base, Packet packet);

    void sendPacket(Packet packet);

    void sendSyncInfos(Collection<SyncBaseItem> syncItem);

    void sendUserMessage(UserMessage userMessage);

    Collection<SimpleBase> getOnlineBases();
}
