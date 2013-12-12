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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.planet.db.DbRegionResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.05.2010
 * Time: 11:36:12
 */
public class RegionResource {
    private final ArrayList<SyncResourceItem> syncResourceItems = new ArrayList<>();
    private PlanetServices planetServices;
    private ResourceType resourceType;
    private Log log = LogFactory.getLog(RegionResource.class);
    private int count;
    private Region region;
    private int minDistanceToItems;

    public RegionResource(DbRegionResource dbRegionResource, PlanetServices planetServices) {
        this.planetServices = planetServices;
        resourceType = (ResourceType) dbRegionResource.getResourceItemType().createItemType();
        count = dbRegionResource.getCount();
        region = dbRegionResource.getRegion().createRegion();
        minDistanceToItems = dbRegionResource.getMinDistanceToItems();
    }

    public ResourceType getResourceItemType() {
        return resourceType;
    }

    public void adjust(Collection<SyncResourceItem> resourceItems) {
        synchronized (syncResourceItems) {
            syncResourceItems.clear();
            for (SyncResourceItem resourceItem : resourceItems) {
                if (region.isInside(resourceItem)) {
                    syncResourceItems.add(resourceItem);
                }
            }
        }
        balance();
    }

    private void balance() {
        if (syncResourceItems.size() > count) {
            killResourceItems(syncResourceItems.size() - count);
        } else if (count > syncResourceItems.size()) {
            createResourceItems(count - syncResourceItems.size());
        }
    }

    private void createResourceItems(int count) {
        try {
            for (int i = 0; i < count; i++) {
                Index position = planetServices.getCollisionService().getFreeRandomPosition(resourceType, region, minDistanceToItems, true, false);
                SyncItem syncItem = planetServices.getItemService().createSyncObject(resourceType, position, null, null);
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
            planetServices.getItemService().killSyncItem(syncResourceItem, null, true, false);
        }
    }

    public boolean resourceItemDeleted(SyncResourceItem syncResourceItem) {
        if (syncResourceItems.remove(syncResourceItem)) {
            if (count > syncResourceItems.size()) {
                createResourceItems(count - syncResourceItems.size());
            }
            return true;
        } else {
            return false;
        }
    }

    public void resolveCollision() {
        synchronized (syncResourceItems) {
            ArrayList<SyncResourceItem> copy = new ArrayList<>(syncResourceItems);
            for (SyncResourceItem syncResourceItem : copy) {
                if (!planetServices.getTerrainService().isFree(syncResourceItem.getSyncItemArea().getPosition(), resourceType, null, null)) {
                    planetServices.getItemService().killSyncItem(syncResourceItem, null, true, false);
                }
            }
        }
    }

    public Region getRegion() {
        return region;
    }


    public void deleteAllResources() {
        Collection<SyncItem> itemsToKill = new ArrayList<SyncItem>(syncResourceItems);
        syncResourceItems.clear();
        planetServices.getItemService().killSyncItems(itemsToKill);
    }

}
