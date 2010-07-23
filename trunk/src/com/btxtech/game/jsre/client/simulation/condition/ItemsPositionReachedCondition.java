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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.condition.ItemsPositionReachedConditionConfig;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:05:57
 */
public class ItemsPositionReachedCondition extends AbstractCondition {
    private ItemsPositionReachedConditionConfig itemsPositionReachedConditionConfig;

    public ItemsPositionReachedCondition(ItemsPositionReachedConditionConfig itemConditionConfig) {
        this.itemsPositionReachedConditionConfig = itemConditionConfig;
    }

    @Override
    public boolean isFulfilledSyncItemDeactivated(SyncBaseItem deactivatedItem) {
        for (Integer id : itemsPositionReachedConditionConfig.getIds()) {
            try {
                SyncBaseItem syncItem = (SyncBaseItem) ItemContainer.getInstance().getItem(ItemContainer.getInstance().createSimulationId(id));
                if (syncItem.hasSyncMovable()) {
                    if (syncItem.getSyncMovable().isActive()) {
                        return false;
                    } else {
                        if (!itemsPositionReachedConditionConfig.getRegion().contains(syncItem.getPosition())) {
                            return false;
                        }
                    }
                }
            } catch (ItemDoesNotExistException e) {
                GwtCommon.handleException(e);
                return false;
            }
        }
        return true;
    }
}