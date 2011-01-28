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

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class SyncItemIdComparison implements AbstractSyncItemComparison {
    private Collection<Integer> syncItemIds;

    public SyncItemIdComparison(Collection<Integer> syncItemIds) {
        this.syncItemIds = syncItemIds;
    }

    public void onSyncItem(SyncItem syncItem) {
        syncItemIds.remove(syncItem.getId().getId());
    }

    @Override
    public boolean isFulfilled() {
        return syncItemIds.isEmpty();
    }

}