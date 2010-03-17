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

package com.btxtech.game.jsre.common.ai;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:35:29
 */
public class BaseBalancer {
    private ArrayList<ItemTypeBalance> itemTypeBalances;
    private Services services;
    private SimpleBase simpleBase;

    public BaseBalancer(ArrayList<ItemTypeBalance> itemTypeBalances, Services services, SimpleBase simpleBase) {
        this.itemTypeBalances = itemTypeBalances;
        this.services = services;
        this.simpleBase = simpleBase;
    }

    public void doBalance() throws NoSuchItemTypeException {
        Map<BaseItemType, List<SyncBaseItem>> items = services.getItemService().getItems4Base(simpleBase);
        for (ItemTypeBalance itemTypeBalance : itemTypeBalances) {
            BaseItemType itemTypeToBalance = (BaseItemType) services.getItemService().getItemType(itemTypeBalance.getItemTypeName());
            List<SyncBaseItem> syncBaseItems = items.get(itemTypeToBalance);
            if (syncBaseItems == null || syncBaseItems.size() < itemTypeBalance.getCount()) {
                doBalanceItemType(items, itemTypeToBalance);
                return;
            }
        }
    }

    private void doBalanceItemType(Map<BaseItemType, List<SyncBaseItem>> items, BaseItemType itemTypeToBalance) {
        List<BaseItemType> baseItemTypes = services.getItemService().ableToBuild(itemTypeToBalance);
        for (BaseItemType type : baseItemTypes) {
            List<SyncBaseItem> buildeItems = items.get(type);
            if (buildeItems != null) {
                for (SyncBaseItem builder : buildeItems) {
                    doBuild(itemTypeToBalance, builder);
                }
            }
        }
    }

    public void doBuild(BaseItemType itemTypeToBuild, SyncBaseItem builder) {
        if (builder.hasSyncBuilder()) {
            if (builder.getSyncBuilder().isActive()) {
                return;
            }
            Index position = services.getCollisionService().getFreeRandomPosition(itemTypeToBuild, builder, 0, 200);
            services.getActionService().buildFactory(builder, position, itemTypeToBuild);
        } else if (builder.hasSyncFactory()) {
            if (builder.getSyncFactory().isActive()) {
                return;
            }
            services.getActionService().build(builder, itemTypeToBuild);
        } else {
            throw new IllegalArgumentException(this + " " + builder + " don't know how to build: " + itemTypeToBuild);
        }
    }

    public void doHarvest(SyncBaseItem harvester) throws NoSuchItemTypeException {
        List<SyncItem> syncItems = services.getItemService().getItems(Constants.MONEY, null);
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


}
