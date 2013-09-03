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
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.RectangleFormation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.action.impl.CommonActionServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
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
    public interface CommandListener {
        void onCommand(BaseCommand baseCommand);
    }
    public interface IdleListener {
        void onIdle(SyncBaseItem syncBaseItem);
    }

    private final static ActionHandler INSTANCE = new ActionHandler();
    private static final int TICK_INTERVAL = 40;
    private long lastTickTime = 0;
    private final HashSet<SyncTickItem> activeItems = new HashSet<SyncTickItem>();
    private HashSet<SyncTickItem> tmpAddActiveItems = new HashSet<SyncTickItem>();
    private HashSet<SyncTickItem> tmpRemoveActiveItems = new HashSet<SyncTickItem>();
    private Logger log = Logger.getLogger(ActionHandler.class.getName());
    private CommandListener commandListener;
    private IdleListener idleListener;

    public static ActionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ActionHandler() {
        Timer timer = new TimerPerfmon(PerfmonEnum.ACTION_HANDLER) {
            @Override
            public void runPerfmon() {
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
                        try {
                            iterator.remove();
                            activeItem.stop();
                            if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                                ActionHandler.getInstance().addGuardingBaseItem(activeItem);
                                Connection.getInstance().sendSyncInfo(activeItem);
                                if (activeItem instanceof SyncBaseItem) {
                                    SimulationConditionServiceImpl.getInstance().onSyncItemDeactivated((SyncBaseItem) activeItem);
                                    if(idleListener != null) {
                                        idleListener.onIdle((SyncBaseItem) activeItem);
                                    }
                                }
                            }
                        } catch (Throwable throwable) {
                            ClientExceptionHandler.handleException(throwable);
                            Connection.getInstance().sendSyncInfo(activeItem);
                        }
                    }
                } catch (ItemDoesNotExistException ife) {
                    iterator.remove();
                    activeItem.stop();
                    Connection.getInstance().sendSyncInfo(activeItem);
                    log.warning("ItemDoesNotExistException");
                } catch (PositionTakenException ife) {
                    iterator.remove();
                    activeItem.stop();
                    Connection.getInstance().sendSyncInfo(activeItem);
                    log.warning("PositionTakenException: " + ife.getMessage());
                } catch (PathCanNotBeFoundException e) {
                    iterator.remove();
                    activeItem.stop();
                    Connection.getInstance().sendSyncInfo(activeItem);
                    log.warning("PathCanNotBeFoundException: " + e.getMessage());
                } catch (PlaceCanNotBeFoundException e) {
                    iterator.remove();
                    activeItem.stop();
                    Connection.getInstance().sendSyncInfo(activeItem);
                    log.warning("PlaceCanNotBeFoundException: " + e.getMessage());
                } catch (Throwable throwable) {
                    ClientExceptionHandler.handleException(throwable);
                    activeItem.stop();
                    iterator.remove();
                    Connection.getInstance().sendSyncInfo(activeItem);
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

    public void move(Collection<SyncBaseItem> syncBaseItems, Index destination) {
        if (syncBaseItems.isEmpty()) {
            return;
        }
        try {
            RectangleFormation rectangleFormation = new RectangleFormation(destination, syncBaseItems, getPlanetServices().getTerrainService());
            for (SyncBaseItem syncBaseItem : syncBaseItems) {
                if (syncBaseItem.hasSyncMovable()) {
                    Index pos = null;
                    while (pos == null) {
                        pos = rectangleFormation.calculateNextEntry();
                        if (pos == null) {
                            continue;
                        }
                        SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(pos);
                        if (!syncBaseItem.getTerrainType().getSurfaceTypes().contains(surfaceType)) {
                            pos = null;
                        }
                    }
                    move(syncBaseItem, pos);
                }
            }
            Connection.getInstance().sendCommandQueue();
        } catch (PathCanNotBeFoundException e) {
            log.warning("move: " + e.getMessage());
        }
    }

    public void build(Collection<SyncBaseItem> syncBaseItems, Index positionToBeBuild, BaseItemType toBeBuilt) throws NoSuchItemTypeException {
        if (!ClientBase.getInstance().checkItemLimit4ItemAddingDialog(toBeBuilt)) {
            return;
        }

        GroupCommandHelperPosition groupCommandHelperPosition = new GroupCommandHelperPosition() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem builder, BaseItemType toBeBuilt, Index positionToBeBuild) {
                return builder.hasSyncBuilder();
            }

            @Override
            protected void executeCommand(SyncBaseItem builder, BaseItemType toBeBuilt, Index positionToBeBuild, Index destinationHint, double destinationAngel) {
                build(builder, positionToBeBuild, toBeBuilt, destinationHint, destinationAngel);
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem) {
                return syncBaseItem.getSyncBuilder().getBuilderType().getRange();
            }
        };
        groupCommandHelperPosition.process(CommonJava.getFirst(syncBaseItems), toBeBuilt, positionToBeBuild, true);
    }

    public void finalizeBuild(Collection<SyncBaseItem> builders, SyncBaseItem building) {
        GroupCommandHelperItemType<SyncBaseItem> commandHelperItemType = new GroupCommandHelperItemType<SyncBaseItem>() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem syncBaseItem, SyncBaseItem building) {
                return syncBaseItem.hasSyncBuilder() && syncBaseItem.getSyncBuilder().getBuilderType().isAbleToBuild(building.getItemType().getId());
            }

            @Override
            protected void executeCommand(SyncBaseItem syncBaseItem, SyncBaseItem building, Index destinationHint, double destinationAngel) {
                finalizeBuild(syncBaseItem, building, destinationHint, destinationAngel);
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem, SyncBaseItem target) {
                return syncBaseItem.getSyncBuilder().getBuilderType().getRange();
            }
        };
        commandHelperItemType.process(builders, building, true);
    }

    public void fabricate(Collection<SyncBaseItem> syncBaseItems, BaseItemType itemTypeToBuild) throws NoSuchItemTypeException {
        if (!ClientBase.getInstance().checkItemLimit4ItemAddingDialog(itemTypeToBuild)) {
            return;
        }

        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            if (syncBaseItem.hasSyncFactory()) {
                fabricate(syncBaseItem, itemTypeToBuild);
            } else {
                log.severe("ActionHandler.build(): can not cast to FactorySyncItem:" + syncBaseItem);
            }
        }
        Connection.getInstance().sendCommandQueue();
    }

    public void attack(Collection<SyncBaseItem> syncBaseItems, SyncBaseItem target) {
        GroupCommandHelperItemType<SyncBaseItem> commandHelperItemType = new GroupCommandHelperItemType<SyncBaseItem>() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem syncBaseItem, SyncBaseItem target) {
                return syncBaseItem.hasSyncWeapon() && syncBaseItem.getSyncWeapon().isItemTypeAllowed(target) && syncBaseItem.isEnemy(target);
            }

            @Override
            protected void executeCommand(SyncBaseItem syncBaseItem, SyncBaseItem target, Index destinationHint, double destinationAngel) {
                attack(syncBaseItem, target, destinationHint, destinationAngel, syncBaseItem.hasSyncMovable());
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem, SyncBaseItem target) {
                return syncBaseItem.getSyncWeapon().getWeaponType().getRange();
            }
        };
        commandHelperItemType.process(syncBaseItems, target, true);
    }

    public void collect(Collection<SyncBaseItem> syncBaseItems, SyncResourceItem money) {
        GroupCommandHelperItemType<SyncResourceItem> commandHelperItemType = new GroupCommandHelperItemType<SyncResourceItem>() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem syncBaseItem, SyncResourceItem money) {
                return syncBaseItem.hasSyncHarvester();
            }

            @Override
            protected void executeCommand(SyncBaseItem syncBaseItem, SyncResourceItem money, Index destinationHint, double destinationAngel) {
                collect(syncBaseItem, money, destinationHint, destinationAngel);
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem, SyncResourceItem money) {
                return syncBaseItem.getSyncHarvester().getHarvesterType().getRange();
            }
        };
        commandHelperItemType.process(syncBaseItems, money, true);
    }

    public void pickupBox(Collection<SyncBaseItem> clientSyncItems, SyncBoxItem box) {
        GroupCommandHelperItemType<SyncBoxItem> commandHelperItemType = new GroupCommandHelperItemType<SyncBoxItem>() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem syncBaseItem, SyncBoxItem box) {
                return true;
            }

            @Override
            protected void executeCommand(SyncBaseItem picker, SyncBoxItem box, Index destinationHint, double destinationAngel) {
                pickupBox(picker, box, destinationHint, destinationAngel);
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem, SyncBoxItem box) {
                return syncBaseItem.getBaseItemType().getBoxPickupRange();
            }
        };
        commandHelperItemType.process(clientSyncItems, box, true);
    }

    public void loadContainer(SyncBaseItem container, Collection<SyncBaseItem> items) {
        if (!container.hasSyncItemContainer()) {
            log.severe("ActionHandler.loadContainer(): can not cast to ItemContainer:" + container);
            return;
        }
        GroupCommandHelperItemType<SyncBaseItem> commandHelperItemType = new GroupCommandHelperItemType<SyncBaseItem>() {
            @Override
            protected boolean isCommandPossible(SyncBaseItem syncBaseItem, SyncBaseItem container) {
                return container.getSyncItemContainer().isAbleToLoad(syncBaseItem) && syncBaseItem.hasSyncMovable();
            }

            @Override
            protected void executeCommand(SyncBaseItem syncBaseItem, SyncBaseItem container, Index destinationHint, double destinationAngel) {
                loadContainer(container, syncBaseItem);
            }

            @Override
            protected int getRange(SyncBaseItem syncBaseItem, SyncBaseItem container) {
                return container.getSyncItemContainer().getRange();
            }
        };
        commandHelperItemType.process(items, container, false);
    }

    public void unloadContainerFindPosition(SyncBaseItem container, Index unloadPos) {
        try {
            Id id = CommonJava.getFirst(container.getSyncItemContainer().getContainedItems());
            SyncBaseItem containedItem = (SyncBaseItem) ItemContainer.getInstance().getItem(id);

            GroupCommandHelperPosition groupCommandHelperPosition = new GroupCommandHelperPosition() {
                @Override
                protected boolean isCommandPossible(SyncBaseItem container, BaseItemType toBeUnloaded, Index unloadPosition) {
                    return container.hasSyncItemContainer() && container.getSyncItemContainer().atLeastOneAllowedToUnload(unloadPosition);
                }

                @Override
                protected void executeCommand(SyncBaseItem container, BaseItemType toBeUnloaded, Index unloadPos, Index destinationHint, double destinationAngel) {
                    unloadContainer(container, unloadPos);
                }

                @Override
                protected int getRange(SyncBaseItem syncBaseItem) {
                    return syncBaseItem.getSyncItemContainer().getRange();
                }
            };
            groupCommandHelperPosition.process(container, containedItem.getBaseItemType(), unloadPos, false);
        } catch (ItemDoesNotExistException e) {
            log.log(Level.SEVERE, "ActionHandler.unloadContainer()", e);
        }
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

        launch(selection.getFirst(), new Index(absoluteX, absoluteY));
        Connection.getInstance().sendCommandQueue();
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
        Connection.getInstance().sendSyncInfo(syncItem);
        SoundHandler.getInstance().playCommandSound(syncItem);
        if (commandListener != null) {
            commandListener.onCommand(baseCommand);
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
        clearGuardingBaseItem();
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return ClientGlobalServices.getInstance();
    }

    @Override
    protected PlanetServices getPlanetServices() {
        return ClientPlanetServices.getInstance();
    }

    public boolean isBusy() {
        return !activeItems.isEmpty() || !tmpAddActiveItems.isEmpty();
    }

    public void moveItemTypeEditor(SyncBaseItem syncBaseItem, Index destination, double destinationAngel) {
        syncBaseItem.stop();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(syncBaseItem.getId());
        moveCommand.setTimeStamp();
        Path path = new Path(syncBaseItem.getSyncItemArea().getPosition(), destination, true);
        path.setDestinationAngel(destinationAngel);
        List<Index> indexPath = new ArrayList<Index>();
        indexPath.add(syncBaseItem.getSyncItemArea().getPosition());
        indexPath.add(destination);
        path.setPath(indexPath);
        moveCommand.setPathToDestination(path);
        try {
            executeCommand(syncBaseItem, moveCommand);
        } catch (PathCanNotBeFoundException e) {
            log.warning("PathCanNotBeFoundException: " + e.getMessage());
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void setCommandListener(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    public void setIdleListener(IdleListener idleListener) {
        this.idleListener = idleListener;
    }
}
