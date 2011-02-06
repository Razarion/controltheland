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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

/**
 * User: beat
 * Date: 05.10.2010
 * Time: 22:32:21
 */
abstract public class SyncTickItem extends SyncItem{
    public SyncTickItem(Id id, Index position, ItemType itemType, Services services) {
        super(id, position, itemType, services);
    }

    public abstract boolean tick(double factor) throws ItemDoesNotExistException, NoSuchItemTypeException;

    public abstract void stop();
}
