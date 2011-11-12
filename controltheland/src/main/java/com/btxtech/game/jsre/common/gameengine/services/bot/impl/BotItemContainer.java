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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 18.09.2010
 * Time: 11:41:21
 */
public class BotItemContainer {
    private HashMap<SyncBaseItem, BotSyncBaseItem> botItems = new HashMap<SyncBaseItem, BotSyncBaseItem>();
    private Map<BotItemConfig, Collection<BotSyncBaseItem>> buildingItems = new HashMap<BotItemConfig, Collection<BotSyncBaseItem>>();
    private Need need;
    private Logger log = Logger.getLogger(BotItemContainer.class.getName());
    private Services services;
    private String botName;
    private Rectangle realm;

    public BotItemContainer(Collection<BotItemConfig> botItems, Rectangle realm, Services services, String botName) {
        this.realm = realm;
        this.services = services;
        this.botName = botName;
        need = new Need(botItems);
    }

    public void work(SimpleBase simpleBase) {
        if (!isFulfilled(simpleBase)) {
            buildItems(simpleBase);
        }
        handleIdleItems();
    }

    public boolean isFulfilled(SimpleBase simpleBase) {
        updateState(simpleBase);
        return need.getNeedCount() == 0;
    }

    public void killAllItems() {
        services.getItemService().killSyncItems(new ArrayList<SyncItem>(botItems.keySet()));
    }

    public Collection<BotSyncBaseItem> getAllIdleAttackers() {
        Collection<BotSyncBaseItem> idleAttackers = new ArrayList<BotSyncBaseItem>();
        for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
            if (botSyncBaseItem.isIdle()) {
                idleAttackers.add(botSyncBaseItem);
            }
        }
        return idleAttackers;
    }

    private void updateState(SimpleBase simpleBase) {
        Collection<SyncBaseItem> newItems = services.getBaseService().getItems(simpleBase);
        if (newItems != null) {
            newItems.removeAll(botItems.keySet());
            for (SyncBaseItem newItem : newItems) {
                add(newItem);
            }
        }

        ArrayList<BotSyncBaseItem> remove = new ArrayList<BotSyncBaseItem>();
        for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
            if (botSyncBaseItem.isAlive()) {
                botSyncBaseItem.updateIdleState();
            } else {
                remove.add(botSyncBaseItem);
            }
        }
        for (BotSyncBaseItem botSyncBaseItem : remove) {
            remove(botSyncBaseItem);
        }
        for (Iterator<Map.Entry<BotItemConfig, Collection<BotSyncBaseItem>>> iterator = buildingItems.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<BotItemConfig, Collection<BotSyncBaseItem>> entry = iterator.next();
            for (Iterator<BotSyncBaseItem> builderIterator = entry.getValue().iterator(); builderIterator.hasNext();) {
                BotSyncBaseItem buildingItem = builderIterator.next();
                if (!buildingItem.isAlive() || buildingItem.isIdle()) {
                    builderIterator.remove();
                }
                if (entry.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    private void add(SyncBaseItem syncBaseItem) {
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, services);
        botItems.put(syncBaseItem, botSyncBaseItem);
        need.onItemAdded(botSyncBaseItem);
    }

    private void remove(BotSyncBaseItem botSyncBaseItem) {
        botItems.remove(botSyncBaseItem.getSyncBaseItem());
        if (!botSyncBaseItem.getBotItemConfig().isNoRebuild()) {
            need.onItemRemoved(botSyncBaseItem);
        }
    }

    private void buildItems(SimpleBase simpleBase) {
        for (BotItemConfig botItemConfig : need.getItemNeed()) {

            int effectiveNeed = need.getNeedCount(botItemConfig);
            Collection<BotSyncBaseItem> currentlyBuilding = buildingItems.get(botItemConfig);
            if (currentlyBuilding != null) {
                effectiveNeed -= currentlyBuilding.size();
                if (effectiveNeed < 0) {
                    effectiveNeed = 0;
                }
            }

            for (int i = 0; i < effectiveNeed; i++) {
                try {
                    createItem(botItemConfig, simpleBase);
                } catch (PlaceCanNotBeFoundException t) {
                    log.warning(botName + ": " + t.getMessage());
                } catch (Exception e) {
                    log.log(Level.SEVERE, botName, e);
                }
            }
        }
    }

    private void createItem(BotItemConfig botItemConfig, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        BaseItemType toBeBuilt = botItemConfig.getBaseItemType();
        if (botItemConfig.isCreateDirectly()) {
            Index position = services.getCollisionService().getFreeRandomPosition(toBeBuilt, botItemConfig.getRegion(), 100, false, false);
            SyncBaseItem newItem = (SyncBaseItem) services.getItemService().createSyncObject(toBeBuilt, position, null, simpleBase, 0);
            newItem.setBuildup(1.0);
        } else {
            BotSyncBaseItem botSyncBuilder = getFirstIdleBuilder(toBeBuilt);
            if (botSyncBuilder == null) {
                return;
            }
            if (botSyncBuilder.getSyncBaseItem().hasSyncFactory()) {
                botSyncBuilder.buildUnit(toBeBuilt);
            } else {
                Index position = services.getCollisionService().getFreeRandomPosition(toBeBuilt, botItemConfig.getRegion(), 0, false, true);
                botSyncBuilder.buildBuilding(position, toBeBuilt);
            }
            Collection<BotSyncBaseItem> builders = buildingItems.get(botItemConfig);
            if (builders == null) {
                builders = new ArrayList<BotSyncBaseItem>();
                buildingItems.put(botItemConfig, builders);
            }
            builders.add(botSyncBuilder);
        }
    }


    private BotSyncBaseItem getFirstIdleBuilder(BaseItemType toBeBuilt) {
        for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
            if (botSyncBaseItem.isIdle() && botSyncBaseItem.isAbleToBuild(toBeBuilt)) {
                return botSyncBaseItem;
            }
        }
        return null;
    }

    private void handleIdleItems() {
        for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
            if (!botSyncBaseItem.isIdle()) {
                continue;
            }

            BotItemConfig botItemConfig = botSyncBaseItem.getBotItemConfig();
            if (botItemConfig.isMoveRealmIfIdle() && botSyncBaseItem.canMove() && !realm.contains(botSyncBaseItem.getPosition())) {
                botSyncBaseItem.move(realm);
            } else if (botItemConfig.getIdleTtl() != null && botSyncBaseItem.getIdleTimeStamp() + botItemConfig.getIdleTtl() < System.currentTimeMillis()) {
                botSyncBaseItem.kill();
            }
        }
    }

}
