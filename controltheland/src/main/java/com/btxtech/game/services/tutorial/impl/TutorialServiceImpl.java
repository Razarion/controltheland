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

import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.tutorial.hint.DbResourceHintConfig;
import com.btxtech.game.services.tutorial.hint.ResourceHintManager;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:52:27
 */
@Component("tutorialService")
public class TutorialServiceImpl implements TutorialService, ResourceHintManager {
    @Autowired
    private CrudRootServiceHelper<DbTutorialConfig> dbTutorialConfigCrudRootServiceHelper;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ItemService itemService;
    private final Map<DbSimulationLevel, TutorialConfig> tutorialConfigMap = new HashMap<DbSimulationLevel, TutorialConfig>();
    private int imageId;
    private HashMap<Integer, DbResourceHintConfig> resourceHints = new HashMap<Integer, DbResourceHintConfig>();
    private Log log = LogFactory.getLog(TutorialServiceImpl.class);

    @PostConstruct
    public void ini() {
        dbTutorialConfigCrudRootServiceHelper.init(DbTutorialConfig.class);
    }

    @Override
    public CrudRootServiceHelper<DbTutorialConfig> getDbTutorialCrudRootServiceHelper() {
        return dbTutorialConfigCrudRootServiceHelper;
    }

    @Override
    public void activate() {
        List<DbAbstractLevel> dbAbstractLevels = userGuidanceService.getDbLevels();
        if (dbAbstractLevels.isEmpty()) {
            log.error("No levels defined");
            return;
        }
        synchronized (tutorialConfigMap) {
            imageId = 0;
            resourceHints.clear();
            tutorialConfigMap.clear();
            for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
                if (dbAbstractLevel instanceof DbSimulationLevel) {
                    DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) dbAbstractLevel;
                    DbTutorialConfig dbTutorialConfig = dbTutorialConfigCrudRootServiceHelper.readDbChild(dbSimulationLevel.getDbTutorialConfig().getId());
                    if (dbTutorialConfig == null) {
                        log.warn("No DbTutorialConfig for level: " + dbSimulationLevel);
                        continue;
                    }
                    TutorialConfig tutorialConfig = dbTutorialConfig.createTutorialConfig(this, itemService);
                    tutorialConfigMap.put(dbSimulationLevel, tutorialConfig);
                }
            }
        }
    }

    @Override
    public TutorialConfig getTutorialConfig(DbSimulationLevel dbSimulationLevel) {
        TutorialConfig tutorialConfig = tutorialConfigMap.get(dbSimulationLevel);
        if (tutorialConfig == null) {
            throw new IllegalArgumentException("No TutorialConfig for: " + dbSimulationLevel);
        }
        return tutorialConfig;
    }

    @Override
    public int addResource(DbResourceHintConfig dbResourceHintConfig) {
        imageId++;
        resourceHints.put(imageId, dbResourceHintConfig);
        return imageId;
    }

    @Override
    public DbResourceHintConfig getDbResourceHintConfig(int id) {
        DbResourceHintConfig dbResourceHintConfig = resourceHints.get(id);
        if (dbResourceHintConfig == null) {
            throw new IllegalArgumentException("No DbResourceHintConfig for id: " + id);
        }
        return dbResourceHintConfig;
    }
}
