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

package com.btxtech.game.services.action.impl;

import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.impl.CommonActionServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.BaseDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.PathToDestinationCommand;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:50:08 PM
 */
@Component("actionService")
public class ActionServiceImpl extends CommonActionServiceImpl implements ActionService {
    public static final int TICK_TIME_MILI_SECONDS = 100;

    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ServerEnergyService energyService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private ServerConditionService serverConditionService;
    private final HashSet<SyncTickItem> activeItems = new HashSet<SyncTickItem>();
    private final ArrayList<SyncTickItem> tmpActiveItems = new ArrayList<SyncTickItem>();
    private Timer timer;
    private Log log = LogFactory.getLog(ActionServiceImpl.class);
    private long lastTickTime = 0;
    private boolean pause = false;

    private class ActionServiceTimerTask extends TimerTask {
        @Override
        public void run() {
            if (pause) {
                return;
            }
            try {
                synchronized (activeItems) {
                    synchronized (tmpActiveItems) {
                        activeItems.addAll(tmpActiveItems);
                        tmpActiveItems.clear();
                    }
                    Iterator<SyncTickItem> iterator = activeItems.iterator();
                    long time = System.currentTimeMillis();
                    double factor = calculateFactor(time);
                    while (iterator.hasNext()) {
                        SyncTickItem activeItem = iterator.next();
                        try {
                            if (!activeItem.tick(factor)) {
                                iterator.remove();
                                try {
                                    activeItem.stop();
                                    addGuardingBaseItem(activeItem);
                                    connectionService.sendSyncInfo(activeItem);
                                    if (activeItem instanceof SyncBaseItem) {
                                        SyncBaseItem syncBaseItem = (SyncBaseItem) activeItem;
                                        if (syncBaseItem.hasSyncHarvester()) {
                                            baseService.sendAccountBaseUpdate((SyncBaseItem) activeItem);
                                        }
                                        if (syncBaseItem.isMoneyEarningOrConsuming()) {
                                            baseService.sendAccountBaseUpdate((SyncBaseItem) activeItem);
                                        }
                                        serverConditionService.onSyncItemDeactivated(syncBaseItem);
                                    }
                                } catch (Throwable t) {
                                    log.error("Error during deactivation of active item: " + activeItem, t);
                                }
                            }
                        } catch (BaseDoesNotExistException e) {
                            log.info("BaseDoesNotExistException: " + e.getMessage());
                            activeItem.stop();
                            iterator.remove();
                        } catch (PositionTakenException ife) {
                            log.info("PositionTakenException: " + ife.getMessage());
                            activeItem.stop();
                            iterator.remove();
                            connectionService.sendSyncInfo(activeItem);
                        } catch (PathCanNotBeFoundException e) {
                            log.info("PathCanNotBeFoundException: " + e.getMessage());
                            activeItem.stop();
                            iterator.remove();
                            connectionService.sendSyncInfo(activeItem);
                        } catch (PlaceCanNotBeFoundException e) {
                            log.info("PlaceCanNotBeFoundException: " + e.getMessage());
                            activeItem.stop();
                            iterator.remove();
                            connectionService.sendSyncInfo(activeItem);
                        } catch (Throwable t) {
                            log.error("ActiveItem: " + activeItem, t);
                            activeItem.stop();
                            iterator.remove();
                            connectionService.sendSyncInfo(activeItem);
                        }
                    }
                    lastTickTime = time;
                }
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    @PostConstruct
    public void start() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(new ActionServiceTimerTask(), 0, TICK_TIME_MILI_SECONDS);
    }

    @PreDestroy
    public void stop() {
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch(Throwable t) {
           log.error("", t);
        }
    }

    @Override
    public void pause(boolean pause) {
        if (!pause) {
            lastTickTime = 0;
        }
        this.pause = pause;
    }

    @Override
    public void reload() {
        synchronized (activeItems) {
            activeItems.clear();
            clearGuardingBaseItem();
            tmpActiveItems.clear();
            Collection<SyncItem> syncItems = itemService.getItemsCopy();
            for (SyncItem syncItem : syncItems) {
                if (syncItem instanceof SyncTickItem) {
                    activeItems.add((SyncTickItem) syncItem);
                }
            }
        }

    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case BUILD:
                if (syncItem instanceof SyncBaseItem) {
                    if (((SyncBaseItem) syncItem).isReady()) {
                        addGuardingBaseItem((SyncBaseItem) syncItem);
                    }
                }
                break;
            case POSITION:
                if (syncItem instanceof SyncBaseItem) {
                    interactionGuardingItems((SyncBaseItem) syncItem);
                }
                break;
            case ITEM_TYPE_CHANGED:
                energyService.onItemChanged((SyncBaseItem) syncItem);
                break;
            case CONTAINED_IN_CHANGED:
                energyService.onItemChanged((SyncBaseItem) syncItem);
                break;
        }

        if (change == Change.POSITION && syncItem instanceof SyncBaseItem) {
            interactionGuardingItems((SyncBaseItem) syncItem);
        }
    }

