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

import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.connection.CommonConnectionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: Jul 15, 2009
 * Time: 1:20:07 PM
 */
public interface ServerConnectionService extends CommonConnectionService {
    boolean hasConnection(UserState userState);

    void createConnection(UserState userState, String startUuid);

    void sendPacket(SimpleBase base, Packet packet);

    boolean sendPacket(UserState userState, Packet packet);

    void sendPacket(Packet packet);

    void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog);

    void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog);

    void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter);

    void sendSyncInfos(Collection<SyncBaseItem> syncItem);

    Collection<OnlineUserDTO> getOnlineConnections();

    void activate();

    void deactivate();

    void onLogout();
}
