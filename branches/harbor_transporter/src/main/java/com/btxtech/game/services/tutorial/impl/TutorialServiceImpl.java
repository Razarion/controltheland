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

package com.btxtech.game.services.tutorial.impl;

import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainDbUtil;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:52:27
 */
@Component("tutorialService")
public class TutorialServiceImpl implements TutorialService {
    @Autowired
    private CrudRootServiceHelper<DbTutorialConfig> dbTutorialConfigCrudRootServiceHelper;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TerrainImageService terrainImageService;

    @PostConstruct
    public void ini() {
        dbTutorialConfigCrudRootServiceHelper.init(DbTutorialConfig.class);
    }

    @Override
    public CrudRootServiceHelper<DbTutorialConfig> getDbTutorialCrudRootServiceHelper() {
        return dbTutorialConfigCrudRootServiceHelper;
    }

    @Override
    public DbTutorialConfig getDbTutorialConfig(int levelTaskId) throws InvalidLevelStateException {
        try {
            return userGuidanceService.getDbLevel().getDbTutorialConfigFromTask(levelTaskId);
        } catch (NoSuchChildException e) {
            throw userGuidanceService.createInvalidLevelState();
        }
    }

    @Override
    public DbTutorialConfig getDbTutorialConfig4Tracking(int levelTaskId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, levelTaskId);
        if (dbLevelTask == null) {
            throw new NoSuchChildException(levelTaskId, DbLevelTask.class);
        }
        return dbLevelTask.getDbTutorialConfig();
    }


    @Transactional
    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int tutorialId) {
        DbTutorialConfig dbTutorialConfig = dbTutorialConfigCrudRootServiceHelper.readDbChild(tutorialId);
        DbTerrainSetting dbTerrainSetting = dbTutorialConfig.getDbTerrainSetting();
        TerrainDbUtil.modifyTerrainSetting(dbTerrainSetting, terrainImagePositions, surfaceRects, terrainImageService);
        dbTutorialConfigCrudRootServiceHelper.updateDbChild(dbTutorialConfig);
    }
}
