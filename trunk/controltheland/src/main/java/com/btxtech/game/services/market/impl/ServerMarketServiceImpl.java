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
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.QueueWorker;
import com.btxtech.game.services.common.ReadonlyCollectionContentProvider;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.AvailableMarketEntry;
import com.btxtech.game.services.market.DbMarketCategory;
import com.btxtech.game.services.market.DbMarketEntry;
import com.btxtech.game.services.market.DbMarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.XpSettings;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:11:36
 */
@Component("marketService")
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
    @Autowired
    private CrudRootServiceHelper<DbMarketCategory> marketCategoryCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbMarketFunction> marketFunctionCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbMarketEntry> marketEntryCrudRootServiceHelper;
    private HibernateTemplate hibernateTemplate;
    private Timer timer;
    private XpSettings xpSettings;
    private Log log = LogFactory.getLog(ServerMarketServiceImpl.class);
    private XpPerKillQueueWorker xpPerKillQueueWorker;

    class XpPerKillQueueWorker extends QueueWorker<XpPerKill> {
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
    }

    @PostConstruct
    public void start() {
        marketCategoryCrudRootServiceHelper.init(DbMarketCategory.class);
        marketFunctionCrudRootServiceHelper.init(DbMarketFunction.class);
        marketEntryCrudRootServiceHelper.init(DbMarketEntry.class);
        loadXpPointSettings();
        stop();
        if (xpSettings.getPeriodMilliSeconds() > 0) {
            timer = new Timer(getClass().getName(), true);
            timer.scheduleAtFixedRate(new XpPeriodTask(), xpSettings.getPeriodMilliSeconds(), xpSettings.getPeriodMilliSeconds());
        }
        xpPerKillQueueWorker = new XpPerKillQueueWorker();
    }

    @PreDestroy
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (xpPerKillQueueWorker != null) {
            xpPerKillQueueWorker.stop();
            xpPerKillQueueWorker = null;
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
    public void buy(DbMarketEntry dbMarketEntry) {
        if (!userGuidanceService.isBaseItemTypeAllowedInLevel((DbBaseItemType) dbMarketEntry.getItemType())) {
            throw new IllegalStateException("Item type not allowed in level: " + userGuidanceService.getDbLevel() + " " + dbMarketEntry.getItemType());
        }
        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess();
        userItemTypeAccess.buy(dbMarketEntry);
        if (connectionService.hasConnection()) {
            ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = new ItemTypeAccessSyncInfo();
            itemTypeAccessSyncInfo.setAllowedItemTypes(userItemTypeAccess.getItemTypeIds());
            baseService.sendPackage(itemTypeAccessSyncInfo);
            baseService.sendXpUpdate(userItemTypeAccess, baseService.getBase());
        }
    }

    @Override
    public UserItemTypeAccess getUserItemTypeAccess() {
        return createOrGetUserItemTypeAccess(userService.getUserState());
    }

    private UserItemTypeAccess createOrGetUserItemTypeAccess(UserState userState) {
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
    private Collection<DbMarketEntry> getAlwaysAllowed() {
        return (Collection<DbMarketEntry>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbMarketEntry.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("alwaysAllowed", true));
                return criteria.list();
            }
        });
    }

    @Override
    public CrudRootServiceHelper<DbMarketCategory> getCrudMarketCategoryService() {
        return marketCategoryCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbMarketFunction> getCrudMarketFunctionService() {
        return marketFunctionCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbMarketEntry> getCrudMarketEntryService() {
        return marketEntryCrudRootServiceHelper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbMarketEntry> getMarketEntries(final DbMarketCategory dbMarketCategory) {
        return (List<DbMarketEntry>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbMarketEntry.class);
                criteria.add(Restrictions.eq("dbMarketCategory", dbMarketCategory));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
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
        UserItemTypeAccess userItemTypeAccess = createOrGetUserItemTypeAccess(base.getUserState());
        increaseXpInternal(base, deltaXp, userItemTypeAccess);
    }

    private void increaseXpInternal(Base base, int deltaXp, UserItemTypeAccess userItemTypeAccess) {
        int xp = userItemTypeAccess.getXp();
        DbRealGameLevel dbRealGameLevel = userGuidanceService.getDbLevel(base.getSimpleBase());
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

        return createOrGetUserItemTypeAccess(userState);
    }

    public XpSettings getXpPointSettings() {
        XpSettings xpSettings;
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
        return xpSettings;
    }

    private void loadXpPointSettings() {
        xpSettings = getXpPointSettings();
    }


    @Transactional
    public void saveXpPointSettings(XpSettings xpSettings) {
        hibernateTemplate.saveOrUpdate(xpSettings);
        start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbMarketCategory> getMarketCategories() {
        return hibernateTemplate.loadAll(DbMarketCategory.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbMarketFunction> getMarketFunctions() {
        return hibernateTemplate.loadAll(DbMarketFunction.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbMarketCategory> getUsedMarketCategories() {
        return (List<DbMarketCategory>) hibernateTemplate.find("from com.btxtech.game.services.market.DbMarketCategory where exists (from com.btxtech.game.services.market.DbMarketEntry where dbMarketCategory is not null)");
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
                    if (base == null || base.isAbandoned() || base.getUserState().isBot()) {
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

    @Override
    public ReadonlyCollectionContentProvider<AvailableMarketEntry> getAvailableCrud() {
        ArrayList<AvailableMarketEntry> availableMarketEntries = new ArrayList<AvailableMarketEntry>();

        UserItemTypeAccess userItemTypeAccess = getUserItemTypeAccess();
        Collection<DbMarketEntry> marketEntries = marketEntryCrudRootServiceHelper.readDbChildren();
        for (DbMarketEntry marketEntry : marketEntries) {
            if (userItemTypeAccess.contains(marketEntry)) {
                continue;
            }
            if(marketEntry.getItemType() == null) {
                // this should not be checked here. This is a miss configuration.
                continue;
            }
            if (!userGuidanceService.isBaseItemTypeAllowedInLevel((DbBaseItemType) marketEntry.getItemType())) {
                continue;
            }

            availableMarketEntries.add(new AvailableMarketEntry(marketEntry));
        }
        return new ReadonlyCollectionContentProvider<AvailableMarketEntry>(availableMarketEntries);
    }
}
