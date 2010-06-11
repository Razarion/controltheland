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

package com.btxtech.game.services.market.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.market.MarketCategory;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.MarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.XpSettings;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
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
public class ServerMarketServiceImpl implements ServerMarketService {
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
    @Autowired
    private UserGuidanceService userGuidanceService;
    private HibernateTemplate hibernateTemplate;
    private Timer timer;
    private XpSettings xpSettings;
    private Log log = LogFactory.getLog(ServerMarketServiceImpl.class);

    @PostConstruct
    public void start() {
        stop();
        loadXpPointSettings();
        if (xpSettings.getPeriodMilliSeconds() > 0) {
            timer = new Timer(getClass().getName(), true);
            timer.scheduleAtFixedRate(new XpPeriodTask(), xpSettings.getPeriodMilliSeconds(), xpSettings.getPeriodMilliSeconds());
        }
    }

    private void loadXpPointSettings() {
        List<XpSettings> settings = hibernateTemplate.loadAll(XpSettings.class);
        if (settings.isEmpty()) {
            log.warn("No XpSettings found in DB. Will be created.");
            xpSettings = new XpSettings();
            xpSettings.setKillPriceFactor(0.1);
            xpSettings.setPeriodItemFactor(0.001);
            xpSettings.setPeriodMinutes(10);
            hibernateTemplate.saveOrUpdate(xpSettings);
        } else if (settings.size() != 1) {
            log.warn("More then one XpSettings found in DB.");
            xpSettings = settings.get(0);
        } else {
            xpSettings = settings.get(0);
        }
    }

    @PreDestroy
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
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
    public boolean isAllowed(int itemTypeId, Base base) {
        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess(base);
        if(userItemTypeAccess != null) {
            return userItemTypeAccess.contains(itemTypeId);
        }   else {
            return false;
        }
    }

    @Override
    public void buy(MarketEntry marketEntry) {
        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess();
        userItemTypeAccess.buy(marketEntry);
        if (connectionService.hasConnection()) {
            ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = new ItemTypeAccessSyncInfo();
            itemTypeAccessSyncInfo.setAllowedItemTypes(userItemTypeAccess.getItemTypeIds());
            baseService.sendPackage(itemTypeAccessSyncInfo);
            baseService.sendXpUpdate(userItemTypeAccess, baseService.getBase());
        }
        if (userItemTypeAccess.isPersistent()) {
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

    @Override
    public UserItemTypeAccess getUserItemTypeAccess(Base base) {
        if (base.isAbandoned()) {
            return null;
        }
        User user = baseService.getUser(base.getSimpleBase());
        if (user != null) {
            return createOrGetUserItemTypeAccess(user);
        }
        return getUserItemTypeAccess();
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
            setUserItemTypeAccess(user, userItemTypeAccess);
        }
        return userItemTypeAccess;
    }

    public void setUserItemTypeAccess(User user, UserItemTypeAccess userItemTypeAccess) {
        user.setUserItemTypeAccess(userItemTypeAccess);
        userService.save(user);
    }

    private Collection<MarketEntry> getAlwaysAllowed() {
        return (Collection<MarketEntry>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(MarketEntry.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("alwaysAllowed", true));
                return criteria.list();
            }
        });
    }

    @Override
    public List<MarketEntry> getItemTypeAccessEntries() {
        return (List<MarketEntry>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(MarketEntry.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public List<MarketEntry> getMarketEntries(final MarketCategory marketCategory) {
        return (List<MarketEntry>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(MarketEntry.class);
                criteria.add(Restrictions.eq("marketCategory", marketCategory));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public void saveItemTypeAccessEntries(ArrayList<MarketEntry> marketEntries) {
        hibernateTemplate.saveOrUpdateAll(marketEntries);
    }

    @Override
    public void createNewItemTypeAccessEntry() {
        hibernateTemplate.saveOrUpdate(new MarketEntry());
    }

    @Override
    public void deleteItemTypeAccessEntry(MarketEntry marketEntry) {
        hibernateTemplate.delete(marketEntry);
    }

    public int getXp() {
        return getUserItemTypeAccess().getXp();
    }

    private void increaseXpPerItem(SyncBaseItem syncBaseItem) {
        SimpleBase simpleBase = syncBaseItem.getBase();
        Base base = baseService.getBase(simpleBase);
        if (base.isAbandoned()) {
            return;
        }

        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess4Base(base);
        if (userItemTypeAccess != null) {
            if (userItemTypeAccess.isPersistent()) {
                hibernateTemplate.refresh(userItemTypeAccess);
            }
            increaseXp((int) (syncBaseItem.getBaseItemType().getPrice() * xpSettings.getPeriodItemFactor()), userItemTypeAccess, base);
        }
    }

    private void increaseXp(int amount, UserItemTypeAccess userItemTypeAccess, Base base) {
        userItemTypeAccess.increaseXp(amount);
        baseService.sendXpUpdate(userItemTypeAccess, base);
        userGuidanceService.onIncreaseXp(base, userItemTypeAccess.getXp());
        if (userItemTypeAccess.isPersistent()) {
            hibernateTemplate.saveOrUpdate(userItemTypeAccess);
        }
    }

    @Override
    public void increaseXp(Base actorBase, SyncBaseItem syncBaseItem) {
        if (actorBase.getUserItemTypeAccess() != null && !actorBase.isAbandoned()) {
            increaseXp((int) (syncBaseItem.getBaseItemType().getPrice() * xpSettings.getKillPriceFactor()), actorBase.getUserItemTypeAccess(), actorBase);
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

    public XpSettings getXpPointSettings() {
        try {
            return (XpSettings) xpSettings.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveXpPointSettings(XpSettings xpSettings) {
        hibernateTemplate.saveOrUpdate(xpSettings);
        start();
    }

    @Override
    public void addMarketCategory() {
        hibernateTemplate.save(new MarketCategory());
    }

    @Override
    public void addMarketFunction() {
        hibernateTemplate.save(new MarketFunction());
    }

    @Override
    public List<MarketCategory> getMarketCategories() {
        return hibernateTemplate.loadAll(MarketCategory.class);
    }

    @Override
    public List<MarketFunction> getMarketFunctions() {
        return hibernateTemplate.loadAll(MarketFunction.class);
    }

    @Override
    public void deleteMarketCategory(MarketCategory category) {
        hibernateTemplate.delete(category);
    }

    @Override
    public void saveMarketCategories(ArrayList<MarketCategory> marketCategories) {
        hibernateTemplate.saveOrUpdateAll(marketCategories);
    }

    @Override
    public void deleteMarketFunction(MarketFunction marketFunction) {
        hibernateTemplate.delete(marketFunction);
    }

    @Override
    public void saveMarketFunctions(ArrayList<MarketFunction> marketFunctions) {
        hibernateTemplate.saveOrUpdateAll(marketFunctions);
    }

    @Override
    public List<MarketCategory> getUsedMarketCategories() {
        return (List<MarketCategory>) hibernateTemplate.find("from com.btxtech.game.services.market.MarketCategory where exists (from com.btxtech.game.services.market.MarketEntry where marketCategory is not null)");
    }

    class XpPeriodTask extends TimerTask {
        @Override
        public void run() {
            List<SyncItem> syncItems = itemService.getItemsCopyNoDummies();
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
    }
}
