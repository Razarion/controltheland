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
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.DbLevelPromotion;
import com.btxtech.game.services.utg.DbMissionAction;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.DbUserMessage;
import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.UserCommand;
import com.btxtech.game.services.utg.UserHistory;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorDetailInfo;
import com.btxtech.game.services.utg.VisitorInfo;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
    public void newSession(BrowserDetails browserDetails) {
        try {
            hibernateTemplate.saveOrUpdate(browserDetails);
        } catch (NoConnectionException e) {
            log.error("", e);
        }
    }

    @Override
    public void startUpTaskFinished(StartupTask state, long duration) {
        try {
            GameStartup gameStartup = new GameStartup(GameStartup.FINISHED, state, duration, null, baseService.getBase().getName(), session.getUser(), session.getSessionId());
            hibernateTemplate.saveOrUpdate(gameStartup);
        } catch (NoConnectionException e) {
            log.error("Can not track game startup: " + e.getMessage());
        }
    }

    @Override
    public void startUpTaskFailed(StartupTask state, long duration, String failureText) {
        try {
            GameStartup gameStartup = new GameStartup(GameStartup.FAILED, state, duration, failureText, baseService.getBase().getName(), session.getUser(), session.getSessionId());
            hibernateTemplate.saveOrUpdate(gameStartup);
        } catch (NoConnectionException e) {
            log.error("Can not track game startup: " + e.getMessage());
        }
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
    public List<VisitorInfo> getVisitorInfos() {
        ArrayList<VisitorInfo> visitorInfos = new ArrayList<VisitorInfo>();
        List<Object[]> datesAndHits = (List<Object[]>) hibernateTemplate.find("select u.timeStamp, u.sessionId, u.cookieId, u.referer ,count(p) from com.btxtech.game.services.utg.BrowserDetails u, com.btxtech.game.services.utg.PageAccess p where u.sessionId = p.sessionId and u.isCrawler = false group by u.sessionId order by u.timeStamp desc");
        for (Object[] datesAndHit : datesAndHits) {
            Date timeStamp = (Date) datesAndHit[0];
            String sessionId = (String) datesAndHit[1];
            boolean cookie = datesAndHit[2] != null;
            String referer = (String) datesAndHit[3];
            int hits = ((Long) datesAndHit[4]).intValue();
            int enterGameHits = getHitsForGameStartup(sessionId);
            int commands = getUserCommandCount(sessionId, null, null, null);
            int completedMissions = getCompletedMissionCount(sessionId, null, null);
            visitorInfos.add(new VisitorInfo(timeStamp, sessionId, hits, enterGameHits, commands, completedMissions, cookie, referer));
        }
        return visitorInfos;
    }

    private int getHitsForGameStartup(final String sessionId) {
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(GameStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                // criteria.add(Restrictions.eq("state", GameStartupState.SERVER));  // TODO
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    private int getUserCommandCount(final String sessionId, final Class<? extends BaseCommand> command, final Date from, final Date to) {
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

    private List<UserCommand> getUserCommands(final String sessionId, final Date from, final Date to) {
        List<UserCommand> list = (List<UserCommand>) hibernateTemplate.execute(new HibernateCallback() {
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
        return list;
    }

    private int getCompletedMissionCount(final String sessionId, final Date from, final Date to) {
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbMissionAction.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.like("action", MissionAction.MISSION_COMPLETED));
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

    private List<DbMissionAction> getMissionActions(final String sessionId, final Date from, final Date to) {
        List<DbMissionAction> list = (List<DbMissionAction>) hibernateTemplate.execute(new HibernateCallback() {
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
        return list;
    }

    @Override
    public VisitorDetailInfo getVisitorDetails(final String sessionId) {
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
        visitorDetailInfo.setCompletedMissionCount(getCompletedMissionCount(sessionId, null, null));
        return visitorDetailInfo;
    }

    private List<GameTrackingInfo> getGameTrackingInfos(final String sessionId) {
        ArrayList<GameTrackingInfo> gameTrackingInfos = new ArrayList<GameTrackingInfo>();
        /*
        // Get all game startups
        List<GameStartup> list = (List<GameStartup>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(GameStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });
        // Sort game startups
        GameTrackingInfo gameTrackingInfo = null;
        for (GameStartup gameStartup : list) {
            switch (gameStartup.getState()) {
                case SERVER:
                    gameTrackingInfo = new GameTrackingInfo();
                    gameTrackingInfos.add(gameTrackingInfo);
                    gameTrackingInfo.setServerGameStartup(gameStartup);
                    gameTrackingInfo.setBaseName(gameStartup.getBaseName());
                    break;
                case CLIENT_START:
                    if (gameTrackingInfo == null) {
                        // Browser did not reload the page
                        gameTrackingInfo = new GameTrackingInfo();
                        gameTrackingInfos.add(gameTrackingInfo);
                    }
                    gameTrackingInfo.setClientStartGameStartup(gameStartup);
                    break;
                case CLIENT_RUNNING:
                    if (gameTrackingInfo != null) {
                        gameTrackingInfo.setClientRunningGameStartup(gameStartup);
                    }
                    break;
                case CLIENT_MAP_SURFACE_IMAGES_LOADED:
                    if (gameTrackingInfo != null) {
                        gameTrackingInfo.setMapBgLoaded(gameStartup.getClientTimeStamp());
                    }
                    break;
                case CLIENT_MAP_IMAGES_LOADED:
                    if (gameTrackingInfo != null) {
                        gameTrackingInfo.setMapImagesLoaded(gameStartup.getClientTimeStamp());
                    }
                    break;
            }
        }

        // Get game start and end
        for (int i = 0; i < gameTrackingInfos.size(); i++) {
            GameTrackingInfo trackingInfo = gameTrackingInfos.get(i);
            if (trackingInfo.getClientStartGameStartup() == null) {
                continue;
            }
            trackingInfo.setStart(trackingInfo.getClientStartGameStartup().getClientTimeStamp());
            if (i + 1 < gameTrackingInfos.size()) {
                GameTrackingInfo trackingInfoNext = gameTrackingInfos.get(i + 1);
                if (trackingInfoNext.getClientStartGameStartup() != null) {
                    trackingInfo.setEnd(trackingInfoNext.getServerGameStartup().getClientTimeStamp());
                }
            }
        }

        // Fill the overview data and user commands
        for (GameTrackingInfo trackingInfo : gameTrackingInfos) {
            if (trackingInfo.getStart() == null) {
                continue;
            }
            trackingInfo.setAttackCommandCount(getUserCommandCount(sessionId, AttackCommand.class, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setMoveCommandCount(getUserCommandCount(sessionId, MoveCommand.class, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setBuilderCommandCount(getUserCommandCount(sessionId, BuilderCommand.class, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setFactoryCommandCount(getUserCommandCount(sessionId, FactoryCommand.class, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setMoneyCollectCommandCount(getUserCommandCount(sessionId, MoneyCollectCommand.class, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setCompletedMissionCount(getCompletedMissionCount(sessionId, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setUserActions(getUserActions(sessionId, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setUserCommands(getUserCommands(sessionId, trackingInfo.getStart(), trackingInfo.getEnd()));
            trackingInfo.setMissionActions(getMissionActions(sessionId, trackingInfo.getStart(), trackingInfo.getEnd()));
            // Fix end if not set
            if (trackingInfo.getEnd() == null && !trackingInfo.getUserActionCommand().isEmpty()) {
                List<UserActionCommandMissions> actions = trackingInfo.getUserActionCommand();
                trackingInfo.setEnd(actions.get(actions.size() - 1).getClientTimeStamp());
            }
        }
        */
        return gameTrackingInfos;
    }

    private List<DbUserAction> getUserActions(final String sessionId, final Date from, final Date to) {
        List<DbUserAction> list = (List<DbUserAction>) hibernateTemplate.execute(new HibernateCallback() {
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
        return list;
    }

    private List<PageAccess> getPageAccessHistory(final String sessionId) {
        List<PageAccess> list = (List<PageAccess>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.addOrder(Order.asc("timeStamp"));
                return criteria.list();
            }
        });
        return list;
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
    public void levelPromotion(Base base, String oldLevel) {
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
}
