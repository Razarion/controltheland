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
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
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
    private boolean isFulfilled = false;
    private Collection<SyncItem> fulfilledItems = new HashSet<SyncItem>();
    private Long fulfilledTime;

    public ItemTypePositionComparison(Integer excludedTerritoryId, Map<ItemType, Integer> itemTypes, Rectangle region, Integer time) {
        super(excludedTerritoryId);
        this.itemTypes = itemTypes;
        this.region = region;
        this.time = time;
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
            if (fulfilledTime == null) {
                fulfilledTime = System.currentTimeMillis();
            } else {
                isFulfilled = fulfilledTime + time < System.currentTimeMillis();
            }
        } else {
            fulfilledTime = null;
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
}