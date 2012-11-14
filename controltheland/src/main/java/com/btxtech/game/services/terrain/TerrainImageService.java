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

import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.CommonTerrainImageService;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:55:20 AM
 */
public interface TerrainImageService extends CommonTerrainImageService {
    DbTerrainImage getDbTerrainImage(int id);

    DbSurfaceImage getDbSurfaceImage(int id);

    int getDbTerrainImagesSizeInBytes();

    void activate();

    void setupTerrainImages(TerrainInfo terrainInfo);

    CrudRootServiceHelper<DbTerrainImageGroup> getDbTerrainImageGroupCrudServiceHelper();

    CrudRootServiceHelper<DbSurfaceImage> getDbSurfaceImageCrudServiceHelper();
}
