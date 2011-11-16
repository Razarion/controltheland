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

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbUserMessage;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.RealGameTrackingInfo;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.SessionDetailDto;
import com.btxtech.game.services.utg.SessionOverviewDto;
import com.btxtech.game.services.utg.TutorialTrackingInfo;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.tracker.DbBrowserWindowTracking;
import com.btxtech.game.services.utg.tracker.DbDialogTracking;
import com.btxtech.game.services.utg.tracker.DbSyncItemInfo;
import com.btxtech.game.services.utg.tracker.DbEventTrackingItem;
import com.btxtech.game.services.utg.tracker.DbEventTrackingStart;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.services.utg.tracker.DbScrollTrackingItem;
import com.btxtech.game.services.utg.tracker.DbSelectionTrackingItem;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.services.utg.tracker.DbStartup;
import com.btxtech.game.services.utg.tracker.DbStartupTask;
import com.btxtech.game.services.utg.tracker.DbTutorialProgress;
import com.btxtech.game.services.utg.tracker.DbUserCommand;
import com.btxtech.game.services.utg.tracker.DbUserHistory;
import com.btxtech.game.wicket.pages.Game;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:41:05
 */
@Component("userTrackingService")
public class UserTrackingServiceImpl implements UserTrackingService {
    @Autowired
    private Session session;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private UserService userService;
    @Autowired
    private HistoryService historyService;
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(UserTrackingServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @Transactional
    public void pageAccess(Class theClass) {
        try {
            DbPageAccess dbPageAccess = new DbPageAccess(session.getSessionId(), theClass.getName(), null);
            hibernateTemplate.save(dbPageAccess);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }

    @Override
    @Transactional
    public void pageAccess(String pageName, String additional) {
        try {
            DbPageAccess dbPageAccess = new DbPageAccess(session.getSessionId(), pageName, additional);
            hibernateTemplate.saveOrUpdate(dbPageAccess);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }

    @Override
    public boolean hasCookieToAdd() {
        return session.getCookieIdToBeSet() != null;
    }

    @Override
    public String getAndClearCookieToAdd() {
        String cookieId = session.getCookieIdToBeSet();
        if (cookieId == null) {
            throw new IllegalStateException("cookieId == null");
        }
        session.clearCookieIdToBeSet();
        return cookieId;
    }

    @Override
    @Transactional
    public void saveBrowserDetails(DbSessionDetail dbSessionDetail) {
        try {
            hibernateTemplate.saveOrUpdate(dbSessionDetail);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public List<SessionOverviewDto> getSessionOverviewDtos(UserTrackingFilter filter) {
        ArrayList<SessionOverviewDto> sessionOverviewDtos = new ArrayList<SessionOverviewDto>();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -filter.getDays());


        Criteria criteria = hibernateTemplate.getSessionFactory().getCurrentSession().createCriteria(DbSessionDetail.class);
        if (!filter.getJsEnabled().equals(UserTrackingFilter.BOTH)) {
            criteria.add(Restrictions.eq("javaScriptDetected", filter.getJsEnabled().equals(UserTrackingFilter.ENABLED)));
        }
        criteria.add(Restrictions.gt("timeStamp", gregorianCalendar.getTime()));
        if (filter.getSessionId() != null && !filter.getSessionId().trim().isEmpty()) {
            criteria.add(Restrictions.eq("sessionId", filter.getSessionId()));
        }
        if (filter.getCookieId() != null && !filter.getCookieId().trim().isEmpty()) {
            criteria.add(Restrictions.eq("cookieId", filter.getCookieId()));
        }

        criteria.addOrder(Order.desc("timeStamp"));
        List<DbSessionDetail> browserDetails = criteria.list();

        for (DbSessionDetail browserDetail : browserDetails) {
            int successfulStarts = getSuccessfulStarts(browserDetail.getSessionId());
            boolean failure = hasFailureStarts(browserDetail.getSessionId());
            int enterGameHits = getGameAttempts(browserDetail.getSessionId());
            int commands = getUserCommandCount(browserDetail.getSessionId(), null, null, null);
            int levelPromotions = historyService.getLevelPromotionCount(browserDetail.getSessionId());
            sessionOverviewDtos.add(new SessionOverviewDto(browserDetail.getTimeStamp(),
                    browserDetail.getSessionId(),
                    getPageHits(browserDetail.getSessionId()),
                    enterGameHits,
                    successfulStarts,
                    failure,
                    commands,
                    levelPromotions,
                    browserDetail.getReferer()));
        }
        return sessionOverviewDtos;
    }

    private int getSuccessfulStarts(final String sessionId) {
        return hibernateTemplate.execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setProjection(Projections.rowCount());
                return ((Number) criteria.list().get(0)).intValue();
            }
        });
    }

    private boolean hasFailureStarts(final String sessionId) {
        return hibernateTemplate.execute(new HibernateCallback<Boolean>() {
            @Override
            public Boolean doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                Criteria dbStartupTaskCriteria = criteria.createCriteria("dbStartupTasks", "dbStartupTasksAlias");
                dbStartupTaskCriteria.add(Restrictions.isNotNull("failureText"));
                criteria.setProjection(Projections.rowCount());
                return ((Number) criteria.list().get(0)).intValue() > 0;
            }
        });
    }

