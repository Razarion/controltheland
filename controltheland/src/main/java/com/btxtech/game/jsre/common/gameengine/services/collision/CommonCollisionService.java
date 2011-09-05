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

package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 19:46:17
 */
public interface CommonCollisionService {
    static final int MAX_TRIES = 1000000;

    Index getRallyPoint(SyncBaseItem factory, Collection<SurfaceType> allowedSurfaces);

    AttackFormation.AttackFormationItem getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItemArea target, TerrainType targetTerrainType);
}
