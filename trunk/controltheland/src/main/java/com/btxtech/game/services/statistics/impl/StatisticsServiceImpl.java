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
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;
import com.btxtech.game.services.statistics.DbStatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private SessionFactory sessionFactory;
    private final Map<UserState, DbStatisticsEntry> currentStatisticsCache = new HashMap<UserState, DbStatisticsEntry>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private long nextDay;
    private Log log = LogFactory.getLog(StatisticsServiceImpl.class);

    // Bugs
    // 18.09.2011	Statistics wird auf DB nicht augeräumt. Alte Einträge bleiben erhalten.
    // 04.10.2011  More than one entry in all time statistics for user: User: 'beat'
    // 22.09.2011	Kill Total, Kill Bot Total, Kill Player Total
    // 22.09.2011	Lost (Bot, Player, Units, Structure, All)
    // 01.11.2011  End of Day runs more than 100 times (30.10.2011)
    // 20.11.2011  More than one entry in all time statistics for user
    // 20.11.2011  Loop in end of day processing
    // 17.02.2012  00:12:45.656  INFO Start end of day processing
    // 17.02.2012  00:12:45.656 ERROR org.hibernate.service.UnknownServiceException: Unknown service requested [org.hibernate.service.jdbc.connections.spi.ConnectionProvider]
    // ------------------------------------------------------------------------------------------------
    // 17.02.2012 00:12:42.659 ERROR
    // java.lang.NullPointerException
    // 17.02.2012 00:12:42.659  INFO Finished end of day processing
    // 17.02.2012 00:12:42.659  INFO Start end of day processing
    // ------------------------------------------------------------------------------------------------
    // java.lang.NullPointerException
    // 20.11.2011 11:37:36.606  INFO Finished end of day processing
    // 20.11.2011 11:37:36.606  INFO Start end of day processing
    // 20.11.2011 11:37:36.609 ERROR
    // ------------------------------------------------------------------------------------------------
    // 27.01.2011	After Prod shutdown:
    // "StatisticsServiceImpl Thread 1" prio=10 tid=0x00007f3b745d8800 nid=0xf8e waiting on condition [0x00007f3b82180000]
    //    java.lang.Thread.State: WAITING (parking)
    //         at sun.misc.Unsafe.park(Native Method)
    //         - parking to wait for  <0x0000000758f8a198> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
    //         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:186)
    //         at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2043)
    //         at java.util.concurrent.DelayQueue.take(DelayQueue.java:189)
    //         at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:688)
    //         at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:681)
    //         at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1043)
    //         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1103)
    //         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
    //         at java.lang.Thread.run(Thread.java:636)
    // ------------------------------------------------------------------------------------------------
    // 17.02.2012 00:11:57.311  INFO Start end of day processing
    // 17.02.2012 00:11:57.323  WARN SQL Error: 0, SQLState: 08S01
    // 17.02.2012 00:11:57.323 ERROR Communications link failure
