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

import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.utg.DbAbstractLevel;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:55:20 AM
 */
public interface TerrainService extends AbstractTerrainService {
    DbTerrainImage getDbTerrainImage(int id);

    DbSurfaceImage getDbSurfaceImage(int id);

    int getDbTerrainImagesBitSize();

    void activateTerrain();

    void saveTerrainImages(Set<DbTerrainImage> newDbTerrainImages, Set<DbTerrainImage> updatedDbTerrainImages, Set<DbTerrainImage> deletedDbTerrainImages, List<DbSurfaceImage> dbSurfaceImages);    

    void saveAndActivateTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int terrainId);

    void setupTerrain(GameInfo gameInfo, DbAbstractLevel dbAbstractLevel);

    void setupTerrain(TerrainInfo terrainInfo, int terrainId);

    CrudServiceHelper<DbTerrainSetting> getDbTerrainSettingCrudServiceHelper();

    void saveDbTerrainSetting(List<DbTerrainSetting> dbTerrainSettings);

    CrudServiceHelper<DbTerrainImage> getDbTerrainImageCrudServiceHelper();

    CrudServiceHelper<DbSurfaceImage> getDbSurfaceImageCrudServiceHelper();
}
