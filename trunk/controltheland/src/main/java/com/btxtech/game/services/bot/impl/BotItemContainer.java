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
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 18.09.2010
 * Time: 11:41:21
 */
@Component(value = "botItemContainer")
@Scope("prototype")
public class BotItemContainer {
    private HashMap<SyncBaseItem, BotSyncBaseItem> botItems = new HashMap<SyncBaseItem, BotSyncBaseItem>();
    private Map<DbBotItemConfig, Collection<BotSyncBaseItem>> buildingItems = new HashMap<DbBotItemConfig, Collection<BotSyncBaseItem>>();
    private Need need;
    @Autowired
    private ActionService actionService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    private Log log = LogFactory.getLog(BotItemContainer.class);

    public void init(Collection<DbBotItemConfig> dbBotItemConfigs) {
        need = new Need(dbBotItemConfigs);
    }

    public void buildup(SimpleBase simpleBase, UserState userState) {
        if (isFulfilled(userState)) {
            return;
        }
        buildItems(simpleBase);
    }

    public boolean isFulfilled(UserState userStat) {
        updateState(userStat);
        return need.getNeedCount() == 0;
    }

    public void killAllItems() {
        itemService.killSyncItems(new ArrayList<SyncItem>(botItems.keySet()));
    }

    public BotSyncBaseItem getFirstIdleAttacker(SyncBaseItem target) {
        int distance = Integer.MAX_VALUE;
        BotSyncBaseItem attacker = null;
        for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
            if (botSyncBaseItem.isIdle() && botSyncBaseItem.isAbleToAttack(target.getBaseItemType())) {
                int dist = botSyncBaseItem.getDistanceTo(target);
                if (dist < distance) {
                    distance = dist;
                    attacker = botSyncBaseItem;
                }
            }
        }
        return attacker;
    }

    private void updateState(UserState userState) {
        Set<SyncBaseItem> newItems = getItems(userState);
        newItems.removeAll(botItems.keySet());
        for (SyncBaseItem newItem : newItems) {
            add(newItem);
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
        for (Iterator<Map.Entry<DbBotItemConfig, Collection<BotSyncBaseItem>>> iterator = buildingItems.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<DbBotItemConfig, Collection<BotSyncBaseItem>> entry = iterator.next();
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

    private Set<SyncBaseItem> getItems(UserState userState) {
        Base base = baseService.getBase(userState);
        if (base != null) {
            return new HashSet<SyncBaseItem>(base.getItems());
        } else {
            return Collections.emptySet();
        }
    }

    private void add(SyncBaseItem syncBaseItem) {
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, actionService);
        botItems.put(syncBaseItem, botSyncBaseItem);
        need.onItemAdded(botSyncBaseItem);
    }

    private void remove(BotSyncBaseItem botSyncBaseItem) {
        botItems.remove(botSyncBaseItem.getSyncBaseItem());
        need.onItemRemoved(botSyncBaseItem);
    }

    private void buildItems(SimpleBase simpleBase) {
        for (DbBotItemConfig dbBotItemConfig : need.getItemNeed()) {

            int effectiveNeed = need.getNeedCount(dbBotItemConfig);
            Collection<BotSyncBaseItem> currentlyBuilding = buildingItems.get(dbBotItemConfig);
            if (currentlyBuilding != null) {
                effectiveNeed -= currentlyBuilding.size();
                if (effectiveNeed < 0) {
                    effectiveNeed = 0;
                }
            }

            for (int i = 0; i < effectiveNeed; i++) {
                try {
                    createItem(dbBotItemConfig, simpleBase);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    private void createItem(DbBotItemConfig dbBotItemConfig, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        BaseItemType toBeBuilt = (BaseItemType) itemService.getItemType(dbBotItemConfig.getBaseItemType());
        if (dbBotItemConfig.isCreateDirectly()) {
            Index position = collisionService.getFreeRandomPosition(toBeBuilt, dbBotItemConfig.getRegion(), 100, false);
            SyncBaseItem newItem = (SyncBaseItem) itemService.createSyncObject(toBeBuilt, position, null, simpleBase, 0);
            newItem.setBuildup(1.0);
        } else {
            BotSyncBaseItem botSyncBuilder = getFirstIdleBuilder(toBeBuilt);
            if (botSyncBuilder == null) {
                return;
            }
            if (botSyncBuilder.getSyncBaseItem().hasSyncFactory()) {
                botSyncBuilder.buildUnit(toBeBuilt);
            } else {
                Index position = collisionService.getFreeRandomPosition(toBeBuilt, dbBotItemConfig.getRegion(), 0, false);
                botSyncBuilder.buildBuilding(position, toBeBuilt);
            }
            Collection<BotSyncBaseItem> builders = buildingItems.get(dbBotItemConfig);
            if (builders == null) {
                builders = new ArrayList<BotSyncBaseItem>();
                buildingItems.put(dbBotItemConfig, builders);
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

}
