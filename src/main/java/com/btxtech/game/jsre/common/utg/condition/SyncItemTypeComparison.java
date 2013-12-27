/*
 * Copyright (c) 2011.
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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.utg.ConditionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat Date: 18.07.2010 Time: 21:06:41
 */
public class SyncItemTypeComparison extends AbstractSyncItemComparison {
    private Map<ItemType, Integer> remaining;
    private Map<ItemType, Integer> total;

    public SyncItemTypeComparison(Map<ItemType, Integer> itemType) {
        remaining = new HashMap<ItemType, Integer>(itemType);
        total = new HashMap<ItemType, Integer>(itemType);
    }

    @Override
    protected void privateOnSyncItem(SyncItem syncItem) {
        Integer remainingCount = remaining.get(syncItem.getItemType());
        if (remainingCount == null) {
            return;
        }
        remainingCount--;
        if (remainingCount == 0) {
            remaining.remove(syncItem.getItemType());
        } else {
            remaining.put(syncItem.getItemType(), remainingCount);
        }
        onProgressChanged();
    }

    @Override
    public boolean isFulfilled() {
        return remaining.isEmpty();
    }

    public Map<ItemType, Integer> getRemaining() {
        return remaining;
    }

    public void setRemaining(Map<ItemType, Integer> remaining) {
        this.remaining = remaining;
    }

    @Override
    public void fillGenericComparisonValues(GenericComparisonValueContainer genericComparisonValueContainer) {
        GenericComparisonValueContainer itemCounts = genericComparisonValueContainer.createChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        for (Map.Entry<ItemType, Integer> entry : remaining.entrySet()) {
            itemCounts.addChild(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void restoreFromGenericComparisonValue(GenericComparisonValueContainer genericComparisonValueContainer) {
        remaining.clear();
        GenericComparisonValueContainer itemCounts = genericComparisonValueContainer.getChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        for (Map.Entry entry : itemCounts.getEntries()) {
            remaining.put((ItemType) entry.getKey(), ((Number) entry.getValue()).intValue());
        }
    }

    @Override
    public void fillQuestProgressInfo(QuestProgressInfo questProgressInfo, ConditionService conditionService) {
        Map<Integer, QuestProgressInfo.Amount> itemIdAmounts = new HashMap<Integer, QuestProgressInfo.Amount>();
        for (Map.Entry<ItemType, Integer> entry : total.entrySet()) {
            Integer remaining = this.remaining.get(entry.getKey());
            if (remaining == null) {
                remaining = 0;
            }
            int amount = entry.getValue() - remaining;
            itemIdAmounts.put(entry.getKey().getId(), new QuestProgressInfo.Amount(amount, entry.getValue()));
        }
        questProgressInfo.setItemIdAmounts(itemIdAmounts);
    }
}