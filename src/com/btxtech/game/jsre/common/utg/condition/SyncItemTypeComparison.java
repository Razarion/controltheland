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

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class SyncItemTypeComparison extends AbstractSyncItemComparison {
    private Map<ItemType, Integer> remaining;

    public SyncItemTypeComparison(Map<ItemType, Integer> itemType) {
        remaining = new HashMap<ItemType, Integer>(itemType);
    }

    @Override
    public void onSyncItem(SyncItem syncItem) {
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
    }

    @Override
    public boolean isFulfilled() {
        return remaining.isEmpty();
    }

}