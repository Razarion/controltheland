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
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:37 PM
 */
@Component("historyService")
public class HistoryServiceImpl implements HistoryService {
    static private final int NEWEST_HISTORY_ELEMENT_COUNT = 30;
    private Log log = LogFactory.getLog(HistoryServiceImpl.class);
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private com.btxtech.game.services.connection.Session session;

    private DbHistoryElement.Source determineSource(SimpleBase actor, SimpleBase target) {
        if (actor != null && !baseService.isBot(actor)) {
            return DbHistoryElement.Source.HUMAN;
        }
        if (target != null && !baseService.isBot(target)) {
            return DbHistoryElement.Source.HUMAN;
        }
        return DbHistoryElement.Source.BOT;
    }

    @Override
    @Transactional
    public void addBaseStartEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_STARTED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                null,
                baseService,
                getSessionId(simpleBase),
                determineSource(simpleBase, null)));
    }

    @Override
    @Transactional
    public void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_DEFEATED,
                userService.getUser(actor),
                userService.getUser(target),
                actor,
                target,
                null,
                null,
                null,
                baseService,
                getSessionId(actor),
                determineSource(actor, target)));
    }

    @Override
    @Transactional
    public void addBaseSurrenderedEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_SURRENDERED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                null,
                baseService,
                getSessionId(simpleBase),
                determineSource(simpleBase, null)));
    }

    @Override
    @Transactional
    public void addItemCreatedEntry(SyncBaseItem syncBaseItem) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_CREATED,
                userService.getUser(syncBaseItem.getBase()),
                null,
                syncBaseItem.getBase(),
                null,
                syncBaseItem,
                null,
                null,
                baseService,
                getSessionId(syncBaseItem.getBase()),
                determineSource(syncBaseItem.getBase(), null)));
    }

    @Override
    @Transactional
    public void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_DESTROYED,
                userService.getUser(actor),
                userService.getUser(target.getBase()),
                actor,
                target.getBase(),
                target,
                null,
                null,
                baseService,
                getSessionId(actor),
                determineSource(actor, target.getBase())));
    }

    @Override
    @Transactional
    public void addLevelPromotionEntry(UserState userState, DbLevel level) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_PROMOTION,
                userState.getUser(),
                null,
                null,
                null,              // TODO
                null,
                level,
                null,
                baseService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN));
    }

    @Override
    @Transactional
    public void addLevelTaskCompletedEntry(UserState userState, DbLevelTask levelTask) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_TASK_COMPLETED,
                userState.getUser(),
                null,
                null,
                null,              // TODO
                null,
                null,
                levelTask,
                baseService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN));
    }

    private String getSessionId(SimpleBase simpleBase) {
        UserState userState = baseService.getUserState(simpleBase);
        if (userState != null && userState.getSessionId() != null) {
            return userState.getSessionId();
        }
        try {
            return session.getSessionId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DisplayHistoryElement> getNewestHistoryElements(final User user, final int count) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<DisplayHistoryElement>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        criteria.setMaxResults(count);
        criteria.add(Restrictions.or(Restrictions.eq("actorUserName", user.getUsername()), Restrictions.eq("targetUserName", user.getUsername())));
        criteria.addOrder(Property.forName("timeStampMs").desc());
        criteria.addOrder(Property.forName("id").desc()); // If Timestamp is equals, assume id is in ascending form
        for (DbHistoryElement dbHistoryElement : (Collection<DbHistoryElement>) criteria.list()) {
            displayHistoryElements.add(convert(user, null, dbHistoryElement));
        }
        return displayHistoryElements;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DisplayHistoryElement> getHistoryElements(final Long from, final Long to, final String sessionId, final Integer baseId) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<DisplayHistoryElement>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        if (baseId != null) {
            criteria.add(Restrictions.or(Restrictions.eq("sessionId", sessionId), Restrictions.or(Restrictions.eq("actorBaseId", baseId), Restrictions.eq("targetBaseId", baseId))));
        } else {
            criteria.add(Restrictions.eq("sessionId", sessionId));
        }
        if (from != null) {
            criteria.add(Restrictions.ge("timeStampMs", from));
        }
        if (to != null) {
            criteria.add(Restrictions.lt("timeStampMs", to));
        }
        criteria.addOrder(Property.forName("timeStampMs").desc());
        criteria.addOrder(Property.forName("id").desc()); // If Timestamp is equals, assume id is in ascending form
        for (DbHistoryElement dbHistoryElement : (Collection<DbHistoryElement>) criteria.list()) {
            displayHistoryElements.add(convert(null, baseId, dbHistoryElement));
        }
        return displayHistoryElements;
    }

    @Override
    public int getLevelPromotionCount(final String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        criteria.add(Restrictions.eq("type", DbHistoryElement.Type.LEVEL_PROMOTION));
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    private DisplayHistoryElement convert(User user, Integer baseId, DbHistoryElement dbHistoryElement) {
        DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(dbHistoryElement.getTimeStampMs(), dbHistoryElement.getId());
        String userName = null;
        if (user != null) {
            userName = user.getUsername();
        }
        switch (dbHistoryElement.getType()) {
            case BASE_STARTED:
                displayHistoryElement.setMessage("Base created: " + dbHistoryElement.getActorBaseName());
                break;
            case BASE_DEFEATED:
                if (userName != null) {
                    if (userName.equals(dbHistoryElement.getActorUserName())) {
                        displayHistoryElement.setMessage("Base destroyed: " + dbHistoryElement.getTargetBaseName());
                    } else if (userName.equals(dbHistoryElement.getTargetBaseName())) {
                        displayHistoryElement.setMessage("Your base has been destroyed by " + dbHistoryElement.getActorBaseName());
                    } else {
                        displayHistoryElement.setMessage("Internal error 1");
                        log.error("Unknown state 1: " + userName + " " + dbHistoryElement.getActorUserName() + " " + dbHistoryElement.getTargetBaseName());
                    }
                } else if (baseId != null) {
                    if (baseId.equals(dbHistoryElement.getActorBaseId())) {
                        displayHistoryElement.setMessage("Base destroyed: " + dbHistoryElement.getTargetBaseName());
                    } else if (baseId.equals(dbHistoryElement.getTargetBaseId())) {
                        displayHistoryElement.setMessage("Base was destroyed by: " + dbHistoryElement.getActorBaseName());
                    } else {
                        displayHistoryElement.setMessage("Internal error 2");
                        log.error("Unknown state 2: " + baseId + " " + dbHistoryElement.getActorBaseId() + " " + dbHistoryElement.getTargetBaseId());
                    }
                } else {
                    throw new IllegalArgumentException("user and baseId are null");
                }
                break;
            case BASE_SURRENDERED:
                displayHistoryElement.setMessage("Base surrendered");
                break;
            case ITEM_CREATED:
                displayHistoryElement.setMessage("Item created: " + dbHistoryElement.getItemTypeName());
                break;
            case ITEM_DESTROYED:
                if (userName != null) {
                    if (userName.equals(dbHistoryElement.getActorUserName())) {
                        displayHistoryElement.setMessage("Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName());
                    } else if (userName.equals(dbHistoryElement.getTargetBaseName())) {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            displayHistoryElement.setMessage(dbHistoryElement.getActorBaseName() + " destroyed your " + dbHistoryElement.getItemTypeName());
                        } else {
                            displayHistoryElement.setMessage(dbHistoryElement.getItemTypeName() + " has been sold");
                        }
                    } else {
                        displayHistoryElement.setMessage("Internal error 3");
                        log.error("Unknown state 3: " + userName + " " + dbHistoryElement.getActorUserName() + " " + dbHistoryElement.getTargetBaseName());
                    }
                } else if (baseId != null) {
                    if (baseId.equals(dbHistoryElement.getActorBaseId())) {
                        displayHistoryElement.setMessage("Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName());
                    } else if (baseId.equals(dbHistoryElement.getTargetBaseId())) {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            displayHistoryElement.setMessage(dbHistoryElement.getActorBaseName() + " destroyed a " + dbHistoryElement.getItemTypeName());
                        } else {
                            displayHistoryElement.setMessage(dbHistoryElement.getItemTypeName() + " has been sold");
                        }
                    } else {
                        displayHistoryElement.setMessage("Internal error 4");
                        log.error("Unknown state 4: " + baseId + " " + dbHistoryElement.getActorBaseId() + " " + dbHistoryElement.getTargetBaseId());
                    }
                } else {
                    throw new IllegalArgumentException("user and baseId are null");
                }
                break;
            case LEVEL_PROMOTION:
                displayHistoryElement.setMessage("Level reached: " + dbHistoryElement.getLevelName());
                break;
            case LEVEL_TASK_COMPLETED:
                displayHistoryElement.setMessage("Level Task competed: " + dbHistoryElement.getLevelTaskName());
                break;
            default:
                throw new IllegalArgumentException("Unknown: " + dbHistoryElement.getType());
        }
        return displayHistoryElement;
    }

    private void save(DbHistoryElement dbHistoryElement) {
        try {
            sessionFactory.getCurrentSession().save(dbHistoryElement);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public ReadonlyListContentProvider<DisplayHistoryElement> getNewestHistoryElements() {
        User user = userService.getUser();
        if (user != null) {
            return new ReadonlyListContentProvider<DisplayHistoryElement>(getNewestHistoryElements(user, NEWEST_HISTORY_ELEMENT_COUNT));
        } else {
            return new ReadonlyListContentProvider<DisplayHistoryElement>(Collections.<DisplayHistoryElement>emptyList());
        }
    }
}
