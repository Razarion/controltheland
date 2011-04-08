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

package com.btxtech.game.services.resource.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: 09.05.2010
 * Time: 11:36:12
 */
public class RegionResource {
    private DbRegionResource dbRegionResource;
    private ItemService itemService;
    private CollisionService collisionService;
    private TerrainService terrainService;
    private ResourceType resourceType;
    final private ArrayList<SyncResourceItem> syncResourceItems = new ArrayList<SyncResourceItem>();
    private Log log = LogFactory.getLog(RegionResource.class);

    public RegionResource(DbRegionResource dbRegionResource, ItemService itemService, CollisionService collisionService, TerrainService terrainService) {
        this.dbRegionResource = dbRegionResource;
        this.itemService = itemService;
        this.collisionService = collisionService;
        this.terrainService = terrainService;
        resourceType = (ResourceType) dbRegionResource.getResourceItemType().createItemType();
    }

    public ResourceType getResourceItemType() {
        return resourceType;
    }

    public void adjust(List<SyncResourceItem> resourceItems) {
        synchronized (syncResourceItems) {
            syncResourceItems.clear();
            for (SyncResourceItem resourceItem : resourceItems) {
                if (dbRegionResource.getRegion().contains(resourceItem.getPosition())) {
                    syncResourceItems.add(resourceItem);
                }
            }
        }
        balance();
    }

    private void balance() {
        if (syncResourceItems.size() > dbRegionResource.getCount()) {
            killResourceItems(syncResourceItems.size() - dbRegionResource.getCount());
        } else if (dbRegionResource.getCount() > syncResourceItems.size()) {
            createResourceItems(dbRegionResource.getCount() - syncResourceItems.size());
        }
    }

    private void createResourceItems(int count) {
        try {
            for (int i = 0; i < count; i++) {
                Index position = collisionService.getFreeRandomPosition(resourceType, dbRegionResource.getRegion(), dbRegionResource.getMinDistanceToItems(), true);
                SyncItem syncItem = itemService.createSyncObject(resourceType, position, null, null, 0);
                synchronized (syncResourceItems) {
                    syncResourceItems.add((SyncResourceItem) syncItem);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void killResourceItems(int count) {
        for (int i = 0; i < count; i++) {
            SyncResourceItem syncResourceItem = syncResourceItems.get(0);
            // Will be removed from syncResourceItems via resourceItemDeleted callback
            itemService.killSyncItem(syncResourceItem, null, true, false);
        }
    }

    public boolean resourceItemDeleted(SyncResourceItem syncResourceItem) {
        if (syncResourceItems.remove(syncResourceItem)) {
            if (dbRegionResource.getCount() > syncResourceItems.size()) {
                createResourceItems(dbRegionResource.getCount() - syncResourceItems.size());
            }
            return true;
        } else {
            return false;
        }
    }

    public void resolveCollision() {
        synchronized (syncResourceItems) {
            ArrayList<SyncResourceItem> copy = new ArrayList<SyncResourceItem>(syncResourceItems);
            for (SyncResourceItem syncResourceItem : copy) {
                if (!terrainService.isFree(syncResourceItem.getPosition(), resourceType)) {
                    itemService.killSyncItem(syncResourceItem, null, true, false);
                }
            }
        }
    }

    public DbRegionResource getDbRegionResource() {
        return dbRegionResource;
    }
}