//    The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
//    17.02.2012 00:11:57.323 ERROR
//    org.springframework.transaction.CannotCreateTransactionException: Could not open Hibernate Session for transaction; nested exception is org.
//    hibernate.exception.JDBCConnectionException: Could not open connection
//            at org.springframework.orm.hibernate4.HibernateTransactionManager.doBegin(HibernateTransactionManager.java:440)
//            at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java
//    :371)
//            at org.springframework.transaction.support.TransactionTemplate.execute(TransactionTemplate.java:127)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl.endOfDayProcessing(StatisticsServiceImpl.java:153)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl$1.run(StatisticsServiceImpl.java:104)
//            at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:471)
//            at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:334)
//            at java.util.concurrent.FutureTask.run(FutureTask.java:166)
//            at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$101(ScheduledThreadPoolExecutor.java:165)
//            at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:266)
//            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1110)
//            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
//            at java.lang.Thread.run(Thread.java:636)
//    Caused by: org.hibernate.exception.JDBCConnectionException: Could not open connection
//            at org.hibernate.exception.internal.SQLStateConverter.convert(SQLStateConverter.java:107)
//            at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:125)
//            at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:110)
//            at org.hibernate.engine.jdbc.internal.LogicalConnectionImpl.obtainConnection(LogicalConnectionImpl.java:304)
//            at org.hibernate.engine.jdbc.internal.LogicalConnectionImpl.getConnection(LogicalConnectionImpl.java:169)
//            at org.hibernate.engine.transaction.internal.jdbc.JdbcTransaction.doBegin(JdbcTransaction.java:67)
//            at org.hibernate.engine.transaction.spi.AbstractTransactionImpl.begin(AbstractTransactionImpl.java:160)
//            at org.hibernate.internal.SessionImpl.beginTransaction(SessionImpl.java:1263)
//            at org.springframework.orm.hibernate4.HibernateTransactionManager.doBegin(HibernateTransactionManager.java:399)
//            ... 12 more
//    Caused by: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure
//
//    The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
//            at sun.reflect.GeneratedConstructorAccessor1016.newInstance(Unknown Source)
//            at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
//            at java.lang.reflect.Constructor.newInstance(Constructor.java:532)
//            at com.mysql.jdbc.Util.handleNewInstance(Util.java:407)
//            at com.mysql.jdbc.SQLError.createCommunicationsException(SQLError.java:1116)
//            at com.mysql.jdbc.MysqlIO.<init>(MysqlIO.java:343)
//            at com.mysql.jdbc.ConnectionImpl.coreConnect(ConnectionImpl.java:2334)
//            at com.mysql.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:2371)
//            at com.mysql.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:2163)
//            at com.mysql.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:794)
//            at com.mysql.jdbc.JDBC4Connection.<init>(JDBC4Connection.java:47)
//            at sun.reflect.GeneratedConstructorAccessor916.newInstance(Unknown Source)
//            at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
//            at java.lang.reflect.Constructor.newInstance(Constructor.java:532)
//            at com.mysql.jdbc.Util.handleNewInstance(Util.java:407)
//            at com.mysql.jdbc.ConnectionImpl.getInstance(ConnectionImpl.java:378)
//            at com.mysql.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:305)
//            at java.sql.DriverManager.getConnection(DriverManager.java:620)
//            at java.sql.DriverManager.getConnection(DriverManager.java:169)
//            at org.springframework.jdbc.datasource.DriverManagerDataSource.getConnectionFromDriverManager(DriverManagerDataSource.java:173)
//            at org.springframework.jdbc.datasource.DriverManagerDataSource.getConnectionFromDriver(DriverManagerDataSource.java:164)
//            at org.springframework.jdbc.datasource.AbstractDriverBasedDataSource.getConnectionFromDriver(AbstractDriverBasedDataSource.java:149)
//            at org.springframework.jdbc.datasource.AbstractDriverBasedDataSource.getConnection(AbstractDriverBasedDataSource.java:119)
//            at org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection(DatasourceConnectionProviderImpl.j
//    ava:141)
//            at org.hibernate.internal.AbstractSessionImpl$NonContextualJdbcConnectionAccess.obtainConnection(AbstractSessionImpl.java:276)
//            at org.hibernate.engine.jdbc.internal.LogicalConnectionImpl.obtainConnection(LogicalConnectionImpl.java:297)
//            ... 17 more
//    Caused by: java.net.ConnectException: Connection refused
//            at java.net.PlainSocketImpl.socketConnect(Native Method)
//            at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:327)
//            at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:193)
//            at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:180)
//            at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:384)
//            at java.net.Socket.connect(Socket.java:546)
//            at java.net.Socket.connect(Socket.java:495)
//            at java.net.Socket.<init>(Socket.java:392)
//            at java.net.Socket.<init>(Socket.java:235)
//            at com.mysql.jdbc.StandardSocketFactory.connect(StandardSocketFactory.java:254)
//            at com.mysql.jdbc.MysqlIO.<init>(MysqlIO.java:292)
//            ... 37 more
//    17.02.2012 00:11:57.324  INFO Finished end of day processing
//    ------------------------------------------------------------------------------------------------
//    28.02.2012 07:45:18.271 ERROR
//    java.lang.NullPointerException
//        at com.btxtech.game.controllers.StatisticsController.handleRequest(StatisticsController.java:39)
//        at org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter.handle(SimpleControllerHandlerAdapter.java:48)
//        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:900)
//        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:827)
//        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:882)
//        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:778)
//        at javax.servlet.http.HttpServlet.service(HttpServlet.java:617)
//        at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
//        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)
//        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
//        at org.apache.wicket.protocol.http.WicketFilter.doFilter(WicketFilter.java:370)
//        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
//        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
//        at org.springframework.orm.hibernate4.support.OpenSessionInViewFilter.doFilterInternal(OpenSessionInViewFilter.java:119)
//        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:76)
//        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
//        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
//        at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:87)
//        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
//        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
//        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)
//        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
//        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:128)
//        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
//        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
//        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:293)
//        at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:849)
//        at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:583)
//        at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:454)
//        at java.lang.Thread.run(Thread.java:636)
//    ------------------------------------------------------------------------------------------------
//    Loop!!! End of day processing
//    21.03.2012 00:00:09.945  INFO Finished end of day processing
//    21.03.2012 00:00:09.945  INFO Start end of day processing
//    21.03.2012 00:00:09.947 ERROR
//    java.lang.NullPointerException
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl.moveCacheToDb(StatisticsServiceImpl.java:149)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl.closeDayIfNecessary(StatisticsServiceImpl.java:408)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl.access$300(StatisticsServiceImpl.java:62)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl$2.doInTransactionWithoutResult(StatisticsServiceImpl.java:170)
//            at org.springframework.transaction.support.TransactionCallbackWithoutResult.doInTransaction(TransactionCallbackWithoutResult.java:33)
//            at org.springframework.transaction.support.TransactionTemplate.execute(TransactionTemplate.java:130)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl.endOfDayProcessing(StatisticsServiceImpl.java:167)
//            at com.btxtech.game.services.statistics.impl.StatisticsServiceImpl$1.run(StatisticsServiceImpl.java:104)
//            at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
//            at java.util.concurrent.FutureTask$Sync.innerRun(Unknown Source)
//            at java.util.concurrent.FutureTask.run(Unknown Source)
//            at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(Unknown Source)
//            at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
//            at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
//            at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
//            at java.lang.Thread.run(Unknown Source)
//    ------------------------------------------------------------------------------------------------


    @PostConstruct
    public void init() {
        dayStatistics.init(DbStatisticsEntry.class);
        dayStatistics.putCriterion(DbStatisticsEntry.Type.DAY, Restrictions.eq("type", DbStatisticsEntry.Type.DAY));
        weekStatistics.init(DbStatisticsEntry.class);
        weekStatistics.putCriterion(DbStatisticsEntry.Type.WEEK, Restrictions.eq("type", DbStatisticsEntry.Type.WEEK));
        allTimeStatistics.init(DbStatisticsEntry.class);
        allTimeStatistics.putCriterion(DbStatisticsEntry.Type.ALL_TIME, Restrictions.eq("type", DbStatisticsEntry.Type.ALL_TIME));
        setupNextDay();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("StatisticsServiceImpl Thread " + getClass().getName() + " "));
        scheduleNextEndDayProcessing();

    }

    private void scheduleNextEndDayProcessing() {
        /*scheduledThreadPoolExecutor.schedule(new Runnable() {
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
        */
    }

    private void setupNextDay() {
        Date date = DateUtil.dayStart(new Date());
        nextDay = DateUtil.addOneDay(date).getTime();
    }

    @PreDestroy
    @Transactional
    public void cleanup() {
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                moveCacheToDb();
            } catch (Throwable t) {
                log.error("", t);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
            if (scheduledThreadPoolExecutor != null) {
                scheduledThreadPoolExecutor.shutdownNow();
                scheduledThreadPoolExecutor = null;
            }
        } catch (Throwable t) {
            log.error("StatisticsServiceImpl.cleanup() failed", t);
        }
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
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbStatisticsEntries);
    }

    protected void endOfDayProcessing(final Date processingDay) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
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
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    private void makeDay(final Date day) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
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
        HibernateUtil.deleteAll(sessionFactory, toBeDeleted);
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

        // Check first if last week has already been processed
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
        criteria.add(Restrictions.eq("date", lastWeekStart));
        criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.WEEK));
        criteria.setProjection(Projections.count("user"));
        if ((Long) criteria.list().get(0) > 0) {
            return;
        }

        // Process last week
        criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
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
        HibernateUtil.saveOrUpdateAll(sessionFactory, userWeek.values());
    }

    private void sumUpAllTime(final Date date) {
        // Check first if all time is up to date
        Date lastProcessingDate = null;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
        criteria.add(Restrictions.eq("type", DbStatisticsEntry.Type.ALL_TIME));
        criteria.setProjection(Projections.max("date"));
        List list = criteria.list();
        if (!list.isEmpty()) {
            lastProcessingDate = (Date) list.get(0);
        }
        if (lastProcessingDate != null && (lastProcessingDate.after(date) || lastProcessingDate.equals(date))) {
            return;
        }

        criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
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

        criteria = sessionFactory.getCurrentSession().createCriteria(DbStatisticsEntry.class);
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
        HibernateUtil.saveOrUpdateAll(sessionFactory, allTimeMap.values());
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
            entries.add(new CurrentStatisticEntry(userGuidanceService.getDbLevel(userState),
                    userState.getUser(),
                    baseName,
                    upTime,
                    itemCount,
                    money));
        }
        return new ReadonlyListContentProvider<CurrentStatisticEntry>(entries);
    }


}
