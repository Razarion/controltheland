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

import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:32:46 PM
 */
@Component("terrainEditor")
public class TerrainEditoImpl implements TerrainEditor {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private TerritoryService territoryService;
    private Log log = LogFactory.getLog(TerrainEditoImpl.class);

    @Override
    public TerrainInfo getTerrainInfo() {
        try {
            TerrainInfo terrainInfo = new TerrainInfo();
            terrainInfo.setTerrainSettings(terrainService.getTerrainSettings());
            terrainInfo.setTerrainImagePositions(terrainService.getTerrainImagePositions());
            terrainInfo.setTerrainImages(terrainService.getTerrainImages());
            terrainInfo.setSurfaceImages(terrainService.getSurfaceImages());
            terrainInfo.setSurfaceRects(terrainService.getSurfaceRects());
            return terrainInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void saveTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects) {
        try {
            terrainService.saveAndActivateTerrain(terrainImagePositions, surfaceRects);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void saveTerritory(Territory territory) {
        try {
            territoryService.saveTerritory(territory);
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
}
