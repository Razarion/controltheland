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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 26.02.2010
 * Time: 22:01:27
 */
public class PlayerSimulation {
    public static final int TIME = 1000;
    private static final PlayerSimulation INSTANCE = new PlayerSimulation();
    private static boolean isActive = false;
    private boolean isRunning = false;
    private ItemType cvType;
    private ItemType factoryType;
    private BaseItemType harvesterType;
    private BaseItemType jeepType;
    private ItemType money;
    private static final ArrayList<ItemTypeBalance> itemTypeBalances = new ArrayList<ItemTypeBalance>();

    static {
        itemTypeBalances.add(new ItemTypeBalance(Constants.FACTORY, 1)); // First prio
        itemTypeBalances.add(new ItemTypeBalance(Constants.HARVESTER, 3));// Second prio
        itemTypeBalances.add(new ItemTypeBalance(Constants.JEEP, 30));// Third prio
    }

    /**
     * Singleton
     */
    private PlayerSimulation() {
    }

    public static PlayerSimulation getInstance() {
        return INSTANCE;
    }

    public void start() {
        if (!isActive) {
            return;
        }
        isRunning = true;
        try {
            // ItemTypes
            cvType = ItemContainer.getInstance().getItemType(Constants.CONSTRUCTION_VEHICLE);
            factoryType = ItemContainer.getInstance().getItemType(Constants.FACTORY);
            harvesterType = (BaseItemType) ItemContainer.getInstance().getItemType(Constants.HARVESTER);
            jeepType = (BaseItemType) ItemContainer.getInstance().getItemType(Constants.JEEP);
            money = ItemContainer.getInstance().getItemType(Constants.MONEY);

            // Build Factory
            /*List<SyncItem> syncItems = ItemContainer.getInstance().getItems(cvType, true);
            if (!syncItems.isEmpty()) {
                SyncItem cvItem = syncItems.iterator().next();
                Index position = ItemContainer.getInstance().getFreeRandomPosition(factoryType, cvItem, 0, 200);
                ActionHandler.getInstance().buildFactory((SyncBaseItem) cvItem, position, (BaseItemType) factoryType);
            }*/
            doBalance();
            // Start all harvesters
            doAllIdleHarvest();
            doAllIdleAttackers();
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
        Timer timer = new Timer() {
            @Override
            public void run() {
                doBalance();
                // Start all harvesters
                doAllIdleHarvest();
                doAllIdleAttackers();
            }
        };
        timer.scheduleRepeating(TIME);
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void setActive(boolean active) {
        isActive = active;
    }

    public void onItemBuilt(ClientSyncBaseItemView clientSyncBaseItemView) {
        if (!isRunning) {
            return;
        }
        doBalance();
        /* if (clientSyncBaseItemView.isMyOwnProperty() &&
               clientSyncBaseItemView.getSyncBaseItem().getBaseItemType().getName().equals(Constants.FACTORY) &&
               clientSyncBaseItemView.getSyncBaseItem().isReady()) {
           //ActionHandler.getInstance().build(clientSyncBaseItemView.getSyncBaseItem(), harvesterType);
           ActionHandler.getInstance().build(clientSyncBaseItemView.getSyncBaseItem(), jeepType);
       } */
    }

    public void onItemCreated(ClientSyncItemView clientSyncItemView) {
        if (!isRunning) {
            return;
        }

        if (!(clientSyncItemView instanceof ClientSyncBaseItemView)) {
            return;
        }
        ClientSyncBaseItemView clientSyncBaseItemView = (ClientSyncBaseItemView) clientSyncItemView;

        if (!clientSyncBaseItemView.isMyOwnProperty()) {
            return;
        }
        doCommand(clientSyncBaseItemView.getSyncBaseItem());
    }


    public void onSyncItemDeactivated(SyncBaseItem activeItem) {
        if (!isRunning) {
            return;
        }

        if (!ClientBase.getInstance().isMyOwnProperty(activeItem)) {
            return;
        }
        doCommand(activeItem);
    }

    private void doCommand(SyncBaseItem item) {
        if (item.getBaseItemType().getName().equals(Constants.HARVESTER)) {
            doHarvest(item);
        } else if (item.getBaseItemType().getName().equals(Constants.FACTORY)) {
            doBalance();
            //    ActionHandler.getInstance().build(item, jeepType);
        } else if (item.getBaseItemType().getName().equals(Constants.JEEP)) {
            doAttack(item);
        }
    }

    private void doHarvest(SyncBaseItem harvester) {
        List<SyncItem> syncItems = ItemContainer.getInstance().getItems(money, false);
        if (syncItems.isEmpty()) {
            throw new IllegalStateException("No money item found");
        }

        SyncResourceItem moneyItem = (SyncResourceItem) syncItems.get(Random.nextInt(syncItems.size()));
        ActionHandler.getInstance().collect(harvester, moneyItem);
    }

    private void doAllIdleHarvest() {
        for (SyncItem syncItem : ItemContainer.getInstance().getItems(harvesterType, true)) {
            SyncBaseItem harvester = (SyncBaseItem) syncItem;
            if (harvester.getSyncHarvester().isActive()) {
                continue;
            }
            doHarvest(harvester);
        }
    }

    private void doAttack(SyncBaseItem attacker) {
        List<ClientSyncBaseItemView> syncItems = ItemContainer.getInstance().getEnemyItems();
        if (syncItems.isEmpty()) {
            return;
        }

        ClientSyncBaseItemView target = syncItems.get(Random.nextInt(syncItems.size()));
        ActionHandler.getInstance().attack(attacker, target.getSyncBaseItem());
    }

    private void doAllIdleAttackers() {
        for (SyncItem syncItem : ItemContainer.getInstance().getItems(jeepType, true)) {
            SyncBaseItem attacker = (SyncBaseItem) syncItem;
            if (attacker.getSyncWaepon().isActive()) {
                continue;
            }
            doHarvest(attacker);
        }
    }


    private void doBalance() {
        Map<BaseItemType, List<SyncBaseItem>> items = ItemContainer.getInstance().getItems4Base(ClientBase.getInstance().getSimpleBase());
        for (ItemTypeBalance itemTypeBalance : itemTypeBalances) {
            try {
                BaseItemType itemTypeToBalance = (BaseItemType) ItemContainer.getInstance().getItemType(itemTypeBalance.getItemTypeName());
                List<SyncBaseItem> syncBaseItems = items.get(itemTypeToBalance);
                if (syncBaseItems == null || syncBaseItems.size() < itemTypeBalance.getCount()) {
                    doBalanceItemType(items, itemTypeToBalance);
                    return;
                }
            } catch (NoSuchItemTypeException e) {
                GwtCommon.handleException(e);
            }
        }
    }

    private void doBalanceItemType(Map<BaseItemType, List<SyncBaseItem>> items, BaseItemType itemTypeToBalance) {
        List<BaseItemType> builderType = ItemContainer.getInstance().ableToBuild(itemTypeToBalance);
        for (BaseItemType type : builderType) {
            List<SyncBaseItem> buildeItems = items.get(type);
            if (buildeItems != null) {
                for (SyncBaseItem builder : buildeItems) {
                    doBuild(itemTypeToBalance, builder);
                }
            }
        }
    }

    private void doBuild(BaseItemType itemTypeToBuild, SyncBaseItem builder) {
        if (builder.hasSyncBuilder()) {
            Index position = ItemContainer.getInstance().getFreeRandomPosition(itemTypeToBuild, builder, 0, 200);
            ActionHandler.getInstance().buildFactory(builder, position, itemTypeToBuild);
        } else if (builder.hasSyncFactory()) {
            ActionHandler.getInstance().build(builder, itemTypeToBuild);
        } else {
            throw new IllegalArgumentException(this + " " + builder + " don't know how to build: " + itemTypeToBuild);
        }
    }

}
