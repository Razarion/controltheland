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
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import java.util.List;

/**
 * User: beat
 * Date: May 24, 2009
 * Time: 6:46:48 PM
 */
public interface CollisionService extends CommonCollisionService {
    List<PassableRectangle> getPassableRectangles();

    Index getFreeRandomPosition(ItemType itemType, int edgeLength);

    void addCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener);

    void removeCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener);

    List<Index> setupPathToDestination(Index start, Index destination);
}
