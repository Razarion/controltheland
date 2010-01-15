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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.UserDetails;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:41:05
 */
@Component("userTrackingService")
public class UserTrackingServiceImpl implements UserTrackingService {
    @Autowired
    private Session session;
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void pageAccess(BasePage basePage) {
        PageAccess pageAccess = new PageAccess(session.getSessionId(), basePage.getClass().getName(), basePage.getAdditionalPageInfo());
        hibernateTemplate.saveOrUpdate(pageAccess);
    }

    @Override
    public void newSession(UserDetails userDetails) {
        hibernateTemplate.saveOrUpdate(userDetails);
    }

    @Override
    public void gameStartup(GameStartupState state, Date timeStamp) {
        GameStartup gameStartup = new GameStartup(session.getSessionId(), state, timeStamp);
        hibernateTemplate.saveOrUpdate(gameStartup);
    }

    @Override
    public void saveUserActions(ArrayList<UserAction> userActions) {
        ArrayList<DbUserAction> dbUserActions = new ArrayList<DbUserAction>();
        for (UserAction userAction : userActions) {
            dbUserActions.add(new DbUserAction(userAction, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbUserActions);        
    }
}
