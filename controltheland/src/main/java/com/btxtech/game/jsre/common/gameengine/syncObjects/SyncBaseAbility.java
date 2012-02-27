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

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

import java.util.List;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 19:22:47
 */
public abstract class SyncBaseAbility {
    private SyncBaseItem syncBaseItem;

    public SyncBaseAbility(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public Services getServices() {
        return syncBaseItem.getServices();
    }

    public SyncItemArea getSyncItemArea() {
        return syncBaseItem.getSyncItemArea();
    }

    public void setPathToDestinationIfSyncMovable(List<Index> pathToDestination) {
        if (syncBaseItem.hasSyncMovable()) {
            syncBaseItem.getSyncMovable().setPathToDestination(pathToDestination);
        }
    }

    public boolean isNewPathRecalculationAllowed() {
        return getServices().getConnectionService().getGameEngineMode() == GameEngineMode.MASTER;
    }

    public double recalculateNewPath(int range, SyncItemArea target, TerrainType targetTerrainType) {
        SyncBaseItem syncItem = getSyncBaseItem();
        AttackFormationItem format = getServices().getCollisionService().getDestinationHint(syncItem,
                range,
                target,
                targetTerrainType);
        if (format.isInRange()) {
            setPathToDestinationIfSyncMovable(getServices().getCollisionService().setupPathToDestination(syncItem, format.getDestinationHint()));
            return format.getDestinationAngel();
        } else {
            throw new PathCanNotBeFoundException("Can not find path in recalculateNewPath: " + syncItem, syncItem.getSyncItemArea().getPosition(), null);
        }
    }

    public abstract void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException;

    public abstract void fillSyncItemInfo(SyncItemInfo syncItemInfo);

}
