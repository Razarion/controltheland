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

package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.09.2010
 * Time: 19:01:40
 */
public class IntruderHandler {
    private Map<SyncBaseItem, Collection<BotSyncBaseItem>> intruders = new HashMap<SyncBaseItem, Collection<BotSyncBaseItem>>();
    private BotItemContainer botItemContainer;
    private Rectangle region;
    private Services services;
    private Logger log = Logger.getLogger(IntruderHandler.class.getName());

    public IntruderHandler(BotItemContainer botItemContainer, Rectangle region, Services services) {
        this.botItemContainer = botItemContainer;
        this.region = region;
        this.services = services;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void handleIntruders(SimpleBase simpleBase) {
        removeDeadAttackers();
        List<SyncBaseItem> items = services.getItemService().getEnemyItems(simpleBase, region, true);
        Map<SyncBaseItem, Collection<BotSyncBaseItem>> oldIntruders = intruders;
        intruders = new HashMap<SyncBaseItem, Collection<BotSyncBaseItem>>();
        for (SyncBaseItem intruder : items) {
            Collection<BotSyncBaseItem> attacker = oldIntruders.remove(intruder);
            if (attacker != null) {
                intruders.put(intruder, attacker);
                checkIntruder(intruder);
            } else {
                putAttackerToIntruder(intruder);
            }
        }

        for (Collection<BotSyncBaseItem> botSyncBaseItems : oldIntruders.values()) {
            for (BotSyncBaseItem botSyncBaseItem : botSyncBaseItems) {
                botSyncBaseItem.stop();
            }
        }
    }

    private void removeDeadAttackers() {
        for (Iterator<Collection<BotSyncBaseItem>> attackersIterator = intruders.values().iterator(); attackersIterator.hasNext();) {
            Collection<BotSyncBaseItem> attackers = attackersIterator.next();
            for (Iterator<BotSyncBaseItem> attackerIterator = attackers.iterator(); attackerIterator.hasNext();) {
                BotSyncBaseItem attacker = attackerIterator.next();
                if (!attacker.isAlive() || attacker.isIdle()) {
                    attackerIterator.remove();
                }
            }
            if (attackers.isEmpty()) {
                attackersIterator.remove();
            }
        }
    }

    private void checkIntruder(SyncBaseItem intruder) {
        Collection<BotSyncBaseItem> attackers = intruders.get(intruder);
        if (attackers == null || attackers.isEmpty()) {
            putAttackerToIntruder(intruder);
        }
    }

    private void putAttackerToIntruder(SyncBaseItem intruder) {
        Collection<BotSyncBaseItem> attackers = intruders.get(intruder);
        if (attackers == null) {
            attackers = new ArrayList<BotSyncBaseItem>();
            intruders.put(intruder, attackers);
        }
        BotSyncBaseItem attacker = botItemContainer.getFirstIdleAttacker(intruder);
        if (attacker != null) {
            try {
                AttackFormationItem attackFormationItem = services.getCollisionService().getDestinationHint(attacker.getSyncBaseItem(),
                        attacker.getSyncBaseItem().getBaseItemType().getWeaponType().getRange(),
                        intruder.getSyncItemArea(),
                        attacker.getSyncBaseItem().getTerrainType());
                if (attackFormationItem.isInRange()) {
                    attacker.attack(intruder, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel());
                    attackers.add(attacker);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "", e);
            }
        }
    }
}
