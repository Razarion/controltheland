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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.DbQuestHub;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService, ConditionServiceListener<UserState, Integer> {
    public static final String NO_MISSION_TARGET = "<center>There are no new mission targets.<br><h1>Please check back later</h1></center>";
    @Autowired
    private BaseService baseService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private CrudRootServiceHelper<DbQuestHub> crudQuestHub;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ItemService itemService;
    @Autowired
    private XpService xpService;
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);
    private Map<Integer, LevelScope> levelScopes = new HashMap<Integer, LevelScope>();
    private Map<UserState, Collection<Integer>> openLevelTasks = new HashMap<UserState, Collection<Integer>>();

    @PostConstruct
    public void init() {
        crudQuestHub.init(DbQuestHub.class, "orderIndex", true, true, null);
        serverConditionService.setConditionServiceListener(this);
    }

    @Override
    public void init2() {
        try {
            activateLevels();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void createBaseInQuestHub(UserState userState) {
        DbLevel dbLevel = getDbLevel(userState);
        DbQuestHub dbQuestHub = dbLevel.getDbQuestHub();
        if (!dbQuestHub.isRealBaseRequired()) {
            throw new IllegalStateException("QuestHub does not allow to start new base: " + dbQuestHub);
        }
        Territory territory = territoryService.getTerritory(dbQuestHub.getStartTerritory());

        try {
            baseService.createNewBase(userState, dbQuestHub.getStartItemType(), territory, dbQuestHub.getStartItemFreeRange());
        } catch (Exception e) {
            log.error("Can not create base for user: " + userState, e);
        }

        Base base = baseService.getBase(userState);
        base.setAccountBalance(dbQuestHub.getStartMoney());
        baseService.sendAccountBaseUpdate(base.getSimpleBase());

        log.debug("User: " + userState + " will be resurrected: " + dbQuestHub);
    }

    @Override
    public void sendResurrectionMessage(SimpleBase simpleBase) {
        Message message = new Message();
        message.setMessage("You lost your base. A new base was created.");
        connectionService.sendPacket(simpleBase, message);
    }

    @Override
    public void promote(UserState userState, int newDbLevelId) {
        promote(userState, getDbLevel(newDbLevelId));
    }

    private void promote(UserState userState, DbLevel dbNextLevel) {
        // Cleanup
        cleanupConditions(userState);
        // Prepare
        DbLevel dbOldLevel = getDbLevel(userState.getDbLevelId());
        userState.setDbLevelId(dbNextLevel.getId());
        // Tracking
        historyService.addLevelPromotionEntry(userState, dbNextLevel);
        statisticsService.onLevelPromotion(userState);
        log.debug("User: " + userState + " has been promoted: " + dbOldLevel + " to " + dbNextLevel);

        if (baseService.getBase(userState) == null && dbNextLevel.getDbQuestHub().isRealBaseRequired()) {
            createBaseInQuestHub(userState);
        }

        // Prepare next level
        activateConditions(userState, dbNextLevel);

        // Send level update packet
        if (baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(getLevelScope(dbNextLevel.getId()));
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
    }

    @Override
    public void conditionPassed(UserState userState, Integer taskId) {
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            conditionPassedInSession(userState, taskId);
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                conditionPassedInSession(userState, taskId);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    private void conditionPassedInSession(UserState userState, Integer taskId) {
        if (taskId != null) {
            handleLevelTaskCompletion(userState, taskId);
        } else {
            DbLevel dbOldLevel = getDbLevel(userState);
            DbLevel dbNextLevel = getNextDbLevel(dbOldLevel);
            promote(userState, dbNextLevel);
        }
    }

    @Override
    public GameFlow onTutorialFinished(int levelTaskId) {
        DbLevel oldLevel = getDbLevel();
        UserState userState = userService.getUserState();
        serverConditionService.onTutorialFinished(userState, levelTaskId);
        DbLevel newLevel = getDbLevel();

        if (!oldLevel.getDbQuestHub().isRealBaseRequired() && newLevel.getDbQuestHub().isRealBaseRequired()) {
            return new GameFlow(GameFlow.Type.START_REAL_GAME, null);
        } else if (!newLevel.getDbQuestHub().isRealBaseRequired()) {
            DbLevelTask dbLevelTask = newLevel.getFirstTutorialLevelTask();
            return new GameFlow(GameFlow.Type.START_NEXT_LEVEL_TASK_TUTORIAL, dbLevelTask.getId());
        } else {
            return new GameFlow(GameFlow.Type.SHOW_LEVEL_TASK_DONE_PAGE, null);
        }
    }

    private void activateConditions(UserState userState, DbLevel dbLevel) {
        if (dbLevel.getDbConditionConfig() != null) {
            ConditionConfig levelCondition = dbLevel.getDbConditionConfig().createConditionConfig(itemService);
            serverConditionService.activateCondition(levelCondition, userState, null);
        }
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            serverConditionService.activateCondition(dbLevelTask.createConditionConfig(itemService), userState, dbLevelTask.getId());
            Collection<Integer> openTasks = openLevelTasks.get(userState);
            if (openTasks == null) {
                openTasks = new ArrayList<Integer>();
                openLevelTasks.put(userState, openTasks);
            }
            openTasks.add(dbLevelTask.getId());
        }
    }

    private void cleanupConditions(UserState userState) {
        serverConditionService.deactivateActorConditions(userState, null);
        Collection<Integer> openTasks = openLevelTasks.get(userState);
        if (openTasks != null) {
            for (Integer openTaskId : openTasks) {
                serverConditionService.deactivateActorConditions(userState, openTaskId);
            }
        }
        openLevelTasks.remove(userState);
    }

    private void handleLevelTaskCompletion(UserState userState, int levelTaskId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, levelTaskId);
        historyService.addLevelTaskCompletedEntry(userState, dbLevelTask);
        if (dbLevelTask.getXp() > 0) {
            xpService.onReward(userState, dbLevelTask.getXp());
        }
        Base base = baseService.getBase(userState);
        if (base != null && dbLevelTask.getMoney() > 0) {
            baseService.depositResource(dbLevelTask.getMoney(), base.getSimpleBase());
            baseService.sendAccountBaseUpdate(base.getSimpleBase());
        }
    }

    @Override
    public void onRemoveUserState(UserState userState) {
        cleanupConditions(userState);
    }

    @Override
    public void setLevelForNewUser(UserState userState) {
        DbLevel dbLevel = new ArrayList<DbQuestHub>(crudQuestHub.readDbChildren()).get(0).getLevelCrud().readDbChildren().get(0);
        userState.setDbLevelId(dbLevel.getId());
        activateConditions(userState, dbLevel);
    }

    // TODO Start-Buttons
//    @Override
//    public GameStartupSeq getColdStartupSeq() {
//        DbLevel dbLevel = getDbAbstractLevel();
//        if (dbLevel instanceof DbRealGameLevel) {
//            return GameStartupSeq.COLD_REAL;
//        } else if (dbLevel instanceof DbSimulationLevel) {
//            return GameStartupSeq.COLD_SIMULATED;
//        } else {
//            throw new IllegalArgumentException("Unknown level  " + dbLevel);
//        }
//    }

    private DbLevel getNextDbLevel(DbLevel dbLevel) {
        DbQuestHub dbQuestHub = dbLevel.getDbQuestHub();
        List<DbLevel> dbLevels = dbQuestHub.getLevelCrud().readDbChildren();
        int index = dbLevels.indexOf(dbLevel);
        if (index < 0) {
            throw new IllegalArgumentException("DbLevel can not be found in own DbQuestHub: " + dbLevel);
        }
        index++;
        if (dbLevels.size() > index) {
            return dbLevels.get(index);
        } else {
            List<DbQuestHub> dbQuestHubs = new ArrayList<DbQuestHub>(crudQuestHub.readDbChildren());
            index = dbQuestHubs.indexOf(dbQuestHub);
            if (index < 0) {
                throw new IllegalArgumentException("DbLevel can not be found in own DbQuestHub: " + dbLevel);
            }
            index++;
            if (dbQuestHubs.size() > index) {
                return dbQuestHubs.get(index).getLevelCrud().readDbChildren().get(0);
            } else {
                throw new IllegalArgumentException("Is last DbQuestHub" + dbQuestHub);
            }
        }
    }

    @Override
    public DbLevel getDbLevelCms() {
        // Prevent creating a UserState -> search engine
        if (userService.hasUserState()) {
            return getDbLevel(userService.getUserState());
        } else {
            return null;
        }
    }

    @Override
    public DbLevel getDbLevel() {
        return (DbLevel) sessionFactory.getCurrentSession().get(DbLevel.class, userService.getUserState().getDbLevelId());
    }

    @Override
    public DbLevel getDbLevel(UserState userState) {
        return getDbLevel(userState.getDbLevelId());
    }

    @Override
    public DbLevel getDbLevel(int levelId) {
        return (DbLevel) sessionFactory.getCurrentSession().get(DbLevel.class, levelId);
    }

    public LevelScope getLevelScope(int dbLevelId) {
        LevelScope levelScope = levelScopes.get(dbLevelId);
        if (levelScope == null) {
            throw new IllegalArgumentException("No LevelScope for dbLevelId: " + dbLevelId + ". Did you forget to activate the levels?");
        }
        return levelScope;
    }

    @Override
    public LevelScope getLevelScope() {
        return getLevelScope(userService.getUserState().getDbLevelId());
    }

    @Override
    public LevelScope getLevelScope(SimpleBase simpleBase) {
        UserState userState = baseService.getUserState(simpleBase);
        return getLevelScope(userState.getDbLevelId());
    }

    @Override
    public boolean isStartRealGame() {
        return getDbLevel().getDbQuestHub().isRealBaseRequired();
    }

    @Override
    public int getDefaultLevelTaskId() {
        DbLevel dbLevel = getDbLevel();
        if (dbLevel.getDbQuestHub().isRealBaseRequired()) {
            throw new IllegalArgumentException("If real game is required, no default tutorial LevelTask is is available");
        }
        return dbLevel.getFirstTutorialLevelTask().getId();
    }

//    @Override
//    @Transactional
//    @Deprecated
//    // Use CRUD
//    public void updateDbConditionConfig(DbConditionConfig dbConditionConfig) {
//        sessionFactory.getCurrentSession().saveOrUpdate(dbConditionConfig);
//    }

//    @Override
//    @Transactional
//    @Deprecated
//    // Use CRUD
//    public void createDbComparisonItemCount(DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfigId) {
//        dbSyncItemTypeComparisonConfigId.getCrudDbComparisonItemCount().createDbChild();
//        sessionFactory.getCurrentSession().save(dbSyncItemTypeComparisonConfigId);
//    }

//    @Override
//    @Transactional
//    @Deprecated
//    // Use CRUD
//    public void createDbItemTypeLimitation(DbLevel dbLevel) {
//        dbLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
//        sessionFactory.getCurrentSession().update(dbLevel);
//    }

//    @Override
//    public DbAbstractComparisonConfig getDbAbstractComparisonConfig(int dbAbstractComparisonConfigId) {
//        return HibernateUtil.get(sessionFactory, DbAbstractComparisonConfig.class, dbAbstractComparisonConfigId);
//    }

//    @Override
//    public DbSyncItemTypeComparisonConfig getDbSyncItemTypeComparisonConfig(int dbSyncItemTypeComparisonConfigId) {
//        return HibernateUtil.get(sessionFactory, DbSyncItemTypeComparisonConfig.class, dbSyncItemTypeComparisonConfigId);
//    }

    @Override
    public void activateLevels() throws LevelActivationException {
        levelScopes.clear();
        for (DbQuestHub dbQuestHub : crudQuestHub.readDbChildren()) {
            for (DbLevel dbLevel : dbQuestHub.getLevelCrud().readDbChildren()) {
                levelScopes.put(dbLevel.getId(), dbLevel.createLevelScope());
            }
        }
    }

    //TODO
//    @Override
//    public DbLevel copyDbLevel(Serializable copyFromId) {
//        return crudServiceHelperHibernate.copyDbChild(copyFromId);
//    }

    @Override
    public CrudRootServiceHelper<DbQuestHub> getCrudQuestHub() {
        return crudQuestHub;
    }
}
