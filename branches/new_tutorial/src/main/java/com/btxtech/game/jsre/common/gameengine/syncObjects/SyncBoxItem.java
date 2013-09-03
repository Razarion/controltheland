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
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 20:08:41
 */
public class SyncBoxItem extends SyncItem {
    private long createdTimeStamp; // Is not synchronized
    private boolean alive; // Synchronized in super class

    public SyncBoxItem(Id id, Index position, BoxItemType boxItemType, GlobalServices globalServices, PlanetServices planetServices) {
        super(id, position, boxItemType, globalServices, planetServices);
        createdTimeStamp = System.currentTimeMillis();
        alive = true;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public boolean isInTTL() {
        return System.currentTimeMillis() - createdTimeStamp < ((BoxItemType) getItemType()).getTtl();
    }
}
