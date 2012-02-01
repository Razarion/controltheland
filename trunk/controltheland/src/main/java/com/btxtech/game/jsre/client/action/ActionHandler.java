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
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.RectangleFormation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.action.impl.CommonActionServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Aug 7, 2009
 * Time: 10:22:31 PM
 */
public class ActionHandler extends CommonActionServiceImpl implements CommonActionService {
    private final static ActionHandler INSTANCE = new ActionHandler();
    private static final int TICK_INTERVAL = 40;
    private long lastTickTime = 0;
    private final HashSet<SyncTickItem> activeItems = new HashSet<SyncTickItem>();
    private HashSet<SyncTickItem> tmpAddActiveItems = new HashSet<SyncTickItem>();
    private HashSet<SyncTickItem> tmpRemoveActiveItems = new HashSet<SyncTickItem>();
    private Logger log = Logger.getLogger(ActionHandler.class.getName());

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
            Iterator<SyncTickItem> iterator = activeItems.iterator();
            while (iterator.hasNext()) {
                SyncTickItem activeItem = iterator.next();
                try {
                    if (!activeItem.tick(factor)) {
                        iterator.remove();
                        activeItem.stop();
                        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                            ActionHandler.getInstance().addGuardingBaseItem(activeItem);
                            ClientServices.getInstance().getConnectionService().sendSyncInfo(activeItem);
                            if (activeItem instanceof SyncBaseItem) {
                                SimulationConditionServiceImpl.getInstance().onSyncItemDeactivated((SyncBaseItem) activeItem);
                            }
                        }
                    }
                } catch (ItemDoesNotExistException ife) {
                    iterator.remove();
                    activeItem.stop();
                    ClientServices.getInstance().getConnectionService().sendSyncInfo(activeItem);
                    log.warning("ItemDoesNotExistException");
                } catch (PositionTakenException ife) {
                    iterator.remove();
                    activeItem.stop();
                    ClientServices.getInstance().getConnectionService().sendSyncInfo(activeItem);
                    log.warning("PositionTakenException");
                } catch (PathCanNotBeFoundException e) {
                    iterator.remove();
                    activeItem.stop();
                    ClientServices.getInstance().getConnectionService().sendSyncInfo(activeItem);
                    log.warning("PathCanNotBeFoundException: " + e.getMessage());
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    activeItem.stop();
                    iterator.remove();
                    ClientServices.getInstance().getConnectionService().sendSyncInfo(activeItem);
                }
            }
            lastTickTime = time;
        }
    }

    @Override
    public void syncItemActivated(SyncTickItem syncTickItem) {
        tmpAddActiveItems.add(syncTickItem);
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
            ActionHandler.getInstance().addGuardingBaseItem(syncTickItem);
        }
    }

    public void removeActiveItem(SyncTickItem syncTickItem) {
        tmpRemoveActiveItems.add(syncTickItem);
    }

    public void move(Collection<ClientSyncItem> clientSyncItems, Index destination) {
        if (clientSyncItems.isEmpty()) {
            return;
        }
        RectangleFormation rectangleFormation = new RectangleFormation(destination, clientSyncItems);
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
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
            }
        }
        Connection.getInstance().sendCommandQueue();
    }

    public void build(Collection<ClientSyncItem> clientSyncItems, Index positionToBeBuild, BaseItemType toBeBuilt) throws NoSuchItemTypeException {
        if (!ClientBase.getInstance().checkItemLimit4ItemAddingDialog(toBeBuilt)) {
            return;
        }

        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncBuilder()) {
                if (ClientTerritoryService.getInstance().isAllowed(positionToBeBuild, clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(positionToBeBuild, toBeBuilt)) {

                    build(clientSyncItem.getSyncBaseItem(), positionToBeBuild, toBeBuilt);
                    // Just get the first CV to buildBuilding the building
                    // Prevent to buildBuilding multiple buildings
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

    public void finalizeBuild(Collection<ClientSyncItem> builders, ClientSyncItem building) {
        List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
        for (ClientSyncItem builder : builders) {
            if (builder.getSyncBaseItem().hasSyncBuilder()
                    && ClientTerritoryService.getInstance().isAllowed(building.getSyncItem().getSyncItemArea().getPosition(), builder.getSyncBaseItem())
                    && ClientTerritoryService.getInstance().isAllowed(building.getSyncItem().getSyncItemArea().getPosition(), building.getSyncBaseItem())
                    && builder.getSyncBaseItem().getSyncBuilder().getBuilderType().isAbleToBuild(building.getSyncBaseItem().getItemType().getId())) {
                attackFormationItemList.add(new AttackFormationItem(builder.getSyncBaseItem(), builder.getSyncBaseItem().getSyncBuilder().getBuilderType().getRange()));
            }
        }
        attackFormationItemList = ClientCollisionService.getInstance().setupDestinationHints(building.getSyncBaseItem(), attackFormationItemList);
        for (AttackFormationItem item : attackFormationItemList) {
            if (item.isInRange()) {
                finalizeBuild(item.getSyncBaseItem(), building.getSyncBaseItem(), item.getDestinationHint(), item.getDestinationAngel());
            } else {
                move(item.getSyncBaseItem(), item.getDestinationHint());
            }

        }
        Connection.getInstance().sendCommandQueue();
    }

    public void fabricate(Collection<ClientSyncItem> clientSyncItems, BaseItemType itemTypeToBuild) throws NoSuchItemTypeException {
        if (!ClientBase.getInstance().checkItemLimit4ItemAddingDialog(itemTypeToBuild)) {
            return;
        }

        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                if (ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), itemTypeToBuild)) {
                    fabricate(clientSyncItem.getSyncBaseItem(), itemTypeToBuild);
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.build(): can not cast to FactorySyncItem:" + clientSyncItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
    }

    public void attack(Collection<ClientSyncItem> clientSyncItems, SyncBaseItem target) {
        List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncWeapon()) {
                if (ClientTerritoryService.getInstance().isAllowed(clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())
                        && ClientTerritoryService.getInstance().isAllowed(target.getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())
                        && clientSyncItem.getSyncBaseItem().getSyncWeapon().isItemTypeAllowed(target)) {
                    if (clientSyncItem.getSyncBaseItem().getSyncWeapon().isAttackAllowedWithoutMoving(target)) {
                        attack(clientSyncItem.getSyncBaseItem(),
                                target,
                                clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(),
                                clientSyncItem.getSyncBaseItem().getSyncItemArea().getTurnToAngel(target.getSyncItemArea()),
                                clientSyncItem.getSyncBaseItem().hasSyncMovable());
                    } else if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                        attackFormationItemList.add(new AttackFormationItem(clientSyncItem.getSyncBaseItem(),
                                clientSyncItem.getSyncBaseItem().getSyncWeapon().getWeaponType().getRange()));
                    }
                }
            }
        }
        attackFormationItemList = ClientCollisionService.getInstance().setupDestinationHints(target, attackFormationItemList);
        for (AttackFormationItem item : attackFormationItemList) {
            if (item.isInRange()) {
                attack(item.getSyncBaseItem(), target, item.getDestinationHint(), item.getDestinationAngel(), true);
            } else {
                move(item.getSyncBaseItem(), item.getDestinationHint());
            }

        }

        Connection.getInstance().sendCommandQueue();
    }

    public void collect(Collection<ClientSyncItem> clientSyncItems, SyncResourceItem money) {
        List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
        for (ClientSyncItem clientSyncItem : clientSyncItems) {
            if (clientSyncItem.getSyncBaseItem().hasSyncHarvester()) {
                if (ClientTerritoryService.getInstance().isAllowed(money.getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem())) {
                    attackFormationItemList.add(new AttackFormationItem(clientSyncItem.getSyncBaseItem(), clientSyncItem.getSyncBaseItem().getSyncHarvester().getHarvesterType().getRange()));
                }
            }
        }
        attackFormationItemList = ClientCollisionService.getInstance().setupDestinationHints(money, attackFormationItemList);
        for (AttackFormationItem item : attackFormationItemList) {
            if (item.isInRange()) {
                collect(item.getSyncBaseItem(), money, item.getDestinationHint(), item.getDestinationAngel());
            } else {
                move(item.getSyncBaseItem(), item.getDestinationHint());
            }

        }

        Connection.getInstance().sendCommandQueue();
    }

    public void loadContainer(ClientSyncItem container, Collection<ClientSyncItem> items) {
        if (!container.getSyncBaseItem().hasSyncItemContainer()) {
            GwtCommon.sendLogToServer("ActionHandler.loadContainer(): can not cast to ItemContainer:" + container);
            return;
        }
        SyncBaseItem syncBaseItem = CommonJava.getFirst(items).getSyncBaseItem();
        AttackFormationItem attackFormationItem = ClientCollisionService.getInstance().getDestinationHint(syncBaseItem,
                container.getSyncBaseItem().getSyncItemContainer().getRange(),
                container.getSyncBaseItem().getSyncItemArea(),
                container.getSyncBaseItem().getTerrainType());

        if (!attackFormationItem.isInRange()) {
            move(items, container.getSyncBaseItem().getSyncItemArea().getPosition());
            return;
        }

        for (ClientSyncItem item : items) {
            if (item.getSyncBaseItem().hasSyncMovable()) {
                if (ClientTerritoryService.getInstance().isAllowed(container.getSyncBaseItem().getSyncItemArea().getPosition(), container.getSyncBaseItem())
                        && container.getSyncBaseItem().getSyncItemContainer().isAbleToContain(item.getSyncBaseItem())
                        && item.getSyncBaseItem().getSyncMovable().isLoadPosReachable(container.getSyncBaseItem().getSyncItemContainer())) {
                    loadContainer(container.getSyncBaseItem(), item.getSyncBaseItem(), attackFormationItem.getDestinationHint());
                }
            } else {
                GwtCommon.sendLogToServer("ActionHandler.loadContainer(): has no movable:" + item);
            }
        }
        Connection.getInstance().sendCommandQueue();
    }

    public void unloadContainer(ClientSyncItem container, Index unloadPos) {
        unloadContainer(container.getSyncBaseItem(), unloadPos);
        Connection.getInstance().sendCommandQueue();
    }

    public void executeLaunchCommand(int absoluteX, int absoluteY) {
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (selection.getCount() != 1) {
            return;
        }


        if (!selection.canLaunch()) {
            return;
        }

        launch(selection.getFirst().getSyncBaseItem(), new Index(absoluteX, absoluteY));
        Connection.getInstance().sendCommandQueue();
        SideCockpit.getInstance().getCockpitMode().clearLaunchMode();
    }

    protected void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) throws ItemLimitExceededException, HouseSpaceExceededException, ItemDoesNotExistException, NoSuchItemTypeException, InsufficientFundsException {
        try {
            syncItem.executeCommand(baseCommand);

            Connection.getInstance().addCommandToQueue(baseCommand);
            tmpAddActiveItems.add(syncItem);
            if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                removeGuardingBaseItem(syncItem);
            }
        } catch (ItemDoesNotExistException ignore) {
            // Target has may been killed in the meantime
        } catch (Throwable t) {
            log.log(Level.SEVERE, "", t);
        }
        ClientServices.getInstance().getConnectionService().sendSyncInfo(syncItem);
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
        clearGuardingBaseItem();
    }

    @Override
    protected Services getServices() {
        return ClientServices.getInstance();
    }
}
