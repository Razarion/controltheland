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

package com.btxtech.game.jsre.client.simulation.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsKilledConditionConfig;
import java.util.TreeSet;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 21:05:57
 */
public class ItemsKilledCondition extends AbstractCondition {
    private ItemsKilledConditionConfig itemsKilledConditionConfig;
    private TreeSet<Integer> killedItems = new TreeSet<Integer>();

    public ItemsKilledCondition(ItemsKilledConditionConfig itemsKilledConditionConfig) {
        this.itemsKilledConditionConfig = itemsKilledConditionConfig;
    }

    @Override
    public boolean isFulfilledItemsKilled(SyncItem killedItem, SyncBaseItem actor) {
        int id = killedItem.getId().getId();
        if (itemsKilledConditionConfig.getIds().contains(id)) {
            killedItems.add(id);
        }
        for (Integer idToKill : itemsKilledConditionConfig.getIds()) {
            if (!killedItems.contains(idToKill)) {
                return false;
            }
        }
        return true;
    }
}