    @Override
    public void syncItemActivated(SyncTickItem syncTickItem) {
        synchronized (tmpActiveItems) {
            tmpActiveItems.add(syncTickItem);
        }
        addGuardingBaseItem(syncTickItem);
    }

    /**
     * @param time The time this tick has been started
     * @return a factor which will be 1.0 if the last tick was called exacrly one second before
     */
    private double calculateFactor(long time) {
        if (lastTickTime > 0) {
            return (double) (time - lastTickTime) / 1000.0;
        } else {
            return 1.0;
        }
    }

    @Override
    protected void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) throws ItemLimitExceededException, HouseSpaceExceededException, ItemDoesNotExistException, NoSuchItemTypeException, InsufficientFundsException, NotYourBaseException {
        executeCommand(baseCommand, true);
    }

    @Override
    public void executeCommand(BaseCommand baseCommand, boolean cmdFromSystem) throws ItemDoesNotExistException, NotYourBaseException {
        SyncBaseItem syncItem;
        try {
            syncItem = (SyncBaseItem) itemService.getItem(baseCommand.getId());
        } catch (ItemDoesNotExistException e) {
            if (log.isDebugEnabled()) {
                log.debug("Can not execute command. Item does no longer exist " + baseCommand);
            }
            return;
        }
        if (!cmdFromSystem) {
            baseService.checkBaseAccess(syncItem);
            userTrackingService.saveUserCommand(baseCommand);
            if (baseCommand instanceof PathToDestinationCommand) {
                if (!collisionService.checkIfPathValid(((PathToDestinationCommand) baseCommand).getPathToDestination())) {
                    log.error("Path is invalid: " + CommonJava.pathToDestinationAsString(((PathToDestinationCommand) baseCommand).getPathToDestination()));
                    connectionService.sendSyncInfo(syncItem);
                    return;
                }
            }
        }
        try {
            syncItem.stop();
            syncItem.executeCommand(baseCommand);
            finalizeCommand(syncItem);
        } catch (PathCanNotBeFoundException e) {
            connectionService.sendSyncInfo(syncItem);
        } catch (ItemDoesNotExistException e) {
            if (log.isDebugEnabled()) {
                log.debug("Can not execute command. Item does no longer exist " + baseCommand);
            }
            connectionService.sendSyncInfo(syncItem);
        } catch (InsufficientFundsException e) {
            connectionService.sendSyncInfo(syncItem);
            baseService.sendAccountBaseUpdate(syncItem);
        } catch (Exception e) {
            log.error("", e);
            connectionService.sendSyncInfo(syncItem);
        }
    }

    private void finalizeCommand(SyncBaseItem syncItem) {
        synchronized (tmpActiveItems) {
            if (!activeItems.contains(syncItem)) {
                tmpActiveItems.add(syncItem);
            }
        }
        removeGuardingBaseItem(syncItem);
        connectionService.sendSyncInfo(syncItem);
    }

    @Override
    public void executeCommands(List<BaseCommand> baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            try {
                executeCommand(baseCommand, false);
            } catch (Throwable t) {
                log.debug("", t);
            }
        }
    }

    @Override
    public boolean isBusy() {
        return !activeItems.isEmpty() || !tmpActiveItems.isEmpty();
    }

    @Override
    protected Services getServices() {
        return serverServices;
    }
}
