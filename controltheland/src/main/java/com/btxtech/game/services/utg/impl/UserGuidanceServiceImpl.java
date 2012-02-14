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
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.LevelTaskDonePacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.DbQuestHub;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.LevelQuest;
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
    public void createBaseInQuestHub(UserState userState) throws InvalidLevelState {
        DbLevel dbLevel = getDbLevel(userState);
        DbQuestHub dbQuestHub = dbLevel.getParent();
        if (!dbQuestHub.isRealBaseRequired()) {
            throw createInvalidLevelState();
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
        // Create base if needed
        if (baseService.getBase(userState) == null && dbNextLevel.getParent().isRealBaseRequired()) {
            try {
                createBaseInQuestHub(userState);
            } catch (InvalidLevelState invalidLevelState) {
                log.error("Error during base creation: " + userState, invalidLevelState);
            }
        }
        // Prepare next level
        activateConditions(userState, dbNextLevel, null);
        // Send level update packet
        if (baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(getLevelScope(dbNextLevel.getId()));
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
        // Post processing
        userState.setXp(0);
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

        if (!oldLevel.getParent().isRealBaseRequired() && newLevel.getParent().isRealBaseRequired()) {
            return new GameFlow(GameFlow.Type.START_REAL_GAME, null);
        } else if (!newLevel.getParent().isRealBaseRequired()) {
            DbLevelTask dbLevelTask = newLevel.getFirstTutorialLevelTask();
            return new GameFlow(GameFlow.Type.START_NEXT_LEVEL_TASK_TUTORIAL, dbLevelTask.getId());
        } else {
            return new GameFlow(GameFlow.Type.SHOW_LEVEL_TASK_DONE_PAGE, null);
        }
    }

    private void activateConditions(UserState userState, DbLevel dbLevel, Collection<DbLevelTask> levelTaskDone) {
        if (dbLevel.getDbConditionConfig() != null) {
            ConditionConfig levelCondition = dbLevel.getDbConditionConfig().createConditionConfig(itemService);
            serverConditionService.activateCondition(levelCondition, userState, null);
        }
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            if (levelTaskDone != null && levelTaskDone.contains(dbLevelTask)) {
                continue;
            }
            serverConditionService.activateCondition(dbLevelTask.createConditionConfig(itemService), userState, dbLevelTask.getId());
        }
    }

    private void cleanupConditions(UserState userState) {
        serverConditionService.deactivateAllActorConditions(userState);
    }

    private void handleLevelTaskCompletion(UserState userState, int levelTaskId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, levelTaskId);
        // Communication
        log.debug("Level Task completed. userState: " + userState + " " + dbLevelTask);
        historyService.addLevelTaskCompletedEntry(userState, dbLevelTask);
        Base base = baseService.getBase(userState);
        if (base != null) {
            LevelTaskDonePacket levelTaskDonePacket = new LevelTaskDonePacket();
            connectionService.sendPacket(base.getSimpleBase(), levelTaskDonePacket);
        }
        // Rewards
        if (dbLevelTask.getXp() > 0) {
            xpService.onReward(userState, dbLevelTask.getXp());
        }
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
        activateConditions(userState, dbLevel, null);
    }

    private DbLevel getNextDbLevel(DbLevel dbLevel) {
        DbQuestHub dbQuestHub = dbLevel.getParent();
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
        return getDbLevel().getParent().isRealBaseRequired();
    }

    @Override
    public int getDefaultLevelTaskId() {
        DbLevel dbLevel = getDbLevel();
        if (dbLevel.getParent().isRealBaseRequired()) {
            throw new IllegalArgumentException("If real game is required, no default tutorial LevelTask is is available");
        }
        return dbLevel.getFirstTutorialLevelTask().getId();
    }

    @Override
    public void activateLevels() throws LevelActivationException {
        levelScopes.clear();
        for (DbQuestHub dbQuestHub : crudQuestHub.readDbChildren()) {
            for (DbLevel dbLevel : dbQuestHub.getLevelCrud().readDbChildren()) {
                levelScopes.put(dbLevel.getId(), dbLevel.createLevelScope());
            }
        }
    }

    @Override
    public CrudRootServiceHelper<DbQuestHub> getCrudQuestHub() {
        return crudQuestHub;
    }

    @Override
    public ContentProvider<LevelQuest> getQuestsCms() {
        List<LevelQuest> levelQuests = new ArrayList<LevelQuest>();
        UserState userState = userService.getUserState();
        for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() == null) {
                levelQuests.add(new LevelQuest(dbLevelTask, !serverConditionService.hasConditionTrigger(userState, dbLevelTask.getId())));
            }
        }
        return new ReadonlyListContentProvider<LevelQuest>(levelQuests);
    }

    @Override
    public ContentProvider<LevelQuest> getMercenaryMissionCms() {
        List<LevelQuest> levelQuests = new ArrayList<LevelQuest>();
        UserState userState = userService.getUserState();
        for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() != null) {
                levelQuests.add(new LevelQuest(dbLevelTask, !serverConditionService.hasConditionTrigger(userState, dbLevelTask.getId())));
            }
        }
        return new ReadonlyListContentProvider<LevelQuest>(levelQuests);
    }

    @Override
    public void createAndAddBackup(DbUserState dbUserState, UserState userState) {
        DbLevel dbLevel = getDbLevel(userState);
        Collection<DbLevelTask> levelTaskDone = new ArrayList<DbLevelTask>();
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            if (!serverConditionService.hasConditionTrigger(userState, dbLevelTask.getId())) {
                levelTaskDone.add(dbLevelTask);
            }
        }
        dbUserState.setLevelTasksDone(levelTaskDone);
        serverConditionService.createBackup(dbUserState, userState);
    }

    @Override
    public void restoreBackup(Map<DbUserState, UserState> userStates) {
        serverConditionService.deactivateAll();
        for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
            try {
                DbLevel dbLevel = getDbLevel(entry.getValue());
                activateConditions(entry.getValue(), dbLevel, entry.getKey().getLevelTasksDone());
                serverConditionService.restoreBackup(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("Can not restore user: " + userStates, e);
            }
        }
    }

    @Override
    public InvalidLevelState createInvalidLevelState() {
        if (isStartRealGame()) {
            return new InvalidLevelState(null);
        } else {
            return new InvalidLevelState(getDefaultLevelTaskId());
        }
    }
}
