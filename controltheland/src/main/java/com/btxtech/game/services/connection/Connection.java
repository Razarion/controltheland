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

import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.base.Base;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:16:32 PM
 */
public class Connection implements Serializable {
    private Base base;
    private final HashSet<SyncItem> pendingSyncItem = new HashSet<SyncItem>();
    private final HashSet<Packet> pendingPackets = new HashSet<Packet>();
    private int tickCount = 0;
    private String sessionId;
    private int noTickCount = 0;
    private boolean closed = false;
    //private Log log = LogFactory.getLog(Connection.class);

    public Connection(String sessionId) {
        this.sessionId = sessionId;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Collection<Packet> getAndRemovePendingPackets() {
        tickCount++;
        HashSet<Packet> packets;

        synchronized (pendingPackets) {
            packets = new HashSet<Packet>(pendingPackets);
            pendingPackets.clear();
        }
        synchronized (pendingSyncItem) {
            if (pendingSyncItem.isEmpty()) {
                return packets;
            }
            for (SyncItem syncItem : pendingSyncItem) {
                SyncItemInfo syncInfo = syncItem.getSyncInfo();
                //log.debug("Send to client: " + base.getName() + " | " + syncInfo);
                packets.add(syncInfo);
            }
            pendingSyncItem.clear();
        }
        return packets;
    }

    public void sendBaseSyncItem(SyncItem syncItem) {
        synchronized (pendingSyncItem) {
            pendingSyncItem.add(syncItem);
        }
    }

    public void sendPacket(Packet packet) {
        synchronized (pendingPackets) {
            pendingPackets.remove(packet);
            pendingPackets.add(packet);
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

    public void setClosed() {
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
