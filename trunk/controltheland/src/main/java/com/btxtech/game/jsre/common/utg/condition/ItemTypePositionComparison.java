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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ItemTypePositionComparison extends AbstractSyncItemComparison implements TimeAware {
    private Map<ItemType, Integer> itemTypes;
    private Rectangle region;
    private Integer time;
    private Services services;
    private SimpleBase simpleBase;
    private boolean isFulfilled = false;
    private Collection<SyncItem> fulfilledItems = new HashSet<SyncItem>();
    private Long fulfilledTimeStamp;

    public ItemTypePositionComparison(Integer excludedTerritoryId, Map<ItemType, Integer> itemTypes, Rectangle region, Integer time, boolean addExistingItems, Services services, SimpleBase simpleBase) {
        super(excludedTerritoryId);
        this.itemTypes = itemTypes;
        this.region = region;
        this.time = time;
        this.services = services;
        this.simpleBase = simpleBase;
        if (addExistingItems) {
            addInitail();
        }
    }

    @Override
    protected void privateOnSyncItem(SyncItem syncItem) {
        if (isFulfilled) {
            return;
        }
        if (itemTypes != null && !itemTypes.containsKey(syncItem.getItemType())) {
            return;
        }
        if (!checkRegion(syncItem)) {
            return;
        }
        fulfilledItems.add(syncItem);
        if (time == null || time == 0) {
            verifyFulfilledItems();
            isFulfilled = areItemsComplete();
        } else {
            checkIfTimeFulfilled();
        }
    }

    private void addInitail() {
        Collection<SyncBaseItem> items;
        if (region != null) {
            items = services.getItemService().getBaseItemsInRectangle(region, simpleBase, null);
        } else {
            items = services.getItemService().getItems4Base(simpleBase);
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
            checkIfTimeFulfilled();
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
        for (Iterator<SyncItem> iterator = fulfilledItems.iterator(); iterator.hasNext();) {
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
    }

    private boolean checkRegion(SyncItem syncItem) {
        return region == null || syncItem.getSyncItemArea().contains(region);
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
}