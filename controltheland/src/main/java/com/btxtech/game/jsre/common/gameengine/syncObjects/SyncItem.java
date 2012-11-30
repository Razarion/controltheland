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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

import java.util.ArrayList;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 14:17:17
 */
public abstract class SyncItem {
    private Id id;
    private GlobalServices globalServices;
    private PlanetServices planetServices;
    // Own states
    private ItemType itemType;
    private SyncItemArea syncItemArea;
    // Sync states
    private final ArrayList<SyncItemListener> syncItemListeners = new ArrayList<SyncItemListener>();
    private boolean explode = false;


    public SyncItem(Id id, Index position, ItemType itemType, GlobalServices globalServices, PlanetServices planetServices) {
        this.id = id;
        this.itemType = itemType;
        this.globalServices = globalServices;
        this.planetServices = planetServices;
        syncItemArea = new SyncItemArea(this);
        syncItemArea.setPosition(position);
        syncItemArea.correctPosition();
    }

    public Id getId() {
        return id;
    }

    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        syncItemArea.synchronize(syncItemInfo);
    }

    public SyncItemInfo getSyncInfo() {
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setId(id);
        syncItemArea.fillSyncItemInfo(syncItemInfo);
        syncItemInfo.setItemTypeId(itemType.getId());
        syncItemInfo.setAlive(isAlive());
        syncItemInfo.setExplode(explode);
        return syncItemInfo;
    }

    public GlobalServices getGlobalServices() {
        return globalServices;
    }

    public PlanetServices getPlanetServices() {
        return planetServices;
    }

    public ItemType getItemType() {
        return itemType;
    }

    protected void setItemType(ItemType itemType) {
        this.itemType = itemType;
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
                try {
                    syncItemListener.onItemChanged(change, this);
                } catch (Throwable t) {
                    ClientExceptionHandler.handleException("Unable to fire change for sync item: " + this, t);
                }
            }
        }
    }

    public abstract boolean isAlive();

    public SyncItemArea getSyncItemArea() {
        return syncItemArea;
    }

    public TerrainType getTerrainType() {
        return itemType.getTerrainType();
    }

    @Override
    public String toString() {
        return "SyncItem: " + id + " " + itemType + " " + syncItemArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SyncItem)) return false;

        SyncItem syncItem = (SyncItem) o;

        return id.equals(syncItem.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setExplode(boolean explode) {
        this.explode = explode;
    }
}
