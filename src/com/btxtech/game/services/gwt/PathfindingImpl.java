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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.jsre.pathfinding.Pathfinding;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.PassableRectangle;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;
    private Log log = LogFactory.getLog(PathfindingImpl.class);

    @Override
    public Map<TerrainType, List<Rectangle>> getPassableRectangles() {
        try {
            Map<TerrainType, List<Rectangle>> result = new HashMap<TerrainType, List<Rectangle>>();
            Map<TerrainType, List<PassableRectangle>> passableRectangles = collisionService.getPassableRectangles();
            for (Map.Entry<TerrainType, List<PassableRectangle>> entry : passableRectangles.entrySet()) {
                List<Rectangle> rectangles = new ArrayList<Rectangle>();
                for (PassableRectangle passableRectangle : entry.getValue()) {
                    rectangles.add(passableRectangle.getPixelRectangle(terrainService.getTerrainSettings()));
                }
                result.put(entry.getKey(), rectangles);
            }
            return result;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

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
    public List<Index> findPath(Index start, Index destination, TerrainType terrainType) {
        try {
            return collisionService.setupPathToDestination(start, destination, terrainType);
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }


}