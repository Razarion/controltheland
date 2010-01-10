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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:55:20 AM
 */
public interface TerrainService {
    @Deprecated
    TerrainFieldTile getTerrainFieldTile(int indexX, int indexY);

    @Deprecated
    Map<Index, TerrainFieldTile> getTerrainFieldTilesCopy();

    void addTerrainChangeListener(TerrainChangeListener terrainChangeListener);

    void removeTerrainChangeListener(TerrainChangeListener terrainChangeListener);

    DbTerrainSetting getTerrainSetting();

    TerrainImage getTerrainImage(int id);

    List<TerrainImage> getTerrainImagesCopy();

    Collection<Integer> getTerrainImageIds();

    void saveAndActivateTerrainImages(List<TerrainImage> terrainImages, TerrainImage terrainBackgroundImage);
}
