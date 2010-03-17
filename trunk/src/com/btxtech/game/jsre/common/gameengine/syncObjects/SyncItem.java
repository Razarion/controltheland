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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 14:17:17
 */
public abstract class SyncItem {
    private Id id;
    private Services services;
    // Own states
    private ItemType itemType;
    private Index position;
    // Sync states
    private final ArrayList<SyncItemListener> syncItemListeners = new ArrayList<SyncItemListener>();

    public SyncItem(Id id, Index position, ItemType itemType, Services services) {
        this.id = id;
        this.position = position;
        this.itemType = itemType;
        this.services = services;
    }

    public Id getId() {
        return id;
    }

    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        setPosition(syncItemInfo.getPosition());
        id.synchronize(syncItemInfo.getId());
        checkItemType(syncItemInfo.getItemTypeId());
    }

    public SyncItemInfo getSyncInfo() {
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setId(id);
        syncItemInfo.setPosition(position);
        syncItemInfo.setItemTypeId(itemType.getId());
        syncItemInfo.setAlive(isAlive());
        return syncItemInfo;
    }

    private void checkItemType(int itemTypeId) {
        if (itemType.getId() != itemTypeId) {
            throw new IllegalArgumentException(this + " item type do not match client: " + itemType.getId() + " sync: " + itemTypeId);
        }
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
        fireItemChanged(SyncItemListener.Change.POSITION);
    }

    public Services getServices() {
        return services;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void addSyncItemListener(SyncItemListener syncItemListener) {
        synchronized (syncItemListeners) {
            syncItemListeners.add(syncItemListener);
        }
    }

    public void removeSyncItemListener(SyncItemListener syncItemListener) {
        synchronized (syncItemListeners) {
            syncItemListeners.remove(syncItemListener);
        }
    }

    public void fireItemChanged(SyncItemListener.Change change) {
        synchronized (syncItemListeners) {
            for (SyncItemListener syncItemListener : syncItemListeners) {
                syncItemListener.onItemChanged(change, this);
            }
        }
    }

    public abstract boolean isAlive();

    public boolean hasSyncTurnable() {
        return false;
    }

    public SyncTurnable getSyncTurnable() {
        return null;
    }

    public Rectangle getRectangle() {
        return new Rectangle(position.getX() - itemType.getWidth() / 2,
                position.getY() - itemType.getHeight() / 2,
                itemType.getWidth(),
                itemType.getHeight());
    }

    @Override
    public String toString() {
        return "SyncItem: " + id + " " + itemType + " pos: " + position;
    }
}
