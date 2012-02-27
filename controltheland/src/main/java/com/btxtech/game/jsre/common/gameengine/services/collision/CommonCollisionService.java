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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 19:46:17
 */
public interface CommonCollisionService {
    Index getRallyPoint(SyncBaseItem factory, Collection<ItemType> ableToBuild);

    AttackFormationItem getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItemArea target, TerrainType targetTerrainType);

    List<Index> setupPathToSyncMovableRandomPositionIfTaken(SyncItem syncItem);

    List<Index> setupPathToDestination(SyncBaseItem syncItem, Index destination);

    List<Index> setupPathToDestination(Index position, Index destinationHint, TerrainType terrainType, BoundingBox boundingBox);

    Map<TerrainType, Collection<PassableRectangle>> getPassableRectangles();

    List<AttackFormationItem> setupDestinationHints(SyncItemArea target, TerrainType targetTerrainType, List<AttackFormationItem> items);

    List<AttackFormationItem> setupDestinationHints(SyncItem target, List<AttackFormationItem> items);

    Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange, boolean botFree, boolean ignoreMovable);
}
