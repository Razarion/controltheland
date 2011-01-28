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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import java.util.Collection;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
public class CountComparison implements AbstractSyncItemComparison {
    private int value;
    private int count;

    public CountComparison(int count) {
        this.count = count;
        value = 0;
    }

    @Override
    public void onSyncItem(SyncItem syncItem) {
        value++;
    }

/*    @Override
    public void onSyncItem(SyncItem syncItem) {
        value++;
    } */

    @Override
    public boolean isFulfilled() {
        return value >= count;
    }
}
