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

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 18.09.2010
 * Time: 11:41:21
 */
public class BotItemContainer {
    private Collection<BotSyncBaseItem> botSyncBaseItems = new ArrayList<BotSyncBaseItem>();
    private Collection<BotSyncBaseItem> buildingItems = new ArrayList<BotSyncBaseItem>();
    private Map<BaseItemType, Integer> needs = new HashMap<BaseItemType, Integer>();

    public BotItemContainer(Map<BaseItemType, Integer> needs) {
        this.needs = needs;
    }

    public boolean add(BotSyncBaseItem botSyncBaseItem) {
        Integer needCount = needs.get(botSyncBaseItem.getSyncBaseItem().getBaseItemType());
        if (needCount == null || needCount == 0) {
            return false;
        }
        needs.put(botSyncBaseItem.getSyncBaseItem().getBaseItemType(), needCount - 1);
        botSyncBaseItems.add(botSyncBaseItem);
        return true;
    }

    public boolean isFulfilled() {
        updateState();
        int totalNeedCount = 0;
        for (Integer needCount : needs.values()) {
            totalNeedCount += needCount;
        }
        return totalNeedCount <= botSyncBaseItems.size() && buildingItems.isEmpty();
    }

    public HashMap<BaseItemType, Integer> getNeeds() {
        HashMap<BaseItemType, Integer> copyNeeds = new HashMap<BaseItemType, Integer>(needs);
        for (Iterator<Integer> iterator = copyNeeds.values().iterator(); iterator.hasNext();) {
            Integer needCount = iterator.next();
            if (needCount == 0) {
                iterator.remove();
            }
        }
        return copyNeeds;
    }

    public BotSyncBaseItem getFirstIdleItem() {
        for (BotSyncBaseItem botSyncBaseItem : botSyncBaseItems) {
            if (botSyncBaseItem.isIdle()) {
                return botSyncBaseItem;
            }
        }
        return null;
    }

    private void updateState() {
        for (Iterator<BotSyncBaseItem> itemIterator = botSyncBaseItems.iterator(); itemIterator.hasNext();) {
            BotSyncBaseItem botSyncBaseItem = itemIterator.next();
            if (botSyncBaseItem.isAlive()) {
                botSyncBaseItem.updateIdleState();
            } else {
                itemIterator.remove();
                int count = needs.get(botSyncBaseItem.getSyncBaseItem().getBaseItemType());
                needs.put(botSyncBaseItem.getSyncBaseItem().getBaseItemType(), count + 1);
            }
        }
        for (Iterator<BotSyncBaseItem> builderIterator = buildingItems.iterator(); builderIterator.hasNext();) {
            BotSyncBaseItem buildingItem = builderIterator.next();
            if (!buildingItem.isAlive() || buildingItem.isIdle()) {
                builderIterator.remove();
            }
        }
    }

    public void addBuildingItem(BotSyncBaseItem botSyncBaseItem) {
        buildingItems.add(botSyncBaseItem);
    }
}
