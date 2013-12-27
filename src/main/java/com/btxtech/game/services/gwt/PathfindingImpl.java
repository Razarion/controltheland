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

import com.btxtech.game.jsre.mapeditor.TerrainInfoImpl;
import com.btxtech.game.jsre.pathfinding.Pathfinding;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:32:46 PM
 */
public class PathfindingImpl extends AutowiredRemoteServiceServlet implements Pathfinding {
    @Autowired
    private TerrainImageService terrainService;
    private Log log = LogFactory.getLog(PathfindingImpl.class);

    @Override
    public com.btxtech.game.jsre.common.TerrainInfo getTerrainInfo(int terrainId) {
        try {
            com.btxtech.game.jsre.common.TerrainInfo terrainInfo = new TerrainInfoImpl();
            // TODO setup terrain
            terrainService.setupTerrainImages(terrainInfo);
            return terrainInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }
}