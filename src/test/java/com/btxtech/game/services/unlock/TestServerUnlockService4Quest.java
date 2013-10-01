package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 15.02.13
 * Time: 17:33
 */
public class TestServerUnlockService4Quest extends AbstractServiceTest {
    @Autowired
    private ServerUnlockService unlockService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerConditionService serverConditionService;

    @Test
    @DirtiesContext
    public void testActivateUnlockQuest() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(TEST_LEVEL_TASK_2_2_REAL_ID);
        dbLevelTask.setUnlockCrystals(8);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID, Locale.ENGLISH);
        // Build 2, but condition should not be fulfilled
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeBaseItemCount(TEST_PLANET_1_ID, 4);
        Assert.assertEquals(TEST_LEVEL_TASK_2_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Unlock quest
        getUserState().setCrystals(100);
        UnlockContainer unlockContainer = unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        Assert.assertTrue(unlockContainer.containsQuestId(TEST_LEVEL_TASK_2_2_REAL_ID));
        // Fulfill quest
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeBaseItemCount(TEST_PLANET_1_ID, 6);
        Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testActivateUnlockQuestInDeep() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(TEST_LEVEL_TASK_2_2_REAL_ID);
        dbLevelTask.setUnlockCrystals(8);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        getOrCreateBase();
        Thread.sleep(1000); // Wait for money packet
        clearPackets();
        // Activate
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID, Locale.ENGLISH);
        // Verify packet
        QuestInfo questInfo = new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", null, null, 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null, false, 8);
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setQuestInfo(questInfo);
        levelTaskPacket.setQuestProgressInfo(null);
        assertPackagesIgnoreSyncItemInfoAndClear(levelTaskPacket);
        // Verify condition not activate
        Assert.assertFalse(serverConditionService.hasConditionTrigger(getUserState(), dbLevelTask.getId()));
        // Unlock quest
        getUserState().setCrystals(100);
        UnlockContainer unlockContainer = unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        // Verify
        Assert.assertTrue(unlockContainer.containsQuestId(TEST_LEVEL_TASK_2_2_REAL_ID));
        // Verify packet
        questInfo = new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", null, null, 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null, false, 8);
        levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setQuestInfo(questInfo);
        QuestProgressInfo questProgressInfo = new QuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT);
        questProgressInfo.setAmount(new QuestProgressInfo.Amount(0, 2));
        levelTaskPacket.setQuestProgressInfo(questProgressInfo);
        assertPackagesIgnoreSyncItemInfoAndClear(levelTaskPacket);
        // Verify condition activate
        Assert.assertTrue(serverConditionService.hasConditionTrigger(getUserState(), dbLevelTask.getId()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUnlockActivateQuest() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(TEST_LEVEL_TASK_2_2_REAL_ID);
        dbLevelTask.setUnlockCrystals(8);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        // Unlock quest
        getUserState().setCrystals(100);
        UnlockContainer unlockContainer = unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        Assert.assertTrue(unlockContainer.containsQuestId(TEST_LEVEL_TASK_2_2_REAL_ID));
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID, Locale.ENGLISH);
        Assert.assertEquals(TEST_LEVEL_TASK_2_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Fulfill quest
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeBaseItemCount(TEST_PLANET_1_ID, 3);
        Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testUnlockActivateQuestInDeep() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(TEST_LEVEL_TASK_2_2_REAL_ID);
        dbLevelTask.setUnlockCrystals(8);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        getOrCreateBase();
        Thread.sleep(1000); // Wait for money packet
        clearPackets();
        // Unlock quest
        getUserState().setCrystals(100);
        UnlockContainer unlockContainer = unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        // Verify
        Assert.assertTrue(unlockContainer.containsQuestId(TEST_LEVEL_TASK_2_2_REAL_ID));
        assertPackagesIgnoreSyncItemInfoAndClear();
        Assert.assertFalse(serverConditionService.hasConditionTrigger(getUserState(), dbLevelTask.getId()));
        // Activate quest
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID, Locale.ENGLISH);
        // Verify
        Assert.assertTrue(serverConditionService.hasConditionTrigger(getUserState(), dbLevelTask.getId()));
        // Verify packet
        QuestInfo questInfo = new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", null, null, 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null, false, 8);
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setQuestInfo(questInfo);
        QuestProgressInfo questProgressInfo = new QuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT);
        questProgressInfo.setAmount(new QuestProgressInfo.Amount(0, 2));
        levelTaskPacket.setQuestProgressInfo(questProgressInfo);
        assertPackagesIgnoreSyncItemInfoAndClear(levelTaskPacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNoRazarion() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().readDbChild(TEST_LEVEL_TASK_2_2_REAL_ID);
        dbLevelTask.setUnlockCrystals(8);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        QuestInfo questInfo = dbLevelTask.createQuestInfo(Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(0);
        try {
            unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Not enough crystals to unlock: DbLevelTask{id=3, name='TEST_LEVEL_TASK_2_2_REAL_NAME} user: UserState: user=null", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getCrystals());
        Assert.assertTrue(unlockService.isQuestLocked(questInfo, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNotLocked() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(0);
        try {
            unlockService.unlockQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Quest can not be unlocked: DbLevelTask{id=3, name='TEST_LEVEL_TASK_2_2_REAL_NAME}", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isItemLocked() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setUnlockCrystals(1);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setUnlockCrystals(1);
        setupCondition(dbLevelTask2);
        DbLevelTask dbLevelTask3 = dbLevel.getLevelTaskCrud().createDbChild();
        setupCondition(dbLevelTask3);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        QuestInfo questInfo1 = dbLevelTask1.createQuestInfo(Locale.ENGLISH);
        QuestInfo questInfo2 = dbLevelTask2.createQuestInfo(Locale.ENGLISH);
        QuestInfo questInfo3 = dbLevelTask3.createQuestInfo(Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockQuest(dbLevelTask1.getId());
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getUserState()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getUserState()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo3, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupCondition(DbLevelTask dbLevelTask1) {
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(3);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask1.setDbConditionConfig(dbConditionConfig);
    }

    @Test
    @DirtiesContext
    public void testHistory() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setName("LT1");
        dbLevelTask1.setUnlockCrystals(10);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setName("LT2");
        dbLevelTask2.setUnlockCrystals(20);
        setupCondition(dbLevelTask2);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().setCrystals(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbHistoryElement> history = HibernateUtil.loadAll(getSessionFactory(), DbHistoryElement.class);
        DbHistoryElement dbHistoryElement = history.get(2);
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_QUEST, dbHistoryElement.getType());
        Assert.assertEquals("LT1", dbHistoryElement.getLevelTaskName());
        Assert.assertEquals(dbLevelTask1.getId(), dbHistoryElement.getLevelTaskId());
        Assert.assertEquals(90, (int) dbHistoryElement.getCrystals());
        Assert.assertEquals(10, (int) dbHistoryElement.getDeltaCrystals());
        dbHistoryElement = history.get(4);
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_QUEST, dbHistoryElement.getType());
        Assert.assertEquals("LT2", dbHistoryElement.getLevelTaskName());
        Assert.assertEquals(dbLevelTask2.getId(), dbHistoryElement.getLevelTaskId());
        Assert.assertEquals(70, (int) dbHistoryElement.getCrystals());
        Assert.assertEquals(20, (int) dbHistoryElement.getDeltaCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackend() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setName("LT1");
        dbLevelTask1.setUnlockCrystals(10);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setName("LT2");
        dbLevelTask2.setUnlockCrystals(20);
        setupCondition(dbLevelTask2);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        QuestInfo questInfo1 = dbLevelTask1.createQuestInfo(Locale.ENGLISH);
        QuestInfo questInfo2 = dbLevelTask2.createQuestInfo(Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Create Base
        // Assert empty
        Collection<DbLevelTask> unlockedQuests = unlockService.getUnlockQuests(getUserState());
        Assert.assertTrue(unlockedQuests.isEmpty());
        // Add item
        Thread.sleep(1000); // wait for AccountBalancePacket
        clearPackets();
        unlockService.setUnlockedQuestsBackend(Arrays.asList(userGuidanceService.getDbLevelTask4Id(dbLevelTask1.getId())), getUserState());
        // Verify
        UnlockContainerPacket unlockContainerPacket = new UnlockContainerPacket();
        UnlockContainer unlockContainer = new UnlockContainer();
        unlockContainer.setQuests(Arrays.asList(dbLevelTask1.getId()));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedQuests = unlockService.getUnlockQuests(getUserState());
        Assert.assertEquals(1, unlockedQuests.size());
        Assert.assertTrue(unlockedQuests.contains(userGuidanceService.getDbLevelTask4Id(dbLevelTask1.getId())));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getUserState()));
        // Add item
        clearPackets();
        unlockService.setUnlockedQuestsBackend(Arrays.asList(userGuidanceService.getDbLevelTask4Id(dbLevelTask1.getId()), userGuidanceService.getDbLevelTask4Id(dbLevelTask2.getId())), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setQuests(Arrays.asList(dbLevelTask1.getId(), dbLevelTask2.getId()));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedQuests = unlockService.getUnlockQuests(getUserState());
        Assert.assertEquals(2, unlockedQuests.size());
        Assert.assertTrue(unlockedQuests.contains(userGuidanceService.getDbLevelTask4Id(dbLevelTask1.getId())));
        Assert.assertTrue(unlockedQuests.contains(userGuidanceService.getDbLevelTask4Id(dbLevelTask2.getId())));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getUserState()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getUserState()));
        // Set no items
        clearPackets();
        unlockService.setUnlockedQuestsBackend(new ArrayList<DbLevelTask>(), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setQuests(new ArrayList<Integer>());
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedQuests = unlockService.getUnlockQuests(getUserState());
        Assert.assertEquals(0, unlockedQuests.size());
        Assert.assertTrue(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo1, getUserState()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
