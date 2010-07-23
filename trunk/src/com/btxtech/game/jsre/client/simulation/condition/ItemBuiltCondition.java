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
import com.btxtech.game.jsre.common.tutorial.condition.ItemBuiltConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsKilledConditionConfig;
import java.util.TreeSet;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 21:05:57
 */
public class ItemBuiltCondition extends AbstractCondition {
    private ItemBuiltConditionConfig itemBuiltConditionConfig;

    public ItemBuiltCondition(ItemBuiltConditionConfig itemBuiltConditionConfig) {
        this.itemBuiltConditionConfig = itemBuiltConditionConfig;
    }

    @Override
    public boolean isFulfilledItemBuilt(SyncBaseItem newItem) {
        return newItem.getItemType().getId() == itemBuiltConditionConfig.getItemTypeId();
    }
}