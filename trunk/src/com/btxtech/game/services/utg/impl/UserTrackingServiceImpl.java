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

import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.UserCommand;
import com.btxtech.game.services.utg.UserDetails;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorDetailInfo;
import com.btxtech.game.services.utg.VisitorInfo;
import com.btxtech.game.services.utg.DbMissionAction;
import com.btxtech.game.services.utg.UserActionCommandMissions;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.wicket.pages.entergame.EnterBasePanel;
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
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(UserTrackingServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void pageAccess(BasePage basePage) {
        PageAccess pageAccess = new PageAccess(session.getSessionId(), basePage.getClass().getName(), basePage.getAdditionalPageInfo());
        hibernateTemplate.saveOrUpdate(pageAccess);
    }

    @Override
    public void newSession(UserDetails userDetails) {
        hibernateTemplate.saveOrUpdate(userDetails);
    }

    @Override
    public void gameStartup(GameStartupState state, Date timeStamp) {
        GameStartup gameStartup = new GameStartup(session.getSessionId(), state, timeStamp);
        hibernateTemplate.saveOrUpdate(gameStartup);
    }

    @Override
    public void saveUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions) {
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
    }

    @Override
    public List<VisitorInfo> getVisitorInfos() {
        ArrayList<VisitorInfo> visitorInfos = new ArrayList<VisitorInfo>();
        List<Object[]> datesAndHits = (List<Object[]>) hibernateTemplate.find("select u.timeStamp, u.sessionId, u.cookieId ,count(p) from com.btxtech.game.services.utg.UserDetails u, com.btxtech.game.services.utg.PageAccess p where u.sessionId = p.sessionId and u.isCrawler = false group by u.sessionId order by u.timeStamp desc");
        for (Object[] datesAndHit : datesAndHits) {
            Date timeStamp = (Date) datesAndHit[0];
            String sessionId = (String) datesAndHit[1];
            boolean cookie = datesAndHit[2] != null;
            int hits = ((Long) datesAndHit[3]).intValue();
            int enterSetupHits = getHitsForPage(sessionId, EnterBasePanel.class);
            int enterGameHits = getHitsForGameStartup(sessionId);
            int commands = getUserCommandCount(sessionId, null, null, null);
            int completedMissions = getCompletedMissionCount(sessionId, null, null);
            visitorInfos.add(new VisitorInfo(timeStamp, sessionId, hits, enterSetupHits, enterGameHits, commands, completedMissions, cookie));
        }
        return visitorInfos;
    }

    private int getHitsForPage(final String sessionId, final Class<EnterBasePanel> enterBasePanelClass) {
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PageAccess.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("page", enterBasePanelClass.getName()));
                criteria.setProjection(Projections.rowCount());
                return criteria.list();
            }
        });
        return list.get(0);
    }

    private int getHitsForGameStartup(final String sessionId) {
        List<Integer> list = (List<Integer>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(GameStartup.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                criteria.add(Restrictions.eq("state", GameStartupState.SERVER));
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
        List<UserDetails> list = (List<UserDetails>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(UserDetails.class);
                criteria.add(Restrictions.eq("sessionId", sessionId));
                return criteria.list();
            }
        });
        if (list.size() != 1) {
            throw new IllegalStateException("Only 1 UserDetails expected: " + list.size());
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
                case CLIENT_MAP_BG_LOADED:
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

}
