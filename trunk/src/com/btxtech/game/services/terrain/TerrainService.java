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

package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import java.util.Map;
import java.util.List;
import java.util.Collection;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:55:20 AM
 */
public interface TerrainService {
    TerrainFieldTile getTerrainFieldTile(int indexX, int indexY);

    Index getTerrainFieldTileCount();

    Map<Index, TerrainFieldTile> getTerrainFieldTilesCopy();

    int getPlayFieldXSize();

    int getPlayFieldYSize();

    Tile getTile(int id);

    int[][] getTerrainField();

    void clearTiles();

    void clearTerrain();

    void createNewTerrain(int xCount, int yCount, Tile tile);

    Tile createTile(byte[] imageData,  TerrainType terrainType);

    List<Integer> getTileIds();

    List<Tile> getTiles();

    void saveTile(final Tile tile);

    void deleteTile(Tile tile);

    void createTile();

    void activateTerrainField(int[][] filed);

    boolean isTerrainValid();

    void addTerrainChangeListener(TerrainChangeListener terrainChangeListener);

    void removeTerrainChangeListener(TerrainChangeListener terrainChangeListener);

    Collection<Integer> getPassableTerrainTileIds();
}
