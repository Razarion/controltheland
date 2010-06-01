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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
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
    private Collection<ClientSyncBaseItemView> clientSyncItems = new ArrayList<ClientSyncBaseItemView>();

    public void addItem(ClientSyncBaseItemView clientSyncItemView) {
        clientSyncItems.add(clientSyncItemView);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection<ClientSyncBaseItemView> otherClientSyncItems = ((Group) o).clientSyncItems;

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
        for (ClientSyncItemView item1 : clientSyncItems) {
            boolean found = false;
            for (ClientSyncItemView item2 : otherClientSyncItems) {
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
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncWaepon()) {
                return false;
            }
        }
        return true;
    }

    public boolean canCollect() {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncHarvester()) {
                return false;
            }
        }
        return true;
    }

    public boolean canMove() {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyFactories() {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                return false;
            }
        }
        return true;
    }

    public boolean onlyConstructionVehicle() {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncBuilder()) {
                return false;
            }
        }
        return true;
    }

    public void setSelected(boolean selected) {
        for (ClientSyncItemView clientSyncItem : clientSyncItems) {
            clientSyncItem.setSelected(selected);
        }
    }

    public boolean contains(ClientSyncItemView clientSyncItemView) {
        return clientSyncItems.contains(clientSyncItemView);
    }

    public void remove(ClientSyncItemView clientSyncItemView) {
        clientSyncItems.remove(clientSyncItemView);
    }

    public boolean isEmpty() {
        return clientSyncItems.isEmpty();
    }

    public int getCount() {
        return clientSyncItems.size();
    }

    public Collection<ClientSyncBaseItemView> getItems() {
        return clientSyncItems;
    }

    public Collection<SyncBaseItem> getSyncBaseItems() {
        ArrayList<SyncBaseItem> syncBaseItems = new ArrayList<SyncBaseItem>();
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            syncBaseItems.add(clientSyncItem.getSyncBaseItem());
        }
        return syncBaseItems;
    }

    public Collection<ClientSyncBaseItemView> getMovableItems() {
        ArrayList<ClientSyncBaseItemView> movables = new ArrayList<ClientSyncBaseItemView>();
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                movables.add(clientSyncItem);
            }
        }
        return movables;
    }

    public ClientSyncBaseItemView getFirst() {
        return clientSyncItems.iterator().next();
    }

    public int count() {
        return clientSyncItems.size();
    }

    public Map<ItemType, Collection<ClientSyncItemView>> getGroupedItems() {
        HashMap<ItemType, Collection<ClientSyncItemView>> map = new HashMap<ItemType, Collection<ClientSyncItemView>>();
        for (ClientSyncItemView clientSyncItem : clientSyncItems) {
            Collection<ClientSyncItemView> collection = map.get(clientSyncItem.getSyncItem().getItemType());
            if (collection == null) {
                collection = new ArrayList<ClientSyncItemView>();
                map.put(clientSyncItem.getSyncItem().getItemType(), collection);
            }
            collection.add(clientSyncItem);
        }
        return map;
    }

    public Collection<SurfaceType> getAllowedSurfaceTypes() {
        HashSet<SurfaceType> result = new HashSet<SurfaceType>();
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            result.addAll(clientSyncItem.getSyncBaseItem().getTerrainType().getSurfaceTypes());
        }
        return result;
    }
}
