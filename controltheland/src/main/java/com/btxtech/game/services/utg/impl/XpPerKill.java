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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 11.10.2010
 * Time: 19:36:01
 */
public class XpPerKill {
    private SimpleBase actorBase;
    private SyncBaseItem killedItem;
    private PlanetServices planetServices;

    public XpPerKill(SimpleBase actorBase, SyncBaseItem killedItem, PlanetServices planetServices) {
        this.actorBase = actorBase;
        this.killedItem = killedItem;
        this.planetServices = planetServices;
    }

    public SimpleBase getActorBase() {
        return actorBase;
    }

    public SyncBaseItem getKilledItem() {
        return killedItem;
    }

    public PlanetServices getPlanetServices() {
        return planetServices;
    }
}
