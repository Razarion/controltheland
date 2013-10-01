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

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.user.UserState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * User: beat Date: May 31, 2009 Time: 8:16:32 PM
 */
public class Connection implements Serializable {
    private UserState userState;
    private final HashSet<SyncItem> pendingSyncItem = new HashSet<>();
    private final List<Packet> pendingPackets = new ArrayList<>();
    private int tickCount = 0;
    private String sessionId;
    private int noTickCount = 0;
    private NoConnectionException.Type closedReason;
    private String startUuid;
    private List<Packet> sentPackager = new ArrayList<>();
    private ServerPlanetServices serverPlanetServices;
    private ServerGlobalServices serverGlobalServices;
    private ChatMessageFilter chatMessageFilter;


    public Connection(UserState userState, ServerPlanetServices serverPlanetServices, ServerGlobalServices serverGlobalServices, String startUuid) {
        this.userState = userState;
        this.serverPlanetServices = serverPlanetServices;
        this.serverGlobalServices = serverGlobalServices;
        this.sessionId = serverGlobalServices.getServerGlobalConnectionService().getSession().getSessionId();
        this.startUuid = startUuid;
        chatMessageFilter = ChatMessageFilter.GLOBAL;
    }

    public UserState getUserState() {
        return userState;
    }

    public String getStartUuid() {
        return startUuid;
    }

    public ServerPlanetServices getServerPlanetServices() {
        return serverPlanetServices;
    }

    public List<Packet> getAndRemovePendingPackets(boolean resendLast) {
        tickCount++;
        if (!resendLast) {
            sentPackager.clear();
        }

        synchronized (pendingPackets) {
            sentPackager.addAll(pendingPackets);
            pendingPackets.clear();
        }
        synchronized (pendingSyncItem) {
            if (pendingSyncItem.isEmpty()) {
                return sentPackager;
            }
            for (SyncItem syncItem : pendingSyncItem) {
                SyncItemInfo syncInfo = syncItem.getSyncInfo();
                // log.debug("Send to client: " + base.getName() + " | " +
                // syncInfo);
                sentPackager.add(syncInfo);
            }
            pendingSyncItem.clear();
        }
        return sentPackager;
    }

    public void sendBaseSyncItem(SyncItem syncItem) {
        synchronized (pendingSyncItem) {
            pendingSyncItem.add(syncItem);
        }
    }

    public void sendPacket(Packet packet) {
        Packet convertedPackage = serverGlobalServices.getServerGlobalConnectionService().getMessageIdPacketQueue().convertPacketIfNecessary(packet, chatMessageFilter, userState);
        if (convertedPackage == null) {
            return;
        }

        synchronized (pendingPackets) {
            pendingPackets.add(convertedPackage);
        }
    }

    public int resetAndGetTickCount() {
        if (tickCount == 0) {
            noTickCount++;
        } else {
            noTickCount = 0;
        }
        int tmp = tickCount;
        tickCount = 0;
        return tmp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getNoTickCount() {
        return noTickCount;
    }

    public void setClosed(NoConnectionException.Type closedReason) {
        this.closedReason = closedReason;
    }

    public boolean isClosed() {
        return closedReason != null;
    }

    public NoConnectionException.Type getClosedReason() {
        return closedReason;
    }

    public void setChatMessageFilter(ChatMessageFilter chatMessageFilter) {
        this.chatMessageFilter = chatMessageFilter;
    }
}
