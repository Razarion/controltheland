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

import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
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
import com.btxtech.game.services.utg.DbEventTrackingItem;
import com.btxtech.game.services.utg.DbEventTrackingStart;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelPromotion;
import com.btxtech.game.services.utg.DbMissionAction;
import com.btxtech.game.services.utg.DbTotalStartupTime;
import com.btxtech.game.services.utg.DbTutorialProgress;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.DbUserMessage;
import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
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
import java.util.Collections;
import java.util.Comparator;
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
    public void startUpTaskFinished(StartupTask state, Date clientTimeStamp, long duration) {
        GameStartup gameStartup;
        if (connectionService.hasConnection()) {
            gameStartup = new GameStartup(clientTimeStamp, GameStartup.FINISHED, state, duration, null, baseService.getBase().getName(), session.getUser(), session.getSessionId());
        } else {
            gameStartup = new GameStartup(clientTimeStamp, GameStartup.FINISHED, state, duration, null, "<Tutorial>", session.getUser(), session.getSessionId());
        }
        hibernateTemplate.saveOrUpdate(gameStartup);
    }

    @Override
    public void startUpTaskFailed(StartupTask state, Date clientTimeStamp, long duration, String failureText) {
        GameStartup gameStartup;
        if (connectionService.hasConnection()) {
            gameStartup = new GameStartup(clientTimeStamp, GameStartup.FAILED, state, duration, failureText, baseService.getBase().getName(), session.getUser(), session.getSessionId());
        } else {
            gameStartup = new GameStartup(clientTimeStamp, GameStartup.FAILED, state, duration, failureText, "<Tutorial>", session.getUser(), session.getSessionId());
        }
        hibernateTemplate.saveOrUpdate(gameStartup);
    }

    @Override
    public void saveUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions) {
        try {
            ArrayList<DbUserAction> dbUserActions = new ArrayList<DbUserAction>();
            for (UserAction userAction : userActions) {
                dbUserActions.add(new DbUserAction(userAction, session.getSessionId()));
            }
            hibernateTemplate.saveOrUpdateAll(dbUserActions);
            ArrayList<DbMissionAction> dbMissionActions = new ArrayList<DbMissionAction>();
            for (MissionAction missionAction : missionActions) {
                dbMissionActions.add(new DbMissionAction(missionAction, session.getSessionId()));
            }
            hibernateTemplate.saveOrUpdateAll(dbMissionActions);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }

    @Override
    public List<VisitorInfo> getVisitorInfos(UserTrackingFilter filter) {
        ArrayList<VisitorInfo> visitorInfos = new ArrayList<VisitorInfo>();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -filter.getDays());

        String sql;
        if (filter.getJsEnabled().equals(UserTrackingFilter.ENABLED)) {
            sql = ("select u.timeStamp, u.sessionId, u.cookieId, u.referer ,count(p) from com.btxtech.game.services.utg.BrowserDetails u, com.btxtech.game.services.utg.PageAccess p where u.sessionId = p.sessionId and u.javaScriptDetected = true and u.timeStamp > ? group by u.sessionId order by u.timeStamp desc");
        } else if (filter.getJsEnabled().equals(UserTrackingFilter.DISABLED)) {
            sql = ("select u.timeStamp, u.sessionId, u.cookieId, u.referer ,count(p) from com.btxtech.game.services.utg.BrowserDetails u, com.btxtech.game.services.utg.PageAccess p where u.sessionId = p.sessionId and u.javaScriptDetected = false and u.timeStamp > ? group by u.sessionId order by u.timeStamp desc");
        } else if (filter.getJsEnabled().equals(UserTrackingFilter.BOTH)) {
            sql = ("select u.timeStamp, u.sessionId, u.cookieId, u.referer ,count(p) from com.btxtech.game.services.utg.BrowserDetails u, com.btxtech.game.services.utg.PageAccess p where u.sessionId = p.sessionId and u.timeStamp > ? group by u.sessionId order by u.timeStamp desc");
        } else {
            throw new IllegalArgumentException("Unknown JS enabled state: " + filter.getJsEnabled());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> datesAndHits = (List<Object[]>) hibernateTemplate.find(sql, gregorianCalendar.getTime());
        for (Object[] datesAndHit : datesAndHits) {
            Date timeStamp = (Date) datesAndHit[0];
            String sessionId = (String) datesAndHit[1];
            boolean cookie = datesAndHit[2] != null;
            String referer = (String) datesAndHit[3];
            int hits = ((Long) datesAndHit[4]).intValue();
            int enterGameHits = getGameAttempts(sessionId);
            int commands = getUserCommandCount(sessionId, null, null, null);
            int tasks = getTaskCount(sessionId);
            visitorInfos.add(new VisitorInfo(timeStamp, sessionId, hits, enterGameHits, commands, tasks, cookie, referer));
        }
        return visitorInfos;
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

    @SuppressWarnings("unchecked")
    private List<DbMissionAction> getMissionActions(final String sessionId, final Date from, final Date to) {
        return (List<DbMissionAction>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbMissionAction.class);
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
        visitorDetailInfo.setGameTrackingInfos(getGameTrackingInfos(sessionId));
        visitorDetailInfo.setPageAccessHistory(getPageAccessHistory(sessionId));
        visitorDetailInfo.setAttackCommands(getUserCommandCount(sessionId, AttackCommand.class, null, null));
        visitorDetailInfo.setMoveCommands(getUserCommandCount(sessionId, MoveCommand.class, null, null));
        visitorDetailInfo.setBuilderCommands(getUserCommandCount(sessionId, BuilderCommand.class, null, null));
        visitorDetailInfo.setFactoryCommands(getUserCommandCount(sessionId, FactoryCommand.class, null, null));
        visitorDetailInfo.setMoneyCollectCommands(getUserCommandCount(sessionId, MoneyCollectCommand.class, null, null));
        visitorDetailInfo.setCompletedMissionCount(getTaskCount(sessionId));
        visitorDetailInfo.setGameAttempts(getGameAttempts(sessionId));
        visitorDetailInfo.setDbEventTrackingStart(getDbEventTrackingStart(sessionId));
        return visitorDetailInfo;
    }

    private List<GameTrackingInfo> getGameTrackingInfos(final String sessionId) {
        // Get all game startups
        @SuppressWarnings("unchecked")
        List<GameStartup> gameStartups = (List<GameStartup>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(GameStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("clientTimeStamp"));
                return criteria.list();
            }
        });
        ArrayList<GameTrackingInfo> gameTrackingInfos = new ArrayList<GameTrackingInfo>();
        GameTrackingInfo gameTrackingInfo = null;
        for (GameStartup gameStartup : gameStartups) {
            if (StartupTask.isFirstTask(gameStartup.getState())) {
                gameTrackingInfo = new GameTrackingInfo(gameStartup);
                gameTrackingInfos.add(gameTrackingInfo);
            } else {
                if (gameTrackingInfo == null) {
                    log.error("gameTrackingInfo == null");
                    continue;
                }
                gameTrackingInfo.getGameStartups().add(gameStartup);
            }
        }

        @SuppressWarnings("unchecked")
        List<DbTotalStartupTime> dbTotalStartupTimes = (List<DbTotalStartupTime>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTotalStartupTime.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });

        @SuppressWarnings("unchecked")
        List<DbCloseWindow> dbCloseWindows = (List<DbCloseWindow>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbCloseWindow.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });

        for (int index = 0, gameTrackingInfosSize = gameTrackingInfos.size(); index < gameTrackingInfosSize; index++) {
            GameTrackingInfo trackingInfo = gameTrackingInfos.get(index);
            if (dbTotalStartupTimes.size() > index) {
                trackingInfo.setTotalStartup(dbTotalStartupTimes.get(index).getStartupDuration());
            }

            Date toDate = null;
            if (dbCloseWindows.size() > index) {
                DbCloseWindow dbCloseWindow = dbCloseWindows.get(index);
                trackingInfo.setDuration(dbCloseWindow.getRunningGameDuration());
                toDate = new Date(dbCloseWindow.getRunningGameDuration() + trackingInfo.getStart().getTime());
            }

            trackingInfo.setAttackCommandCount(getUserCommandCount(sessionId, AttackCommand.class, trackingInfo.getStart(), toDate));
            trackingInfo.setMoveCommandCount(getUserCommandCount(sessionId, MoveCommand.class, trackingInfo.getStart(), toDate));
            trackingInfo.setBuilderCommandCount(getUserCommandCount(sessionId, BuilderCommand.class, trackingInfo.getStart(), toDate));
            trackingInfo.setFactoryCommandCount(getUserCommandCount(sessionId, FactoryCommand.class, trackingInfo.getStart(), toDate));
            trackingInfo.setMoneyCollectCommandCount(getUserCommandCount(sessionId, MoneyCollectCommand.class, trackingInfo.getStart(), toDate));
            trackingInfo.setUserActions(getUserActions(sessionId, trackingInfo.getStart(), toDate));
            trackingInfo.setUserCommands(getUserCommands(sessionId, trackingInfo.getStart(), toDate));
            trackingInfo.setMissionActions(getMissionActions(sessionId, trackingInfo.getStart(), toDate));
            // Sort GameStartups
            Collections.sort(trackingInfo.getGameStartups(), new Comparator<GameStartup>() {
                @Override
                public int compare(GameStartup g1, GameStartup g2) {
                    return g1.getState().compareTo(g2.getState());
                }
            });

        }

        return gameTrackingInfos;
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
            UserCommand userUserCommand = new UserCommand(session.getConnection(), baseCommand);
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
                //
                userHistory.setBaseName(base.getName());
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
            userHistory.setBaseName(base.getName());
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
            userHistory.setBaseName(base.getName());
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
            userHistory.setBaseName(base.getName());
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
            DbLevelPromotion dbLevelPromotion = new DbLevelPromotion(sessionId, base, oldLevel);
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
            DbLevelPromotion dbLevelPromotion = new DbLevelPromotion(sessionId, base, targetLevel, interimPromotion);
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
    public void onTutorialProgressChanged(TutorialConfig.TYPE type, String name, String parent, long duration) {
        try {
            if (type == TutorialConfig.TYPE.TUTORIAL) {
                userGuidanceService.onTutorialFinished();
            }
            hibernateTemplate.saveOrUpdate(new DbTutorialProgress(session.getSessionId(), type.name(), name, parent, duration));
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    private int getTaskCount(final String sessionId) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTutorialProgress.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("type", TutorialConfig.TYPE.TASK.name()));
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
    public void sendTotalStartupTime(long totalStartupTime) {
        hibernateTemplate.save(new DbTotalStartupTime(totalStartupTime, session.getSessionId()));
    }

    @Override
    public void sendCloseWindow(long totalRunningTime) {
        hibernateTemplate.save(new DbCloseWindow(totalRunningTime, session.getSessionId()));
    }

    @Override
    public void onEventTrackerItems(List<EventTrackingItem> eventTrackingItems) {
        ArrayList<DbEventTrackingItem> dbEventTrackingItems = new ArrayList<DbEventTrackingItem>();
        for (EventTrackingItem eventTrackingItem : eventTrackingItems) {
            dbEventTrackingItems.add(new DbEventTrackingItem(eventTrackingItem, session.getSessionId()));
        }
        hibernateTemplate.saveOrUpdateAll(dbEventTrackingItems);
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

}
