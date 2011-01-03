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

package com.btxtech.game.jsre.common.level.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import java.util.Collection;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
public class SyncObjectIdComparison extends AbstractComparison {
    private int syncObjectId;
    private boolean isFulfilled = false;

    @Override
    public void onSyncItem(SyncItem syncItem) {
        if (!isFulfilled) {
            isFulfilled = syncItem.getId().getId() == syncObjectId;
        }
    }

    @Override
    public void onSyncItems(Collection<SyncBaseItem> syncItems) {
        if (!isFulfilled) {
            for (SyncBaseItem syncItem : syncItems) {
                onSyncItem(syncItem);
                if (isFulfilled) {
                    return;
                }
            }
        }
    }

    @Override
    public boolean isFulfilled() {
        return isFulfilled;
    }
}
