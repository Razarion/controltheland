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
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.LevelStatePacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
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
 * User: beat Date: 29.01.2010 Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService, ConditionServiceListener<UserState, Integer> {
    public static final String NO_MISSION_TARGET = "<center>There are no new mission targets.<br /><h1>Please check back later</h1></center>";
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
    private SessionFactory sessionFactory;
    @Autowired
    private ItemService itemService;
    @Autowired
    private XpService xpService;
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);
    private Map<Integer, LevelScope> levelScopes = new HashMap<>();
    private final Map<UserState, Collection<Integer>> levelTaskDone = new HashMap<>();
    private final Map<UserState, Collection<Integer>> levelTaskActive = new HashMap<>();

    @PostConstruct
    public void init() {
        crudQuestHub.init(DbQuestHub.class, "orderIndex", true, true, null);
        serverConditionService.setConditionServiceListener(this);
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            activateLevels();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
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
        activateConditions4Level(userState, dbNextLevel);
        // Send level update packet
        if (baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelStatePacket levelPacket = new LevelStatePacket();
            levelPacket.setLevel(getLevelScope(dbNextLevel.getId()));
            levelPacket.setXp(0);
            levelPacket.setXp2LevelUp(dbNextLevel.getXp());
            levelPacket.setQuestsDone(0);
            levelPacket.setTotalQuests(dbNextLevel.getQuestCount());
            levelPacket.setMissionsDone(0);
            levelPacket.setTotalMissions(dbNextLevel.getMissionCount());
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

    private void activateConditions4Level(UserState userState, DbLevel dbLevel) {
        ConditionConfig levelCondition = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(null, dbLevel.getXp(), null));
        serverConditionService.activateCondition(levelCondition, userState, null);
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() != null && (!levelTaskDone.containsKey(userState) || levelTaskDone.get(userState).contains(dbLevelTask.getId()))) {
                serverConditionService.activateCondition(dbLevelTask.createConditionConfig(itemService), userState, dbLevelTask.getId());
                addLevelTaskActive(userState, dbLevelTask);
            }
        }
    }

    private void cleanupConditions(UserState userState) {
        serverConditionService.deactivateAllActorConditions(userState);
        synchronized (levelTaskDone) {
            levelTaskDone.remove(userState);
        }
        synchronized (levelTaskActive) {
            levelTaskActive.remove(userState);
        }
    }

    private void handleLevelTaskCompletion(UserState userState, int levelTaskId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, levelTaskId);
        synchronized (levelTaskDone) {
            addLevelTaskDone(userState, dbLevelTask);
        }
        removeLevelTaskActive(userState, dbLevelTask);

        // Communication
        log.debug("Level Task completed. userState: " + userState + " " + dbLevelTask);
        historyService.addLevelTaskCompletedEntry(userState, dbLevelTask);
        Base base = baseService.getBase(userState);
        if (base != null) {
            LevelStatePacket levelPacket = new LevelStatePacket();
            addMissionQuestCount(levelPacket, userState, dbLevelTask.getParent());
            levelPacket.setMissionQuestCompleted(true);
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
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

    private void addMissionQuestCount(LevelStatePacket levelPacket, UserState userState, DbLevel dbLevel) {
        int missionCount = 0;
        int questCount = 0;
        synchronized (levelTaskDone) {
            Collection<Integer> levelTaskIds = levelTaskDone.get(userState);
            if (levelTaskIds != null) {
                for (Integer taskDoneId : levelTaskIds) {
                    if (dbLevel.getLevelTaskCrud().readDbChild(taskDoneId).getDbTutorialConfig() != null) {
                        missionCount++;
                    } else {
                        questCount++;
                    }
                }
            }
        }
        levelPacket.setQuestsDone(questCount);
        levelPacket.setTotalQuests(dbLevel.getQuestCount());
        levelPacket.setMissionsDone(missionCount);
        levelPacket.setTotalMissions(dbLevel.getMissionCount());
    }

    private void addLevelTaskDone(UserState userState, DbLevelTask dbLevelTask) {
        Collection<Integer> tasks = levelTaskDone.get(userState);
        if (tasks == null) {
            tasks = new ArrayList<>();
            levelTaskDone.put(userState, tasks);
        }
        tasks.add(dbLevelTask.getId());
    }

    private void addLevelTaskActive(UserState userState, DbLevelTask dbLevelTask) {
        synchronized (levelTaskActive) {
            Collection<Integer> tasks = levelTaskActive.get(userState);
            if (tasks == null) {
                tasks = new ArrayList<>();
                levelTaskActive.put(userState, tasks);
            }
            tasks.add(dbLevelTask.getId());
        }
    }

    private void removeLevelTaskActive(UserState userState, DbLevelTask dbLevelTask) {
        synchronized (levelTaskActive) {
            Collection<Integer> tasks = levelTaskActive.get(userState);
            if (tasks == null) {
                log.warn("Level task was not on the active list " + dbLevelTask + " userState: " + userState);
                return;
            }
            tasks.remove(dbLevelTask.getId());
            if (tasks.isEmpty()) {
                levelTaskActive.remove(userState);
            }
        }
    }

    @Override
    public void onRemoveUserState(UserState userState) {
        cleanupConditions(userState);
    }

    @Override
    public void setLevelForNewUser(UserState userState) {
        DbLevel dbLevel = new ArrayList<>(crudQuestHub.readDbChildren()).get(0).getLevelCrud().readDbChildren().get(0);
        userState.setDbLevelId(dbLevel.getId());
        activateConditions4Level(userState, dbLevel);
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
            List<DbQuestHub> dbQuestHubs = new ArrayList<>(crudQuestHub.readDbChildren());
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
        DbLevel dbLevel = (DbLevel) sessionFactory.getCurrentSession().get(DbLevel.class, userService.getUserState().getDbLevelId());
        if (dbLevel == null) {
            log.error("----DbLevel is null----");
            log.error("session: " + userService.getUserState().getSessionId());
        }
        return dbLevel;
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
        List<LevelQuest> levelQuests = new ArrayList<>();
        UserState userState = userService.getUserState();
        DbLevelTask activeTask = null;
        Collection<Integer> activeTaskIds = levelTaskActive.get(userState);
        if (activeTaskIds != null)
            for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
                if (dbLevelTask.getDbTutorialConfig() == null && activeTaskIds.contains(dbLevelTask.getId())) {
                    activeTask = dbLevelTask;
                }
            }

        Collection<Integer> userLevelTaskDone = levelTaskDone.get(userState);
        for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() == null) {
                levelQuests.add(new LevelQuest(dbLevelTask,
                        userLevelTaskDone != null && userLevelTaskDone.contains(dbLevelTask.getId()),
                        activeTask != null && dbLevelTask.equals(activeTask),
                        activeTask != null && !dbLevelTask.equals(activeTask)));
            }
        }

        return new ReadonlyListContentProvider<>(levelQuests);
    }

    @Override
    public ContentProvider<LevelQuest> getMercenaryMissionCms() {
        List<LevelQuest> levelQuests = new ArrayList<>();
        UserState userState = userService.getUserState();
        Collection<Integer> userLevelTaskDone = levelTaskDone.get(userState);
        for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() != null) {
                levelQuests.add(new LevelQuest(dbLevelTask, userLevelTaskDone != null && userLevelTaskDone.contains(dbLevelTask.getId()), false, false));
            }
        }
        return new ReadonlyListContentProvider<>(levelQuests);
    }

    @Override
    public void createAndAddBackup(DbUserState dbUserState, UserState userState) {
        DbLevel dbLevel = getDbLevel(userState);
        Collection<DbLevelTask> tasksDone = new ArrayList<>();
        synchronized (levelTaskDone) {
            Collection<Integer> taskIds = levelTaskDone.get(userState);
            if (taskIds != null) {
                for (Integer taskId : taskIds) {
                    tasksDone.add(dbLevel.getLevelTaskCrud().readDbChild(taskId));
                }
            }
        }
        dbUserState.setLevelTasksDone(tasksDone);
        Collection<DbLevelTask> tasksActive = new ArrayList<>();
        synchronized (levelTaskActive) {
            Collection<Integer> taskIds = levelTaskActive.get(userState);
            if (taskIds != null) {
                for (Integer taskId : taskIds) {
                    tasksActive.add(dbLevel.getLevelTaskCrud().readDbChild(taskId));
                }
            }
        }
        dbUserState.setLevelTasksActive(tasksActive);
        serverConditionService.createBackup(dbUserState, userState);
    }

    @Override
    public void restoreBackup(Map<DbUserState, UserState> userStates) {
        serverConditionService.deactivateAll();
        synchronized (levelTaskActive) {
            levelTaskActive.clear();
        }
        synchronized (levelTaskDone) {
            levelTaskDone.clear();
            levelTaskActive.clear();
            for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
                try {
                    if (entry.getKey().getLevelTasksDone() != null) {
                        for (DbLevelTask taskDone : entry.getKey().getLevelTasksDone()) {
                            addLevelTaskDone(entry.getValue(), taskDone);
                        }
                    }
                    activateConditionsRestore(entry.getValue(), getDbLevel(entry.getValue()), entry.getKey().getLevelTasksActive());
                    serverConditionService.restoreBackup(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    log.error("Can not restore user: " + entry.getValue().getUser(), e);
                }
            }
        }
    }

    private void activateConditionsRestore(UserState userState, DbLevel dbLevel, Collection<DbLevelTask> levelTasksActive) {
        ConditionConfig levelCondition = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(null, dbLevel.getXp(), null));
        serverConditionService.activateCondition(levelCondition, userState, null);

        if (levelTasksActive != null) {
            for (DbLevelTask dbLevelTask : levelTasksActive) {
                serverConditionService.activateCondition(dbLevelTask.createConditionConfig(itemService), userState, dbLevelTask.getId());
                addLevelTaskActive(userState, dbLevelTask);
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

    @Override
    public void activateLevelTaskCms(int dbLevelTaskId) {
        DbLevel dbLevel = getDbLevel();
        DbLevelTask dbLevelTask;
        try {
            dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(dbLevelTaskId);
        } catch (NoSuchChildException e) {
            // It is may possible, that an old window from the previous level is still open or it's a crawler
            return;
        }
        if (dbLevelTask.getDbTutorialConfig() != null) {
            return;
        }
        UserState userState = userService.getUserState();
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTaskId)) {
            return;
        }
        if (levelTaskActive.containsKey(userState) && levelTaskActive.get(userState).contains(dbLevelTaskId)) {
            return;
        }
        serverConditionService.activateCondition(dbLevelTask.createConditionConfig(itemService), userState, dbLevelTaskId);
        addLevelTaskActive(userState, dbLevelTask);
        historyService.addLevelTaskActivated(userState, dbLevelTask);
        if (baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelStatePacket levelPacket = new LevelStatePacket();
            levelPacket.setActiveQuestLevelTaskId(dbLevelTask.getId());
            levelPacket.setActiveQuestTitle(dbLevelTask.getName());
            levelPacket.setActiveQuestProgress(serverConditionService.getProgressHtml(userState, dbLevelTaskId));
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
    }

    @Override
    public void deactivateLevelTaskCms(int dbLevelTaskId) {
        DbLevel dbLevel = getDbLevel();
        DbLevelTask dbLevelTask;
        try {
            dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(dbLevelTaskId);
        } catch (NoSuchChildException e) {
            // It is may possible, that an old window from the previous level is still open or it's a crawler
            return;
        }
        if (dbLevelTask.getDbTutorialConfig() != null) {
            return;
        }
        UserState userState = userService.getUserState();
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTaskId)) {
            return;
        }
        if (!levelTaskActive.containsKey(userState) || !levelTaskActive.get(userState).contains(dbLevelTaskId)) {
            return;
        }
        serverConditionService.deactivateActorConditions(userState, dbLevelTaskId);
        removeLevelTaskActive(userState, dbLevelTask);
        historyService.addLevelTaskDeactivated(userState, dbLevelTask);
        if (baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelStatePacket levelPacket = new LevelStatePacket();
            levelPacket.setQuestDeactivated(true);
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
    }

    @Override
    public void fillRealGameInfo(RealGameInfo realGameInfo) {
        DbLevel dbLevel = getDbLevel();
        UserState userState = userService.getUserState();
        LevelStatePacket levelStatePacket = new LevelStatePacket();
        levelStatePacket.setXp(userState.getXp());
        levelStatePacket.setXp2LevelUp(dbLevel.getXp());
        DbLevelTask activeTask = getActiveQuestTask(userState, dbLevel);
        if (activeTask != null) {
            levelStatePacket.setActiveQuestLevelTaskId(activeTask.getId());
            levelStatePacket.setActiveQuestTitle(activeTask.getName());
            levelStatePacket.setActiveQuestProgress(serverConditionService.getProgressHtml(userState, activeTask.getId()));
        }
        addMissionQuestCount(levelStatePacket, userState, dbLevel);
        levelStatePacket.setLevel(getLevelScope());
        realGameInfo.setLevelStatePacket(levelStatePacket);
    }

    private DbLevelTask getActiveQuestTask(UserState userState, DbLevel dbLevel) {
        for (DbLevelTask dbLevelTask : dbLevel.getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() == null && serverConditionService.hasConditionTrigger(userState, dbLevelTask.getId())) {
                return dbLevelTask;
            }
        }
        return null;
    }
}
