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

package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
@RemoteServiceRelativePath("terrainEditor")
public interface TerrainEditor extends RemoteService {
    TerrainInfo getPlanetTerrainInfo(int planetId);

    TerrainInfo getTutorialTerrainInfo(int tutorialId);

    void savePlanetTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int planetId);

    void saveTutorialTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int tutorialId);

    Map<String, Collection<Integer>> getTerrainImageGroups();

    void saveRegionToDb(Region region);

    Region loadRegionFromDb(int regionId);
}
