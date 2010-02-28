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
import java.util.List;

/**
 * User: beat
 * Date: 26.02.2010
 * Time: 22:01:27
 */
public class PlayerSimulation {
    public static final PlayerSimulation INSTANCE = new PlayerSimulation();
    private static boolean isActive = false;
    private boolean isRunning = false;
    private ItemType cvType;
    private ItemType factoryType;
    private BaseItemType harvesterType;
    private ItemType money;

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
            money = ItemContainer.getInstance().getItemType(Constants.MONEY);

            // Build Factory
            List<SyncItem> syncItems = ItemContainer.getInstance().getItems(cvType, true);
            if (!syncItems.isEmpty()) {
                SyncItem cvItem = syncItems.iterator().next();
                Index position = ItemContainer.getInstance().getFreeRandomPosition(factoryType, cvItem, 0, 200);
                ActionHandler.getInstance().buildFactory((SyncBaseItem) cvItem, position, (BaseItemType) factoryType);
            }

            // Start all harvesters
            doAllHarvest();
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
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

        if (clientSyncBaseItemView.isMyOwnProperty() &&
                clientSyncBaseItemView.getSyncBaseItem().getBaseItemType().getName().equals(Constants.FACTORY) &&
                clientSyncBaseItemView.getSyncBaseItem().isReady()) {
            ActionHandler.getInstance().build(clientSyncBaseItemView.getSyncBaseItem(), harvesterType);
        }
    }

    public void onItemCreated(ClientSyncItemView clientSyncItemView) {
        if (!isRunning) {
            return;
        }

        if (clientSyncItemView instanceof ClientSyncBaseItemView && ((ClientSyncBaseItemView) clientSyncItemView).isMyOwnProperty()
                && ((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem().getBaseItemType().getName().equals(Constants.HARVESTER)) {
            doHarvest(((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem());
        }
    }


    public void onSyncItemDeactivated(SyncBaseItem activeItem) {
        if (!isRunning) {
            return;
        }

        if (!ClientBase.getInstance().isMyOwnProperty(activeItem)) {
            return;
        }
        
        if (activeItem.getBaseItemType().getName().equals(Constants.HARVESTER)) {
            doHarvest(activeItem);
        } else if(activeItem.getBaseItemType().getName().equals(Constants.FACTORY)) {
            ActionHandler.getInstance().build(activeItem, harvesterType);
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

    private void doAllHarvest() {
        for (SyncItem harvester : ItemContainer.getInstance().getItems(harvesterType, true)) {
            doHarvest((SyncBaseItem) harvester);
        }
    }
}
