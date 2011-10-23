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

import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.jsre.pathfinding.Pathfinding;
import com.btxtech.game.services.terrain.TerrainService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:32:46 PM
 */
@Component("pathfinding")
public class PathfindingImpl implements Pathfinding {
    @Autowired
    private TerrainService terrainService;
    private Log log = LogFactory.getLog(PathfindingImpl.class);

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
}