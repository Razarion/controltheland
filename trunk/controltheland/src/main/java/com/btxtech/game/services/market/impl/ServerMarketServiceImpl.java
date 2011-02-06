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
import com.btxtech.game.services.common.QueueWorker;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.MarketCategory;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.MarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.XpSettings;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private ServerConditionService serverConditionService;
    private HibernateTemplate hibernateTemplate;
    private Timer timer;
    private XpSettings xpSettings;
    private Log log = LogFactory.getLog(ServerMarketServiceImpl.class);
    private QueueWorker<XpPerKill> xpPerKillQueueWorker = new QueueWorker<XpPerKill>() {
        @Override
        protected void processEntries(List<XpPerKill> xpPerKills) {
            HashMap<SimpleBase, Integer> baseXpHashMap = new HashMap<SimpleBase, Integer>();
            for (XpPerKill xpPerKill : xpPerKills) {
                if (xpPerKill.getActorBase().isAbandoned()) {
                    continue;
                }
                sumUpXpPerBase(baseXpHashMap, xpPerKill.getActorBase().getSimpleBase(), xpPerKill.getKilledItem(), xpSettings.getKillPriceFactor());
            }

            increaseXpPerBase(baseXpHashMap);
        }
    };

    @PostConstruct
    public void start() {
        loadXpPointSettings();
        if (xpSettings.getPeriodMilliSeconds() > 0) {
            timer = new Timer(getClass().getName(), true);
            timer.scheduleAtFixedRate(new XpPeriodTask(), xpSettings.getPeriodMilliSeconds(), xpSettings.getPeriodMilliSeconds());
        }
    }

    private void loadXpPointSettings() {
        @SuppressWarnings("unchecked")
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
        xpPerKillQueueWorker.stop();
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
        return userItemTypeAccess != null && userItemTypeAccess.contains(itemTypeId);
    }

    @Override
    public void buy(MarketEntry marketEntry) {
       if(! userGuidanceService.isBaseItemTypeAllowedInLevel((DbBaseItemType) marketEntry.getItemType())) {
          throw new IllegalStateException("Item type not allowed in level: " + userGuidanceService.getDbLevel() + " " +  marketEntry.getItemType());
       }
        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess();
        userItemTypeAccess.buy(marketEntry);
        if (connectionService.hasConnection()) {
            ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = new ItemTypeAccessSyncInfo();
            itemTypeAccessSyncInfo.setAllowedItemTypes(userItemTypeAccess.getItemTypeIds());
            baseService.sendPackage(itemTypeAccessSyncInfo);
            baseService.sendXpUpdate(userItemTypeAccess, baseService.getBase());
        }
    }

    @Override
    public UserItemTypeAccess getUserItemTypeAccess() {
        UserItemTypeAccess userItemTypeAccess = userService.getUserState().getUserItemTypeAccess();
        if (userItemTypeAccess == null) {
            userItemTypeAccess = createOrGetUserItemTypeAccess(userService.getUserState());
        }
        return userItemTypeAccess;
    }

    @Override
    public UserItemTypeAccess getUserItemTypeAccess(Base base) {
        if (base.isAbandoned()) {
            return null;
        }
        UserState userState = baseService.getUserState(base.getSimpleBase());
        return createOrGetUserItemTypeAccess(userState);
    }

    public UserItemTypeAccess createOrGetUserItemTypeAccess(UserState userState) {
        if (userState != null) {
            UserItemTypeAccess access = userState.getUserItemTypeAccess();
            if (access != null) {
                return access;
            }
        }
        return createUserItemTypeAccess(userState);
    }

    private UserItemTypeAccess createUserItemTypeAccess(UserState userState) {
        UserItemTypeAccess userItemTypeAccess = new UserItemTypeAccess(getAlwaysAllowed());
        userState.setUserItemTypeAccess(userItemTypeAccess);
        return userItemTypeAccess;
    }

    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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

    private void increaseXp(int amount, UserItemTypeAccess userItemTypeAccess, Base base) {
        try {
            increaseXpInternal(base, amount, userItemTypeAccess);
            serverConditionService.onIncreaseXp(base.getSimpleBase(), amount);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void increaseXp(Base actorBase, SyncBaseItem killedItem) {
        xpPerKillQueueWorker.put(new XpPerKill(actorBase, killedItem));
    }

    @Override
    public void increaseXp(Base base, int deltaXp) {
        UserItemTypeAccess userItemTypeAccess = base.getUserState().getUserItemTypeAccess();
        increaseXpInternal(base, deltaXp, userItemTypeAccess);
    }

    private void increaseXpInternal(Base base, int deltaXp, UserItemTypeAccess userItemTypeAccess) {
        int xp = userItemTypeAccess.getXp();
        DbRealGameLevel dbRealGameLevel = userGuidanceService.getDbLevel();
        if (xp == dbRealGameLevel.getMaxXp()) {
            return;
        } else if (xp > dbRealGameLevel.getMaxXp()) {
            userItemTypeAccess.setXp(dbRealGameLevel.getMaxXp());
        } else if (xp + deltaXp > dbRealGameLevel.getMaxXp()) {
            userItemTypeAccess.increaseXp(dbRealGameLevel.getMaxXp() - xp);
        } else {
            userItemTypeAccess.increaseXp(deltaXp);
        }
        baseService.sendXpUpdate(userItemTypeAccess, base);
    }

    private UserItemTypeAccess getUserItemTypeAccess4Base(SimpleBase simpleBase) {
        UserState userState = baseService.getUserState(simpleBase);
        if (userState == null) {
            // Base is may be killed in the mean time
            return null;
        }

        if (userState.getUserItemTypeAccess() != null) {
            return userState.getUserItemTypeAccess();
        } else {
            return createOrGetUserItemTypeAccess(userState);
        }
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
    @SuppressWarnings("unchecked")
    public List<MarketCategory> getMarketCategories() {
        return hibernateTemplate.loadAll(MarketCategory.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MarketFunction> getMarketFunctions() {
        return hibernateTemplate.loadAll(MarketFunction.class);
    }

    @Override
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public List<MarketCategory> getUsedMarketCategories() {
        return (List<MarketCategory>) hibernateTemplate.find("from com.btxtech.game.services.market.MarketCategory where exists (from com.btxtech.game.services.market.MarketEntry where marketCategory is not null)");
    }

    class XpPeriodTask extends TimerTask {
        @Override
        public void run() {
            try {
                periodicalXpIncrease();
            } catch (Exception e) {
                log.error("", e);
            }
        }

        private void periodicalXpIncrease() {
            HashMap<SimpleBase, Integer> xpIncreasePreBase = new HashMap<SimpleBase, Integer>();
            List<SyncItem> syncItems = itemService.getItemsCopy();
            for (SyncItem syncItem : syncItems) {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    Base base = baseService.getBase(syncBaseItem.getBase());
                    if (base == null || base.isAbandoned()) {
                        // Base is may be killed in the mean time
                        continue;
                    }
                    // Increase XP
                    sumUpXpPerBase(xpIncreasePreBase, base.getSimpleBase(), syncBaseItem, xpSettings.getPeriodItemFactor());
                }
            }

            increaseXpPerBase(xpIncreasePreBase);
        }

    }

    private void sumUpXpPerBase(HashMap<SimpleBase, Integer> xpIncreasePreBase, SimpleBase base, SyncBaseItem syncBaseItem, double factor) {
        Integer xp = xpIncreasePreBase.get(base);
        if (xp == null) {
            xp = 0;
        }
        xp += (int) (syncBaseItem.getBaseItemType().getPrice() * factor);
        xpIncreasePreBase.put(base, xp);
    }

    private void increaseXpPerBase(HashMap<SimpleBase, Integer> xpIncreasePreBase) {
        for (Map.Entry<SimpleBase, Integer> entry : xpIncreasePreBase.entrySet()) {
            UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess4Base(entry.getKey());
            if (userItemTypeAccess == null) {
                continue;
            }
            Base base = baseService.getBase(entry.getKey());
            if (base == null || base.isAbandoned()) {
                // Base is may be killed in the mean time
                continue;
            }
            increaseXp(entry.getValue(), userItemTypeAccess, base);
        }
    }
}
