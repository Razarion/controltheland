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
import com.btxtech.game.services.tutorial.DbResourceHintConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.ResourceHintManager;
import com.btxtech.game.services.tutorial.TutorialService;
import java.util.Collection;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:52:27
 */
@Component("tutorialService")
public class TutorialServiceImpl implements TutorialService, ResourceHintManager {
    private HibernateTemplate hibernateTemplate;
    private CrudServiceHelper<DbTutorialConfig> tutorialCrudServiceHelper;
    private TutorialConfig tutorialConfig;
    private int imageId;
    private HashMap<Integer, DbResourceHintConfig> resourceHints = new HashMap<Integer, DbResourceHintConfig>();
    private Log log = LogFactory.getLog(TutorialServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        try {
            tutorialCrudServiceHelper = new CrudServiceHelperHibernateImpl<DbTutorialConfig>(hibernateTemplate, DbTutorialConfig.class);
            activate();
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
        Collection<DbTutorialConfig> dbTutorialConfigs = tutorialCrudServiceHelper.readDbChildren();
        if (dbTutorialConfigs.isEmpty()) {
            throw new IllegalStateException("No tutorial defined");
        }
        DbTutorialConfig dbTutorialConfig = dbTutorialConfigs.iterator().next();
        imageId = 0;
        resourceHints.clear();
        tutorialConfig = dbTutorialConfig.createTutorialConfig(this);
    }

    @Override
    public TutorialConfig getTutorialConfig() {
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
