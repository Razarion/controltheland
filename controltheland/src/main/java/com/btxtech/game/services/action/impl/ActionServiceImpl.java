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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:50:08 PM
 */
@Component("actionService")
public class ActionServiceImpl extends TimerTask implements ActionService {
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
    private final HashSet<SyncTickItem> activeItems = new HashSet<SyncTickItem>();
    private final HashSet<SyncBaseItem> guardingItems = new HashSet<SyncBaseItem>();
    private final ArrayList<SyncTickItem> tmpActiveItems = new ArrayList<SyncTickItem>();
    private Timer timer;
    private Log log = LogFactory.getLog(ActionServiceImpl.class);
    private long lastTickTime = 0;
    private boolean pause = false;

    @PostConstruct
    public void start() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(this, 0, TICK_TIME_MILI_SECONDS);
    }

    @PreDestroy
    public void stop() {
        if (timer != null) {
            timer.cancel();
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
            guardingItems.clear();
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
                            activeItem.stop();
                            addGuardingBaseItem(activeItem);
                            connectionService.sendSyncInfo(activeItem);
                            if (activeItem instanceof SyncBaseItem && ((SyncBaseItem) activeItem).hasSyncHarvester()) {
                                baseService.sendAccountBaseUpdate((SyncBaseItem) activeItem);
                            }
                            if (activeItem instanceof SyncBaseItem && ((SyncBaseItem) activeItem).isMoneyEarningOrConsuming()) {
                                baseService.sendAccountBaseUpdate((SyncBaseItem) activeItem);
                            }
                        }
                    } catch (PositionTakenException ife) {
                        log.info("PositionTakenException: " + ife.getMessage());
                        activeItem.stop();
                        iterator.remove();
                        connectionService.sendSyncInfo(activeItem);
                    } catch (Throwable t) {
                        log.error("", t);
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

    @Override
    public void addGuardingBaseItem(SyncTickItem syncTickItem) {
        if (!(syncTickItem instanceof SyncBaseItem)) {
            return;
        }

        SyncBaseItem syncBaseItem = (SyncBaseItem) syncTickItem;
        if (!syncBaseItem.hasSyncWeapon() || !syncBaseItem.isAlive()) {
            return;
        }

        if (syncBaseItem.hasSyncConsumer() && !syncBaseItem.getSyncConsumer().isOperating()) {
            return;
        }

        if (syncBaseItem.getPosition() == null) {
            return;
        }

        if (!territoryService.isAllowed(syncBaseItem.getPosition(), syncBaseItem)) {
            return;
        }

        if (checkGuardingItemHasEnemiesInRange(syncBaseItem)) {
            return;
        }

        synchronized (guardingItems) {
            guardingItems.add(syncBaseItem);
        }
    }

    public void removeGuardingBaseItem(SyncBaseItem syncItem) {
        if (!syncItem.hasSyncWeapon()) {
            return;
        }

        synchronized (guardingItems) {
            guardingItems.remove(syncItem);
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

    private boolean checkGuardingItemHasEnemiesInRange(SyncBaseItem guardingItem) {
        SyncBaseItem target = itemService.getFirstEnemyItemInRange(guardingItem);
        if (target == null) {
            return false;
        }
        AttackCommand attackCommand = createAttackCommand(guardingItem, target);
        try {
            executeCommand(attackCommand, true);
        } catch (IllegalAccessException e) {
            log.error("", e);
        } catch (ItemDoesNotExistException e) {
            // Ignore, may the item has just now been destroyed
        }
        return true;
    }

    @Override
    public void interactionGuardingItems(SyncBaseItem target) {
        ArrayList<AttackCommand> cmds = new ArrayList<AttackCommand>();
        // Collect command
        synchronized (guardingItems) {
            for (SyncBaseItem attacker : guardingItems) {
                //TankSyncItem tank = (TankSyncItem) baseSyncItem;
                if (attacker.isEnemy(target)
                        && attacker.getSyncWeapon().isAttackAllowedWithoutMoving(target)
                        && attacker.getSyncWeapon().isItemTypeAllowed(target)) {
                    AttackCommand attackCommand = createAttackCommand(attacker, target);
                    cmds.add(attackCommand);
                    connectionService.sendSyncInfo(target);
                }
            }
        }
        // Execute commands
        for (AttackCommand cmd : cmds) {
            try {
                executeCommand(cmd, true);
            } catch (IllegalAccessException e) {
                log.error("", e);
            } catch (ItemDoesNotExistException e) {
                // Ignore, may the item has just now been destroyed
            }
        }
    }

    private AttackCommand createAttackCommand(SyncBaseItem attacker, SyncBaseItem target) {
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attacker.getId());
        attackCommand.setTimeStamp();
        attackCommand.setFollowTarget(false);
        attackCommand.setTarget(target.getId());
        return attackCommand;
    }


    @Override
    public void buildFactory(SyncBaseItem builder, Index position, BaseItemType itemTypeToBuild) {
        builder.stop();
        BuilderCommand builderCommand = new BuilderCommand();
        builderCommand.setId(builder.getId());
        builderCommand.setTimeStamp();
        builderCommand.setToBeBuilt(itemTypeToBuild.getId());
        builderCommand.setPositionToBeBuilt(position);
        try {
            executeCommand(builderCommand, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void build(SyncBaseItem factory, BaseItemType itemType) {
        FactoryCommand factoryCommand = new FactoryCommand();
        factoryCommand.setId(factory.getId());
        factoryCommand.setTimeStamp();
        factoryCommand.setToBeBuilt(itemType.getId());
        try {
            executeCommand(factoryCommand, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void collect(SyncBaseItem collector, SyncResourceItem money) {
        collector.stop();
        MoneyCollectCommand collectCommand = new MoneyCollectCommand();
        collectCommand.setId(collector.getId());
        collectCommand.setTimeStamp();
        collectCommand.setTarget(money.getId());

        try {
            executeCommand(collectCommand, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void attack(SyncBaseItem tank, SyncBaseItem target, boolean followTarget) {
        tank.stop();
        AttackCommand attackCommand = createAttackCommand(tank, target);
        attackCommand.setFollowTarget(true);

        try {
            executeCommand(attackCommand, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void move(SyncBaseItem syncItem, Index destination) {
        syncItem.stop();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(syncItem.getId());
        moveCommand.setTimeStamp();
        moveCommand.setDestination(destination);
        try {
            executeCommand(moveCommand, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void upgrade(SyncBaseItem item) {
        throw new UnsupportedOperationException();
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
    public void executeCommand(BaseCommand baseCommand, boolean cmdFromSystem) throws IllegalAccessException, ItemDoesNotExistException {
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
        }
        try {
            syncItem.stop();
            syncItem.executeCommand(baseCommand);
            finalizeCommand(syncItem);
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

    @Override
    public void executeCommands(List<BaseCommand> baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            try {
                executeCommand(baseCommand, false);
            } catch (Throwable t) {
                log.debug(" ", t);
            }
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

}
