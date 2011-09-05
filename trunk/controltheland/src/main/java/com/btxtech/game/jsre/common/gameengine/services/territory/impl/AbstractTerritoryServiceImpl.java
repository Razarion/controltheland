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

package com.btxtech.game.jsre.common.gameengine.services.territory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 13:37:42
 */
public abstract class AbstractTerritoryServiceImpl implements AbstractTerritoryService {
    private Map<Integer, Territory> territories = new HashMap<Integer, Territory>();

    @Override
    public Collection<Territory> getTerritories() {
        return new ArrayList<Territory>(territories.values());
    }

    @Override
    public Territory getTerritory(int id) {
        Territory territory = territories.get(id);
        if (territory == null) {
            throw new IllegalArgumentException("No territory for id: " + id);
        }
        return territory;
    }

    public void setTerritories(Collection<Territory> territories) {
        this.territories.clear();
        for (Territory territory : territories) {
            this.territories.put(territory.getId(), territory);
        }
    }

    public Territory getTerritoryTile(int tileX, int tileY) {
        return getTerritoryTile(new Index(tileX, tileY));
    }

    public Territory getTerritoryTile(Index tile) {
        for (Territory territory : territories.values()) {
            if (territory.contains(tile)) {
                return territory;
            }
        }
        return null;
    }

    @Override
    public Territory getTerritory(int absX, int absY) {
        return getTerritory(new Index(absX, absY));
    }

    @Override
    public Territory getTerritory(Index absPos) {
        Index tile = getTerrainService().getTerrainTileIndexForAbsPosition(absPos);
        return getTerritoryTile(tile);
    }

    @Override
    public boolean isAllowed(Index position, int itemTypeId) {
        Territory territory = getTerritory(position);
        return territory == null || territory.isItemAllowed(itemTypeId);
    }

    @Override
    public boolean isAllowed(Index position, BaseItemType baseItemType) {
        return isAllowed(position, baseItemType.getId());
    }

    @Override
    public boolean isAllowed(Index position, SyncBaseItem syncBaseItem) {
        return isAllowed(position, syncBaseItem.getBaseItemType());
    }

    @Override
    public boolean isAllowed(Index target, ProjectileItemType projectileItemType) {
        return isAllowed(target, projectileItemType.getId());
    }

    @Override
    public boolean isAtLeastOneAllowed(Index absIndex, Collection<SyncBaseItem> items) {
        for (SyncBaseItem item : items) {
            if (isAllowed(absIndex, item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAtLeastOneAllowed(Collection<SyncBaseItem> items) {
        for (SyncBaseItem item : items) {
            if (isAllowed(item.getSyncItemArea().getPosition(), item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTerritory(int territoryId, int absX, int absY) {
        return isTerritory(territoryId, new Index(absX, absY));
    }

    @Override
    public boolean isTerritory(int territoryId, Index absPos) {
        Territory territory = getTerritory(absPos);
        return territory != null && territory.compareId(territoryId);
    }

    protected abstract AbstractTerrainService getTerrainService();
}
