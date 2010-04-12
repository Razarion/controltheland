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

package com.btxtech.game.jsre.client.action;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserGuidance;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.RectangleFormation;
import com.btxtech.game.jsre.common.bot.PlayerSimulation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UpgradeCommand;
import com.google.gwt.user.client.Timer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * User: beat
 * Date: Aug 7, 2009
 * Time: 10:22:31 PM
 */
public class ActionHandler implements CommonActionService {
    private final static ActionHandler INSTANCE = new ActionHandler();
    private static final int TICK_INTERVALL = 60;
    private long lastTickTime = 0;
    private final HashSet<SyncBaseItem> activeItems = new HashSet<SyncBaseItem>();
    private HashSet<SyncBaseItem> tmpAddactiveItems = new HashSet<SyncBaseItem>();
    private HashSet<SyncBaseItem> tmpRemoveActiveItems = new HashSet<SyncBaseItem>();

    public static ActionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ActionHandler() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                tick();
            }
        };
        timer.scheduleRepeating(TICK_INTERVALL);
    }

    private void tick() {
        synchronized (activeItems) {
            activeItems.addAll(tmpAddactiveItems);
            tmpAddactiveItems.clear();
            activeItems.removeAll(tmpRemoveActiveItems);
            tmpRemoveActiveItems.clear();
            long time = System.currentTimeMillis();
            double factor = calculateFactor(time);
            Iterator<SyncBaseItem> iterator = activeItems.iterator();
            while (iterator.hasNext()) {
                SyncBaseItem activeItem = iterator.next();
                try {
                    if (!activeItem.tick(factor)) {
                        ClientUserGuidance.getInstance().onSyncItemDeactivated(activeItem);
                        PlayerSimulation.getInstance().onSyncItemDeactivated(activeItem);
                        iterator.remove();
                    }
                } catch (ItemDoesNotExistException ife) {
                    iterator.remove();
                    activeItem.stop();
                } catch (InsufficientFundsException ife) {
                    iterator.remove();
                    activeItem.stop();
                    if (ClientBase.getInstance().isMyOwnProperty(activeItem) && !PlayerSimulation.isActive()) {
                        MessageDialog.show("Insufficient Money!", "You do not have enough money. You have to Collect more money");
                    }
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    activeItem.stop();
                    iterator.remove();
                }
            }
            lastTickTime = time;
        }
    }

    public void addActiveItem(SyncBaseItem syncItem) {
        tmpAddactiveItems.add(syncItem);
    }

    public void removeActiveItem(SyncBaseItem baseSyncItem) {
        tmpRemoveActiveItems.add(baseSyncItem);
    }

    public void move(Collection<ClientSyncBaseItemView> clientSyncItems, Index destination) {
        if (clientSyncItems.isEmpty()) {
            return;
        }
        RectangleFormation rectangleFormation = new RectangleFormation(destination, clientSyncItems);
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                Index pos = null;
                while (pos == null) {
                    pos = rectangleFormation.calculateNextEntry();
                    if (!TerrainView.getInstance().getTerrainHandler().isTerrainPassable(pos)) {
                        pos = null;
                    }
                }
                move(clientSyncItem.getSyncBaseItem(), pos);
            } else {
                GwtCommon.sendLogToServer("ActionHandler.move(): can not cast to MovableSyncItem:" + clientSyncItem);
            }
        }
    }

    public void move(SyncBaseItem syncItem, Index destination) {
        if (checkCommand(syncItem)) {
            return;
        }
        syncItem.stop();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(syncItem.getId());
        moveCommand.setTimeStamp();
        moveCommand.setDestination(destination);
        try {
            syncItem.executeCommand(moveCommand);
            executeCommand(syncItem, moveCommand);
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void buildFactory(Collection<ClientSyncBaseItemView> clientSyncItems, Index positionToBeBuild, BaseItemType toBeBuilt) {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncBuilder()) {
                buildFactory(clientSyncItem.getSyncBaseItem(), positionToBeBuild, toBeBuilt);
                // Just get the first CV to build the building
                // Prevent to build multiple buildings
                return;
            } else {
                GwtCommon.sendLogToServer("ActionHandler.buildFactory(): can not cast to ConstructionVehicleSyncItem:" + clientSyncItem);
            }
        }
    }

    @Override
    public void buildFactory(SyncBaseItem syncItem, Index positionToBeBuild, BaseItemType toBeBuilt) {
        if (checkCommand(syncItem)) {
            return;
        }
        syncItem.stop();
        BuilderCommand builderCommand = new BuilderCommand();
        builderCommand.setId(syncItem.getId());
        builderCommand.setTimeStamp();
        builderCommand.setToBeBuilt(toBeBuilt.getId());
        builderCommand.setPositionToBeBuilt(positionToBeBuild);
        try {
            syncItem.executeCommand(builderCommand);
            executeCommand(syncItem, builderCommand);
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void build(Collection<ClientSyncBaseItemView> clientSyncItems, BaseItemType itemType) {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                build(clientSyncItem.getSyncBaseItem(), itemType);
            } else {
                GwtCommon.sendLogToServer("ActionHandler.build(): can not cast to FactorySyncItem:" + clientSyncItem);
            }
        }
    }

    @Override
    public void build(SyncBaseItem factory, BaseItemType itemType) {
        if (checkCommand(factory) || !factory.isReady() || factory.getSyncFactory().isActive()) {
            return;
        }
        FactoryCommand factoryCommand = new FactoryCommand();
        factoryCommand.setId(factory.getId());
        factoryCommand.setTimeStamp();
        factoryCommand.setToBeBuilt(itemType.getId());

        try {
            factory.executeCommand(factoryCommand);
            executeCommand(factory, factoryCommand);
        } catch (InsufficientFundsException e) {
            if (!PlayerSimulation.isActive()) {
                MessageDialog.show("Insufficient Money!", "You do not have enough money. You have to Collect more money");
            }
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void attack(Collection<ClientSyncBaseItemView> clientSyncItems, SyncBaseItem target) {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncWaepon()) {
                attack(clientSyncItem.getSyncBaseItem(), target);
            } else {
                GwtCommon.sendLogToServer("ActionHandler.attack(): can not cast to TankSyncItem:" + clientSyncItem);
            }
        }
    }

    @Override
    public void attack(SyncBaseItem tank, SyncBaseItem target) {
        if (checkCommand(tank)) {
            return;
        }
        tank.stop();
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(tank.getId());
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target.getId());
        attackCommand.setFollowTarget(true);

        try {
            tank.executeCommand(attackCommand);
            executeCommand(tank, attackCommand);
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void collect(Collection<ClientSyncBaseItemView> clientSyncItems, SyncResourceItem money) {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncHarvester()) {
                collect(clientSyncItem.getSyncBaseItem(), money);
            } else {
                GwtCommon.sendLogToServer("ActionHandler.collect(): can not cast to MoneyCollectorSyncItem:" + clientSyncItem);
            }
        }
    }

    @Override
    public void collect(SyncBaseItem collector, SyncResourceItem money) {
        if (checkCommand(collector)) {
            return;
        }
        collector.stop();
        MoneyCollectCommand collectCommand = new MoneyCollectCommand();
        collectCommand.setId(collector.getId());
        collectCommand.setTimeStamp();
        collectCommand.setTarget(money.getId());

        try {
            collector.executeCommand(collectCommand);
            executeCommand(collector, collectCommand);
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    @Override
    public void upgrade(SyncBaseItem item) {
        if (checkCommand(item)) {
            return;
        }
        item.stop();
        UpgradeCommand upgradeCommand = new UpgradeCommand();
        upgradeCommand.setId(item.getId());
        upgradeCommand.setTimeStamp();
        try {
            item.executeCommand(upgradeCommand);
            executeCommand(item, upgradeCommand);
        } catch (InsufficientFundsException e) {
            if (!PlayerSimulation.isActive()) {
                MessageDialog.show("Insufficient Money!", "You do not have enough money. You have to Collect more money");
            }
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    private boolean checkCommand(SyncItem syncItem) {
        Id id = syncItem.getId();
        if (id == null) {
            GwtCommon.sendLogToServer("Can not send command: id is null");
            return true;
        }
        if (!id.isSynchronized()) {
            GwtCommon.sendLogToServer("Can not send comman: Id is not synchronized: " + id);
            return true;
        }
        return false;
    }


    private void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        Connection.getInstance().sendCommand(baseCommand);
        ClientUserGuidance.getInstance().onExecuteCommand(syncItem, baseCommand);
        addActiveItem(syncItem);
    }

    private double calculateFactor(long time) {
        if (lastTickTime > 0) {
            return (double) (time - lastTickTime) / 1000.0;
        } else {
            return 1.0;
        }
    }

}
