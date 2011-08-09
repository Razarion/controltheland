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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 22.09.2010
 * Time: 19:01:40
 */
@Deprecated
public class BotDefenseContainer {
    private Map<SyncBaseItem, Collection<BotSyncBaseItem>> intruders = new HashMap<SyncBaseItem, Collection<BotSyncBaseItem>>();
    private int superiority;
    private BotItemContainer defense;
    private BotDefenseContainer canTakeFrom;
    private Rectangle region;
    private Log log = LogFactory.getLog(BotRunner.class);

    public BotDefenseContainer(BotItemContainer defense, int superiority, BotDefenseContainer canTakeFrom, Rectangle region) {
        this.defense = defense;
        this.superiority = superiority;
        this.canTakeFrom = canTakeFrom;
        this.region = region;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void handleIntruders(Collection<SyncBaseItem> intruders) {
        for (SyncBaseItem intruder : intruders) {
            addIntruder(intruder);
        }
        attackIntruders();
    }

    private void addIntruder(SyncBaseItem attacker) {
        Collection<BotSyncBaseItem> botSyncBaseItems = intruders.get(attacker);
        if (botSyncBaseItems == null) {
            botSyncBaseItems = new ArrayList<BotSyncBaseItem>();
            intruders.put(attacker, botSyncBaseItems);
        }
    }

    private void attackIntruders() {
        for (Iterator<Map.Entry<SyncBaseItem, Collection<BotSyncBaseItem>>> iterator = intruders.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<SyncBaseItem, Collection<BotSyncBaseItem>> intruder = iterator.next();
            if (!intruder.getKey().isAlive() || !region.contains(intruder.getKey().getPosition())) {
                Collection<BotSyncBaseItem> idleUnits = intruder.getValue();
                iterator.remove();
                for (BotSyncBaseItem idleUnit : idleUnits) {
                    idleUnit.stop();
                }
                continue;
            }
            Collection<BotSyncBaseItem> defenceUnits = intruder.getValue();
            for (Iterator<BotSyncBaseItem> iterator1 = defenceUnits.iterator(); iterator1.hasNext();) {
                BotSyncBaseItem defenceUnit = iterator1.next();
                if (!defenceUnit.isAlive()) {
                    iterator1.remove();
                }
            }

            while (defenceUnits.size() < superiority) {
           /* TODO     BotSyncBaseItem botSyncBaseItem = defense.getFirstIdleAttacker(intruder.getKey());
                if (botSyncBaseItem == null) {
                    break;
                }
                doAttack(intruder.getKey(), defenceUnits, botSyncBaseItem); */
            }

            if (defenceUnits.size() < superiority || canTakeFrom == null) {
                continue;
            }
            while (defenceUnits.size() < superiority) {
                BotSyncBaseItem botSyncBaseItem = canTakeFrom.pullWorkingItem();
                if (botSyncBaseItem == null) {
                    break;
                }
                doAttack(intruder.getKey(), defenceUnits, botSyncBaseItem);
            }
        }
    }

    private void doAttack(SyncBaseItem target, Collection<BotSyncBaseItem> defenceUnits, BotSyncBaseItem defender) {
        try {
            defender.attack(target);
            defenceUnits.add(defender);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private BotSyncBaseItem pullWorkingItem() {
        for (Iterator<Collection<BotSyncBaseItem>> iterator = intruders.values().iterator(); iterator.hasNext();) {
            Collection<BotSyncBaseItem> botSyncBaseItems = iterator.next();
            if (botSyncBaseItems.isEmpty()) {
                continue;
            }
            BotSyncBaseItem botSyncBaseItem = botSyncBaseItems.iterator().next();
            botSyncBaseItems.iterator().remove();
            if (botSyncBaseItems.isEmpty()) {
                iterator.remove();
            }
            return botSyncBaseItem;
        }
        return null;
    }
}
