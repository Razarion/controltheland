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
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.RectangleFormation;
import com.btxtech.game.jsre.common.bot.PlayerSimulation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
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
    private static final int TICK_INTERVAL = 60;
    private long lastTickTime = 0;
    private final HashSet<SyncBaseItem> activeItems = new HashSet<SyncBaseItem>();
    private HashSet<SyncBaseItem> tmpAddActiveItems = new HashSet<SyncBaseItem>();
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
        timer.scheduleRepeating(TICK_INTERVAL);
    }

    private void tick() {
        synchronized (activeItems) {
            activeItems.addAll(tmpAddActiveItems);
            tmpAddActiveItems.clear();
            activeItems.removeAll(tmpRemoveActiveItems);
            tmpRemoveActiveItems.clear();
            long time = System.currentTimeMillis();
            double factor = calculateFactor(time);
            Iterator<SyncBaseItem> iterator = activeItems.iterator();
            while (iterator.hasNext()) {
                SyncBaseItem activeItem = iterator.next();
                try {
                    if (!activeItem.tick(factor)) {
                        PlayerSimulation.getInstance().onSyncItemDeactivated(activeItem);
                        Simulation.getInstance().onSyncItemDeactivated(activeItem);
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
        tmpAddActiveItems.add(syncItem);
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
                    if (pos == null) {
                        continue;
                    }
                    SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(pos);
                    if (!clientSyncItem.getSyncBaseItem().getTerrainType().getSurfaceTypes().contains(surfaceType)) {
                        pos = null;
                    }
                }
                move(clientSyncItem.getSyncBaseItem(), pos);
            } else {
                GwtCommon.sendLogToServer("ActionHandler.moveDelta(): can not cast to MovableSyncItem:" + clientSyncItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
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
                if (ClientTerritoryService.getInstance().isAllowed(positionToBeBuild, clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(positionToBeBuild, toBeBuilt)) {

                    buildFactory(clientSyncItem.getSyncBaseItem(), positionToBeBuild, toBeBuilt);
                    // Just get the first CV to build the building
                    // Prevent to build multiple buildings
                    Connection.getInstance().sendCommandQueue();
                    return;
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.buildFactory(): can not cast to ConstructionVehicleSyncItem:" + clientSyncItem);
                return;
            }
        }
        GwtCommon.sendLogToServer("ActionHandler.buildFactory(): can not build on territory: " + clientSyncItems + " " + positionToBeBuild + " " + toBeBuilt);
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

    public void build(Collection<ClientSyncBaseItemView> clientSyncItems, BaseItemType itemTypeToBuild) {
        for (ClientSyncBaseItemView clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                if (ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getPosition(), clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getPosition(), itemTypeToBuild)) {
                    build(clientSyncItem.getSyncBaseItem(), itemTypeToBuild);
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.build(): can not cast to FactorySyncItem:" + clientSyncItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
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
                if (ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getPosition(), clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(target.getPosition(), clientSyncItem.getSyncBaseItem())
                        && clientSyncItem.getSyncBaseItem().getSyncWaepon().isItemTypeAllowed(target)) {
                    attack(clientSyncItem.getSyncBaseItem(), target);
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.attack(): can not cast to TankSyncItem:" + clientSyncItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
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
                if (ClientTerritoryService.getInstance().isAllowed(money.getPosition(), clientSyncItem.getSyncBaseItem())) {
                    collect(clientSyncItem.getSyncBaseItem(), money);
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.collect(): can not cast to MoneyCollectorSyncItem:" + clientSyncItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
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
            Connection.getInstance().sendCommandQueue();
        } catch (InsufficientFundsException e) {
            if (!PlayerSimulation.isActive()) {
                MessageDialog.show("Insufficient Money!", "You do not have enough money. You have to Collect more money");
            }
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void loadContainer(ClientSyncBaseItemView container, Collection<ClientSyncBaseItemView> items) {
        if (!container.getSyncBaseItem().hasSyncItemContainer()) {
            GwtCommon.sendLogToServer("ActionHandler.loadContainer(): can not cast to ItemContainer:" + container);
            return;
        }

        for (ClientSyncBaseItemView item : items) {
            if (item.getSyncBaseItem().hasSyncMovable()) {
                if (ClientTerritoryService.getInstance().isAllowed(container.getSyncBaseItem().getPosition(), container.getSyncBaseItem())
                        && container.getSyncBaseItem().getSyncItemContainer().isAbleToContain(item.getSyncBaseItem())
                        && item.getSyncBaseItem().getSyncMovable().isLoadPosReachable(container.getSyncBaseItem().getSyncItemContainer())) {
                    putToContainer(container.getSyncBaseItem(), item.getSyncBaseItem());
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.loadContainer(): has no movable:" + item);
            }
        }
        Connection.getInstance().sendCommandQueue();
    }

    private void putToContainer(SyncBaseItem container, SyncBaseItem item) {
        if (checkCommand(item)) {
            return;
        }
        if (checkCommand(container)) {
            return;
        }

        container.stop();
        LoadContainCommand loadContainCommand = new LoadContainCommand();
        loadContainCommand.setId(item.getId());
        loadContainCommand.setTimeStamp();
        loadContainCommand.setItemContainer(container.getId());

        try {
            item.executeCommand(loadContainCommand);
            executeCommand(item, loadContainCommand);
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    public void unloadContainer(SyncBaseItem container, Index unloadPos) {
        if (checkCommand(container)) {
            return;
        }

        if (!container.hasSyncItemContainer()) {
            GwtCommon.sendLogToServer("ActionHandler.unloadContainer(): can not cast to ItemContainer:" + container);
            return;
        }

        if (!ClientTerritoryService.getInstance().isAllowed(unloadPos, container)) {
            GwtCommon.sendLogToServer("ActionHandler.unloadContainer(): can not unload on territory:" + unloadPos);
            return;
        }

        UnloadContainerCommand unloadContainerCommand = new UnloadContainerCommand();
        unloadContainerCommand.setId(container.getId());
        unloadContainerCommand.setTimeStamp();
        unloadContainerCommand.setUnloadPos(unloadPos);

        try {
            container.executeCommand(unloadContainerCommand);
            executeCommand(container, unloadContainerCommand);
            Connection.getInstance().sendCommandQueue();
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
            GwtCommon.sendLogToServer("Can not send command: Id is not synchronized: " + id);
            return true;
        }
        return false;
    }

    private void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        Connection.getInstance().addCommandToQueue(baseCommand);
        ClientUserTracker.getInstance().onExecuteCommand(baseCommand);
        Simulation.getInstance().onSendCommand(syncItem, baseCommand);
        addActiveItem(syncItem);
    }

    public void injectCommand(BaseCommand baseCommand) {
        try {
            SyncBaseItem syncBaseItem = (SyncBaseItem) ItemContainer.getInstance().getItem(baseCommand.getId());
            syncBaseItem.executeCommand(baseCommand);
            executeCommand(syncBaseItem, baseCommand);
            Connection.getInstance().sendCommandQueue();
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }

    private double calculateFactor(long time) {
        if (lastTickTime > 0) {
            return (double) (time - lastTickTime) / 1000.0;
        } else {
            return 1.0;
        }
    }

    public void clear() {
        tmpAddActiveItems.clear();
        tmpRemoveActiveItems.addAll(activeItems);
    }
}
