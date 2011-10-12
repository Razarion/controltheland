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

package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 17.09.2010
 * Time: 20:05:33
 */
public class BotSyncBaseItem {
    private SyncBaseItem syncBaseItem;
    private CommonActionService actionService;
    private Logger log = Logger.getLogger(BotSyncBaseItem.class.getName());
    private boolean idle = true;

    public BotSyncBaseItem(SyncBaseItem syncBaseItem, CommonActionService actionService) {
        this.syncBaseItem = syncBaseItem;
        this.actionService = actionService;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public boolean isIdle() {
        return idle;
    }

    public boolean isAbleToBuild(BaseItemType toBeBuilt) {
        return syncBaseItem.hasSyncFactory() && syncBaseItem.getSyncFactory().getFactoryType().isAbleToBuild(toBeBuilt.getId())
                || syncBaseItem.hasSyncBuilder() && syncBaseItem.getSyncBuilder().getBuilderType().isAbleToBuild(toBeBuilt.getId());
    }

    public boolean isAbleToAttack(BaseItemType baseItemType) {
        return syncBaseItem.hasSyncWeapon() && syncBaseItem.hasSyncMovable() && syncBaseItem.getSyncWeapon().getWeaponType().isItemTypeAllowed(baseItemType.getId());

    }

    public void buildBuilding(Index position, BaseItemType toBeBuilt) {
        try {
            actionService.build(syncBaseItem, position, toBeBuilt);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.log(Level.SEVERE, "", e);
        }
    }

    public void buildUnit(BaseItemType toBeBuilt) {
        try {
            actionService.fabricate(syncBaseItem, toBeBuilt);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.log(Level.SEVERE, "", e);
        }
    }

    public void attack(SyncBaseItem target, Index destinationHint, double destinationAngel) {
        try {
            actionService.attack(syncBaseItem, target, destinationHint, destinationAngel, true);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.log(Level.SEVERE, "", e);
        }
    }

    public void updateIdleState() {
        idle = syncBaseItem.isIdle();
    }

    public boolean isAlive() {
        return syncBaseItem.isAlive();
    }

    public void stop() {
        syncBaseItem.stop();
        idle = true;
    }

    public double getDistanceTo(SyncBaseItem syncBaseItem) {
        return this.syncBaseItem.getSyncItemArea().getDistance(syncBaseItem);
    }
}
