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

package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:32:46 PM
 */
public class TerrainEditoImpl extends AutowiredRemoteServiceServlet implements TerrainEditor {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private TerritoryService territoryService;
    private Log log = LogFactory.getLog(TerrainEditoImpl.class);

    @Override
    public TerrainInfo getTerrainInfo(int terrainId) {
        try {
            TerrainInfo terrainInfo = new TerrainInfo();
            terrainService.setupTerrain(terrainInfo, terrainId);
            return terrainInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void saveTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int terrainId) {
        try {
            terrainService.saveTerrain(terrainImagePositions, surfaceRects, terrainId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void saveTerritory(int territoryId, Collection<Rectangle> territoryTileRegions) {
        try {
            territoryService.saveTerritory(territoryId, territoryTileRegions);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public Collection<Territory> getTerritories() {
        try {
            return territoryService.getTerritories();
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public Map<String, Collection<Integer>> getTerrainImageGroups() {
        try {
            Map<String, Collection<Integer>> imageGroups = new HashMap<String, Collection<Integer>>();
            for (DbTerrainImageGroup imageGroup : terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren()) {
                Collection<Integer> imageId = new ArrayList<Integer>();
                imageGroups.put(imageGroup.getName(), imageId);
                for (DbTerrainImage dbTerrainImage : imageGroup.getTerrainImageCrud().readDbChildren()) {
                    imageId.add(dbTerrainImage.getId());
                }
            }
            return imageGroups;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }
}
