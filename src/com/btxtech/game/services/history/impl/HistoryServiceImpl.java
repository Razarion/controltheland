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
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import java.util.ArrayList;
import java.util.List;
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
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void addBaseStartEntry(SimpleBase simpleBase) {
        save(new HistoryElement(HistoryElement.Type.BASE_STARTED, baseService.getBaseName(simpleBase), baseService.getUser(simpleBase), null, null, null, null));
    }

    @Override
    public void addBaseDefeatedEntry(SyncBaseItem actor, SimpleBase target) {
        save(new HistoryElement(HistoryElement.Type.BASE_DEFEATED, baseService.getBaseName(actor.getBase()), baseService.getUser(actor.getBase()), actor, baseService.getBaseName(target), baseService.getUser(target), null));
    }

    @Override
    public void addBaseSurrenderedEntry(SimpleBase simpleBase) {
        save(new HistoryElement(HistoryElement.Type.BASE_SURRENDERED, baseService.getBaseName(simpleBase), baseService.getUser(simpleBase), null, null, null, null));
    }

    @Override
    public void addItemCreatedEntry(SyncBaseItem syncBaseItem) {
        save(new HistoryElement(HistoryElement.Type.ITEM_CREATED, baseService.getBaseName(syncBaseItem.getBase()), baseService.getUser(syncBaseItem.getBase()), syncBaseItem, null, null, null));
    }

    @Override
    public void addItemDestroyedEntry(SyncBaseItem actor, SyncBaseItem target) {
        save(new HistoryElement(HistoryElement.Type.ITEM_DESTROYED, baseService.getBaseName(actor.getBase()), baseService.getUser(actor.getBase()), actor, baseService.getBaseName(target.getBase()), baseService.getUser(target.getBase()), target));
    }

    @Override
    public List<DisplayHistoryElement> getNewestHistoryElements(final User user, final int count) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<DisplayHistoryElement>();
        @SuppressWarnings("unchecked")
        List<HistoryElement> historyElements = (List<HistoryElement>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(HistoryElement.class);
                criteria.setMaxResults(count);
                criteria.add(Restrictions.or(Restrictions.eq("user", user), Restrictions.eq("targetUser", user)));
                criteria.addOrder(Property.forName("timeStampMs").desc());
                return criteria.list();
            }
        });
        for (HistoryElement historyElement : historyElements) {
            displayHistoryElements.add(convert(user, historyElement));
        }
        return displayHistoryElements;
    }

    private DisplayHistoryElement convert(User user, HistoryElement historyElement) {
        DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(historyElement.getTimeStamp());
        switch (historyElement.getType()) {
            case BASE_STARTED:
                displayHistoryElement.setMessage("Base created: " + historyElement.getBaseName());
                break;
            case BASE_DEFEATED:
                if (user.equals(historyElement.getUser())) {
                    displayHistoryElement.setMessage("Base destroyed: " + historyElement.getTargetBaseName());
                } else {
                    displayHistoryElement.setMessage("Your base " + historyElement.getTargetBaseName() + " has been destroyed by " + historyElement.getBaseName());
                }
                break;
            case BASE_SURRENDERED:
                displayHistoryElement.setMessage("Base surrendered: " + historyElement.getBaseName());
                break;
            case ITEM_CREATED:
                displayHistoryElement.setMessage("Item created: " + historyElement.getItemName());
                break;
            case ITEM_DESTROYED:
                if (user.equals(historyElement.getUser())) {
                    displayHistoryElement.setMessage("Destroyed a " + historyElement.getTargetItemName() + " from " + historyElement.getTargetBaseName());
                } else {
                    displayHistoryElement.setMessage(historyElement.getBaseName() + " destroyed your " + historyElement.getTargetItemName());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown: " + historyElement.getType());
        }
        return displayHistoryElement;
    }

    private void save(HistoryElement historyElement) {
        try {
            hibernateTemplate.saveOrUpdate(historyElement);
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
