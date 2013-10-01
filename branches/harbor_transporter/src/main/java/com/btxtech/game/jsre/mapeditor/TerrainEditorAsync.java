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
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
public interface TerrainEditorAsync {
    public static final String TERRAIN_SETTING_ID = "terrain_id";
    public static final String ROOT_ID = "parent_id";
    public static final String ROOT_TYPE = "parent_type";
    public static final String ROOT_TYPE_PLANET = "planet";
    public static final String ROOT_TYPE_MISSION = "mission";
    public static final String REGION_ID = "region_id";

    void savePlanetTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int planetId, AsyncCallback<Void> async);

    void saveTutorialTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int tutorialId, AsyncCallback<Void> async);

    void getPlanetTerrainInfo(int planetId, AsyncCallback<TerrainInfo> async);

    void getTutorialTerrainInfo(int tutorialId, AsyncCallback<TerrainInfo> asyncCallback);

    void getTerrainImageGroups(AsyncCallback<Map<String, Collection<Integer>>> asyncCallback);

    void saveRegionToDb(Region region, AsyncCallback<Void> asyncCallback);

    void loadRegionFromDb(int regionId, AsyncCallback<Region> asyncCallback);
}