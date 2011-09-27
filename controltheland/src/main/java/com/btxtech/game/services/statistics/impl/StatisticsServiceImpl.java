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

package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;
import com.btxtech.game.services.statistics.DbStatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:52 PM
 */
@Component("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private CrudRootServiceHelper<DbStatisticsEntry> dayStatistics;
    @Autowired
    private CrudRootServiceHelper<DbStatisticsEntry> weekStatistics;
    @Autowired
    private CrudRootServiceHelper<DbStatisticsEntry> allTimeStatistics;
    @Autowired
    private UserService userService;
    private HibernateTemplate hibernateTemplate;
    private final Map<UserState, DbStatisticsEntry> currentStatisticsCache = new HashMap<UserState, DbStatisticsEntry>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private long nextDay;
    private Log log = LogFactory.getLog(StatisticsServiceImpl.class);

    @PostConstruct
    public void init() {
        dayStatistics.init(DbStatisticsEntry.class);
        dayStatistics.putCriterion(DbStatisticsEntry.Type.DAY, Restrictions.eq("type", DbStatisticsEntry.Type.DAY));
        weekStatistics.init(DbStatisticsEntry.class);
        weekStatistics.putCriterion(DbStatisticsEntry.Type.WEEK, Restrictions.eq("type", DbStatisticsEntry.Type.WEEK));
        allTimeStatistics.init(DbStatisticsEntry.class);
        allTimeStatistics.putCriterion(DbStatisticsEntry.Type.ALL_TIME, Restrictions.eq("type", DbStatisticsEntry.Type.ALL_TIME));
        setupNextDay();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduleNextEndDayProcessing();

    }

    private void scheduleNextEndDayProcessing() {
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("Start end of day processing");
                    endOfDayProcessing(DateUtil.removeOneDay(new Date(nextDay)));
                    log.info("Finished end of day processing");
                } catch (Throwable t) {
                    log.error(t);
                }
                try {
                    scheduleNextEndDayProcessing();
                } catch (Throwable t) {
                    log.error(t);
                }
            }
        }, nextDay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    private void setupNextDay() {
        Date date = DateUtil.dayStart(new Date());
        nextDay = DateUtil.addOneDay(date).getTime();
    }

    @PreDestroy
    @Transactional
    public void cleanup() {
        moveCacheToDb();
        scheduledThreadPoolExecutor.shutdownNow();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    void moveCacheToDb() {
        Collection<DbStatisticsEntry> dbStatisticsEntries = new ArrayList<DbStatisticsEntry>();
        synchronized (currentStatisticsCache) {
            Date date = DateUtil.removeOneDay(new Date(nextDay));
            for (Map.Entry<UserState, DbStatisticsEntry> entry : currentStatisticsCache.entrySet()) {
                User user = entry.getKey().getUser();
                if (user != null) {
                    DbStatisticsEntry dbStatisticsEntry = entry.getValue();
                    dbStatisticsEntry.setType(DbStatisticsEntry.Type.DAY);
                    dbStatisticsEntry.setUser(user);
                    dbStatisticsEntry.setDate(date);
                    dbStatisticsEntries.add(dbStatisticsEntry);
                }
            }
            currentStatisticsCache.clear();
        }
        hibernateTemplate.saveOrUpdateAll(dbStatisticsEntries);
    }

    protected void endOfDayProcessing(final Date processingDay) {
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    closeDayIfNecessary();
                    makeDay(processingDay);
                    makeWeek(processingDay);
                    sumUpAllTime(processingDay);
                }
            });
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    private void makeDay(final Date day) {
        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.le("date", day));
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.DAY));
                // Order all entries
                @SuppressWarnings("unchecked")
                Map<Date, Map<User, List<DbStatisticsEntry>>> dateMap = setupDateMap(criteria.list());
                // Find broken days and sum up
                List<DbStatisticsEntry> toBeDeleted = new ArrayList<DbStatisticsEntry>();
                for (Map<User, List<DbStatisticsEntry>> userListMap : dateMap.values()) {
                    for (List<DbStatisticsEntry> dbStatisticsEntries : userListMap.values()) {
                        if (dbStatisticsEntries.size() > 1) {
                            DbStatisticsEntry summedUp = null;
                            for (DbStatisticsEntry dbStatisticsEntry : dbStatisticsEntries) {
                                if (summedUp == null) {
                                    summedUp = dbStatisticsEntry;
                                } else {
                                    summedUp.sumUp(dbStatisticsEntry);
                                    toBeDeleted.add(dbStatisticsEntry);
                                }
                            }
                        }
                    }
                }
                hibernateTemplate.deleteAll(toBeDeleted);
                return null;
            }
        });
    }

    private Map<Date, Map<User, List<DbStatisticsEntry>>> setupDateMap(List<DbStatisticsEntry> statisticsEntries) {
        Map<Date, Map<User, List<DbStatisticsEntry>>> dateMap = new HashMap<Date, Map<User, List<DbStatisticsEntry>>>();
        for (DbStatisticsEntry statisticsEntry : statisticsEntries) {
            Map<User, List<DbStatisticsEntry>> userMap = dateMap.get(statisticsEntry.getDate());
            if (userMap == null) {
                userMap = new HashMap<User, List<DbStatisticsEntry>>();
                dateMap.put(statisticsEntry.getDate(), userMap);
            }
            List<DbStatisticsEntry> entryList = userMap.get(statisticsEntry.getUser());
            if (entryList == null) {
                entryList = new ArrayList<DbStatisticsEntry>();
                userMap.put(statisticsEntry.getUser(), entryList);
            }
            entryList.add(statisticsEntry);
        }
        return dateMap;
    }

    private void makeWeek(Date date) {
        final Date thisWeekStart = DateUtil.weekStart(date);
        final Date lastWeekStart = DateUtil.removeOneWeek(thisWeekStart);

        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                // Check first if last week has already been processed
                Criteria criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.eq("date", lastWeekStart));
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.WEEK));
                criteria.setProjection(Projections.count("user"));
                if ((Long) criteria.list().get(0) > 0) {
                    return null;
                }

                // Process last week                
                criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.lt("date", thisWeekStart));
                criteria.add(Restrictions.ge("date", lastWeekStart));
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.DAY));

                @SuppressWarnings("unchecked")
                List<DbStatisticsEntry> dbStatisticsEntries = criteria.list();
                Map<User, DbStatisticsEntry> userWeek = new HashMap<User, DbStatisticsEntry>();
                for (DbStatisticsEntry dbStatisticsEntry : dbStatisticsEntries) {
                    DbStatisticsEntry week = userWeek.get(dbStatisticsEntry.getUser());
                    if (week == null) {
                        week = new DbStatisticsEntry();
                        week.setDate(lastWeekStart);
                        week.setType(DbStatisticsEntry.Type.WEEK);
                        week.setUser(dbStatisticsEntry.getUser());
                        userWeek.put(dbStatisticsEntry.getUser(), week);
                    }
                    week.sumUp(dbStatisticsEntry);

                }
                hibernateTemplate.saveOrUpdateAll(userWeek.values());
                return null;
            }
        });
    }

    private void sumUpAllTime(final Date date) {
        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                // Check first if all time is up to date
                Date lastProcessingDate = null;
                Criteria criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.ALL_TIME));
                criteria.setProjection(Projections.max("date"));
                List list = criteria.list();
                if (!list.isEmpty()) {
                    lastProcessingDate = (Date) list.get(0);
                }
                if (lastProcessingDate != null && (lastProcessingDate.after(date) || lastProcessingDate.equals(date))) {
                    return null;
                }

                criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.ALL_TIME));
                @SuppressWarnings("unchecked")
                List<DbStatisticsEntry> existingList = criteria.list();
                Map<User, DbStatisticsEntry> allTimeMap = new HashMap<User, DbStatisticsEntry>();
                for (DbStatisticsEntry dbStatisticsEntry : existingList) {
                    if (allTimeMap.containsKey(dbStatisticsEntry.getUser())) {
                        log.warn("More than one entry in all time statistics for user: " + dbStatisticsEntry.getUser());
                    }
                    allTimeMap.put(dbStatisticsEntry.getUser(), dbStatisticsEntry);
                }

                criteria = session.createCriteria(DbStatisticsEntry.class);
                criteria.add(Restrictions.le("date", date));
                if (lastProcessingDate != null) {
                    criteria.add(Restrictions.gt("date", lastProcessingDate));
                }
                criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.DAY));
                // Order all entries
                @SuppressWarnings("unchecked")
                List<DbStatisticsEntry> dbStatisticsEntries = criteria.list();

                for (DbStatisticsEntry dbStatisticsEntry : dbStatisticsEntries) {
                    DbStatisticsEntry allTime = allTimeMap.get(dbStatisticsEntry.getUser());
                    if (allTime == null) {
                        allTime = new DbStatisticsEntry();
                        allTime.setType(DbStatisticsEntry.Type.ALL_TIME);
                        allTime.setUser(dbStatisticsEntry.getUser());
                        allTimeMap.put(dbStatisticsEntry.getUser(), allTime);
                    }
                    allTime.sumUp(dbStatisticsEntry);
                }
                for (DbStatisticsEntry dbStatisticsEntry : allTimeMap.values()) {
                    dbStatisticsEntry.setDate(date);
                }
                hibernateTemplate.saveOrUpdateAll(allTimeMap.values());
                return null;
            }
        });
    }

    @Override
    @Transactional
    public void onMoneyEarned(SimpleBase simpleBase, double amount) {
        DbStatisticsEntry dbStatisticsEntry = getDbStatisticsEntry(simpleBase);
        dbStatisticsEntry.increaseMoneyEarned(amount);
    }

    @Override
    @Transactional
    public void onMoneySpent(SimpleBase simpleBase, double amount) {
        DbStatisticsEntry dbStatisticsEntry = getDbStatisticsEntry(simpleBase);
        dbStatisticsEntry.increaseMoneySpent(amount);
    }

    @Override
    @Transactional
    public void onItemKilled(SyncBaseItem targetItem, SimpleBase actorBase) {
        if (!baseService.isBot(actorBase) && !baseService.isAbandoned(actorBase)) {
            DbStatisticsEntry actorEntry = getDbStatisticsEntry(actorBase);
            if (baseService.isBot(targetItem.getBase())) {
                if (targetItem.hasSyncMovable()) {
                    actorEntry.increaseKilledUnitsBot();
                } else {
                    actorEntry.increaseKilledStructureBot();
                }
            } else {
                if (targetItem.hasSyncMovable()) {
                    actorEntry.increaseKilledUnitsPlayer();
                } else {
                    actorEntry.increaseKilledStructurePlayer();
                }
            }
        }
    }

    @Override
    @Transactional
    public void onItemCreated(SyncBaseItem syncBaseItem) {
        if (!baseService.isBot(syncBaseItem.getBase())) {
            DbStatisticsEntry entry = getDbStatisticsEntry(syncBaseItem.getBase());
            if (syncBaseItem.hasSyncMovable()) {
                entry.increaseBuiltUnits();
            } else {
                entry.increaseBuiltStructures();
            }
        }
    }

    @Override
    @Transactional
    public void onBaseKilled(SimpleBase target, SimpleBase actor) {
        if (!baseService.isBot(actor) && !baseService.isAbandoned(actor)) {
            DbStatisticsEntry actorEntry = getDbStatisticsEntry(actor);
            if (baseService.isBot(target)) {
                actorEntry.increaseBasesDestroyedBot();
            } else {
                actorEntry.increaseBasesDestroyedPlayer();
            }
        }
        if (!baseService.isBot(target) && !baseService.isAbandoned(target)) {
            DbStatisticsEntry targetEntry = getDbStatisticsEntry(target);
            targetEntry.increaseOwnBaseLost();
        }
    }

    @Override
    @Transactional
    public void onLevelPromotion(UserState userState) {
        DbStatisticsEntry entry = getDbStatisticsEntry(userState);
        entry.increaseLevelCompleted();
    }

    private DbStatisticsEntry getDbStatisticsEntry(UserState userState) {
        synchronized (currentStatisticsCache) {
            closeDayIfNecessary();
            DbStatisticsEntry dbStatisticsEntry = currentStatisticsCache.get(userState);
            if (dbStatisticsEntry == null) {
                dbStatisticsEntry = new DbStatisticsEntry();
                currentStatisticsCache.put(userState, dbStatisticsEntry);
            }
            return dbStatisticsEntry;
        }
    }

    private DbStatisticsEntry getDbStatisticsEntry(SimpleBase simpleBase) {
        return getDbStatisticsEntry(baseService.getUserState(simpleBase));
    }

    private void closeDayIfNecessary() {
        synchronized (currentStatisticsCache) {
            if (System.currentTimeMillis() >= nextDay) {
                moveCacheToDb();
                setupNextDay();
            }
        }
    }

    @Override
    public CrudRootServiceHelper<DbStatisticsEntry> getDayStatistics() {
        dayStatistics.putCriterion("date", Restrictions.eq("date", DateUtil.removeOneDay(new Date(nextDay))));
        return dayStatistics;
    }

    @Override
    public CrudRootServiceHelper<DbStatisticsEntry> getWeekStatistics() {
        Date date = DateUtil.removeOneDay(new Date(nextDay));
        date = DateUtil.removeOneWeek(date);
        date = DateUtil.weekStart(date);
        weekStatistics.putCriterion("date", Restrictions.eq("date", date));
        return weekStatistics;
    }

    @Override
    public CrudRootServiceHelper<DbStatisticsEntry> getAllTimeStatistics() {
        return allTimeStatistics;
    }

    @Override
    public ReadonlyListContentProvider<CurrentStatisticEntry> getCurrentStatistics() {
        List<CurrentStatisticEntry> entries = new ArrayList<CurrentStatisticEntry>();
        for (UserState userState : userService.getAllUserStates()) {
            String baseName = null;
            Integer money = null;
            Integer itemCount = null;
            Long upTime = null;
            if (userState.getBase() != null) {
                baseName = baseService.getBaseName(userState.getBase().getSimpleBase());
                //upTime = WebCommon.formatDuration(userState.getBase().getUptime());
                upTime = userState.getBase().getUptime();
                itemCount = userState.getBase().getItemCount();
                money = (int) Math.round(userState.getBase().getAccountBalance());
            }
            entries.add(new CurrentStatisticEntry(userState.getCurrentAbstractLevel(),
                    userState.getUser(),
                    baseName,
                    upTime,
                    itemCount,
                    money));
        }
        return new ReadonlyListContentProvider<CurrentStatisticEntry>(entries);
    }


}
