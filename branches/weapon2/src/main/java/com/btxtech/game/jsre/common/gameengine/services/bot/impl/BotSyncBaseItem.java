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
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.TargetHasNoPositionException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 17.09.2010
 * Time: 20:05:33
 */
public class BotSyncBaseItem {
    private SyncBaseItem syncBaseItem;
    private PlanetServices planetServices;
    private BotItemConfig botItemConfig;
    private Logger log = Logger.getLogger(BotSyncBaseItem.class.getName());
    private boolean idle;
    private long idleTimeStamp;

    public BotSyncBaseItem(SyncBaseItem syncBaseItem, BotItemConfig botItemConfig, PlanetServices planetServices) {
        this.syncBaseItem = syncBaseItem;
        this.botItemConfig = botItemConfig;
        this.planetServices = planetServices;
        setIdle();
    }

    public BotItemConfig getBotItemConfig() {
        return botItemConfig;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public boolean isIdle() {
        return idle;
    }

    public long getIdleTimeStamp() {
        return idleTimeStamp;
    }

    public boolean isAbleToBuild(BaseItemType toBeBuilt) {
        return syncBaseItem.hasSyncFactory() && syncBaseItem.getSyncFactory().getFactoryType().isAbleToBuild(toBeBuilt.getId())
                || syncBaseItem.hasSyncBuilder() && syncBaseItem.getSyncBuilder().getBuilderType().isAbleToBuild(toBeBuilt.getId());
    }

    public boolean isAbleToAttack(BaseItemType baseItemType) {
        return syncBaseItem.hasSyncWeapon() && syncBaseItem.hasSyncMovable() && syncBaseItem.getSyncWeapon().getWeaponType().isItemTypeAllowed(baseItemType.getId());
    }

    public boolean canMove() {
        return syncBaseItem.hasSyncMovable();
    }

    public void buildBuilding(Index position, BaseItemType toBeBuilt) {
        try {
            planetServices.getActionService().build(syncBaseItem, position, toBeBuilt);
            clearIdle();
        } catch (PathCanNotBeFoundException e) {
            setIdle();
            log.warning("BotSyncBaseItem: " + e.getMessage());
        } catch (Exception e) {
            setIdle();
            log.log(Level.SEVERE, "", e);
        }
    }

    public void buildUnit(BaseItemType toBeBuilt) {
        try {
            planetServices.getActionService().fabricate(syncBaseItem, toBeBuilt);
            clearIdle();
        } catch (PathCanNotBeFoundException e) {
            setIdle();
            log.warning("BotSyncBaseItem: " + e.getMessage());
        } catch (Exception e) {
            setIdle();
            log.log(Level.SEVERE, "", e);
        }
    }

    public void attack(SyncBaseItem target, Index destinationHint, double destinationAngel) {
        try {
            planetServices.getActionService().attack(syncBaseItem, target, destinationHint, destinationAngel, true);
            clearIdle();
        } catch (PathCanNotBeFoundException e) {
            setIdle();
            log.warning("BotSyncBaseItem: " + e.getMessage());
        } catch (Exception e) {
            setIdle();
            log.log(Level.SEVERE, "", e);
        }
    }

    public void move(Region region) {
        try {
            Index position = planetServices.getCollisionService().getFreeRandomPosition(syncBaseItem.getBaseItemType(), region, 0, false, false);
            planetServices.getActionService().move(syncBaseItem, position);
            clearIdle();
        } catch (PathCanNotBeFoundException e) {
            setIdle();
            log.warning("BotSyncBaseItem: " + e.getMessage());
        } catch (Exception e) {
            setIdle();
            log.log(Level.SEVERE, "", e);
        }
    }

    public void kill() {
        planetServices.getItemService().killSyncItem(syncBaseItem, null, true, false);
    }

    public void updateIdleState() {
        boolean tmpIdle = syncBaseItem.isIdle();
        if (tmpIdle != idle) {
            if (tmpIdle) {
                setIdle();
            } else {
                clearIdle();
            }
        }
    }

    public boolean isAlive() {
        return syncBaseItem.isAlive();
    }

    public void stop() {
        syncBaseItem.stop();
        setIdle();
    }

    public double getDistanceTo(SyncBaseItem syncBaseItem) throws TargetHasNoPositionException {
        return this.syncBaseItem.getSyncItemArea().getDistance(syncBaseItem);
    }

    public Index getPosition() {
        return syncBaseItem.getSyncItemArea().getPosition();
    }

    private void setIdle() {
        idleTimeStamp = System.currentTimeMillis();
        idle = true;
    }

    private void clearIdle() {
        idle = false;
    }

}
