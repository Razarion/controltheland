package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 23.01.2012
 * Time: 11:37:41
 */
public class TestUserGuidanceServiceImpl extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private XpService xpService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void noBaseAllowed() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().getRealGameInfo(START_UID_1, null);
            Assert.fail("InvalidLevelStateException expected");
        } catch (InvalidLevelStateException invalidLevelStateException) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void levelUp() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        // Verify first level
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        int levelTaskId = userGuidanceService.getDefaultLevelTaskId();
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED_ID, levelTaskId);
        SimulationInfo simulationInfo = getMovableService().getSimulationGameInfo(levelTaskId);
        Assert.assertNotNull(simulationInfo);
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, userGuidanceService.getDbLevelCms().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        // Level Up
        beginHttpRequestAndOpenSessionInViewFilter();
        GameFlow gameFlow = userGuidanceService.onTutorialFinished(levelTaskId);
        Assert.assertEquals(GameFlow.Type.START_REAL_GAME, gameFlow.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        // Verify second level
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(TEST_LEVEL_2_REAL, userGuidanceService.getDbLevelCms().getNumber());
        Assert.assertTrue(userGuidanceService.isStartRealGame());
        createBase(new Index(1000,1000));
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertNotNull(realGameInfo);
        Assert.assertEquals(1, planetSystemService.getServerPlanetServices().getBaseService().getBases().size());
        List<DbLevelTask> levelTask = new ArrayList<>(userGuidanceService.getDbLevelCms().getLevelTaskCrud().readDbChildren());
        Assert.assertEquals(2, levelTask.size());
        endHttpRequestAndOpenSessionInViewFilter();
        // Level Up
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID, Locale.ENGLISH);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_1_2_REAL_ID, Locale.ENGLISH);
        sendCollectCommand(getFirstSynItemId(TEST_HARVESTER_ITEM_ID), getFirstSynItemId(TEST_RESOURCE_ITEM_ID));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        // Verify third level
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(TEST_LEVEL_3_REAL, userGuidanceService.getDbLevelCms().getNumber());
        Assert.assertTrue(userGuidanceService.isStartRealGame());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void levelUpUserOfflinePassivelyAttacksOtherUnit() throws Exception {
        BaseItemType mockBaseItemType = EasyMock.createStrictMock(BaseItemType.class);
        EasyMock.expect(mockBaseItemType.getXpOnKilling()).andReturn(1000);

        SyncBaseItem mockSyncBaseItem = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockSyncBaseItem.getBaseItemType()).andReturn(mockBaseItemType);
        EasyMock.replay(mockBaseItemType, mockSyncBaseItem);

        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_2_REAL_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(500, 500));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Condition: 220 XP
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, userState.getDbLevelId());
        xpService.onItemKilled(userState.getBase().getSimpleBase(), mockSyncBaseItem, planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID));
        Thread.sleep(100);
        // TODO failed on 14.11.2012, 23.11.2012, 14.12.2012, 03.09.2013
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, userState.getDbLevelId());
    }

    @Test
    @DirtiesContext
    public void levelUpUserOfflineReward() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_2_REAL_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(500, 500));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Condition: 220 XP
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, userState.getDbLevelId());
        xpService.onReward(userState, 200);
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, userState.getDbLevelId());
        xpService.onReward(userState, 19);
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, userState.getDbLevelId());
        xpService.onReward(userState, 1);
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, userState.getDbLevelId());
    }

    @Test
    @DirtiesContext
    public void gameFlow() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Setup
        DbLevel dbSimLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbSimLevel1.setXp(2);
        dbSimLevel1.setNumber(1);
        DbLevelTask dbSimLevelTask11 = dbSimLevel1.getLevelTaskCrud().createDbChild();
        dbSimLevelTask11.setDbTutorialConfig(createTutorial1());
        dbSimLevelTask11.setXp(1);
        DbLevelTask dbSimLevelTask12 = dbSimLevel1.getLevelTaskCrud().createDbChild();
        dbSimLevelTask12.setDbTutorialConfig(createTutorial1());
        dbSimLevelTask12.setXp(1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbSimLevel1);

        DbLevel dbSimLevel2 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbSimLevel2.setXp(1);
        dbSimLevel2.setNumber(2);
        DbLevelTask dbSimLevelTask2 = dbSimLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask2.setDbTutorialConfig(createTutorial1());
        dbSimLevelTask2.setXp(1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbSimLevel2);

        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBaseItemType dbBaseItemType = createSimpleBuilding();
        dbPlanet.setStartItemType(dbBaseItemType);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.activate();

        DbLevel realGameLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        realGameLevel.setNumber(3);
        realGameLevel.setXp(Integer.MAX_VALUE);
        DbLevelTask dbLevelTask = realGameLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.setDbTutorialConfig(createTutorial1());
        realGameLevel.setDbPlanet(dbPlanet);
        userGuidanceService.getDbLevelCrud().updateDbChild(realGameLevel);
        userGuidanceService.activateLevels();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask11.getId(), userGuidanceService.getDefaultLevelTaskId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask11.getId(), userGuidanceService.getDefaultLevelTaskId());

        GameFlow gameFlow = userGuidanceService.onTutorialFinished(dbSimLevelTask11.getId());
        Assert.assertEquals(GameFlow.Type.START_NEXT_LEVEL_TASK_TUTORIAL, gameFlow.getType());
        Assert.assertEquals((int) dbSimLevelTask12.getId(), gameFlow.getNextTutorialLevelTaskId());
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask12.getId(), userGuidanceService.getDefaultLevelTaskId());

        gameFlow = userGuidanceService.onTutorialFinished(dbSimLevelTask12.getId());
        Assert.assertEquals(GameFlow.Type.START_NEXT_LEVEL_TASK_TUTORIAL, gameFlow.getType());
        Assert.assertEquals((int) dbSimLevelTask2.getId(), gameFlow.getNextTutorialLevelTaskId());
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask2.getId(), userGuidanceService.getDefaultLevelTaskId());

        gameFlow = userGuidanceService.onTutorialFinished(dbSimLevelTask2.getId());
        Assert.assertEquals(GameFlow.Type.START_REAL_GAME, gameFlow.getType());
        Assert.assertTrue(userGuidanceService.isStartRealGame());
        gameFlow = userGuidanceService.onTutorialFinished(dbLevelTask.getId());
        Assert.assertEquals(GameFlow.Type.START_REAL_GAME, gameFlow.getType());
        Assert.assertTrue(userGuidanceService.isStartRealGame());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getQuestOverview() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        //DbPlanet dbPlanet = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel.setXp(100);
        DbLevelTask dbLevelTask0 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask0.getI18nTitle().putString("dbLevelTask0");
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.getI18nTitle().putString("dbLevelTask1");
        dbLevelTask1.setDbTutorialConfig(dbTutorialConfig);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.getI18nTitle().putString("dbLevelTask2");
        DbLevelTask dbLevelTask3 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask3.getI18nTitle().putString("dbLevelTask3");
        dbLevelTask3.setDbTutorialConfig(dbTutorialConfig);
        DbLevelTask dbLevelTask4 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask4.getI18nTitle().putString("dbLevelTask4");
        DbLevelTask dbLevelTask5 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask5.getI18nTitle().putString("dbLevelTask5");
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState = userService.getUserState();
        QuestOverview questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(0, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(0, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(6, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask0", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(2).getTitle());
        Assert.assertEquals("dbLevelTask3", questOverview.getQuestInfos().get(3).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(4).getTitle());
        Assert.assertEquals("dbLevelTask5", questOverview.getQuestInfos().get(5).getTitle());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask0.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(1, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(0, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(5, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask3", questOverview.getQuestInfos().get(2).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(3).getTitle());
        Assert.assertEquals("dbLevelTask5", questOverview.getQuestInfos().get(4).getTitle());
        userGuidanceService.activateQuest(dbLevelTask5.getId(), Locale.ENGLISH);
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask5.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(0, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(4, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask3", questOverview.getQuestInfos().get(2).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(3).getTitle());
        userGuidanceService.activateQuest(dbLevelTask3.getId(), Locale.ENGLISH);
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask3.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(1, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(3, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(2).getTitle());
        userGuidanceService.activateQuest(dbLevelTask1.getId(), Locale.ENGLISH);
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask1.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(2, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(1).getTitle());
        userGuidanceService.activateQuest(dbLevelTask2.getId(), Locale.ENGLISH);
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask2.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(3, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(1, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(0).getTitle());
        userGuidanceService.activateQuest(dbLevelTask4.getId(), Locale.ENGLISH);
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask4.getId());
        questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(4, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(0, questOverview.getQuestInfos().size());
    }

    @Test
    @DirtiesContext
    public void setAndGetDoneDbLevelTasks() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, userGuidanceService.getDoneDbLevelTasks(userState).size());
        Collection<DbLevelTask> dbLevelTasksDone = new ArrayList<>();
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_1_3_REAL_ID));
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_2_3_REAL_ID));
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_3_3_SIMULATED_ID));
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_4_3_SIMULATED_ID));
        userGuidanceService.setDoneDbLevelTasks(dbLevelTasksDone, userState);
        Assert.assertEquals(4, userGuidanceService.getDoneDbLevelTasks(userState).size());
        Assert.assertTrue(userGuidanceService.getDoneDbLevelTasks(userState).contains(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_1_3_REAL_ID)));
        Assert.assertTrue(userGuidanceService.getDoneDbLevelTasks(userState).contains(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_2_3_REAL_ID)));
        Assert.assertTrue(userGuidanceService.getDoneDbLevelTasks(userState).contains(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_3_3_SIMULATED_ID)));
        Assert.assertTrue(userGuidanceService.getDoneDbLevelTasks(userState).contains(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_4_3_SIMULATED_ID)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        QuestOverview questOverview = userGuidanceService.getQuestOverview(Locale.ENGLISH);
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void setDoneDbLevelTasksFail() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<DbLevelTask> dbLevelTasksDone = new ArrayList<>();
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_1_2_REAL_ID));
        try {
            userGuidanceService.setDoneDbLevelTasks(dbLevelTasksDone, userState);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Quest does not belong to level: DbLevelTask{id=2, name='TEST_LEVEL_TASK_1_2_REAL_NAME}", e.getMessage());
        }
        Assert.assertEquals(0, userGuidanceService.getDoneDbLevelTasks(userState).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getLevelTaskState() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<DbLevelTask> dbLevelTasksDone = new ArrayList<>();
        dbLevelTasksDone.add(userGuidanceService.getDbLevelTask4Id(TEST_LEVEL_TASK_2_3_REAL_ID));
        userGuidanceService.setDoneDbLevelTasks(dbLevelTasksDone, userState);
        // Verify
        Assert.assertEquals(UserGuidanceService.QuestState.ACTIVE, userGuidanceService.getLevelTaskState(TEST_LEVEL_TASK_1_3_REAL_ID, userState));
        Assert.assertEquals(UserGuidanceService.QuestState.DONE, userGuidanceService.getLevelTaskState(TEST_LEVEL_TASK_2_3_REAL_ID, userState));
        Assert.assertEquals(UserGuidanceService.QuestState.OPEN, userGuidanceService.getLevelTaskState(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userState));
        Assert.assertEquals(UserGuidanceService.QuestState.OPEN, userGuidanceService.getLevelTaskState(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userState));
        userGuidanceService.setDoneDbLevelTasks(dbLevelTasksDone, userState);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getActiveQuest() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = userService.getUserState();
        userGuidanceService.promote(userState, TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, (int)userGuidanceService.getActiveQuest(userState).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }



}