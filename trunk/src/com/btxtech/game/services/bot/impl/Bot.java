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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.ai.BaseBalancer;
import com.btxtech.game.jsre.common.ai.ItemTypeBalance;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.services.base.Base;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:24:08
 */
public class Bot implements SyncItemListener {
    enum EnemyStatge {
        NOOB
    }

    private Base humanBase;
    private Base botBase;
    private Services services;
    private Thread thread;
    private BaseBalancer baseBalancer;
    private EnemyStatge enemyStatge = EnemyStatge.NOOB;
    private SyncBaseItem enemyTarget;
    private SyncBaseItem attacker;
    private boolean stopAttack = false;
    private boolean running;

    public Bot(Base botBase, Base humanBase, Services services, Thread thread) {
        this.humanBase = humanBase;
        this.botBase = botBase;
        this.services = services;
        this.thread = thread;
        ArrayList<ItemTypeBalance> itemTypeBalances = new ArrayList<ItemTypeBalance>();
        itemTypeBalances.add(new ItemTypeBalance(Constants.FACTORY, 1)); // First prio
        itemTypeBalances.add(new ItemTypeBalance(Constants.HARVESTER, 1));// Second prio
        itemTypeBalances.add(new ItemTypeBalance(Constants.JEEP, 1));// Third prio
        baseBalancer = new BaseBalancer(itemTypeBalances, services, botBase.getSimpleBase());
        running = true;
    }

    public void action() throws NoSuchItemTypeException {
        baseBalancer.doBalance();
        baseBalancer.doAllIdleHarvest();
        // Handle attack
        switch (enemyStatge) {
            case NOOB:
                runNoobState();
                break;
            default:
                throw new IllegalStateException("Unknwon stage: " + enemyStatge);
        }
    }

    public SimpleBase getBotBase() {
        return botBase.getSimpleBase();
    }

    public SimpleBase getHumanBase() {
        return humanBase.getSimpleBase();
    }

    private void runNoobState() {
        if (stopAttack) {
            stopAttack = false;
            enemyTarget.removeSyncItemListener(this);
            Index dest = services.getCollisionService().getFreeRandomPosition(attacker.getBaseItemType(), attacker, 200, 400);
            services.getActionService().move(attacker, dest);
            enemyTarget = null;
            attacker = null;
        } else {
            boolean hasCahnged = false;

            if (enemyTarget == null || !enemyTarget.isAlive()) {
                enemyTarget = getEnemyNoobStage();
                hasCahnged = true;
            }

            if (enemyTarget == null) {
                return;
            }

            if (attacker == null || !attacker.isAlive()) {
                attacker = getAttacker();
                hasCahnged = true;
            }

            if (attacker == null) {
                return;
            }

            if (!hasCahnged) {
                return;
            }
            enemyTarget.addSyncItemListener(this);
            services.getActionService().attack(attacker, enemyTarget);
        }
    }

    private SyncBaseItem getEnemyNoobStage() {
        SyncBaseItem anyItem = null;
        for (SyncBaseItem syncBaseItem : humanBase.getItems()) {
            if (syncBaseItem.hasSyncFactory()) {
                if (syncItemCanBeAttacked(syncBaseItem))
                    return syncBaseItem;
            }
            if (syncItemCanBeAttacked(syncBaseItem)) {
                anyItem = syncBaseItem;
            }
        }
        return anyItem;
    }

    private boolean syncItemCanBeAttacked(SyncBaseItem syncBaseItem) {
        return syncBaseItem.getHealth() > (double) syncBaseItem.getBaseItemType().getHealth() * 0.3;
    }

    private SyncBaseItem getAttacker() {
        for (SyncBaseItem syncBaseItem : botBase.getItems()) {
            if (syncBaseItem.hasSyncWaepon()) {
                return syncBaseItem;
            }
        }
        return null;
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        if (syncItem.equals(enemyTarget) && change == Change.HEALTH) {
            stopAttack = true;
        }
    }

    public boolean isRunning() {
        if (botBase.getItems().isEmpty() || humanBase.getItems().isEmpty()) {
            running = false;
        }
        return running;
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }
}
