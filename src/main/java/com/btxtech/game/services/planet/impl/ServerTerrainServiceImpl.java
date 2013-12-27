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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.CommonTerrainImageService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.planet.ServerTerrainService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
public class ServerTerrainServiceImpl extends AbstractTerrainServiceImpl implements ServerTerrainService {
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Log log = LogFactory.getLog(ServerTerrainServiceImpl.class);
    private ServerGlobalServices serverGlobalServices;

    public void init(ServerGlobalServices serverGlobalServices) {
        this.serverGlobalServices = serverGlobalServices;
    }

    public void activate(DbPlanet dbPlanet) {
        // Terrain settings
        DbTerrainSetting dbTerrainSetting = dbPlanet.getDbTerrainSetting();
        if (dbTerrainSetting == null) {
            log.error("No terrain settings for planet: " + dbPlanet);
            return;
        }
        setTerrainSettings(dbTerrainSetting.createTerrainSettings());

        // Terrain image position
        terrainImagePositions = new ArrayList<>();
        for (DbTerrainImagePosition dbTerrainImagePosition : dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren()) {
            terrainImagePositions.add(dbTerrainImagePosition.createTerrainImagePosition());
        }

        // Surface rectangles
        surfaceRects = new ArrayList<>();
        for (DbSurfaceRect dbSurfaceRect : dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren()) {
            surfaceRects.add(dbSurfaceRect.createSurfaceRect());
        }

        createTerrainTileField(terrainImagePositions, surfaceRects);

        fireTerrainChanged();
    }

    @Override
    protected CommonTerrainImageService getCommonTerrainImageService() {
        return serverGlobalServices.getTerrainImageService();
    }

    @Override
    public void setupTerrainRealGame(GameInfo gameInfo) {
        gameInfo.setTerrainSettings(getTerrainSettings());
        gameInfo.setTerrainImagePositions(terrainImagePositions);
        gameInfo.setSurfaceRects(surfaceRects);
    }
}
