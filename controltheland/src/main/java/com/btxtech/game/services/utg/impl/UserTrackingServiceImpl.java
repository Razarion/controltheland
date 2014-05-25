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

import com.btxtech.game.jsre.client.control.ColdSimulatedGameStartupTaskEnum;
import com.btxtech.game.jsre.client.control.WarmSimulatedGameStartupTaskEnum;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.*;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.*;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.NoBaseException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.history.*;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.tutorial.DbAbstractTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig_;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.*;
import com.btxtech.game.services.utg.*;
import com.btxtech.game.services.utg.tracker.*;
import com.btxtech.game.wicket.pages.Game;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

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
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TutorialService tutorialService;

    @Override
    @Transactional
    public void pageAccess(String pageName, String additional) {
        DbPageAccess dbPageAccess = new DbPageAccess(session.getSessionId(), pageName, additional);
        sessionFactory.getCurrentSession().saveOrUpdate(dbPageAccess);
    }

    @Override
    @Transactional
    public void saveBrowserDetails(DbSessionDetail dbSessionDetail) {
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(dbSessionDetail);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SessionOverviewDto> getSessionOverviewDtos(UserTrackingFilter filter) {
        ArrayList<SessionOverviewDto> sessionOverviewDtos = new ArrayList<>();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -filter.getDays());


        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbSessionDetail.class);
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
        if (filter.getOptionalFacebookAdValue() != null && !filter.getOptionalFacebookAdValue().trim().isEmpty()) {
            criteria.add(Restrictions.like("dbFacebookSource.optionalAdValue", filter.getOptionalFacebookAdValue()));
        }

        criteria.addOrder(Order.desc("timeStamp"));
        criteria.addOrder(Order.desc("id"));
        List<DbSessionDetail> browserDetails = criteria.list();

        for (DbSessionDetail browserDetail : browserDetails) {
            int hits = getPageHits(browserDetail.getSessionId());
            if (filter.getHits() != null && filter.getHits() > hits) {
                continue;
            }
            int startAttempts = getStartAttempts(browserDetail.getSessionId());
            int startSuccess = getStartSucceeded(browserDetail.getSessionId());
            boolean failure = hasFailureStarts(browserDetail.getSessionId()) || startAttempts != startSuccess;
            int enterGameHits = getGameAttempts(browserDetail.getSessionId());
            int commands = getUserCommandCount(browserDetail.getSessionId(), null, null, null);
            int levelPromotions = historyService.getLevelPromotionCount(browserDetail.getSessionId());
            sessionOverviewDtos.add(new SessionOverviewDto(browserDetail.getTimeStamp(),
                    browserDetail.getSessionId(),
                    hits,
                    browserDetail.isNewUser(),
                    enterGameHits,
                    startAttempts,
                    startSuccess,
                    failure,
                    commands,
                    levelPromotions,
                    browserDetail.getReferer()));
        }
        return sessionOverviewDtos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SessionOverviewDto> getSessionOverviewDtos(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbUserHistory.class);
        criteria.add(Restrictions.eq("user", user.getUsername()));
        criteria.add(Restrictions.isNotNull("loggedIn"));
        criteria.addOrder(Order.desc("loggedIn"));
        criteria.setProjection(Projections.property("sessionId"));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<String> userSessions = criteria.list();
        if (userSessions == null || userSessions.isEmpty()) {
            return null;
        }
        criteria = sessionFactory.getCurrentSession().createCriteria(DbSessionDetail.class);
        criteria.add(Restrictions.in("sessionId", userSessions));
        criteria.addOrder(Order.desc("timeStamp"));
        criteria.addOrder(Order.desc("id"));
        List<DbSessionDetail> browserDetails = criteria.list();
        ArrayList<SessionOverviewDto> sessionOverviewDtos = new ArrayList<>();
        for (DbSessionDetail browserDetail : browserDetails) {
            int hits = getPageHits(browserDetail.getSessionId());
            int startAttempts = getStartAttempts(browserDetail.getSessionId());
            int startSuccess = getStartSucceeded(browserDetail.getSessionId());
            boolean failure = hasFailureStarts(browserDetail.getSessionId()) || startAttempts != startSuccess;
            int enterGameHits = getGameAttempts(browserDetail.getSessionId());
            int commands = getUserCommandCount(browserDetail.getSessionId(), null, null, null);
            int levelPromotions = historyService.getLevelPromotionCount(browserDetail.getSessionId());
            sessionOverviewDtos.add(new SessionOverviewDto(browserDetail.getTimeStamp(),
                    browserDetail.getSessionId(),
                    hits,
                    browserDetail.isNewUser(),
                    enterGameHits,
                    startAttempts,
                    startSuccess,
                    failure,
                    commands,
                    levelPromotions,
                    browserDetail.getReferer()));
        }
        return sessionOverviewDtos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getLoginCount(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbUserHistory.class);
        criteria.add(Restrictions.eq("user", user.getUsername()));
        criteria.add(Restrictions.isNotNull("loggedIn"));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }


    @Override
    @SuppressWarnings("unchecked")
    public long calculateInGameTime(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbUserHistory.class);
        criteria.add(Restrictions.eq("user", user.getUsername()));
        criteria.add(Restrictions.isNotNull("gameEntered"));
        criteria.addOrder(Order.asc("gameEntered"));
        List<DbUserHistory> gameEntered = criteria.list();
        if (gameEntered == null || gameEntered.isEmpty()) {
            return 0;
        }
        criteria = sessionFactory.getCurrentSession().createCriteria(DbUserHistory.class);
        criteria.add(Restrictions.eq("user", user.getUsername()));
        criteria.add(Restrictions.isNotNull("gameLeft"));
        criteria.addOrder(Order.asc("gameLeft"));
        List<DbUserHistory> gameLeft = criteria.list();
        if (gameLeft == null || gameLeft.isEmpty()) {
            return 0;
        }

        long gameTimeInMillis = 0;
        while (!gameEntered.isEmpty() && !gameLeft.isEmpty()) {
            Date enterTime = gameEntered.get(0).getGameEntered();
            Date leftTime = gameLeft.get(0).getGameLeft();

            if (enterTime.getTime() < leftTime.getTime()) {
                gameTimeInMillis += leftTime.getTime() - enterTime.getTime();
                gameEntered.remove(0);
                gameLeft.remove(0);
            } else {
                gameLeft.remove(0);
            }

        }
        return gameTimeInMillis;
    }

    private int getStartAttempts(String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTask.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.countDistinct("startUuid"));
        return ((Number) criteria.list().get(0)).intValue();
    }

    private int getStartSucceeded(String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTerminated.class);
        criteria.add(Restrictions.eq("successful", true));
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.countDistinct("startUuid"));
        return ((Number) criteria.list().get(0)).intValue();
    }

    private boolean hasFailureStarts(String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTask.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.add(Restrictions.isNotNull("failureText"));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue() > 0;
    }

    private int getPageHits(String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbPageAccess.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    private int getGameAttempts(String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbPageAccess.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.add(Restrictions.eq("page", Game.class.getName()));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    private int getUserCommandCount(final String sessionId, final Class<? extends BaseCommand> command, final Date from, final Date to) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbUserCommand.class);
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

    @SuppressWarnings("unchecked")
    private List<DbUserCommand> getUserCommand(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter) {
        if (gameHistoryFilter.isShowCommands()) {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbUserCommand.class);
            criteria.add(Restrictions.eq("sessionId", gameHistoryFrame.getSessionId()));
            criteria.add(Restrictions.ge("timeStampMs", gameHistoryFrame.getStartTime()));
            if (gameHistoryFrame.getEndTimeExclusive() > 0) {
                criteria.add(Restrictions.lt("timeStampMs", gameHistoryFrame.getEndTimeExclusive()));
            }
            return (List<DbUserCommand>) criteria.list();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public SessionDetailDto getSessionDetailDto(final String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbSessionDetail.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        List<DbSessionDetail> list = criteria.list();
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

    @Override
    public List<User> getNewUsers(NewUserTrackingFilter newUserTrackingFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<User> userQuery = criteriaBuilder.createQuery(User.class);
        Root<User> from = userQuery.from(User.class);
        if (newUserTrackingFilter.getFromDate() != null && newUserTrackingFilter.getToDate() == null) {
            userQuery.where(criteriaBuilder.greaterThanOrEqualTo(from.get(User_.registerDate), newUserTrackingFilter.getFromDate()));
        } else if (newUserTrackingFilter.getFromDate() == null && newUserTrackingFilter.getToDate() != null) {
            userQuery.where(criteriaBuilder.lessThanOrEqualTo(from.get(User_.registerDate), newUserTrackingFilter.getToDate()));
        } else if (newUserTrackingFilter.getFromDate() != null) {
            userQuery.where(criteriaBuilder.between(from.get(User_.registerDate), newUserTrackingFilter.getFromDate(), newUserTrackingFilter.getToDate()));
        }
        userQuery.orderBy(criteriaBuilder.desc(from.get(User_.registerDate)));
        CriteriaQuery<User> userSelect = userQuery.select(from);
        TypedQuery<User> typedUserQuery = entityManager.createQuery(userSelect);
        return typedUserQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<LifecycleTrackingInfo> getLifecycleTrackingInfos(final String sessionId) {
        // Get all start uuids
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTask.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.groupProperty("startUuid"));
        List<String> uuids = criteria.list();
        ArrayList<LifecycleTrackingInfo> lifecycleTrackingInfos = new ArrayList<>();
        LifecycleTrackingInfo lastReaGameLifecycleTrackingInfo = null;
        for (String uuid : uuids) {
            criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTask.class);
            criteria.add(Restrictions.eq("startUuid", uuid));
            criteria.addOrder(Order.asc("clientTimeStamp"));
            List<DbStartupTask> dbStartupTasks = criteria.list();
            String levelTaskName = getLevelTaskName(dbStartupTasks);
            criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTerminated.class);
            criteria.add(Restrictions.eq("startUuid", uuid));
            List<DbStartupTerminated> startupTerminateds = criteria.list();
            LifecycleTrackingInfo lifecycleTrackingInfo = new LifecycleTrackingInfo(dbStartupTasks, levelTaskName, startupTerminateds);
            if (lifecycleTrackingInfo.isRealGame()) {
                if (lastReaGameLifecycleTrackingInfo != null) {
                    lastReaGameLifecycleTrackingInfo.setNextReaGameLifecycleTrackingInfo(lifecycleTrackingInfo);
                }
                lastReaGameLifecycleTrackingInfo = lifecycleTrackingInfo;
            }
            lifecycleTrackingInfos.add(lifecycleTrackingInfo);
        }
        Collections.sort(lifecycleTrackingInfos);
        return lifecycleTrackingInfos;
    }

    private String getLevelTaskName(List<DbStartupTask> dbStartupTasks) {
        try {
            for (DbStartupTask dbStartupTask : dbStartupTasks) {
                if (dbStartupTask.getLevelTaskId() != null) {
                    return ((DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, dbStartupTask.getLevelTaskId())).getName();
                }
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "getLifecycleTrackingInfos");
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LifecycleTrackingInfo getLifecycleTrackingInfo(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbStartupTask.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        List<DbStartupTask> startups = criteria.list();
        return new LifecycleTrackingInfo(startups, getLevelTaskName(startups), null);
    }

    @Override
    public RealGameTrackingInfo getGameTracking(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter) {
        RealGameTrackingInfo trackingInfoReal = new RealGameTrackingInfo();
        trackingInfoReal.setUserCommands(getUserCommand(gameHistoryFrame, gameHistoryFilter));
        trackingInfoReal.setHistoryElements(historyService.getHistoryElements(gameHistoryFrame, gameHistoryFilter));
        return trackingInfoReal;
    }

    @Override
    public TutorialTrackingInfo getTutorialTrackingInfo(LifecycleTrackingInfo lifecycleTrackingInfo) {
        TutorialTrackingInfo tutorialTrackingInfo = new TutorialTrackingInfo();
        tutorialTrackingInfo.setDbEventTrackingStart(getDbEventTrackingStart(lifecycleTrackingInfo.getStartUuid()));
        tutorialTrackingInfo.setDbTutorialProgresss(getDbTutorialProgresses(lifecycleTrackingInfo.getStartUuid()));
        return tutorialTrackingInfo;
    }

    @SuppressWarnings("unchecked")
    private List<DbTutorialProgress> getDbTutorialProgresses(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbTutorialProgress.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<DbPageAccess> getPageAccessHistory(final String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbPageAccess.class);
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.addOrder(Order.asc("timeStamp"));
        return criteria.list();
    }

    @Override
    @Transactional
    public void saveUserCommand(BaseCommand baseCommand) {
        try {
            DbUserCommand dbUserCommand = new DbUserCommand(session.getConnection(), baseCommand, planetSystemService.getServerPlanetServices().getBaseService().getBaseName());
            // log.debug("User Command: " + dbUserCommand);
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserCommand);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void onUserCreated(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setSessionId(session.getSessionId());
            dbUserHistory.setCookieId(session.getTrackingCookieId());
            dbUserHistory.setCreated();
            dbUserHistory.setVerificationId(user.getVerificationId());
            dbUserHistory.setAwaitingVerificationDate(user.getAwaitingVerificationDate());
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void onUserVerified(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setSessionId(session.getSessionId());
            dbUserHistory.setCookieId(session.getTrackingCookieId());
            dbUserHistory.setVerified();
            dbUserHistory.setVerificationId(user.getVerificationId());
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onUnverifiedUserRemoved(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setDeleteUnverifiedUser();
            dbUserHistory.setVerificationId(user.getVerificationId());
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onPasswordForgotRequested(User user, String forgotPasswordUuid) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setForgotPasswordRequest();
            dbUserHistory.setForgotPasswordUuid(forgotPasswordUuid);
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onPasswordReset(User user, String forgotPasswordUuid) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setPasswordChanged();
            dbUserHistory.setForgotPasswordUuid(forgotPasswordUuid);
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onPasswordForgotRequestedRemoved(DbForgotPassword dbForgotPassword) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(dbForgotPassword.getUser());
            dbUserHistory.setForgotPasswordRequestRemoved();
            dbUserHistory.setForgotPasswordUuid(dbForgotPassword.getUuid());
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void onUserLoggedIn(User user, UserState userState) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setSessionId(session.getSessionId());
            dbUserHistory.setCookieId(session.getTrackingCookieId());
            dbUserHistory.setLoggedIn();
            if (userState != null && userState.getBase() != null) {
                dbUserHistory.setBaseName(planetSystemService.getServerPlanetServices(userState.getBase().getSimpleBase()).getBaseService().getBaseName(userState.getBase().getSimpleBase()));
            }
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void onUserLoggedOut(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setLoggedOut();
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
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
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void onBaseDefeated(User user, Base base) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setBaseDefeated();
            dbUserHistory.setBaseName(planetSystemService.getServerPlanetServices(base.getSimpleBase()).getBaseService().getBaseName(base.getSimpleBase()));
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void onBaseSurrender(User user, Base base) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setBaseSurrender();
            dbUserHistory.setBaseName(planetSystemService.getServerPlanetServices(base.getSimpleBase()).getBaseService().getBaseName(base.getSimpleBase()));
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }


    @Override
    @Transactional
    public void onUserEnterGame(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setGameEntered();
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void onUserLeftGame(User user) {
        try {
            DbUserHistory dbUserHistory = new DbUserHistory(user);
            dbUserHistory.setGameLeft();
            sessionFactory.getCurrentSession().saveOrUpdate(dbUserHistory);
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    public void onUserLeftGameNoSession(User user) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            onUserLeftGame(user);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }

    }

    @Override
    @Transactional
    public void trackChatMessage(ChatMessage chatMessage) {
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(new DbChatMessage(session.getSessionId(), chatMessage));
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }
    }

    @Override
    @Transactional
    public void trackWindowsClosed(String startUUid) {
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(new DbWindowClosed(session.getSessionId(), startUUid));
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
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
    public void onTutorialProgressChanged(TutorialConfig.TYPE type, String startUuid, int taskId, int dbId, String tutorialTaskName, long duration, long clientTimeStamp) {
        try {
            DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, taskId);
            sessionFactory.getCurrentSession().save(new DbTutorialProgress(session.getSessionId(), type.name(), startUuid, dbLevelTask.getId(), dbLevelTask.getName(), dbId, tutorialTaskName, duration, clientTimeStamp));
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "onTutorialProgressChanged");
        }
    }

    @Override
    @Transactional
    public void onEventTrackingStart(EventTrackingStart eventTrackingStart) {
        sessionFactory.getCurrentSession().save(new DbEventTrackingStart(eventTrackingStart, session.getSessionId()));
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

    private void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems) {
        ArrayList<DbEventTrackingItem> dbEventTrackingItems = new ArrayList<>();
        for (EventTrackingItem eventTrackingItem : eventTrackingItems) {
            dbEventTrackingItems.add(new DbEventTrackingItem(eventTrackingItem));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbEventTrackingItems);
    }

    private void saveSyncItemInfos(Collection<SyncItemInfo> syncItemInfos) {
        ArrayList<DbSyncItemInfo> dbSyncItemInfos = new ArrayList<>();
        for (SyncItemInfo syncItemInfo : syncItemInfos) {
            dbSyncItemInfos.add(new DbSyncItemInfo(syncItemInfo));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbSyncItemInfos);
    }

    private void saveSelections(Collection<SelectionTrackingItem> selectionTrackingItems) {
        ArrayList<DbSelectionTrackingItem> dbSelectionTrackingItems = new ArrayList<>();
        for (SelectionTrackingItem command : selectionTrackingItems) {
            dbSelectionTrackingItems.add(new DbSelectionTrackingItem(command));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbSelectionTrackingItems);
    }

    private void saveScrollTrackingItems(Collection<TerrainScrollTracking> terrainScrollTrackings) {
        ArrayList<DbScrollTrackingItem> dbScrollTrackingItems = new ArrayList<>();
        for (TerrainScrollTracking terrainScroll : terrainScrollTrackings) {
            dbScrollTrackingItems.add(new DbScrollTrackingItem(terrainScroll));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbScrollTrackingItems);
    }

    private void saveBrowserWindowTrackings(Collection<BrowserWindowTracking> browserWindowTrackings) {
        ArrayList<DbBrowserWindowTracking> dbBrowserWindowTrackings = new ArrayList<>();
        for (BrowserWindowTracking browserWindowTracking : browserWindowTrackings) {
            dbBrowserWindowTrackings.add(new DbBrowserWindowTracking(browserWindowTracking));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbBrowserWindowTrackings);
    }

    private void saveDialogTrackings(Collection<DialogTracking> dialogTrackings) {
        ArrayList<DbDialogTracking> dbDialogTrackings = new ArrayList<>();
        for (DialogTracking dialogTracking : dialogTrackings) {
            dbDialogTrackings.add(new DbDialogTracking(dialogTracking));
        }
        HibernateUtil.saveOrUpdateAll(sessionFactory, dbDialogTrackings);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DbEventTrackingStart getDbEventTrackingStart(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbEventTrackingStart.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        List<DbEventTrackingStart> dbEventTrackingStarts = criteria.list();
        if (dbEventTrackingStarts.isEmpty()) {
            return null;
        } else {
            return dbEventTrackingStarts.get(0);
        }
    }

    @Override
    @Transactional
    public void saveStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId) {
        session.onJavaScriptDetected(null);
        String baseName = null;
        Integer baseId = null;
        try {
            if (levelTaskId == null && planetSystemService.hasPlanet()) {
                BaseService baseService = planetSystemService.getServerPlanetServices().getBaseService();
                Base base = baseService.getBase();
                baseId = base.getBaseId();
                baseName = baseService.getBaseName(base.getSimpleBase());
            }
        } catch (NoBaseException e) {
            // Ignore
        }
        sessionFactory.getCurrentSession().save(new DbStartupTask(session.getSessionId(), startupTaskInfo, startUuid, userGuidanceService.getDbLevel(), levelTaskId, userService.getUser(), baseId, baseName));
    }

    @Override
    public void saveStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        session.onJavaScriptDetected(null);
        sessionFactory.getCurrentSession().save(new DbStartupTerminated(session.getSessionId(), successful, totalTime, startUuid, levelTaskId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbEventTrackingItem> getDbEventTrackingItem(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbEventTrackingItem.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbSelectionTrackingItem> getDbSelectionTrackingItems(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbSelectionTrackingItem.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbSyncItemInfo> getDbSyncItemInfos(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbSyncItemInfo.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbScrollTrackingItem> getDbScrollTrackingItems(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbScrollTrackingItem.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbBrowserWindowTracking> getDbBrowserWindowTrackings(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBrowserWindowTracking.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbDialogTracking> getDbDialogTrackings(String startUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbDialogTracking.class);
        criteria.add(Restrictions.eq("startUuid", startUuid));
        criteria.addOrder(Order.asc("clientTimeStamp"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewUserDailyDto> getNewUserDailyDto(NewUserDailyTrackingFilter newUserDailyTrackingFilter) {
        Map<Date, NewUserDailyDto> map = new HashMap<>();
        for (User user : getUsers(newUserDailyTrackingFilter)) {
            Date date = DateUtils.truncate(newUserDailyTrackingFilter.correctTimeZoneOffsetSub(user.getRegisterDate()), Calendar.DAY_OF_MONTH);
            NewUserDailyDto newUserDailyDto = map.get(date);
            if (newUserDailyDto == null) {
                newUserDailyDto = new NewUserDailyDto(date);
                map.put(date, newUserDailyDto);
            }
            newUserDailyDto.increaseRegistered();
            try {
                DbLevel dbLevel = userGuidanceService.getDbLevel(user);
                newUserDailyDto.increaseLevelNumber(dbLevel.getNumber());
            } catch (Exception e) {
                ExceptionHandler.handleException(e, "Unable getting level for user: " + user);
            }
        }
        fillSessionTracking(newUserDailyTrackingFilter, map);
        List<NewUserDailyDto> list = new ArrayList<>(map.values());
        Collections.sort(list, new Comparator<NewUserDailyDto>() {
            @Override
            public int compare(NewUserDailyDto o1, NewUserDailyDto o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        return list;
    }

    private void fillSessionTracking(NewUserDailyTrackingFilter newUserDailyTrackingFilter, Map<Date, NewUserDailyDto> map) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<DbSessionDetail> sessionQuery = criteriaBuilder.createQuery(DbSessionDetail.class);
        Root<DbSessionDetail> from = sessionQuery.from(DbSessionDetail.class);
        Predicate predicate = null;
        // criteriaBuilder.
        if (newUserDailyTrackingFilter.hasFromDate()) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(from.<Date>get("timeStamp"), newUserDailyTrackingFilter.getCorrectedFromDate());
        }
        if (newUserDailyTrackingFilter.hasToDate()) {
            Predicate tmpPredict = criteriaBuilder.lessThan(from.<Date>get("timeStamp"), newUserDailyTrackingFilter.getCorrectedExclusiveToDate());
            if (predicate != null) {
                predicate = criteriaBuilder.and(predicate, tmpPredict);
            } else {
                predicate = tmpPredict;
            }
        }
        if (newUserDailyTrackingFilter.hasFacebookAdId()) {
            Predicate tmpPredict = criteriaBuilder.like(from.<String>get("dbFacebookSource").<String>get("optionalAdValue"), newUserDailyTrackingFilter.getFacebookAdId());
            if (predicate != null) {
                predicate = criteriaBuilder.and(predicate, tmpPredict);
            } else {
                predicate = tmpPredict;
            }
        }
        if (predicate != null) {
            sessionQuery.where(predicate);
        }
        for (DbSessionDetail dbSessionDetail : entityManager.createQuery(sessionQuery.select(from)).getResultList()) {
            Date date = DateUtils.truncate(newUserDailyTrackingFilter.correctTimeZoneOffsetSub(dbSessionDetail.getTimeStamp()), Calendar.DAY_OF_MONTH);
            NewUserDailyDto newUserDailyDto = map.get(date);
            if (newUserDailyDto == null) {
                newUserDailyDto = new NewUserDailyDto(date);
                map.put(date, newUserDailyDto);
            }
            newUserDailyDto.increaseSessions();
        }
    }

    private List<User> getUsers(NewUserDailyTrackingFilter newUserDailyTrackingFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<User> userQuery = criteriaBuilder.createQuery(User.class);
        Root<User> from = userQuery.from(User.class);
        Predicate predicate = criteriaBuilder.equal(from.get(User_.accountNonLocked), true);
        // criteriaBuilder.
        if (newUserDailyTrackingFilter.hasFromDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(from.get(User_.registerDate), newUserDailyTrackingFilter.getCorrectedFromDate()));
        }
        if (newUserDailyTrackingFilter.hasToDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(from.get(User_.registerDate), newUserDailyTrackingFilter.getCorrectedExclusiveToDate()));
        }
        if (newUserDailyTrackingFilter.hasFacebookAdId()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(from.get(User_.dbFacebookSource).get(DbFacebookSource_.optionalAdValue), newUserDailyTrackingFilter.getFacebookAdId()));
        }
        userQuery.where(predicate);
        userQuery.orderBy(criteriaBuilder.asc(from.get(User_.registerDate)));
        CriteriaQuery<User> userSelect = userQuery.select(from);
        return entityManager.createQuery(userSelect).getResultList();
    }

    @Override
    public TutorialStatisticDto getTutorialStatistic(QuestTrackingFilter questTrackingFilter) {
        // Distribute the history to the quests
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(questTrackingFilter.getDbId());
        List<TutorialStatisticDto.TutorialQuestEntry> tutorialQuestEntries = new ArrayList<>();
        Integer last = null;
        for (DbAbstractTaskConfig dbAbstractTaskConfig : dbTutorialConfig.getDbTaskConfigs()) {
            int passed = getDoneCount4Tutorial(questTrackingFilter, dbAbstractTaskConfig.getId());
            String percentage = null;
            if (last != null) {
                percentage = Integer.toString((int) ((double) passed / (double) last * 100.0)) + "%";
            }
            last = passed;
            tutorialQuestEntries.add(new TutorialStatisticDto.TutorialQuestEntry(dbAbstractTaskConfig.getName(), passed, percentage));
        }
        return new TutorialStatisticDto(dbTutorialConfig.getName(), getSuccessfulTutorialStart(questTrackingFilter), tutorialQuestEntries);
    }

    private int getDoneCount4Tutorial(QuestTrackingFilter questTrackingFilter, int tutorialTaskId) {
        // Get the History
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DbTutorialProgress> from = countQuery.from(DbTutorialProgress.class);
        Predicate predicate = criteriaBuilder.equal(from.get(DbTutorialProgress_.dbId), tutorialTaskId);
        // criteriaBuilder.
        if (questTrackingFilter.hasFromDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(from.get(DbTutorialProgress_.timeStamp), questTrackingFilter.getFromDate().getTime()));
        }
        if (questTrackingFilter.hasToDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(from.get(DbTutorialProgress_.timeStamp), questTrackingFilter.getToDate().getTime()));
        }
        countQuery.where(predicate);
        CriteriaQuery<Long> userSelect = countQuery.select(criteriaBuilder.count(from));
        return entityManager.createQuery(userSelect).getSingleResult().intValue();
    }

    private int getSuccessfulTutorialStart(QuestTrackingFilter questTrackingFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Get the levelTaskId
        CriteriaQuery<DbLevelTask> questQuery = criteriaBuilder.createQuery(DbLevelTask.class);
        Root<DbLevelTask> fromQuest = questQuery.from(DbLevelTask.class);
        questQuery.where(criteriaBuilder.equal(fromQuest.get(DbLevelTask_.dbTutorialConfig).get(DbTutorialConfig_.id), questTrackingFilter.getDbId()));
        CriteriaQuery<DbLevelTask> questSelect = questQuery.select(fromQuest);
        int successfulStartup = 0;
        for (DbLevelTask dbLevelTask : entityManager.createQuery(questSelect).getResultList()) {
            successfulStartup += getSuccessfulTutorialStartForQuest(questTrackingFilter, dbLevelTask.getId());
        }
        return successfulStartup;
    }

    private int getSuccessfulTutorialStartForQuest(QuestTrackingFilter questTrackingFilter, int questId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Get the successful startups
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DbStartupTask> from = countQuery.from(DbStartupTask.class);

        Predicate predicate = from.get(DbStartupTask_.failureText).isNull();
        predicate = criteriaBuilder.and(predicate, from.get(DbStartupTask_.task).in(ColdSimulatedGameStartupTaskEnum.RUN_SIMULATED_GAME.getStartupTaskEnumHtmlHelper().getNiceText(),
                WarmSimulatedGameStartupTaskEnum.RUN_SIMULATED_GAME.getStartupTaskEnumHtmlHelper().getNiceText()));
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(from.get(DbStartupTask_.levelTaskId), questId));
        if (questTrackingFilter.hasFromDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(from.get(DbStartupTask_.timeStamp), questTrackingFilter.getFromDate()));
        }
        if (questTrackingFilter.hasToDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(from.get(DbStartupTask_.timeStamp), questTrackingFilter.getToDate()));
        }
        countQuery.where(predicate);

        CriteriaQuery<Long> userSelect = countQuery.select(criteriaBuilder.count(from));
        return entityManager.createQuery(userSelect).getSingleResult().intValue();
    }

    @Override
    public QuestStatisticDto getQuestStatistic(QuestTrackingFilter questTrackingFilter) {
        DbLevel dbLevel = userGuidanceService.getDbLevel(questTrackingFilter.getDbId());
        List<QuestStatisticDto.QuestEntry> questEntries = new ArrayList<>();
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            int passed = getDoneCount4Quest(questTrackingFilter, dbLevelTask.getId());
            questEntries.add(new QuestStatisticDto.QuestEntry(dbLevelTask.getName(), passed));
        }
        return new QuestStatisticDto(dbLevel.getNumber(), questEntries);
    }

    private int getDoneCount4Quest(QuestTrackingFilter questTrackingFilter, int levelId) {
        // Get the History
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DbHistoryElement> from = countQuery.from(DbHistoryElement.class);
        Predicate predicate = criteriaBuilder.equal(from.get(DbHistoryElement_.levelTaskId), levelId);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(from.get(DbHistoryElement_.type), DbHistoryElement.Type.LEVEL_TASK_COMPLETED));
        // criteriaBuilder.
        if (questTrackingFilter.hasFromDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(from.get(DbHistoryElement_.timeStamp), questTrackingFilter.getFromDate()));
        }
        if (questTrackingFilter.hasToDate()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(from.get(DbHistoryElement_.timeStamp), questTrackingFilter.getToDate()));
        }
        countQuery.where(predicate);
        CriteriaQuery<Long> userSelect = countQuery.select(criteriaBuilder.count(from));
        return entityManager.createQuery(userSelect).getSingleResult().intValue();
    }
}
