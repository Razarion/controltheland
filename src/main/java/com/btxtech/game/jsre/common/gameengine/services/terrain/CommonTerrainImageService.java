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

import java.util.Collection;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 14:23:09
 */
public interface CommonTerrainImageService {
    Collection<TerrainImage> getTerrainImages();

    Collection<SurfaceImage> getSurfaceImages();

    SurfaceImage getSurfaceImage(int surfaceImageId);

    TerrainImage getTerrainImage(int terrainImageId);

    TerrainImageBackground getTerrainImageBackground();
}
