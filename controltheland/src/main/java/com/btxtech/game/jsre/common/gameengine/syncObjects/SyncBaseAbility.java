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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 19:22:47
 */
public abstract class SyncBaseAbility {
    private SyncBaseItem syncBaseItem;

    public SyncBaseAbility(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public Services getServices() {
        return syncBaseItem.getServices();
    }

    public SyncItemArea getSyncItemArea() {
        return syncBaseItem.getSyncItemArea();
    }

    public abstract void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException;

    public abstract void fillSyncItemInfo(SyncItemInfo syncItemInfo);

}
