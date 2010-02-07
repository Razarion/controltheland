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
import java.util.List;
import java.util.Collection;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 14:23:09
 */
public interface AbstractTerrainService {
    List<Index> setupPathToDestination(Index target, Index destination, int range);

    List<Index> setupPathToDestination(Index start, Index destination);

    Collection<TerrainImagePosition> getTerrainImagePositions();

    TerrainSettings getTerrainSettings();

    void addTerrainListener(TerrainListener terrainListener);

    List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absolutePxRectangle);

    Collection<TerrainImage> getTerrainImages();

    Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition);

    TerrainImage getTerrainImage(TerrainImagePosition terrainImagePosition);

    TerrainImagePosition getTerrainImagePosition(int absoluteX, int absoluteY);

    Index getTerrainTileIndexForAbsPosition(int x, int y);

    int getTerrainTileIndexForAbsXPosition(int x);

    int getTerrainTileIndexForAbsYPosition(int y);

    Index getTerrainTileIndexForAbsPosition(Index absolutePos);

    Index getAbsolutIndexForTerrainTileIndex(Index tileIndex);

    Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile);

    int getAbsolutXForTerrainTile(int xTile);

    int getAbsolutYForTerrainTile(int yTile);

    Rectangle convertToTilePosition(Rectangle rectangle);

    Rectangle convertToAbsolutePosition(Rectangle rectangle);

    boolean isFree(Index posititon, ItemType itemType);

    boolean isTerrainPassable(Index posititon);
}
