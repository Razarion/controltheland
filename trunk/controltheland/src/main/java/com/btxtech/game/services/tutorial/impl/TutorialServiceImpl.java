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

import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
    private ItemService itemService;
    @Autowired
    private SessionFactory sessionFactory;

    @PostConstruct
    public void ini() {
        dbTutorialConfigCrudRootServiceHelper.init(DbTutorialConfig.class);
    }

    @Override
    public CrudRootServiceHelper<DbTutorialConfig> getDbTutorialCrudRootServiceHelper() {
        return dbTutorialConfigCrudRootServiceHelper;
    }

    @Override
    public DbTutorialConfig getDbTutorialConfig(int levelTaskId) throws InvalidLevelState {
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

}
