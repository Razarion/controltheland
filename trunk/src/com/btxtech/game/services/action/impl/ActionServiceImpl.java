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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.CollisionServiceChangedListener;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
public class ActionServiceImpl extends TimerTask implements ActionService, CollisionServiceChangedListener {
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
    private final HashSet<SyncBaseItem> activeItems = new HashSet<SyncBaseItem>();
    private final HashSet<SyncBaseItem> guardingItems = new HashSet<SyncBaseItem>();
    private ArrayList<SyncBaseItem> tmpActiveItems = new ArrayList<SyncBaseItem>();
    private Timer timer;
    private Log log = LogFactory.getLog(ActionServiceImpl.class);
    private long lastTickTime = 0;
    private final HashSet<SyncResourceItem> moneys = new HashSet<SyncResourceItem>();
    private boolean pause = false;

    @PostConstruct
    public void start() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(this, 0, TICK_TIME_MILI_SECONDS);
        collisionService.addCollisionServiceChangedListener(this);
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
    public void moneyItemDeleted(SyncResourceItem moneyImpl) {
        if (moneyImpl.isMissionMoney()) {
            return;
        }
        synchronized (moneys) {
            moneys.remove(moneyImpl);
        }
        setupAllMoneyStacks();
    }

    @Override
    public void reload() {
        synchronized (activeItems) {
            activeItems.clear();
            guardingItems.clear();
            tmpActiveItems.clear();
            moneys.clear();
            Collection<SyncItem> syncItems = itemService.getItemsCopy();
            for (SyncItem syncItem : syncItems) {
                if (syncItem instanceof SyncResourceItem) {
                    moneys.add((SyncResourceItem) syncItem);
                } else if (syncItem instanceof SyncBaseItem) {
                    activeItems.add((SyncBaseItem) syncItem);
                } else {
                    log.error("Unknwon entry during reload: " + syncItem);
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
                activeItems.addAll(tmpActiveItems);
                tmpActiveItems.clear();
                Iterator<SyncBaseItem> iterator = activeItems.iterator();
                long time = System.currentTimeMillis();
                double factor = calculateFactor(time);
                while (iterator.hasNext()) {
                    SyncBaseItem activeItem = iterator.next();
                    try {
                        if (!activeItem.tick(factor)) {
                            iterator.remove();
                            addGuardingBaseItem(activeItem);
                            connectionService.sendSyncInfo(activeItem);
                            if (activeItem.hasSyncHarvester()) {
                                baseService.sendAccountBaseUpdate(activeItem);
                            }
                        }
                    } catch (InsufficientFundsException ife) {
                        activeItem.stop();
                        iterator.remove();
                        connectionService.sendSyncInfo(activeItem);
                        baseService.sendAccountBaseUpdate(activeItem);
                        log.info("InsufficientFundsException " + activeItem);
                    } catch (Throwable t) {
                        activeItem.stop();
                        iterator.remove();
                        connectionService.sendSyncInfo(activeItem);
                        log.error("", t);
                    }
                }
                lastTickTime = time;
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    public void addGuardingBaseItem(SyncBaseItem syncItem) {
        if (!syncItem.hasSyncWaepon() || !syncItem.isAlive()) {
            return;
        }

        if (syncItem.hasSyncConsumer() && !syncItem.getSyncConsumer().isOperating()) {
            return;
        }

        if (checkGuardingItemHasEnemiesInRange(syncItem)) {
            return;
        }

        synchronized (guardingItems) {
            guardingItems.add(syncItem);
        }
    }

    public void removeGuardingBaseItem(SyncBaseItem syncItem) {
        if (!syncItem.hasSyncWaepon()) {
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
                    addGuardingBaseItem((SyncBaseItem) syncItem);
                }
                break;
            case POSITION:
                if (syncItem instanceof SyncBaseItem) {
                    interactionGuardingItems((SyncBaseItem) syncItem);
                }
                break;
        }

        if (change == Change.POSITION && syncItem instanceof SyncBaseItem) {
            interactionGuardingItems((SyncBaseItem) syncItem);
        }
    }

    private boolean checkGuardingItemHasEnemiesInRange(SyncBaseItem guardingItem) {
        SyncBaseItem target = itemService.getFirstEnemyItemInRange(guardingItem, guardingItem.getSyncWaepon().getWeaponType().getRange());
        if (target == null) {
            return false;
        }
        AttackCommand attackCommand = createAttackCommand(target, guardingItem);
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
                if (attacker.isEnemy(target) && attacker.getSyncWaepon().inAttackRange(target)) {
                    AttackCommand attackCommand = createAttackCommand(target, attacker);
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

    private AttackCommand createAttackCommand(SyncBaseItem target, SyncBaseItem attacker) {
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attacker.getId());
        attackCommand.setTimeStamp();
        attackCommand.setFollowTarget(false);
        attackCommand.setTarget(target.getId());
        return attackCommand;
    }

    @Override
    public void syncItemActivated(SyncBaseItem syncBaseItem) {
        synchronized (activeItems) {
            tmpActiveItems.add(syncBaseItem);
        }
        addGuardingBaseItem(syncBaseItem);
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
            log.info("Can not execute command. Item does no longer exist " + baseCommand);
            return;
        }
        if (!cmdFromSystem) {
            baseService.checkBaseAccess(syncItem);
            userTrackingService.saveUserCommand(baseCommand);
        }
        try {
            syncItem.stop();
            syncItem.executeCommand(baseCommand);
            finalizeCommand(syncItem, cmdFromSystem);
        } catch (ItemDoesNotExistException e) {
            log.info("Can not execute command. Item does no longer exist " + baseCommand);
            connectionService.sendSyncInfo(syncItem);
        } catch (InsufficientFundsException e) {
            connectionService.sendSyncInfo(syncItem);
            baseService.sendAccountBaseUpdate(syncItem);
        } catch (Exception e) {
            log.error("", e);
            connectionService.sendSyncInfo(syncItem);
        }
    }

    private void finalizeCommand(SyncBaseItem syncItem, boolean cmdFromSystem) {
        if (cmdFromSystem) {
            tmpActiveItems.add(syncItem);
        } else {
            synchronized (activeItems) {
                if (!activeItems.contains(syncItem)) {
                    activeItems.add(syncItem);
                }
            }
        }
        removeGuardingBaseItem(syncItem);
        connectionService.sendSyncInfo(syncItem);
    }

    @Override
    public void setupAllMoneyStacks() {
        for (SyncResourceItem money : moneys) {
            if (!terrainService.isFree(money.getPosition(), money.getItemType())) {
                log.error("Money has wrong position: " + money);
            }
        }
        for (int i = moneys.size(); i < Constants.MONEY_STACK_COUNT; i++) {
            addMoneyStack();
        }
    }

    private void addMoneyStack() {
        try {
            ItemType itemType = itemService.getItemType(Constants.MONEY);
            Index position = collisionService.getFreeRandomPosition(itemType, Constants.MIN_FREE_MONEY_DISTANCE);
            SyncResourceItem money = (SyncResourceItem) itemService.createSyncObject(itemType, position, null, null, 0);
            connectionService.sendSyncInfo(money);
            synchronized (moneys) {
                moneys.add(money);
            }
        } catch (NoSuchItemTypeException e) {
            log.error("setupMoneyStack: " + e.getMessage());
        }
    }

    @Override
    public void collisionServiceChanged() {
        if (itemService.areItemTypesLoaded()) {
            setupAllMoneyStacks();
        }
    }
}
