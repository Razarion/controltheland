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

import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:55:20 AM
 */
public interface TerrainService extends AbstractTerrainService {
    DbTerrainSetting getDbTerrainSettings();

    DbTerrainImage getDbTerrainImage(int id);

    List<DbTerrainImage> getDbTerrainImagesCopy();

    void saveAndActivateTerrainImages(List<DbTerrainImage> dbTerrainImages, byte[] bgImage, String bgImageType);

    void saveAndActivateTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions);
}
