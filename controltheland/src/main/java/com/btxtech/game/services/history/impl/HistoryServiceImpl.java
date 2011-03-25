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

package com.btxtech.game.services.history.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbAbstractLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:37 PM
 */
@Component("historyService")
public class HistoryServiceImpl implements HistoryService {
    private Log log = LogFactory.getLog(HistoryServiceImpl.class);
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void addBaseStartEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_STARTED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                baseService));
    }

    @Override
    public void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_DEFEATED,
                userService.getUser(actor),
                userService.getUser(target),
                actor,
                target,
                null,
                null,
                baseService));
    }

    @Override
    public void addBaseSurrenderedEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_SURRENDERED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                baseService));
    }

    @Override
    public void addItemCreatedEntry(SyncBaseItem syncBaseItem) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_CREATED,
                userService.getUser(syncBaseItem.getBase()),
                null,
                syncBaseItem.getBase(),
                null,
                syncBaseItem,
                null,
                baseService));
    }

    @Override
    public void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_DESTROYED,
                userService.getUser(actor),
                userService.getUser(target.getBase()),
                actor,
                target.getBase(),
                target,
                null,
                baseService));
    }

    @Override
    public void addLevelPromotionEntry(User user, DbAbstractLevel level) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_PROMOTION,
                user,
                null,
                null,
                null,
                null,
                level,
                baseService));
    }

    @Override
    public List<DisplayHistoryElement> getNewestHistoryElements(final User user, final int count) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<DisplayHistoryElement>();
        @SuppressWarnings("unchecked")
        List<DbHistoryElement> dbHistoryElements = hibernateTemplate.execute(new HibernateCallback<List<DbHistoryElement>>() {
            public List<DbHistoryElement> doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(DbHistoryElement.class);
                criteria.setMaxResults(count);
                criteria.add(Restrictions.or(Restrictions.eq("actorUserName", user.getUsername()), Restrictions.eq("targetUserName", user.getUsername())));
                criteria.addOrder(Property.forName("timeStampMs").desc());
                return criteria.list();
            }
        });
        for (DbHistoryElement dbHistoryElement : dbHistoryElements) {
            displayHistoryElements.add(convert(user, dbHistoryElement));
        }
        return displayHistoryElements;
    }

    private DisplayHistoryElement convert(User user, DbHistoryElement dbHistoryElement) {
        DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(dbHistoryElement.getTimeStamp());
        String userName = user.getUsername();
        switch (dbHistoryElement.getType()) {
            case BASE_STARTED:
                displayHistoryElement.setMessage("Base created: " + dbHistoryElement.getActorBaseName());
                break;
            case BASE_DEFEATED:
                if (userName.equals(dbHistoryElement.getActorUserName())) {
                    displayHistoryElement.setMessage("Base destroyed: " + dbHistoryElement.getTargetBaseName());
                } else {
                    displayHistoryElement.setMessage("Your base has been destroyed by " + dbHistoryElement.getActorBaseName());
                }
                break;
            case BASE_SURRENDERED:
                displayHistoryElement.setMessage("Base surrendered");
                break;
            case ITEM_CREATED:
                displayHistoryElement.setMessage("Item created: " + dbHistoryElement.getItemTypeName());
                break;
            case ITEM_DESTROYED:
                if (userName.equals(dbHistoryElement.getActorUserName())) {
                    displayHistoryElement.setMessage("Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName());
                } else {
                    displayHistoryElement.setMessage(dbHistoryElement.getActorBaseName() + " destroyed your " + dbHistoryElement.getItemTypeName());
                }
                break;
            case LEVEL_PROMOTION:
                displayHistoryElement.setMessage("Level reached: " + dbHistoryElement.getLevelName());
                break;
            default:
                throw new IllegalArgumentException("Unknown: " + dbHistoryElement.getType());
        }
        return displayHistoryElement;
    }

    private void save(DbHistoryElement dbHistoryElement) {
        try {
            hibernateTemplate.save(dbHistoryElement);
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