    private int getPageHits(final String sessionId) {
        return hibernateTemplate.execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbPageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setProjection(Projections.rowCount());
                return ((Number) criteria.list().get(0)).intValue();
            }
        });
    }

    private int getGameAttempts(final String sessionId) {
        return hibernateTemplate.execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbPageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("page", Game.class.getName()));
                criteria.setProjection(Projections.rowCount());
                return ((Number) criteria.list().get(0)).intValue();
            }
        });
    }

    private int getUserCommandCount(final String sessionId, final Class<? extends BaseCommand> command, final Date from, final Date to) {
        return hibernateTemplate.execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserCommand.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                if (command != null) {
                    criteria.add(Restrictions.eq("interactionClass", command.getName()));
                }
                if (from != null) {
                    criteria.add(Restrictions.ge("clientTimeStamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", to));
                }
                criteria.setProjection(Projections.rowCount());
                return ((Number) criteria.list().get(0)).intValue();
            }
        });
    }

    private List<DbUserCommand> getUserCommand(final String sessionId, final Long fromServer, final Long toServer) {
        return hibernateTemplate.execute(new HibernateCallback<List<DbUserCommand>>() {
            @Override
            public List<DbUserCommand> doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserCommand.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                if (fromServer != null) {
                    criteria.add(Restrictions.ge("timeStampMs", fromServer));
                }
                if (toServer != null) {
                    criteria.add(Restrictions.lt("timeStampMs", toServer));
                }
                return (List<DbUserCommand>) criteria.list();
            }
        });
    }

    @Override
    public SessionDetailDto getSessionDetailDto(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<DbSessionDetail> list = (List<DbSessionDetail>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbSessionDetail.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                return criteria.list();
            }
        });
        if (list.size() != 1) {
            throw new IllegalStateException("Only 1 DbSessionDetail expected: " + list.size());
        }
        SessionDetailDto sessionDetailDto = new SessionDetailDto(list.get(0));
        sessionDetailDto.setLifecycleTrackingInfos(getLifecycleTrackingInfos(sessionId));
        sessionDetailDto.setPageAccessHistory(getPageAccessHistory(sessionId));
        sessionDetailDto.setAttackCommands(getUserCommandCount(sessionId, AttackCommand.class, null, null));
        sessionDetailDto.setMoveCommands(getUserCommandCount(sessionId, MoveCommand.class, null, null));
        sessionDetailDto.setBuilderCommands(getUserCommandCount(sessionId, BuilderCommand.class, null, null));
        sessionDetailDto.setFactoryCommands(getUserCommandCount(sessionId, FactoryCommand.class, null, null));
        sessionDetailDto.setMoneyCollectCommands(getUserCommandCount(sessionId, MoneyCollectCommand.class, null, null));
        sessionDetailDto.setGameAttempts(getGameAttempts(sessionId));
        return sessionDetailDto;
    }

    private List<LifecycleTrackingInfo> getLifecycleTrackingInfos(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<DbStartup> startups = (List<DbStartup>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.asc("serverTimeStamp"));
                return criteria.list();
            }
        });
        ArrayList<LifecycleTrackingInfo> lifecycleTrackingInfos = new ArrayList<LifecycleTrackingInfo>();
        for (int i = 0, startupsSize = startups.size(); i < startupsSize; i++) {
            DbStartup startup = startups.get(i);
            LifecycleTrackingInfo lifecycleTrackingInfo = new LifecycleTrackingInfo(sessionId, startup);
            lifecycleTrackingInfos.add(lifecycleTrackingInfo);
            if (i + 1 < startups.size()) {
                DbStartup nextStartup = startups.get(i + 1);
                lifecycleTrackingInfo.setNext(nextStartup);
            }
        }
        return lifecycleTrackingInfos;
    }

    @Override
    public LifecycleTrackingInfo getLifecycleTrackingInfo(final String sessionId, final long startServer) {
        @SuppressWarnings("unchecked")
        List<DbStartup> startups = (List<DbStartup>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("serverTimeStamp", startServer));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.asc("serverTimeStamp"));
                criteria.setFetchSize(2);
                return criteria.list();
            }
        });

        LifecycleTrackingInfo lifecycleTrackingInfo = new LifecycleTrackingInfo(sessionId, startups.get(0));
        if (startups.size() > 1) {
            lifecycleTrackingInfo.setNext(startups.get(1));
        }
        return lifecycleTrackingInfo;
    }


    @Override
    public RealGameTrackingInfo getGameTracking(LifecycleTrackingInfo lifecycleTrackingInfo) {
        RealGameTrackingInfo trackingInfoReal = new RealGameTrackingInfo();
        trackingInfoReal.setUserCommands(getUserCommand(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStartServer(), lifecycleTrackingInfo.getNextStartServer()));
        trackingInfoReal.setHistoryElements(historyService.getHistoryElements(lifecycleTrackingInfo.getStartServer(), lifecycleTrackingInfo.getNextStartServer(), lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getBaseId()));
        return trackingInfoReal;
    }

    @Override
    public TutorialTrackingInfo getTutorialTrackingInfo(LifecycleTrackingInfo lifecycleTrackingInfo) {
        TutorialTrackingInfo tutorialTrackingInfo = new TutorialTrackingInfo();
        tutorialTrackingInfo.setDbEventTrackingStart(getDbEventTrackingStart(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStartClient(), lifecycleTrackingInfo.getNextStartClient()));
        tutorialTrackingInfo.setDbTutorialProgresss(getDbTutorialProgresses(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStartClient(), lifecycleTrackingInfo.getNextStartClient()));
        return tutorialTrackingInfo;
    }

    @SuppressWarnings("unchecked")
    private List<DbTutorialProgress> getDbTutorialProgresses(final String sessionId, final long beginClient, final Long endClient) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTutorialProgress.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", beginClient));
                if (endClient != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endClient));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<DbPageAccess> getPageAccessHistory(final String sessionId) {
        return (List<DbPageAccess>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbPageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void saveUserCommand(BaseCommand baseCommand) {
        try {
            DbUserCommand dbUserCommand = new DbUserCommand(session.getConnection(), baseCommand, baseService.getBaseName(baseService.getBase().getSimpleBase()));
            // log.debug("User Command: " + dbUserCommand);
            hibernateTemplate.saveOrUpdate(dbUserCommand);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    public void onUserCreated(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setSessionId(session.getSessionId());
            dbUserHistory.setCookieId(session.getCookieId());
            dbUserHistory.setCreated();
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    public void onUserLoggedIn(User user, Base base) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setSessionId(session.getSessionId());
            dbUserHistory.setCookieId(session.getCookieId());
            dbUserHistory.setLoggedIn();
            if (base != null) {
                dbUserHistory.setBaseName(baseService.getBaseName(base.getSimpleBase()));
            }
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    public void onUserLoggedOut(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setLoggedOut();
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void onBaseCreated(User user, String baseName) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setBaseCreated();
            dbUserHistory.setBaseName(baseName);
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void onBaseDefeated(User user, Base base) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setBaseDefeated();
            dbUserHistory.setBaseName(baseService.getBaseName(base.getSimpleBase()));
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void onBaseSurrender(User user, Base base) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setBaseSurrender();
            dbUserHistory.setBaseName(baseService.getBaseName(baseService.getBase().getSimpleBase()));
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }


    @Override
    @Transactional
    // ???
    public void onUserEnterGame(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setGameEntered();
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    // ???
    public void onUserLeftGame(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setGameLeft();
            hibernateTemplate.saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    @Transactional
    public void trackUserMessage(UserMessage userMessage) {
        try {
            hibernateTemplate.saveOrUpdate(new DbUserMessage(userMessage));
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onJavaScriptDetected(Boolean html5Support) {
        session.onJavaScriptDetected(html5Support);
    }

    @Override
    public boolean isJavaScriptDetected() {
        return session.isJavaScriptDetected();
    }

    @Override
    public boolean isHtml5Support() {
        return session.isHtml5Support();
    }

    @Override
    @Transactional
    public Level onTutorialProgressChanged(TutorialConfig.TYPE type, String name, String parent, long duration, long clientTimeStamp) {
        if (type == TutorialConfig.TYPE.TUTORIAL) {
            serverConditionService.onTutorialFinished(userService.getUserState());
        }
        hibernateTemplate.saveOrUpdate(new DbTutorialProgress(session.getSessionId(), type.name(), name, parent, duration, clientTimeStamp));
        return userGuidanceService.getDbAbstractLevel().getLevel();
    }

    @Override
    @Transactional
    public void onEventTrackingStart(EventTrackingStart eventTrackingStart) {
        hibernateTemplate.save(new DbEventTrackingStart(eventTrackingStart, session.getSessionId()));
    }

    @Override
    @Transactional
    public void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<SyncItemInfo> syncItemInfos, Collection<SelectionTrackingItem> selectionTrackingItems, Collection<TerrainScrollTracking> terrainScrollTrackings, Collection<BrowserWindowTracking> browserWindowTrackings, Collection<DialogTracking> dialogTrackings) {
        onEventTrackerItems(eventTrackingItems);
        saveSyncItemInfos(syncItemInfos);
        saveSelections(selectionTrackingItems);
        saveScrollTrackingItems(terrainScrollTrackings);
        saveBrowserWindowTrackings(browserWindowTrackings);
        saveDialogTrackings(dialogTrackings);
    }

    @Transactional
    private void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems) {
        ArrayList<DbEventTrackingItem> dbEventTrackingItems = new ArrayList<DbEventTrackingItem>();
        for (EventTrackingItem eventTrackingItem : eventTrackingItems) {
            dbEventTrackingItems.add(new DbEventTrackingItem(eventTrackingItem, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbEventTrackingItems);
    }

    private void saveSyncItemInfos(Collection<SyncItemInfo> syncItemInfos) {
        ArrayList<DbSyncItemInfo> dbSyncItemInfos = new ArrayList<DbSyncItemInfo>();
        for (SyncItemInfo syncItemInfo : syncItemInfos) {
            dbSyncItemInfos.add(new DbSyncItemInfo(syncItemInfo, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbSyncItemInfos);
    }

    private void saveSelections(Collection<SelectionTrackingItem> selectionTrackingItems) {
        ArrayList<DbSelectionTrackingItem> dbSelectionTrackingItems = new ArrayList<DbSelectionTrackingItem>();
        for (SelectionTrackingItem command : selectionTrackingItems) {
            dbSelectionTrackingItems.add(new DbSelectionTrackingItem(command, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbSelectionTrackingItems);
    }

    private void saveScrollTrackingItems(Collection<TerrainScrollTracking> terrainScrollTrackings) {
        ArrayList<DbScrollTrackingItem> dbScrollTrackingItems = new ArrayList<DbScrollTrackingItem>();
        for (TerrainScrollTracking terrainScroll : terrainScrollTrackings) {
            dbScrollTrackingItems.add(new DbScrollTrackingItem(terrainScroll, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbScrollTrackingItems);
    }

    private void saveBrowserWindowTrackings(Collection<BrowserWindowTracking> browserWindowTrackings) {
        ArrayList<DbBrowserWindowTracking> dbBrowserWindowTrackings = new ArrayList<DbBrowserWindowTracking>();
        for (BrowserWindowTracking browserWindowTracking : browserWindowTrackings) {
            dbBrowserWindowTrackings.add(new DbBrowserWindowTracking(browserWindowTracking, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbBrowserWindowTrackings);
    }

    private void saveDialogTrackings(Collection<DialogTracking> dialogTrackings) {
        ArrayList<DbDialogTracking> dbDialogTrackings = new ArrayList<DbDialogTracking>();
        for (DialogTracking dialogTracking : dialogTrackings) {
            dbDialogTrackings.add(new DbDialogTracking(dialogTracking, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbDialogTrackings);
    }

    @Override
    public DbEventTrackingStart getDbEventTrackingStart(final String sessionId, final long beginClient, final Long endClient) {
        @SuppressWarnings("unchecked")
        List<DbEventTrackingStart> dbEventTrackingStarts = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbEventTrackingStart.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", beginClient));
                if (endClient != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endClient));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
        if (dbEventTrackingStarts.isEmpty()) {
            return null;
        } else {
            return dbEventTrackingStarts.get(0);
        }
    }

    @Override
    @Transactional
    public void startUpTaskFinished(List<StartupTaskInfo> infos, long totalTime) {
        Integer baseId;
        String baseName;

        try {
            Base base = baseService.getBase();
            baseId = base.getBaseId();
            baseName = baseService.getBaseName(base.getSimpleBase());
        } catch (NoConnectionException e) {
            baseId = null;
            baseName = null;
        }

        DbStartup dbStartup = new DbStartup(totalTime, infos.get(0).getStartTime(), userGuidanceService.getDbAbstractLevel(), session.getSessionId(), baseName, baseId);
        for (StartupTaskInfo info : infos) {
            dbStartup.addGameStartupTasks(new DbStartupTask(info, dbStartup));
            if (info.getError() != null) {
                log.debug("Startup failed: " + info.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText() + " Error: " + info.getError());
            }
        }
        hibernateTemplate.save(dbStartup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbEventTrackingItem> getDbEventTrackingItem(final String sessionId, final long startClient, final Long endClient) {
        return hibernateTemplate.execute(new HibernateCallback<List<DbEventTrackingItem>>() {
            @Override
            public List<DbEventTrackingItem> doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbEventTrackingItem.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startClient));
                if (endClient != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endClient));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbSelectionTrackingItem> getDbSelectionTrackingItems(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbSelectionTrackingItem.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startTime));
                if (endTime != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endTime));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbSyncItemInfo> getDbSyncItemInfos(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbSyncItemInfo.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startTime));
                if (endTime != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endTime));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbScrollTrackingItem> getDbScrollTrackingItems(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbScrollTrackingItem.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startTime));
                if (endTime != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endTime));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbBrowserWindowTracking> getDbBrowserWindowTrackings(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.execute(new HibernateCallback<List<DbBrowserWindowTracking>>() {
            @Override
            public List<DbBrowserWindowTracking> doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbBrowserWindowTracking.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startTime));
                if (endTime != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endTime));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbDialogTracking> getDbDialogTrackings(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.execute(new HibernateCallback<List<DbDialogTracking>>() {
            @Override
            public List<DbDialogTracking> doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbDialogTracking.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", startTime));
                if (endTime != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", endTime));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }
}
