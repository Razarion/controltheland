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

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.09.2010
 * Time: 22:01:47
 */
public class SyncProjectileItem extends SyncTickItem implements SyncBaseObject {
    private SimpleBase simpleBase;
    private Index target;
    private ProjectileItemType projectileItemType;
    private DecimalPosition position;
    private boolean isAlive = true;

    public SyncProjectileItem(Id id, Index position, ProjectileItemType projectileItemType, GlobalServices globalServices, PlanetServices planetServices, SimpleBase simpleBase) {
        super(id, position, projectileItemType, globalServices, planetServices);
        this.projectileItemType = projectileItemType;
        this.simpleBase = simpleBase;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public boolean tick(double factor) {
        double distance = factor * (double) projectileItemType.getSpeed();
        DecimalPosition newPosition = position.getPointWithDistance(distance, target, false);
        if (newPosition.getDistance(target) < 1.0) {
            explode();
            return false;
        } else {
            position = newPosition;
            fireItemChanged(SyncItemListener.Change.POSITION);
            return true;
        }
    }

    @Override
    public void stop() {
    }

    private void explode() {
        Collection<SyncBaseItem> syncBaseItems = getPlanetServices().getItemService().getBaseItemsInRadius(target, projectileItemType.getExplosionRadius(), null, null);
        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            syncBaseItem.decreaseHealth(projectileItemType.getDamage(), simpleBase);
        }
        isAlive = false;
        getPlanetServices().getItemService().killSyncItem(this, null, true, true);
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        super.synchronize(syncItemInfo);
        simpleBase = syncItemInfo.getBase();
        target = syncItemInfo.getTargetPosition();
    }

    @Override
    public SyncItemInfo getSyncInfo() {
        SyncItemInfo syncItemInfo = super.getSyncInfo();
        syncItemInfo.setBase(simpleBase);
        syncItemInfo.setTargetPosition(target);
        return syncItemInfo;
    }

    public void setTarget(Index target) {
        this.target = target;
    }

    public Index getTarget() {
        return target;
    }

    @Override
    public SimpleBase getBase() {
        return simpleBase;
    }

    public ProjectileItemType getProjectileItemType() {
        return projectileItemType;
    }
}
