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

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.09.2010
 * Time: 19:01:40
 */
public class IntruderHandler {
    private Map<SyncBaseItem, BotSyncBaseItem> intruders = new HashMap<SyncBaseItem, BotSyncBaseItem>();
    private BotEnragementState botEnragementState;
    private Region region;
    private PlanetServices planetServices;
    private Logger log = Logger.getLogger(IntruderHandler.class.getName());

    public IntruderHandler(BotEnragementState botEnragementState, Region region, PlanetServices planetServices) {
        this.botEnragementState = botEnragementState;
        this.region = region;
        this.planetServices = planetServices;
    }

    public Region getRegion() {
        return region;
    }

    public void handleIntruders(SimpleBase simpleBase) {
        removeDeadAttackers();
        Collection<SyncBaseItem> items = planetServices.getItemService().getEnemyItems(simpleBase, region);
        Map<SyncBaseItem, BotSyncBaseItem> oldIntruders = intruders;
        intruders = new HashMap<SyncBaseItem, BotSyncBaseItem>();
        Collection<SyncBaseItem> newIntruders = new ArrayList<SyncBaseItem>();
        for (SyncBaseItem intruder : items) {
            BotSyncBaseItem attacker = oldIntruders.remove(intruder);
            if (attacker != null) {
                intruders.put(intruder, attacker);
            } else {
                newIntruders.add(intruder);
            }
        }

        if (!newIntruders.isEmpty()) {
            putAttackerToIntruders(newIntruders);
        }

        for (BotSyncBaseItem botSyncBaseItem : oldIntruders.values()) {
            botSyncBaseItem.stop();
        }
        botEnragementState.handleIntruders(items, simpleBase);
    }

    private void removeDeadAttackers() {
        for (Iterator<BotSyncBaseItem> attackerIterator = intruders.values().iterator(); attackerIterator.hasNext(); ) {
            BotSyncBaseItem attacker = attackerIterator.next();
            if (!attacker.isAlive() || attacker.isIdle()) {
                attackerIterator.remove();
            }
        }
    }

    private void putAttackerToIntruders(Collection<SyncBaseItem> newIntruders) {
        Collection<BotSyncBaseItem> idleAttackers = botEnragementState.getAllIdleAttackers();
        Map<BotSyncBaseItem, SyncBaseItem> assignedAttackers = ShortestWaySorter.setupAttackerTarget(idleAttackers, newIntruders);

        for (Map.Entry<BotSyncBaseItem, SyncBaseItem> entry : assignedAttackers.entrySet()) {
            putAttackerToIntruder(entry.getKey(), entry.getValue());
        }
    }

    private void putAttackerToIntruder(BotSyncBaseItem attacker, SyncBaseItem intruder) {
        if (attacker != null) {
            try {
                AttackFormationItem attackFormationItem = planetServices.getCollisionService().getDestinationHint(attacker.getSyncBaseItem(),
                        attacker.getSyncBaseItem().getBaseItemType().getWeaponType().getRange(),
                        intruder.getSyncItemArea());
                if (attackFormationItem.isInRange()) {
                    attacker.attack(intruder, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel());
                    intruders.put(intruder, attacker);
                } else {
                    log.warning("Bot is unable to find position to attack item. Bot attacker: " + attacker.getSyncBaseItem() + " Target: " + intruder);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "", e);
            }
        }
    }
}
