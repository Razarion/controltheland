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
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: beat Date: 29.01.2010 Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService, ConditionServiceListener<UserState, Integer> {
    public static final String NO_MISSION_TARGET = "<center>There are no new mission targets.<br /><h1>Please check back later</h1></center>";
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private CrudRootServiceHelper<DbLevel> dbLevelCrud;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private XpService xpService;
    @Autowired
    private ServerUnlockService serverUnlockService;
    @Autowired
    private InvitationService invitationService;
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);
    private Map<Integer, LevelScope> levelScopes = new HashMap<>();
    private final Map<UserState, Collection<Integer>> levelTaskDone = new HashMap<>();
    private final Map<UserState, Integer> activeQuestIds = new HashMap<>();

    @PostConstruct
    public void init() {
        dbLevelCrud.init(DbLevel.class, "orderIndex", true, true, null);
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

        // Send XP
        XpPacket xpPacket = new XpPacket();
        xpPacket.setXp(0);
        xpPacket.setXp2LevelUp(dbNextLevel.getXp());
        planetSystemService.sendPacket(userState, xpPacket);
        // Level
        LevelPacket levelPacket = new LevelPacket();
        levelPacket.setLevel(getLevelScope(dbNextLevel.getId()));
        planetSystemService.sendPacket(userState, levelPacket);

        // Prepare next level
        activateConditions4Level(userState, dbNextLevel);
        // Post processing
        userState.setXp(0);
        activateNextUnDoneLevelTask(userState, null, userState.getLocale());
        invitationService.onLevelUp(userState, dbNextLevel);
    }

    private void activateNextUnDoneLevelTask(UserState userState, DbLevelTask oldLevelTaskId, Locale locale) {
        DbLevelTask dbLevelTask = getNextUnDoneLevelTask(userState, oldLevelTaskId);
        if (dbLevelTask == null) {
            return;
        }
        activateQuest(userState, dbLevelTask, locale);
    }

    private DbLevelTask getNextUnDoneLevelTask(UserState userState, DbLevelTask oldLevelTask) {
        DbLevel dbLevel = getDbLevel(userState);
        Collection<Integer> levelTaskDone = this.levelTaskDone.get(userState);
        List<DbLevelTask> dbLevelTasks = dbLevel.getLevelTaskCrud().readDbChildren();
        if (oldLevelTask != null) {
            int index = dbLevelTasks.indexOf(oldLevelTask);
            if (index < 0) {
                throw new IllegalArgumentException("Unknown level task: " + oldLevelTask);
            }
            if (index + 1 < dbLevelTasks.size()) {
                for (DbLevelTask dbLevelTask : dbLevelTasks.subList(index + 1, dbLevelTasks.size())) {
                    if (levelTaskDone != null && levelTaskDone.contains(dbLevelTask.getId())) {
                        continue;
                    }
                    return dbLevelTask;
                }
            }
            return getNextUnDoneLevelTask(userState, null);
        } else {
            for (DbLevelTask dbLevelTask : dbLevelTasks) {
                if (levelTaskDone != null && levelTaskDone.contains(dbLevelTask.getId())) {
                    continue;
                }
                return dbLevelTask;
            }
        }
        return null;
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
        UserState userState = userService.getUserState();
        serverConditionService.onTutorialFinished(userState, levelTaskId);
        DbLevel newLevel = getDbLevel();

        if (newLevel.hasDbPlanet()) {
            return new GameFlow(GameFlow.Type.START_REAL_GAME, null);
        } else {
            DbLevelTask dbLevelTask = newLevel.getFirstTutorialLevelTask(levelTaskDone.get(userState));
            return new GameFlow(GameFlow.Type.START_NEXT_LEVEL_TASK_TUTORIAL, dbLevelTask.getId());
        }
    }

    private void activateConditions4Level(UserState userState, DbLevel dbLevel) {
        ConditionConfig levelCondition = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(dbLevel.getXp()), null, null, false);
        serverConditionService.activateCondition(levelCondition, userState, null);
    }

    private void cleanupConditions(UserState userState) {
        serverConditionService.deactivateAllActorConditions(userState);
        synchronized (levelTaskDone) {
            levelTaskDone.remove(userState);
        }
        synchronized (activeQuestIds) {
            activeQuestIds.remove(userState);
        }
    }

    private void handleLevelTaskCompletion(UserState userState, int levelTaskId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, levelTaskId);
        synchronized (levelTaskDone) {
            addLevelTaskDone(userState, dbLevelTask);
        }
        removeActiveQuest(userState, dbLevelTask);

        DbLevel oldLevel = getDbLevel(userState);
        // Communication
        log.debug("Level Task completed. userState: " + userState + " " + dbLevelTask);
        historyService.addLevelTaskCompletedEntry(userState, dbLevelTask);
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setCompleted();
        planetSystemService.sendPacket(userState, levelTaskPacket);
        // Rewards
        if (dbLevelTask.getXp() > 0) {
            xpService.onReward(userState, dbLevelTask.getXp());
        }
        Base base = userState.getBase();
        if (base != null && dbLevelTask.getMoney() > 0) {
            base.getPlanet().getPlanetServices().getBaseService().depositResource(dbLevelTask.getMoney(), base.getSimpleBase());
            base.getPlanet().getPlanetServices().getBaseService().sendAccountBaseUpdate(base.getSimpleBase());
        }

        // Activate next quest / mission
        if (oldLevel.equals(getDbLevel(userState))) {
            activateNextUnDoneLevelTask(userState, dbLevelTask, userState.getLocale());
        }
    }

    private void addLevelTaskDone(UserState userState, DbLevelTask dbLevelTask) {
        Collection<Integer> tasks = levelTaskDone.get(userState);
        if (tasks == null) {
            tasks = new ArrayList<>();
            levelTaskDone.put(userState, tasks);
        }
        tasks.add(dbLevelTask.getId());
    }

    private void setActiveQuest(UserState userState, DbLevelTask dbLevelTask) {
        synchronized (activeQuestIds) {
            activeQuestIds.put(userState, dbLevelTask.getId());
        }
    }

    private void removeActiveQuest(UserState userState, DbLevelTask dbLevelTask) {
        synchronized (activeQuestIds) {
            Integer taskId = activeQuestIds.remove(userState);
            if (taskId == null) {
                throw new IllegalArgumentException("DbLevelTask was not active before: " + dbLevelTask + " userState: " + userState);
            }
            if ((int) taskId != dbLevelTask.getId()) {
                throw new IllegalArgumentException("DbLevelTask was not active before: " + dbLevelTask + " userState: " + userState + ". Active level task id: " + taskId);
            }
        }
    }

    @Override
    public void onRemoveUserState(UserState userState) {
        cleanupConditions(userState);
    }

    @Override
    public void setLevelForNewUser(UserState userState) {
        List<DbLevel> levels = new ArrayList<>(dbLevelCrud.readDbChildren());
        if (levels.isEmpty()) {
            throw new IllegalStateException("No level defined");
        }
        DbLevel dbLevel = new ArrayList<>(dbLevelCrud.readDbChildren()).get(0);
        userState.setDbLevelId(dbLevel.getId());
        activateNextUnDoneLevelTask(userState, null, userState.getLocale());
        activateConditions4Level(userState, dbLevel);
    }

    private DbLevel getNextDbLevel(DbLevel dbLevel) {
        List<DbLevel> dbLevels = new ArrayList<>(dbLevelCrud.readDbChildren());
        int index = dbLevels.indexOf(dbLevel);
        if (index < 0) {
            throw new IllegalArgumentException("DbLevel can not be found in own DbPlanet: " + dbLevel);
        }
        index++;
        if (dbLevels.size() > index) {
            return dbLevels.get(index);
        } else {
            throw new IllegalArgumentException("This is the last level" + dbLevel);
        }
    }

    @Override
    public DbLevel getPreviousDbLevel(DbLevel dbLevel) {
        List<DbLevel> dbLevels = new ArrayList<>(dbLevelCrud.readDbChildren());
        int index = dbLevels.indexOf(dbLevel);
        if (index < 0) {
            throw new IllegalArgumentException("DbLevel can not be found in own DbPlanet: " + dbLevel);
        }
        index--;
        if (index < 0) {
            throw new IllegalArgumentException("No previous level for: " + dbLevel);
        }
        if (dbLevels.size() > index) {
            return dbLevels.get(index);
        } else {
            throw new IllegalArgumentException("No previous level for: " + dbLevel);
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
            ExceptionHandler.handleException("----DbLevel is null----");
        }
        return dbLevel;
    }

    @Override
    public DbLevel getDbLevel(UserState userState) {
        return getDbLevel(userState.getDbLevelId());
    }

    @Override
    public DbLevel getDbLevel(User user) {
        return getDbLevel(userService.getUserState(user));
    }

    @Override
    public DbLevel getDbLevel(int levelId) {
        return dbLevelCrud.readDbChild(levelId);
    }

    public LevelScope getLevelScope(int dbLevelId) {
        LevelScope levelScope = levelScopes.get(dbLevelId);
        if (levelScope == null) {
            throw new IllegalArgumentException("No LevelScope for dbLevelId: " + dbLevelId + ". Did you forget to activate the levels?");
        }
        return levelScope;
    }

    @Override
    public LevelScope getLevelScope(UserState userState) {
        return getLevelScope(userState.getDbLevelId());
    }

    @Override
    public LevelScope getLevelScope() {
        return getLevelScope(userService.getUserState().getDbLevelId());
    }

    @Override
    public LevelScope getLevelScope(SimpleBase simpleBase) {
        UserState userState = planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase);
        return getLevelScope(userState.getDbLevelId());
    }

    @Override
    public boolean isStartRealGame() {
        return getDbLevel().hasDbPlanet();
    }

    @Override
    public int getDefaultLevelTaskId() {
        DbLevel dbLevel = getDbLevel();
        if (dbLevel.hasDbPlanet()) {
            throw new IllegalArgumentException("If real game is required, no default tutorial LevelTask is is available");
        }
        return dbLevel.getFirstTutorialLevelTask(levelTaskDone.get(userService.getUserState())).getId();
    }

    @Override
    public void activateLevels() throws LevelActivationException {
        levelScopes.clear();
        for (DbLevel dbLevel : dbLevelCrud.readDbChildren()) {
            levelScopes.put(dbLevel.getId(), dbLevel.createLevelScope());
        }
    }

    @Override
    public CrudRootServiceHelper<DbLevel> getDbLevelCrud() {
        return dbLevelCrud;
    }

    @Override
    public QuestOverview getQuestOverview(Locale locale) {
        List<QuestInfo> levelQuests = new ArrayList<>();
        UserState userState = userService.getUserState();
        Collection<Integer> userLevelTaskDone = levelTaskDone.get(userState);
        int questsDone = 0;
        int totalQuests = 0;
        int missionsDone = 0;
        int totalMissions = 0;
        for (DbLevelTask dbLevelTask : getDbLevel().getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.isDbTutorialConfig()) {
                totalMissions++;
            } else {
                totalQuests++;
            }
            QuestInfo questInfo = dbLevelTask.createQuestInfo(locale);
            if (userLevelTaskDone == null || !userLevelTaskDone.contains(dbLevelTask.getId())) {
                levelQuests.add(questInfo);
            } else {
                if (dbLevelTask.isDbTutorialConfig()) {
                    missionsDone++;
                } else {
                    questsDone++;
                }
            }
        }
        QuestOverview questOverview = new QuestOverview();
        questOverview.setQuestInfos(levelQuests);
        questOverview.setQuestsDone(questsDone);
        questOverview.setTotalQuests(totalQuests);
        questOverview.setMissionsDone(missionsDone);
        questOverview.setTotalMissions(totalMissions);
        return questOverview;
    }

    @Override
    public void createAndAddBackup(DbUserState dbUserState, UserState userState) {
        DbLevel dbLevel = getDbLevel(userState);
        Collection<DbLevelTask> tasksDone = new ArrayList<>();
        synchronized (levelTaskDone) {
            Collection<Integer> taskIds = levelTaskDone.get(userState);
            if (taskIds != null) {
                for (Integer taskId : taskIds) {
                    try {
                        tasksDone.add(dbLevel.getLevelTaskCrud().readDbChild(taskId));
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
        dbUserState.setLevelTasksDone(tasksDone);
        Integer taskId;
        synchronized (activeQuestIds) {
            taskId = activeQuestIds.get(userState);
        }
        if (taskId != null) {
            dbUserState.setActiveQuest(dbLevel.getLevelTaskCrud().readDbChild(taskId));
        }
        serverConditionService.createBackup(dbUserState, userState);
    }

    @Override
    public void restoreBackup(Map<DbUserState, UserState> userStates) {
        serverConditionService.deactivateAll();
        synchronized (levelTaskDone) {
            levelTaskDone.clear();
        }
        synchronized (activeQuestIds) {
            levelTaskDone.clear();
            activeQuestIds.clear();
            for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
                try {
                    if (entry.getKey().getLevelTasksDone() != null) {
                        for (DbLevelTask taskDone : entry.getKey().getLevelTasksDone()) {
                            addLevelTaskDone(entry.getValue(), taskDone);
                        }
                    }
                    activateConditionsRestore(entry.getValue(), getDbLevel(entry.getValue()), entry.getKey().getActiveQuest());
                    serverConditionService.restoreBackup(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    log.error("UserGuidanceServiceImpl: Can not restore user: " + entry.getValue().getUser(), e);
                }
            }
        }
    }

    private void activateConditionsRestore(UserState userState, DbLevel dbLevel, DbLevelTask activeQuest) {
        ConditionConfig levelCondition = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(dbLevel.getXp()), null, null, false);
        serverConditionService.activateCondition(levelCondition, userState, null);

        if (activeQuest != null) {
            serverConditionService.activateCondition(activeQuest.createConditionConfig(serverItemTypeService, null), userState, activeQuest.getId());
            setActiveQuest(userState, activeQuest);
        }
    }

    @Override
    public InvalidLevelStateException createInvalidLevelState() {
        if (isStartRealGame()) {
            return new InvalidLevelStateException(null);
        } else {
            return new InvalidLevelStateException(getDefaultLevelTaskId());
        }
    }

    @Override
    public void activateQuest(int dbLevelTaskId, Locale locale) {
        UserState userState = userService.getUserState();
        DbLevelTask dbLevelTask = getDbLevel().getLevelTaskCrud().readDbChild(dbLevelTaskId);
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTaskId)) {
            throw new IllegalArgumentException("DbLevelTask already done: " + dbLevelTask);
        }

        Integer activeQuestId = activeQuestIds.get(userService.getUserState());
        // Deactivate old quest level task
        if (activeQuestId != null) {
            if (activeQuestId == dbLevelTaskId) {
                // Do not activate same quest again
                return;
            }
            deactivateLevelTask(getDbLevel().getLevelTaskCrud().readDbChild(activeQuestId));
        }
        activateQuest(userState, dbLevelTask, locale);
    }

    @Override
    public void onQuestUnlocked(int dbLevelTaskId) {
        UserState userState = userService.getUserState();
        DbLevelTask dbLevelTask = getDbLevel().getLevelTaskCrud().readDbChild(dbLevelTaskId);
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTaskId)) {
            throw new IllegalArgumentException("DbLevelTask has already been done: " + dbLevelTask);
        }
        if (activeQuestIds.containsKey(userState) && activeQuestIds.get(userState) == dbLevelTaskId) {
            serverConditionService.activateCondition(dbLevelTask.createConditionConfig(serverItemTypeService, userState.getLocale()), userState, dbLevelTask.getId());
            historyService.addLevelTaskActivated(userState, dbLevelTask);
            sendLevelTaskPacket(userState, dbLevelTask, dbLevelTask.createQuestInfo(userState.getLocale()));
        }
    }

    private void activateQuest(UserState userState, DbLevelTask dbLevelTask, Locale locale) {
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTask.getId())) {
            throw new IllegalArgumentException("DbLevelTask already done: " + dbLevelTask);
        }
        if (activeQuestIds.containsKey(userState)) {
            throw new IllegalArgumentException("DbLevelTask already activated: " + activeQuestIds.containsKey(userState) + ". Can not activate new level task: " + dbLevelTask);
        }
        QuestInfo questInfo = dbLevelTask.createQuestInfo(locale);
        if (!serverUnlockService.isQuestLocked(questInfo, userState)) {
            serverConditionService.activateCondition(dbLevelTask.createConditionConfig(serverItemTypeService, locale), userState, dbLevelTask.getId());
            historyService.addLevelTaskActivated(userState, dbLevelTask);
        }
        setActiveQuest(userState, dbLevelTask);
        sendLevelTaskPacket(userState, dbLevelTask, questInfo);
    }

    private void sendLevelTaskPacket(UserState userState, DbLevelTask dbLevelTask, QuestInfo questInfo) {
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setQuestInfo(questInfo);
        if (!dbLevelTask.isDbTutorialConfig() && !serverUnlockService.isQuestLocked(questInfo, userState)) {
            levelTaskPacket.setQuestProgressInfo(serverConditionService.getQuestProgressInfo(userState, dbLevelTask.getId()));
        }
        planetSystemService.sendPacket(userState, levelTaskPacket);
    }

    private void deactivateLevelTask(DbLevelTask dbLevelTask) {
        UserState userState = userService.getUserState();
        if (levelTaskDone.containsKey(userState) && levelTaskDone.get(userState).contains(dbLevelTask.getId())) {
            throw new IllegalArgumentException("DbLevelTask already done: " + dbLevelTask);
        }
        if (!activeQuestIds.containsKey(userState) || activeQuestIds.get(userState) != (int) dbLevelTask.getId()) {
            throw new IllegalArgumentException("DbLevelTask was not active: " + dbLevelTask + ". Active level task id: " + activeQuestIds.containsKey(userState));
        }
        serverConditionService.deactivateActorCondition(userState, dbLevelTask.getId());
        removeActiveQuest(userState, dbLevelTask);
        historyService.addLevelTaskDeactivated(userState, dbLevelTask);
    }

    @Override
    public void fillRealGameInfo(RealGameInfo realGameInfo, Locale locale) {
        DbLevel dbLevel = getDbLevel();
        UserState userState = userService.getUserState();
        // Level task
        Integer activeLevelTaskId = activeQuestIds.get(userState);
        if (activeLevelTaskId != null) {
            DbLevelTask activeTask = dbLevel.getLevelTaskCrud().readDbChild(activeLevelTaskId);
            LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
            QuestInfo questInfo = activeTask.createQuestInfo(locale);
            levelTaskPacket.setQuestInfo(questInfo);
            if (!activeTask.isDbTutorialConfig() && !serverUnlockService.isQuestLocked(questInfo, userState)) {
                levelTaskPacket.setQuestProgressInfo(serverConditionService.getQuestProgressInfo(userState, activeTask.getId()));
            }
            realGameInfo.setLevelTaskPacket(levelTaskPacket);
        }
        // Xp
        XpPacket xpPacket = new XpPacket();
        xpPacket.setXp(userState.getXp());
        xpPacket.setXp2LevelUp(dbLevel.getXp());
        realGameInfo.setXpPacket(xpPacket);
        // Level
        realGameInfo.setLevel(getLevelScope());
    }

    @Override
    public int getXp2LevelUp(UserState userState) {
        return getLevelScope(userState.getDbLevelId()).getXp2LevelUp();
    }

    @Override
    public DbLevelTask getDbLevelTask4Id(int questId) {
        DbLevelTask dbLevelTask = (DbLevelTask) sessionFactory.getCurrentSession().get(DbLevelTask.class, questId);
        if (dbLevelTask == null) {
            throw new IllegalArgumentException("No DbLevelTask for questId: " + questId);
        }
        return dbLevelTask;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public Collection<DbLevelTask> getDoneDbLevelTasks(UserState userState) {
        Collection<DbLevelTask> dbLevelTasksDone = new ArrayList<>();
        synchronized (levelTaskDone) {
            Collection<Integer> levelDoneIds = levelTaskDone.get(userState);
            if (levelDoneIds != null) {
                for (Integer levelDoneId : levelDoneIds) {
                    dbLevelTasksDone.add(getDbLevelTask4Id(levelDoneId));
                }
            }
        }
        return dbLevelTasksDone;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void setDoneDbLevelTasks(Collection<DbLevelTask> dbLevelTasksDone, UserState userState) {
        synchronized (levelTaskDone) {
            Collection<Integer> levelDoneIds = levelTaskDone.get(userState);
            if (levelDoneIds == null) {
                levelDoneIds = new ArrayList<>();
                levelTaskDone.put(userState, levelDoneIds);
            }
            levelDoneIds.clear();
            Collection<DbLevelTask> availableLevelTasks = getDbLevel(userState).getLevelTaskCrud().readDbChildren();
            for (DbLevelTask dbLevelTaskDone : dbLevelTasksDone) {
                if (!availableLevelTasks.contains(dbLevelTaskDone)) {
                    throw new IllegalArgumentException("Quest does not belong to level: " + dbLevelTaskDone);
                }
                levelDoneIds.add(dbLevelTaskDone.getId());
            }
        }
    }

    @Override
    public QuestState getLevelTaskState(int levelTaskId, UserState userState) {
        synchronized (levelTaskDone) {
            Integer activeQuestId = activeQuestIds.get(userState);
            if (activeQuestId != null && activeQuestId == levelTaskId) {
                return QuestState.ACTIVE;
            }
            Collection<Integer> levelTasksDone = levelTaskDone.get(userState);
            if (levelTasksDone != null && levelTasksDone.contains(levelTaskId)) {
                return QuestState.DONE;
            } else {
                return QuestState.OPEN;
            }

        }
    }

    @Override
    public DbLevelTask getActiveQuest(UserState userState) {
        Integer questId = activeQuestIds.get(userState);
        if (questId == null) {
            return null;
        }
        return getDbLevelTask4Id(questId);
    }

}
