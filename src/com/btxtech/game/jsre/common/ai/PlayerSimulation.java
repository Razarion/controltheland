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
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.user.client.Timer;

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
    private BaseBalancer baseBalancer;

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
        BotLevel botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 1);
        botLevel.addItemTypeBalance(Constants.HARVESTER, 3);
        botLevel.addItemTypeBalance(Constants.JEEP, 30);
        baseBalancer = new BaseBalancer(botLevel, ClientServices.getInstance(), ClientBase.getInstance().getSimpleBase());

        isRunning = true;
        try {
            baseBalancer.doBalance();
            // Start all harvesters
            baseBalancer.doAllIdleHarvest();
            baseBalancer.doAllIdleAttackers();
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
        Timer timer = new Timer() {
            @Override
            public void run() {
                try {
                    baseBalancer.doBalance();
                    baseBalancer.doAllIdleHarvest();
                    baseBalancer.doAllIdleAttackers();
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
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
        try {
            baseBalancer.doBalance();
        } catch (Throwable throwable) {
            GwtCommon.handleException(throwable);
        }
    }

    public void onItemCreated(ClientSyncItemView clientSyncItemView) throws NoSuchItemTypeException {
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


    public void onSyncItemDeactivated(SyncBaseItem activeItem) throws NoSuchItemTypeException {
        if (!isRunning) {
            return;
        }

        if (!ClientBase.getInstance().isMyOwnProperty(activeItem)) {
            return;
        }
        doCommand(activeItem);
    }

    private void doCommand(SyncBaseItem item) throws NoSuchItemTypeException {
        if (item.getBaseItemType().getName().equals(Constants.HARVESTER)) {
            baseBalancer.doHarvest(item);
        } else if (item.getBaseItemType().getName().equals(Constants.FACTORY)) {
            try {
                baseBalancer.doBalance();
            } catch (Throwable throwable) {
                GwtCommon.handleException(throwable);
            }
        } else if (item.getBaseItemType().getName().equals(Constants.JEEP)) {
            baseBalancer.doAttack(item);
        }
    }
}
