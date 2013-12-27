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
import com.btxtech.game.jsre.common.gameengine.itemType.HouseType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 14.09.2010
 * Time: 13:28:48
 */
public class SyncHouse extends SyncBaseAbility {
    private HouseType houseType;

    public SyncHouse(HouseType houseType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.houseType = houseType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        // Ignore
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        // Ignore
    }

    public int getSpace() {
        return houseType.getSpace();
    }
}
