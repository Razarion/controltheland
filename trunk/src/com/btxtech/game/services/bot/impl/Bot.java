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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.ai.BaseBalancer;
import com.btxtech.game.jsre.common.ai.BotLevel;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.services.base.Base;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:24:08
 */
public class Bot implements SyncItemListener {
    private Log log = LogFactory.getLog(Bot.class);
    private Base humanBase;
    private Base botBase;
    private Services services;
    private Thread thread;
    private BotLevelFactory botLevelFactory;
    private BaseBalancer baseBalancer;
    private SyncBaseItem enemyTarget;
    private SyncBaseItem attacker;
    private boolean stopAttack = false;
    private boolean running;
    private int level = 0;
    private long lastAttackTime;

    public Bot(Base botBase, Base humanBase, Services services, Thread thread, BotLevelFactory botLevelFactory) {
        this.humanBase = humanBase;
        this.botBase = botBase;
        this.services = services;
        this.thread = thread;
        this.botLevelFactory = botLevelFactory;
        baseBalancer = new BaseBalancer(botLevelFactory.getBotLevel(level), services, botBase.getSimpleBase());
        running = true;
    }

    public void action() throws NoSuchItemTypeException {
        Map<BaseItemType, List<SyncBaseItem>> enemyItems = services.getItemService().getItems4Base(humanBase.getSimpleBase());
        int level = botLevelFactory.getLevelForHumanBase(enemyItems);
        level = Math.max(level, this.level);
        if (level != this.level) {
            this.level = level;
            baseBalancer.setBotLevel(botLevelFactory.getBotLevel(level));
            log.info("Bot '" + botBase + "' reached level: " + this.level);
        }
        baseBalancer.doBalance();
        baseBalancer.doAllIdleHarvest();
        runAttack();
    }

    public SimpleBase getBotBase() {
        return botBase.getSimpleBase();
    }

    public SimpleBase getHumanBase() {
        return humanBase.getSimpleBase();
    }

    private void runAttack() {
        if (stopAttack) {
            stopAttack();
        } else {
            if (botLevelFactory.getBotLevel(level).getAttackPause() > 0) {
                if (System.currentTimeMillis() > botLevelFactory.getBotLevel(level).getAttackPause() + lastAttackTime) {
                    doAttack();
                }
            } else {
                doAttack();
            }
        }
    }

    private void doAttack() {
        boolean hasCahnged = false;

        if (enemyTarget == null || !enemyTarget.isAlive()) {
            enemyTarget = getEnemyTarget();
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
        if (botLevelFactory.getBotLevel(level).getAttackPause() > 0) {
            enemyTarget.addSyncItemListener(this);
        }
        services.getActionService().attack(attacker, enemyTarget);
    }

    private void stopAttack() {
        BotLevel botLevel = botLevelFactory.getBotLevel(level);
        if (botLevel.getAttackPauseMinDistance() > 0 && botLevel.getAttackPauseMaxDistance() > 0) {
            Index dest = services.getCollisionService().getFreeRandomPosition(attacker.getBaseItemType(),
                    attacker, botLevel.getAttackPauseMinDistance(), botLevel.getAttackPauseMaxDistance());
            services.getActionService().move(attacker, dest);
        }
        enemyTarget.removeSyncItemListener(this);
        enemyTarget = null;
        attacker = null;
        stopAttack = false;
    }

    private SyncBaseItem getEnemyTarget() {
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
        double hold = botLevelFactory.getBotLevel(level).getAttackHold();
        return hold <= 0.0 || syncBaseItem.getHealth() > (double) syncBaseItem.getBaseItemType().getHealth() * hold;
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
            lastAttackTime = System.currentTimeMillis();
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
