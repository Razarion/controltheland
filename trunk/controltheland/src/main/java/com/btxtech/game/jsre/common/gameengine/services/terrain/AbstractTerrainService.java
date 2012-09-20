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

package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 14:23:09
 */
public interface AbstractTerrainService {
    public interface TerrainTileEvaluator {
        void evaluate(int x, int y, TerrainTile terrainTile);
    }

    TerrainSettings getTerrainSettings();

    void addTerrainListener(TerrainListener terrainListener);

    @Deprecated
    Index getTerrainTileIndexForAbsPosition(int x, int y);

    @Deprecated
    int getTerrainTileIndexForAbsXPosition(int x);

    @Deprecated
    int getTerrainTileIndexForAbsYPosition(int y);

    @Deprecated
    Index getTerrainTileIndexForAbsPosition(Index absolutePos);

    @Deprecated
    Index getAbsolutIndexForTerrainTileIndex(Index tileIndex);

    Index getTerrainTileIndexForAbsPositionRoundUp(Index absolutePos);

    @Deprecated
    Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile);

    @Deprecated
    int getAbsolutXForTerrainTile(int xTile);

    @Deprecated
    int getAbsolutYForTerrainTile(int yTile);

    @Deprecated
    Rectangle convertToTilePosition(Rectangle rectangle);

    @Deprecated
    Rectangle convertToTilePositionRoundUp(Rectangle rectangle);

    @Deprecated
    Rectangle convertToAbsolutePosition(Rectangle rectangle);

    boolean isFreeZeroSize(Index point, ItemType itemType);

    boolean isFree(Index middlePoint, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces);

    boolean isFree(Index middlePoint, ItemType itemType);

    SurfaceType getSurfaceType(Index tileIndex);

    SurfaceType getSurfaceTypeAbsolute(Index absoluteIndex);

    Index correctPosition(SyncItem syncItem, Index position);

    void createTerrainTileField(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects);

    TerrainTile[][] getTerrainTileField();

    void iteratorOverAllTerrainTiles(Rectangle tileRect, TerrainTileEvaluator terrainTileEvaluator);

    void iteratorOverAllTerrainTiles(Rectangle tileRect, TerrainTileEvaluator terrainTileEvaluator, int xIncrease, int yIncrease);
}
