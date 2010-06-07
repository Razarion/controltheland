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

package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: May 24, 2009
 * Time: 6:46:48 PM
 */
public interface CollisionService extends CommonCollisionService {
    Map<TerrainType, List<PassableRectangle>> getPassableRectangles();

    Index getFreeRandomPosition(Index origin, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces, int targetMinRange, int targetMaxRange);

    Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange);

    void addCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener);

    void removeCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener);

    List<Index> setupPathToDestination(Index start, Index destination, TerrainType terrainType);
}
