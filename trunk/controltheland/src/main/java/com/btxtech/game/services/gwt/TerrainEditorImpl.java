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

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainInfoImpl;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.RegionService;
import com.btxtech.game.services.terrain.TerrainDbUtil;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
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
public class TerrainEditorImpl extends AutowiredRemoteServiceServlet implements TerrainEditor {
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegionService regionService;
    private Log log = LogFactory.getLog(TerrainEditorImpl.class);

    @Override
    public TerrainInfo getPlanetTerrainInfo(int planetId) {
        try {
            TerrainInfoImpl terrainInfoImpl = new TerrainInfoImpl();
            terrainImageService.setupTerrainImages(terrainInfoImpl);
            DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(planetId);
            TerrainDbUtil.loadTerrainFromDb(dbPlanet.getDbTerrainSetting(), terrainInfoImpl);
            return terrainInfoImpl;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public TerrainInfo getTutorialTerrainInfo(int tutorialId) {
        try {
            TerrainInfoImpl terrainInfoImpl = new TerrainInfoImpl();
            terrainImageService.setupTerrainImages(terrainInfoImpl);
            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(tutorialId);
            TerrainDbUtil.loadTerrainFromDb(dbTutorialConfig.getDbTerrainSetting(), terrainInfoImpl);
            return terrainInfoImpl;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void savePlanetTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int planetId) {
        try {
            planetSystemService.saveTerrain(terrainImagePositions, surfaceRects, planetId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void saveTutorialTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int tutorialId) {
        try {
            tutorialService.saveTerrain(terrainImagePositions, surfaceRects, tutorialId);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public Map<String, Collection<Integer>> getTerrainImageGroups() {
        try {
            Map<String, Collection<Integer>> imageGroups = new HashMap<>();
            for (DbTerrainImageGroup imageGroup : terrainImageService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren()) {
                Collection<Integer> imageId = new ArrayList<>();
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

    @Override
    public void saveRegionToDb(Region region) {
        try {
            regionService.saveRegionToDb(region);
        } catch (Throwable t) {
            log.error("", t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public Region loadRegionFromDb(int regionId) {
        try {
            return regionService.loadRegionFromDb(regionId);
        } catch (Throwable t) {
            log.error("", t);
            throw new RuntimeException(t);
        }
    }
}
