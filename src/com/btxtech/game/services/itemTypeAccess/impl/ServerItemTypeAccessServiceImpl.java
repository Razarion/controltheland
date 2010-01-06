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

package com.btxtech.game.services.itemTypeAccess.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.itemTypeAccess.ItemTypeAccessEntry;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:11:36
 */
@Component("serverItemTypeAccess")
public class ServerItemTypeAccessServiceImpl extends TimerTask implements ServerItemTypeAccessService {
    private static final long TICK_TIME_MILI_SECONDS_POINT_DISPATCHING = 10 * 60 *1000;
    private static final double POINT_DISPATCHING_FACTOR = 0.001;
    @Autowired
    private BaseService baseService;
    @Autowired
    private Session session;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;
    private HibernateTemplate hibernateTemplate;
    private Timer timer;
    private Log log = LogFactory.getLog(ServerItemTypeAccessServiceImpl.class);

    @PostConstruct
    public void start() {
        timer = new Timer(getClass().getName(), true);
        timer.scheduleAtFixedRate(this, 0, TICK_TIME_MILI_SECONDS_POINT_DISPATCHING);
    }

    @PreDestroy
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }


    @Override
    public Collection<Integer> getAllowedItemTypes() {
        return getUserItemTypeAccess().getItemTypeIds();
    }

    @Override
    public boolean isAllowed(int itemTypeId) {
        return getUserItemTypeAccess().contains(itemTypeId);
    }

    @Override
    public void buy(ItemTypeAccessEntry itemTypeAccessEntry) {
        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess();
        userItemTypeAccess.buy(itemTypeAccessEntry);
        if (connectionService.hasConnection()) {
            ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = new ItemTypeAccessSyncInfo();
            itemTypeAccessSyncInfo.setAllowedItemTypes(userItemTypeAccess.getItemTypeIds());
            baseService.sendPackage(itemTypeAccessSyncInfo);
            baseService.sendXpUpdate(userItemTypeAccess, baseService.getBase());
        }
        if(userItemTypeAccess.isPersistent()) {
            hibernateTemplate.saveOrUpdate(userItemTypeAccess);
        }
    }

    @Override
    public UserItemTypeAccess getUserItemTypeAccess() {
        UserItemTypeAccess userItemTypeAccess = session.getUserItemTypeAccess();
        if (userItemTypeAccess == null) {
            User user = userService.getLoggedinUser();
            userItemTypeAccess = createOrGetUserItemTypeAccess(user);
            session.setUserItemTypeAccess(userItemTypeAccess);
        } else if (userItemTypeAccess.isPersistent()) {
            hibernateTemplate.refresh(userItemTypeAccess);
        }
        return userItemTypeAccess;
    }

    public UserItemTypeAccess createOrGetUserItemTypeAccess(User user) {
        if (user != null) {
            UserItemTypeAccess access = user.getUserItemTypeAccess();
            if (access != null) {
                hibernateTemplate.refresh(access);
                return access;
            }
        }
        return createUserItemTypeAccess(user);
    }

    private UserItemTypeAccess createUserItemTypeAccess(User user) {
        UserItemTypeAccess userItemTypeAccess = new UserItemTypeAccess(getAlwaysAllowed());
        if (user != null) {
            user.setUserItemTypeAccess(userItemTypeAccess);
            userService.save(user);
        }
        return userItemTypeAccess;
    }

    private Collection<ItemTypeAccessEntry> getAlwaysAllowed() {
        return (Collection<ItemTypeAccessEntry>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ItemTypeAccessEntry.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("alwaysAllowed", true));
                return criteria.list();
            }
        });
    }

    @Override
    public Collection<ItemTypeAccessEntry> getItemTypeAccessEntries() {
        return (Collection<ItemTypeAccessEntry>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ItemTypeAccessEntry.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public void saveItemTypeAccessEntries(ArrayList<ItemTypeAccessEntry> itemTypeAccessEntries) {
        hibernateTemplate.saveOrUpdateAll(itemTypeAccessEntries);
    }

    @Override
    public void createNewItemTypeAccessEntry() {
        hibernateTemplate.saveOrUpdate(new ItemTypeAccessEntry());
    }

    @Override
    public void delteItemTypeAccessEntry(ItemTypeAccessEntry itemTypeAccessEntry) {
        hibernateTemplate.delete(itemTypeAccessEntry);
    }

    public int getXp() {
        return getUserItemTypeAccess().getXp();
    }

    @Override
    public void run() {
        List<SyncItem> syncItems = itemService.getItemsCopy();
        for (SyncItem syncItem : syncItems) {
            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                try {
                    increaseXpPerItem(syncBaseItem);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    private void increaseXpPerItem(SyncBaseItem syncBaseItem) {
        SimpleBase simpleBase = syncBaseItem.getBase();
        Base base = baseService.getBase(simpleBase);
        if (base.isAbandoned()) {
            return;
        }

        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess4Base(base);
        if (userItemTypeAccess != null) {
            if(userItemTypeAccess.isPersistent()) {
               hibernateTemplate.refresh(userItemTypeAccess);
            }
            increaseXp((int) (syncBaseItem.getBaseItemType().getPrice() * POINT_DISPATCHING_FACTOR), userItemTypeAccess, base);
        }
    }

    private void increaseXp(int amount, UserItemTypeAccess userItemTypeAccess, Base base) {
        userItemTypeAccess.increaseXp(amount);
        baseService.sendXpUpdate(userItemTypeAccess, base);
        if (userItemTypeAccess.isPersistent()) {
            hibernateTemplate.saveOrUpdate(userItemTypeAccess);
        }
    }

    @Override
    public void increaseXp(Base actorBase, SyncBaseItem syncBaseItem) {
        if (actorBase.getUserItemTypeAccess() != null && !actorBase.isAbandoned()) {
            increaseXp(syncBaseItem.getBaseItemType().getPrice(), actorBase.getUserItemTypeAccess(), actorBase);
        }
    }

    private UserItemTypeAccess getUserItemTypeAccess4Base(Base base) {
        UserItemTypeAccess userItemTypeAccess = base.getUserItemTypeAccess();
        if (userItemTypeAccess != null) {
            return userItemTypeAccess;
        }
        // Loaded base from DB with registered user which was not online yet
        if (base.getUser() == null) {
            return null;
        }
        return createOrGetUserItemTypeAccess(base.getUser());
    }

    @Override
    public void clearSession() {
        session.setUserItemTypeAccess(null);
    }
}
