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

package com.btxtech.game.jsre.common.bot;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 05.04.2010
 * Time: 15:10:47
 */
public class BaseExecutor {
    private Services services;
    private SimpleBase simpleBase;

    public BaseExecutor(Services services, SimpleBase simpleBase) {
        this.services = services;
        this.simpleBase = simpleBase;
    }

    public void doBalanceItemType(Map<BaseItemType, List<SyncBaseItem>> items, BaseItemType itemTypeToBalance, Index hintPosition) {
        List<BaseItemType> baseItemTypes = services.getItemService().ableToBuild(itemTypeToBalance);
        for (BaseItemType type : baseItemTypes) {
            List<SyncBaseItem> builderItems = items.get(type);
            if (builderItems != null) {
                for (SyncBaseItem builder : builderItems) {
                    boolean buildStarted = doBuild(itemTypeToBalance, builder, hintPosition);
                    if (buildStarted) {
                        return;
                    }
                }
            }
        }
    }

    public boolean doBuild(BaseItemType itemTypeToBuild, SyncBaseItem builder, Index hintPosition) {
        if (builder.hasSyncBuilder()) {
            if (builder.getSyncBuilder().isActive()) {
                return false;
            } else {
                Index position;
                if (hintPosition != null) {
                    position = hintPosition;
                } else {
                    position = services.getCollisionService().getFreeRandomPosition(itemTypeToBuild, builder, 0, 200);
                }
                services.getActionService().buildFactory(builder, position, itemTypeToBuild);
                return true;
            }
        } else if (builder.hasSyncFactory()) {
            if (builder.getSyncFactory().isActive()) {
                return false;
            } else {
                services.getActionService().build(builder, itemTypeToBuild);
                return true;
            }
        } else {
            throw new IllegalArgumentException(this + " " + builder + " don't know how to build: " + itemTypeToBuild);
        }
    }


    public void doHarvest(SyncBaseItem harvester) throws NoSuchItemTypeException {
        List<? extends SyncItem> syncItems = services.getItemService().getItems(Constants.MONEY, null);
        if (syncItems.isEmpty()) {
            throw new IllegalStateException("No money item found");
        }
        SyncResourceItem moneyItem = (SyncResourceItem) syncItems.get((int) (Math.random() * (double) syncItems.size()));
        services.getActionService().collect(harvester, moneyItem);
    }

    public void doAllIdleHarvest() throws NoSuchItemTypeException {
        for (SyncItem syncItem : services.getItemService().getItems(Constants.HARVESTER, simpleBase)) {
            SyncBaseItem harvester = (SyncBaseItem) syncItem;
            if (harvester.getSyncHarvester().isActive()) {
                continue;
            }
            doHarvest(harvester);
        }
    }

    public void doAttack(SyncBaseItem attacker) {
        List<SyncBaseItem> syncItems = services.getItemService().getEnemyItems(attacker.getBase());
        if (syncItems.isEmpty()) {
            return;
        }

        SyncBaseItem target = syncItems.get((int) (Math.random() * (double) syncItems.size()));
        services.getActionService().attack(attacker, target);
    }

    public void doAllIdleAttackers() throws NoSuchItemTypeException {
        for (SyncItem syncItem : services.getItemService().getItems(Constants.JEEP, simpleBase)) {
            SyncBaseItem attacker = (SyncBaseItem) syncItem;
            if (attacker.getSyncWaepon().isActive()) {
                continue;
            }
            doAttack(attacker);
        }
    }

    public void doMove(SyncBaseItem item, Index destination) {
        services.getActionService().move(item, destination);
    }

}
