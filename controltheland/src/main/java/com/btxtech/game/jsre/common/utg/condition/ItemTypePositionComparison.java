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
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat Date: 18.07.2010 Time: 21:06:41
 */
public class ItemTypePositionComparison extends AbstractSyncItemComparison implements TimeAware {
    private Map<ItemType, Integer> itemTypes;
    private Region region;
    private Integer time;
    private PlanetServices planetServices;
    private SimpleBase simpleBase;
    private boolean isFulfilled = false;
    private final Collection<SyncItem> fulfilledItems = new HashSet<SyncItem>();
    private Long fulfilledTimeStamp;

    public ItemTypePositionComparison(Map<ItemType, Integer> itemTypes, Region region, Integer time, boolean addExistingItems, PlanetServices planetServices,
                                      SimpleBase simpleBase) {
        this.itemTypes = itemTypes;
        this.region = region;
        this.time = time;
        this.planetServices = planetServices;
        this.simpleBase = simpleBase;
        if (addExistingItems) {
            addInitail();
            checkFulfilled();
        }
    }

    @Override
    protected void privateOnSyncItem(SyncItem syncItem) {
        if (isFulfilled) {
            return;
        }
        if (itemTypes == null || !itemTypes.containsKey(syncItem.getItemType())) {
            return;
        }
        if (!checkRegion(syncItem)) {
            onProgressChanged();
            return;
        }
        synchronized (fulfilledItems) {
            fulfilledItems.add(syncItem);
            checkFulfilled();
        }
    }

    private void checkFulfilled() {
        if (isTimerNeeded()) {
            checkIfTimeFulfilled();
        } else {
            verifyFulfilledItems();
            isFulfilled = areItemsComplete();
            onProgressChanged();
        }
    }

    private void addInitail() {
        Collection<SyncBaseItem> items;
        if (region != null) {
            items = planetServices.getItemService().getBaseItemsInRectangle(region, simpleBase, null);
        } else {
            items = planetServices.getItemService().getItems4Base(simpleBase);
        }
        fulfilledItems.addAll(items);
    }

    @Override
    public boolean isFulfilled() {
        return isFulfilled;
    }

    @Override
    public void onTimer() {
        if (!isFulfilled) {
            synchronized (fulfilledItems) {
                checkIfTimeFulfilled();
            }
            if (isFulfilled) {
                getAbstractConditionTrigger().setFulfilled();
            }
        }
    }

    @Override
    public boolean isTimerNeeded() {
        return time != null && time > 0;
    }

    private void verifyFulfilledItems() {
        for (Iterator<SyncItem> iterator = fulfilledItems.iterator(); iterator.hasNext(); ) {
            SyncItem fulfilledItem = iterator.next();
            if (!fulfilledItem.isAlive()) {
                iterator.remove();
            }
            if (!checkRegion(fulfilledItem)) {
                iterator.remove();
            }
        }
    }

    private void checkIfTimeFulfilled() {
        verifyFulfilledItems();
        if (areItemsComplete()) {
            if (fulfilledTimeStamp == null) {
                fulfilledTimeStamp = System.currentTimeMillis();
            } else {
                isFulfilled = fulfilledTimeStamp + time < System.currentTimeMillis();
            }
        } else {
            fulfilledTimeStamp = null;
        }
        onProgressChanged();
    }

    private boolean checkRegion(SyncItem syncItem) {
        return region == null || (syncItem.getSyncItemArea().hasPosition() && region.isInside(syncItem));
    }

    private boolean areItemsComplete() {
        if (itemTypes == null) {
            return true;
        }
        Map<ItemType, Integer> tmpItemTypes = new HashMap<ItemType, Integer>(itemTypes);
        for (SyncItem fulfilledItem : fulfilledItems) {
            ItemType fulfilledItemType = fulfilledItem.getItemType();
            Integer count = tmpItemTypes.get(fulfilledItemType);
            if (count == null) {
                continue;
            }
            count--;
            if (count == 0) {
                tmpItemTypes.remove(fulfilledItemType);
            } else {
                tmpItemTypes.put(fulfilledItemType, count);
            }
        }
        return tmpItemTypes.isEmpty();
    }

    @Override
    public void fillGenericComparisonValues(GenericComparisonValueContainer genericComparisonValueContainer) {
        if (fulfilledTimeStamp != null) {
            long remainingTime = time - (System.currentTimeMillis() - fulfilledTimeStamp);
            genericComparisonValueContainer.addChild(GenericComparisonValueContainer.Key.REMAINING_TIME, remainingTime);
        }
    }

    @Override
    public void restoreFromGenericComparisonValue(GenericComparisonValueContainer genericComparisonValueContainer) {
        fulfilledItems.clear();
        addInitail();

        if (genericComparisonValueContainer.hasKey(GenericComparisonValueContainer.Key.REMAINING_TIME)) {
            long remainingTime = (Long) genericComparisonValueContainer.getValue(GenericComparisonValueContainer.Key.REMAINING_TIME);
            fulfilledTimeStamp = remainingTime + System.currentTimeMillis() - time;
        }
    }


    @Override
    public void fillQuestProgressInfo(QuestProgressInfo questProgressInfo) {
        // Add time
        if (time != null) {
            int amount = 0;
            if (fulfilledTimeStamp != null) {
                long longAmount = System.currentTimeMillis() - fulfilledTimeStamp;
                if(longAmount > ClientDateUtil.MILLIS_IN_MINUTE) {
                    amount = (int) (longAmount / ClientDateUtil.MILLIS_IN_MINUTE);
                } else {
                    amount = 1;
                }
            }
            questProgressInfo.setAmount(new QuestProgressInfo.Amount(amount, (int) (time / ClientDateUtil.MILLIS_IN_MINUTE)));
        }
        // Items
        synchronized (fulfilledItems) {
            verifyFulfilledItems();
        }
        Map<Integer, QuestProgressInfo.Amount> itemIdAmounts = new HashMap<Integer, QuestProgressInfo.Amount>();
        for (Map.Entry<ItemType, Integer> entry : itemTypes.entrySet()) {
            QuestProgressInfo.Amount amount = new QuestProgressInfo.Amount(getAmount(entry.getKey()), entry.getValue());
            itemIdAmounts.put(entry.getKey().getId(), amount);
        }
        questProgressInfo.setItemIdAmounts(itemIdAmounts);
    }

    private int getAmount(ItemType itemType) {
        int amount = 0;
        synchronized (fulfilledItems) {
            for (SyncItem fulfilledItem : fulfilledItems) {
                if (fulfilledItem.getItemType().equals(itemType)) {
                    amount++;
                }
            }
        }
        return amount;
    }
}