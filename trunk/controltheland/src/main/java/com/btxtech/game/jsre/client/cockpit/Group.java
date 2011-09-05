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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: beat
 * Date: 09.11.2009
 * Time: 23:05:45
 */
public class Group {
    private Collection<ClientSyncItem> clientSyncItems = new ArrayList<ClientSyncItem>();

    public void addItem(ClientSyncItem clientSyncItem) {
        if (!clientSyncItem.isSyncBaseItem()) {
            throw new IllegalArgumentException(this + " ClientSyncItem must have a SyncBaseItem: " + clientSyncItem);
        }
        clientSyncItems.add(clientSyncItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection<ClientSyncItem> otherClientSyncItems = ((Group) o).clientSyncItems;

        if (clientSyncItems == null) {
            return otherClientSyncItems == null;
        } else if (otherClientSyncItems == null) {
            return false;
        }
        if (clientSyncItems.isEmpty() && otherClientSyncItems.isEmpty()) {
            return true;
        }
        if (clientSyncItems.size() != otherClientSyncItems.size()) {
            return false;
        }
        for (ClientSyncItem item1 : clientSyncItems) {
            boolean found = false;
            for (ClientSyncItem item2 : otherClientSyncItems) {
                if (item1.equals(item2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int hashCode() {
        return clientSyncItems != null ? clientSyncItems.hashCode() : 0;
    }

    public boolean canAttack() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncWeapon()) {
                return false;
            }
        }
        return true;
    }

    public boolean canCollect() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncHarvester()) {
                return false;
            }
        }
        return true;
    }

    public boolean canMove() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                return true;
            }
        }
        return false;
    }

    public boolean canLaunch() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncLauncher() && !clientSyncItem.getSyncBaseItem().getSyncLauncher().isActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean canFinalizeBuild() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncBuilder()) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyFactories() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                return false;
            }
        }
        return true;
    }

    public boolean onlyConstructionVehicle() {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncBuilder()) {
                return false;
            }
        }
        return true;
    }

    public void setSelected(boolean selected) {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            clientSyncItem.setSelected(selected);
        }
    }

    public boolean contains(ClientSyncItem clientSyncItemView) {
        return clientSyncItems.contains(clientSyncItemView);
    }

    public void remove(ClientSyncItem clientSyncItemView) {
        clientSyncItems.remove(clientSyncItemView);
    }

    public boolean isEmpty() {
        return clientSyncItems.isEmpty();
    }

    public int getCount() {
        return clientSyncItems.size();
    }

    public Collection<ClientSyncItem> getItems() {
        return clientSyncItems;
    }

    public Collection<SyncBaseItem> getSyncBaseItems() {
        ArrayList<SyncBaseItem> syncBaseItems = new ArrayList<SyncBaseItem>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            syncBaseItems.add(clientSyncItem.getSyncBaseItem());
        }
        return syncBaseItems;
    }

    public Collection<ClientSyncItem> getMovableItems() {
        ArrayList<ClientSyncItem> movables = new ArrayList<ClientSyncItem>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                movables.add(clientSyncItem);
            }
        }
        return movables;
    }

    public ClientSyncItem getFirst() {
        return clientSyncItems.iterator().next();
    }

    public int count() {
        return clientSyncItems.size();
    }

    public Map<ItemType, Collection<ClientSyncItem>> getGroupedItems() {
        HashMap<ItemType, Collection<ClientSyncItem>> map = new HashMap<ItemType, Collection<ClientSyncItem>>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            Collection<ClientSyncItem> collection = map.get(clientSyncItem.getSyncItem().getItemType());
            if (collection == null) {
                collection = new ArrayList<ClientSyncItem>();
                map.put(clientSyncItem.getSyncItem().getItemType(), collection);
            }
            collection.add(clientSyncItem);
        }
        return map;
    }

    public Collection<SurfaceType> getAllowedSurfaceTypes() {
        HashSet<SurfaceType> result = new HashSet<SurfaceType>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            result.addAll(clientSyncItem.getSyncBaseItem().getTerrainType().getSurfaceTypes());
        }
        return result;
    }


    public boolean atLeastOneItemTypeAllowed2Attack(SyncBaseItem syncBaseItem) {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncWeapon() && clientSyncItem.getSyncBaseItem().getSyncWeapon().isItemTypeAllowed(syncBaseItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean atLeastOneItemTypeAllowed2FinalizeBuild(SyncBaseItem tobeFinalized) {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncBuilder()
                    && clientSyncItem.getSyncBaseItem().getSyncBuilder().getBuilderType().isAbleToBuild(tobeFinalized.getItemType().getId())
                    && ClientItemTypeAccess.getInstance().isAllowed(tobeFinalized.getItemType().getId())
                    && ClientTerritoryService.getInstance().isAllowed(tobeFinalized.getSyncItemArea().getPosition(), tobeFinalized)
                    && ClientTerritoryService.getInstance().isAllowed(tobeFinalized.getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())) {
                return true;
            }
        }
        return false;
    }

    public boolean atLeastOneAllowedToLaunch(Index position) {
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncLauncher()) {
                try {
                    int range = clientSyncItem.getSyncBaseItem().getSyncLauncher().getRange();
                    if (clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition().getDistance(position) <= range
                            && ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())
                            && ClientTerritoryService.getInstance().isAllowed(position, clientSyncItem.getSyncBaseItem().getSyncLauncher().getLauncherType().getProjectileItemType())) {
                        return true;
                    }
                } catch (NoSuchItemTypeException e) {
                    GwtCommon.handleException(e);
                }
            }
        }
        return false;
    }
}
