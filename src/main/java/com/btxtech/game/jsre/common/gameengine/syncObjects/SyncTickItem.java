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
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

/**
 * User: beat
 * Date: 05.10.2010
 * Time: 22:32:21
 */
abstract public class SyncTickItem extends SyncItem {
    public SyncTickItem(Id id, Index position, ItemType itemType, GlobalServices globalServices, PlanetServices planetServices) {
        super(id, position, itemType, globalServices, planetServices);
    }

    /**
     * Ticks this sync item
     *
     * @param factor time in s since the last ticks
     * @return true if more tick are needed to fullfil the job
     * @throws ItemDoesNotExistException if the target item does no exist any longer
     * @throws NoSuchItemTypeException   if the target item type does not exist
     */
    public abstract boolean tick(double factor) throws ItemDoesNotExistException, NoSuchItemTypeException;

    public abstract void stop();
}
