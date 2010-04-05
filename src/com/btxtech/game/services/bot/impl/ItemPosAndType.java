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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 04.04.2010
 * Time: 20:41:25
 */
public class ItemPosAndType {
    private Index position;
    private SyncBaseItem syncBaseItem;
    private BaseItemType baseItemType;

    public ItemPosAndType(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
        position = syncBaseItem.getPosition();
        baseItemType = syncBaseItem.getBaseItemType();
    }

    public Index getPosition() {
        return position;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public BaseItemType getBaseItemType() {
        return baseItemType;
    }

    public boolean isDead() {
        return !syncBaseItem.isAlive();
    }

    public void setSyncBaseItem(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
    }
}
