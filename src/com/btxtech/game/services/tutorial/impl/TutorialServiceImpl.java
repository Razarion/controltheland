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
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperHibernateImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.tutorial.hint.DbResourceHintConfig;
import com.btxtech.game.services.tutorial.hint.ResourceHintManager;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:52:27
 */
@Component("tutorialService")
public class TutorialServiceImpl implements TutorialService, ResourceHintManager {
    private HibernateTemplate hibernateTemplate;
    private CrudServiceHelper<DbTutorialConfig> tutorialCrudServiceHelper;
    private final Map<DbAbstractLevel, TutorialConfig> tutorialConfigMap = new HashMap<DbAbstractLevel, TutorialConfig>();
    private int imageId;
    private HashMap<Integer, DbResourceHintConfig> resourceHints = new HashMap<Integer, DbResourceHintConfig>();
    private Log log = LogFactory.getLog(TutorialServiceImpl.class);
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ItemService itemService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        try {
            tutorialCrudServiceHelper = new CrudServiceHelperHibernateImpl<DbTutorialConfig>(hibernateTemplate, DbTutorialConfig.class);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public CrudServiceHelper<DbTutorialConfig> getDbTutorialCrudServiceHelper() {
        return tutorialCrudServiceHelper;
    }

    @Override
    public void activate() {
        List<DbAbstractLevel> dbAbstractLevels = userGuidanceService.getDbLevels();
        if (dbAbstractLevels.isEmpty()) {
            throw new IllegalStateException("No levels defined");
        }
        synchronized (tutorialConfigMap) {
            imageId = 0;
            resourceHints.clear();
            tutorialConfigMap.clear();
            for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
                if (dbAbstractLevel instanceof DbSimulationLevel) {
                    DbTutorialConfig dbTutorialConfig = ((DbSimulationLevel) dbAbstractLevel).getDbTutorialConfig();
                    if (dbTutorialConfig == null) {
                        log.warn("No DbTutorialConfig for level: " + dbAbstractLevel);
                        continue;
                    }
                    TutorialConfig tutorialConfig = dbTutorialConfig.createTutorialConfig(this, itemService);
                    tutorialConfigMap.put(dbAbstractLevel, tutorialConfig);
                }
            }
        }
    }

    @Override
    public TutorialConfig getTutorialConfig(DbAbstractLevel dbAbstractLevel) {
        TutorialConfig tutorialConfig = tutorialConfigMap.get(dbAbstractLevel);
        if (tutorialConfig == null) {
            throw new IllegalArgumentException("No TutorialConfig for: " + dbAbstractLevel);
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

    @Override
    @Transactional
    public void saveTutorial(DbTutorialConfig dbTutorialConfig) {
        tutorialCrudServiceHelper.updateDbChild(dbTutorialConfig);
    }

    @Override
    public DbStepConfig getDbStepConfig(int dbStepConfigId) {
        return (DbStepConfig) hibernateTemplate.get(DbStepConfig.class, dbStepConfigId);
    }

    @Override
    @Transactional
    public void saveDbStepConfig(DbStepConfig dbStepConfig) {
        hibernateTemplate.update(dbStepConfig);
    }
}
