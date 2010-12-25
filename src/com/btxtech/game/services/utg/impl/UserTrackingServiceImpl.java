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

import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.ScrollTrackingItem;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.UserStage;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.DbCloseWindow;
import com.btxtech.game.services.utg.DbCommand;
import com.btxtech.game.services.utg.DbEventTrackingItem;
import com.btxtech.game.services.utg.DbEventTrackingStart;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelPromotion;
import com.btxtech.game.services.utg.DbScrollTrackingItem;
import com.btxtech.game.services.utg.DbSelectionTrackingItem;
import com.btxtech.game.services.utg.DbStartup;
import com.btxtech.game.services.utg.DbStartupTask;
import com.btxtech.game.services.utg.DbTutorialProgress;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.DbUserMessage;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.TutorialTrackingInfo;
import com.btxtech.game.services.utg.UserCommand;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserHistory;
import com.btxtech.game.services.utg.UserTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorDetailInfo;
import com.btxtech.game.services.utg.VisitorInfo;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(UserTrackingServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void pageAccess(BasePage basePage) {
        try {
            PageAccess pageAccess = new PageAccess(session.getSessionId(), basePage.getClass().getName(), basePage.getAdditionalPageInfo());
            hibernateTemplate.saveOrUpdate(pageAccess);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }

    @Override
    public void pageAccess(Class theClass) {
        try {
            PageAccess pageAccess = new PageAccess(session.getSessionId(), theClass.getName(), "");
            hibernateTemplate.saveOrUpdate(pageAccess);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }


    @Override
    public void saveBrowserDetails(BrowserDetails browserDetails) {
        try {
            hibernateTemplate.saveOrUpdate(browserDetails);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void startUpTaskFinished(Collection<StartupTaskInfo> infos, long totalTime) {
        DbStartup dbStartup = new DbStartup(totalTime, infos.iterator().next().getStartTime(), userGuidanceService.getDbUserStage(), session.getSessionId());
        for (StartupTaskInfo info : infos) {
            dbStartup.addGameStartupTasks(new DbStartupTask(info, dbStartup));
        }
        hibernateTemplate.save(dbStartup);
    }

    @Override
    public List<VisitorInfo> getVisitorInfos(UserTrackingFilter filter) {
        ArrayList<VisitorInfo> visitorInfos = new ArrayList<VisitorInfo>();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -filter.getDays());

        String sql;
        Object[] values;
        if (filter.getJsEnabled().equals(UserTrackingFilter.BOTH)) {
            values = new Object[]{gregorianCalendar.getTime()};
            sql = ("select u.timeStamp, u.sessionId, u.cookieId, u.referer from com.btxtech.game.services.utg.BrowserDetails u where u.timeStamp > ? group by u.sessionId order by u.timeStamp desc");
        } else {
            values = new Object[]{filter.getJsEnabled().equals(UserTrackingFilter.ENABLED), gregorianCalendar.getTime()};
            sql = ("select u.timeStamp, u.sessionId, u.cookieId, u.referer from com.btxtech.game.services.utg.BrowserDetails u where  u.javaScriptDetected = ? and u.timeStamp > ? group by u.sessionId order by u.timeStamp desc");
        }

        @SuppressWarnings("unchecked")
        List<Object[]> datesAndHits = (List<Object[]>) hibernateTemplate.find(sql, values);
        for (Object[] datesAndHit : datesAndHits) {
            Date timeStamp = (Date) datesAndHit[0];
            String sessionId = (String) datesAndHit[1];
            boolean cookie = datesAndHit[2] != null;
            if (filter.getCookieEnabled().equals(UserTrackingFilter.ENABLED) && !cookie) {
                continue;
            } else if (filter.getCookieEnabled().equals(UserTrackingFilter.DISABLED) && cookie) {
                continue;
            }
            String referer = (String) datesAndHit[3];
            int hits = getPageHits(sessionId);
            if (filter.getHits() != null && hits < filter.getHits()) {
                continue;
            }

            int successfulStarts = getSuccessfulStarts(sessionId);
            int enterGameHits = getGameAttempts(sessionId);
            int commands = getUserCommandCount(sessionId, null, null, null);
            int tasks = getTaskCount(sessionId, null, null);
            visitorInfos.add(new VisitorInfo(timeStamp, sessionId, hits, enterGameHits, successfulStarts, commands, tasks, cookie, referer));
        }
        return visitorInfos;
    }

    private int getSuccessfulStarts(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    private int getPageHits(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    private int getGameAttempts(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("page", Game.class.getName()));
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    private int getUserCommandCount(final String sessionId, final Class<? extends BaseCommand> command, final Date from, final Date to) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(UserCommand.class);
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
                return criteria.list();
            }
        });
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<UserCommand> getUserCommands(final String sessionId, final Date from, final Date to) {
        return (List<UserCommand>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(UserCommand.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", from));
                if (to != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", to));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    public VisitorDetailInfo getVisitorDetails(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<BrowserDetails> list = (List<BrowserDetails>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(BrowserDetails.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                return criteria.list();
            }
        });
        if (list.size() != 1) {
            throw new IllegalStateException("Only 1 BrowserDetails expected: " + list.size());
        }
        VisitorDetailInfo visitorDetailInfo = new VisitorDetailInfo(list.get(0));
        visitorDetailInfo.setLifecycleTrackingInfos(getLifecycleTrackingInfos(sessionId));
        visitorDetailInfo.setPageAccessHistory(getPageAccessHistory(sessionId));
        visitorDetailInfo.setAttackCommands(getUserCommandCount(sessionId, AttackCommand.class, null, null));
        visitorDetailInfo.setMoveCommands(getUserCommandCount(sessionId, MoveCommand.class, null, null));
        visitorDetailInfo.setBuilderCommands(getUserCommandCount(sessionId, BuilderCommand.class, null, null));
        visitorDetailInfo.setFactoryCommands(getUserCommandCount(sessionId, FactoryCommand.class, null, null));
        visitorDetailInfo.setMoneyCollectCommands(getUserCommandCount(sessionId, MoneyCollectCommand.class, null, null));
        visitorDetailInfo.setTasks(getTaskCount(sessionId, null, null));
        visitorDetailInfo.setGameAttempts(getGameAttempts(sessionId));
        return visitorDetailInfo;
    }

    private List<LifecycleTrackingInfo> getLifecycleTrackingInfos(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<DbStartup> startups = (List<DbStartup>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.asc("clientTimeStamp"));
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
                lifecycleTrackingInfo.setEnd(new Date(nextStartup.getClientTimeStamp()));
            }
        }
        return lifecycleTrackingInfos;
    }

    @Override
    public GameTrackingInfo getGameTracking(LifecycleTrackingInfo lifecycleTrackingInfo) {
/*        GameTrackingInfo trackingInfo = new GameTrackingInfo();
        trackingInfo.setAttackCommandCount(getUserCommandCount(lifecycleTrackingInfo.getSessionId(), AttackCommand.class, lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setMoveCommandCount(getUserCommandCount(lifecycleTrackingInfo.getSessionId(), MoveCommand.class, lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setBuilderCommandCount(getUserCommandCount(lifecycleTrackingInfo.getSessionId(), BuilderCommand.class, lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setFactoryCommandCount(getUserCommandCount(lifecycleTrackingInfo.getSessionId(), FactoryCommand.class, lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setMoneyCollectCommandCount(getUserCommandCount(lifecycleTrackingInfo.getSessionId(), MoneyCollectCommand.class, lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setUserActions(getUserActions(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        trackingInfo.setUserCommands(getUserCommands(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        return trackingInfo; */
        return null;
    }

    @Override
    public TutorialTrackingInfo getTutorialTrackingInfo(LifecycleTrackingInfo lifecycleTrackingInfo) {
        TutorialTrackingInfo tutorialTrackingInfo = new TutorialTrackingInfo();
        tutorialTrackingInfo.setTaskCount(getTaskCount(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        tutorialTrackingInfo.setDbEventTrackingStart(getDbEventTrackingStart(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        tutorialTrackingInfo.setDbTutorialProgresss(getDbTutorialProgresses(lifecycleTrackingInfo.getSessionId(), lifecycleTrackingInfo.getStart(), lifecycleTrackingInfo.getEnd()));
        return tutorialTrackingInfo;
    }

    @SuppressWarnings("unchecked")
    private List<DbTutorialProgress> getDbTutorialProgresses(final String sessionId, final Date begin, final Date end) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTutorialProgress.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", begin.getTime()));
                if (end != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", end.getTime()));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<DbUserAction> getUserActions(final String sessionId, final Date from, final Date to) {
        return (List<DbUserAction>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserAction.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", from));
                if (to != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", to));
                }
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<PageAccess> getPageAccessHistory(final String sessionId) {
        return (List<PageAccess>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });
    }

    @Override
    public void saveUserCommand(BaseCommand baseCommand) {
        try {
            UserCommand userUserCommand = new UserCommand(session.getConnection(), baseCommand, baseService.getBaseName(baseService.getBase().getSimpleBase()));
            // log.debug("User Command: " + userUserCommand);
            hibernateTemplate.saveOrUpdate(userUserCommand);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onUserCreated(User user) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setSessionId(session.getSessionId());
            userHistory.setCookieId(session.getCookieId());
            userHistory.setCreated();
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onUserLoggedIn(User user, Base base) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setSessionId(session.getSessionId());
            userHistory.setCookieId(session.getCookieId());
            userHistory.setLoggedIn();
            if (base != null) {
                userHistory.setBaseName(baseService.getBaseName(base.getSimpleBase()));
            }
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onUserLoggedOut(User user) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setLoggedOut();
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onBaseCreated(User user, Base base) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setBaseCreated();
            userHistory.setBaseName(baseService.getBaseName(baseService.getBase().getSimpleBase()));
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onBaseDefeated(User user, Base base) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setBaseDefeated();
            userHistory.setBaseName(baseService.getBaseName(base.getSimpleBase()));
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onBaseSurrender(User user, Base base) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setBaseSurrender();
            userHistory.setBaseName(baseService.getBaseName(baseService.getBase().getSimpleBase()));
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }


    public void onUserEnterGame(User user) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setGameEntered();
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    public void onUserLeftGame(User user) {
        try {
            UserHistory userHistory = new UserHistory(user);
            userHistory.setGameLeft();
            hibernateTemplate.saveOrUpdate(userHistory);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void trackUserMessage(UserMessage userMessage) {
        try {
            hibernateTemplate.saveOrUpdate(new DbUserMessage(userMessage));
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void levelPromotion(Base base, DbLevel oldLevel) {
        try {
            String sessionId = null;
            try {
                sessionId = session.getSessionId();
            } catch (Throwable t) {
                // Ignore
                // Error creating bean with name 'scopedTarget.user': Scope 'session' is not active for the current thread
                // This happens when the methode is called from the server side (e.g. XP increase timer)
            }
            DbLevelPromotion dbLevelPromotion = new DbLevelPromotion(sessionId, base, baseService.getBaseName(base.getSimpleBase()), oldLevel);
            hibernateTemplate.saveOrUpdate(dbLevelPromotion);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void levelInterimPromotion(Base base, String targetLevel, String interimPromotion) {
        try {
            String sessionId = null;
            try {
                sessionId = session.getSessionId();
            } catch (Throwable t) {
                // Ignore
                // Error creating bean with name 'scopedTarget.user': Scope 'session' is not active for the current thread
                // This happens when the methode is called from the server side (e.g. XP increase timer)
            }
            DbLevelPromotion dbLevelPromotion = new DbLevelPromotion(sessionId, base, baseService.getBaseName(base.getSimpleBase()), targetLevel, interimPromotion);
            hibernateTemplate.saveOrUpdate(dbLevelPromotion);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onJavaScriptDetected() {
        session.onJavaScriptDetected();
    }

    @Override
    public boolean isJavaScriptDetected() {
        return session.isJavaScriptDetected();
    }

    @Override
    public UserStage onTutorialProgressChanged(TutorialConfig.TYPE type, String name, String parent, long duration, long clientTimeStamp) {
        if (type == TutorialConfig.TYPE.TUTORIAL) {
            userGuidanceService.onTutorialFinished();
        }
        hibernateTemplate.saveOrUpdate(new DbTutorialProgress(session.getSessionId(), type.name(), name, parent, duration, clientTimeStamp));
        return userGuidanceService.getUserStage();
    }

    private int getTaskCount(final String sessionId, final Date from, final Date to) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTutorialProgress.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("type", TutorialConfig.TYPE.TASK.name()));
                if (from != null) {
                    criteria.add(Restrictions.ge("clientTimeStamp", from.getTime()));
                }
                if (to != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", to.getTime()));
                }
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    @Override
    public void onEventTrackingStart(EventTrackingStart eventTrackingStart) {
        hibernateTemplate.save(new DbEventTrackingStart(eventTrackingStart, session.getSessionId()));
    }

    @Override
    public void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<BaseCommand> baseCommands, Collection<SelectionTrackingItem> selectionTrackingItems, List<ScrollTrackingItem> scrollTrackingItems) {
        onEventTrackerItems(eventTrackingItems);
        saveCommand(baseCommands);
        saveSelections(selectionTrackingItems);
        saveScrollTrackingItems(scrollTrackingItems);
    }

    private void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems) {
        ArrayList<DbEventTrackingItem> dbEventTrackingItems = new ArrayList<DbEventTrackingItem>();
        for (EventTrackingItem eventTrackingItem : eventTrackingItems) {
            dbEventTrackingItems.add(new DbEventTrackingItem(eventTrackingItem, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbEventTrackingItems);
    }

    private void saveCommand(Collection<BaseCommand> baseCommand) {
        ArrayList<DbCommand> dbCommands = new ArrayList<DbCommand>();
        for (BaseCommand command : baseCommand) {
            dbCommands.add(new DbCommand(command, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbCommands);
    }

    private void saveSelections(Collection<SelectionTrackingItem> selectionTrackingItems) {
        ArrayList<DbSelectionTrackingItem> dbSelectionTrackingItems = new ArrayList<DbSelectionTrackingItem>();
        for (SelectionTrackingItem command : selectionTrackingItems) {
            dbSelectionTrackingItems.add(new DbSelectionTrackingItem(command, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbSelectionTrackingItems);
    }

    private void saveScrollTrackingItems(List<ScrollTrackingItem> scrollTrackingItems) {
        ArrayList<DbScrollTrackingItem> dbScrollTrackingItems = new ArrayList<DbScrollTrackingItem>();
        for (ScrollTrackingItem scroll : scrollTrackingItems) {
            dbScrollTrackingItems.add(new DbScrollTrackingItem(scroll, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbScrollTrackingItems);
    }

    @Override
    public void onCloseWindow(long totalRunningTime, long clientTimeStamp) {
        hibernateTemplate.save(new DbCloseWindow(totalRunningTime, clientTimeStamp, session.getSessionId()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbEventTrackingStart> getDbEventTrackingStart(final String sessionId) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbEventTrackingStart.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
    }


    private DbEventTrackingStart getDbEventTrackingStart(final String sessionId, final Date begin, final Date end) {
        @SuppressWarnings("unchecked")
        List<DbEventTrackingStart> dbEventTrackingStarts = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbEventTrackingStart.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.ge("clientTimeStamp", begin.getTime()));
                if (end != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", end.getTime()));
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
    @SuppressWarnings("unchecked")
    public List<DbEventTrackingItem> getDbEventTrackingItem(final DbEventTrackingStart begin, final DbEventTrackingStart end) {
        if (end != null && !(begin.getSessionId().equals(end.getSessionId()))) {
            throw new IllegalArgumentException("DbEventTrackingStart must have same session id");
        }
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbEventTrackingItem.class);
                criteria.add(Restrictions.eq("sessionId", begin.getSessionId()));
                criteria.add(Restrictions.ge("clientTimeStamp", begin.getClientTimeStamp()));
                if (end != null) {
                    criteria.add(Restrictions.lt("clientTimeStamp", end.getClientTimeStamp()));
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
    public List<DbCommand> getDbCommands(final String sessionId, final long startTime, final Long endTime) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbCommand.class);
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
}
