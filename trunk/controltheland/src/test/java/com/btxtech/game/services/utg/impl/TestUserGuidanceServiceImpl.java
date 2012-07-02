package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.DbQuestHub;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 23.01.2012
 * Time: 11:37:41
 */
public class TestUserGuidanceServiceImpl extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private XpService xpService;
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void noBaseAllowed() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().getRealGameInfo();
            Assert.fail("InvalidLevelState expected");
        } catch (InvalidLevelState invalidLevelState) {

        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void levelUp() throws Exception {
        configureGameMultipleLevel();

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
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo();
        Assert.assertNotNull(realGameInfo);
        Assert.assertEquals(1, baseService.getBases().size());
        List<DbLevelTask> levelTask = new ArrayList<>(userGuidanceService.getDbLevelCms().getLevelTaskCrud().readDbChildren());
        Assert.assertEquals(2, levelTask.size());
        endHttpRequestAndOpenSessionInViewFilter();
        // Level Up
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_1_2_REAL_ID);
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
        EasyMock.expect(mockBaseItemType.getPrice()).andReturn(1000);

        SyncBaseItem mockSyncBaseItem = EasyMock.createStrictMock(SyncBaseItem.class);
        EasyMock.expect(mockSyncBaseItem.getBaseItemType()).andReturn(mockBaseItemType);
        EasyMock.replay(mockBaseItemType, mockSyncBaseItem);

        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
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
        xpService.onItemKilled(baseService.getBase(userState), mockSyncBaseItem);
        Thread.sleep(100);
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, userState.getDbLevelId());
    }

    @Test
    @DirtiesContext
    public void levelUpUserOfflineReward() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
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
        xpService.onReward(userState, 17);
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, userState.getDbLevelId());
        xpService.onReward(userState, 1);
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, userState.getDbLevelId());
    }

    @Test
    @DirtiesContext
    public void gameFlow() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbQuestHub startQuestHub = userGuidanceService.getCrudQuestHub().createDbChild();
        startQuestHub.setRealBaseRequired(false);
        DbLevel dbSimLevel1 = startQuestHub.getLevelCrud().createDbChild();
        dbSimLevel1.setXp(1);
        dbSimLevel1.setNumber(1);
        DbLevelTask dbSimLevelTask1 = dbSimLevel1.getLevelTaskCrud().createDbChild();
        dbSimLevelTask1.setDbTutorialConfig(createTutorial1());
        dbSimLevelTask1.setXp(1);

        DbLevel dbSimLevel2 = startQuestHub.getLevelCrud().createDbChild();
        dbSimLevel2.setXp(1);
        dbSimLevel2.setNumber(2);
        DbLevelTask dbSimLevelTask2 = dbSimLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask2.setDbTutorialConfig(createTutorial1());
        dbSimLevelTask2.setXp(1);
        userGuidanceService.getCrudQuestHub().updateDbChild(startQuestHub);

        DbQuestHub realGameQuestHub = userGuidanceService.getCrudQuestHub().createDbChild();
        DbBaseItemType dbBaseItemType = createSimpleBuilding();
        realGameQuestHub.setStartTerritory(setupSimpleTerritory("test", dbBaseItemType.getId()));
        realGameQuestHub.setStartItemType(dbBaseItemType);
        DbLevel realGameLevel = realGameQuestHub.getLevelCrud().createDbChild();
        realGameLevel.setNumber(3);
        realGameLevel.setXp(Integer.MAX_VALUE);
        DbLevelTask dbLevelTask = realGameLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.setDbTutorialConfig(createTutorial1());
        userGuidanceService.getCrudQuestHub().updateDbChild(realGameQuestHub);

        userGuidanceService.activateLevels();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask1.getId(), userGuidanceService.getDefaultLevelTaskId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userGuidanceService.isStartRealGame());
        Assert.assertEquals((int) dbSimLevelTask1.getId(), userGuidanceService.getDefaultLevelTaskId());

        GameFlow gameFlow = userGuidanceService.onTutorialFinished(dbSimLevelTask1.getId());
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
        DbQuestHub dbQuestHub = userGuidanceService.getCrudQuestHub().createDbChild();
        DbLevel dbLevel = dbQuestHub.getLevelCrud().createDbChild();
        dbLevel.setXp(100);
        DbLevelTask dbLevelTask0 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask0.setName("dbLevelTask0");
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setName("dbLevelTask1");
        dbLevelTask1.setDbTutorialConfig(dbTutorialConfig);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setName("dbLevelTask2");
        DbLevelTask dbLevelTask3 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask3.setName("dbLevelTask3");
        dbLevelTask3.setDbTutorialConfig(dbTutorialConfig);
        DbLevelTask dbLevelTask4 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask4.setName("dbLevelTask4");
        DbLevelTask dbLevelTask5 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask5.setName("dbLevelTask5");
        userGuidanceService.getCrudQuestHub().updateDbChild(dbQuestHub);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState = userService.getUserState();
        QuestOverview questOverview = userGuidanceService.getQuestOverview();
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
        questOverview = userGuidanceService.getQuestOverview();
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
        userGuidanceService.activateQuest(dbLevelTask5.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask5.getId());
        questOverview = userGuidanceService.getQuestOverview();
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(0, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(4, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask3", questOverview.getQuestInfos().get(2).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(3).getTitle());
        userGuidanceService.activateQuest(dbLevelTask3.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask3.getId());
        questOverview = userGuidanceService.getQuestOverview();
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(1, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(3, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask1", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(1).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(2).getTitle());
        userGuidanceService.activateQuest(dbLevelTask1.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask1.getId());
        questOverview = userGuidanceService.getQuestOverview();
        Assert.assertEquals(2, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(2, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask2", questOverview.getQuestInfos().get(0).getTitle());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(1).getTitle());
        userGuidanceService.activateQuest(dbLevelTask2.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask2.getId());
        questOverview = userGuidanceService.getQuestOverview();
        Assert.assertEquals(3, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(1, questOverview.getQuestInfos().size());
        Assert.assertEquals("dbLevelTask4", questOverview.getQuestInfos().get(0).getTitle());
        userGuidanceService.activateQuest(dbLevelTask4.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask4.getId());
        questOverview = userGuidanceService.getQuestOverview();
        Assert.assertEquals(4, questOverview.getQuestsDone());
        Assert.assertEquals(4, questOverview.getTotalQuests());
        Assert.assertEquals(2, questOverview.getMissionsDone());
        Assert.assertEquals(2, questOverview.getTotalMissions());
        Assert.assertEquals(0, questOverview.getQuestInfos().size());
    }
}
