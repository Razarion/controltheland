package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ItemTypePositionComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.LevelQuest;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 23.01.2012
 * Time: 11:37:41
 */
public class TestServerConditionServiceImpl extends AbstractServiceTest {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private MgmtService mgmtService;
    private UserState actor;
    private Integer identifier;
    private boolean passed = false;


    @Test
    @DirtiesContext
    public void multiplePlayers() throws Exception {
        configureRealGame();

        UserState userState1 = new UserState();
        userState1.setUser("TestUser1");
        Base base1 = new Base(userState1, 1);

        UserState userState2 = new UserState();
        userState2.setUser("TestUser2");
        Base base2 = new Base(userState2, 2);

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.expect(baseServiceMock.getUserState(base2.getSimpleBase())).andReturn(userState2).anyTimes();
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        ConditionConfig conditionTutorial = new ConditionConfig(ConditionTrigger.TUTORIAL, null);
        ConditionConfig conditionXp = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(null, 30, null));
        ConditionConfig conditionMoney = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(null, 80, null));

        serverConditionService.activateCondition(conditionXp, userState1, null);
        serverConditionService.activateCondition(conditionTutorial, userState1, 1);
        serverConditionService.activateCondition(conditionTutorial, userState1, 2);
        serverConditionService.activateCondition(conditionMoney, userState1, 3);

        serverConditionService.activateCondition(conditionXp, userState2, null);
        serverConditionService.activateCondition(conditionTutorial, userState2, 1);
        serverConditionService.activateCondition(conditionTutorial, userState2, 2);
        serverConditionService.activateCondition(conditionMoney, userState2, 3);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestServerConditionServiceImpl.this.actor = actor;
                TestServerConditionServiceImpl.this.identifier = identifier;
            }
        });
        actor = null;
        identifier = null;
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 50);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertActorAndIdentifierAndClear(userState1, 3);
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base2.getSimpleBase(), 60);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState1, 20);
        assertClearActorAndIdentifier();
        serverConditionService.onTutorialFinished(userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
        serverConditionService.onTutorialFinished(userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState2, 25);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState2, 5);
        assertActorAndIdentifierAndClear(userState2, null);
        serverConditionService.onTutorialFinished(userState2, 2);
        assertActorAndIdentifierAndClear(userState2, 2);
        serverConditionService.onTutorialFinished(userState2, 1);
        assertActorAndIdentifierAndClear(userState2, 1);
        serverConditionService.onTutorialFinished(userState2, 2);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base2.getSimpleBase(), 60);
        assertActorAndIdentifierAndClear(userState2, 3);
        serverConditionService.onTutorialFinished(userState1, 2);
        assertActorAndIdentifierAndClear(userState1, 2);
        serverConditionService.onIncreaseXp(userState1, 20);
        assertActorAndIdentifierAndClear(userState1, null);

        Map triggerMap = (Map) getPrivateField(ServerConditionServiceImpl.class, serverConditionService, "triggerMap");
        Assert.assertEquals(0, triggerMap.size());
    }

    private void assertActorAndIdentifierAndClear(UserState expectedActor, Integer expectedIdentifier) {
        Assert.assertEquals(expectedActor, actor);
        Assert.assertEquals(expectedIdentifier, identifier);
        actor = null;
        identifier = null;
    }

    private void assertClearActorAndIdentifier() {
        Assert.assertNull(actor);
        Assert.assertNull(identifier);
    }

    @Test
    @DirtiesContext
    public void baseDeleted() throws Exception {
        final UserState userState = new UserState();
        Base base = new Base(userState, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();
        SimpleBase simpleBase2 = new SimpleBase(2);

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase2)).andReturn(null);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 1, null));
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        serverConditionService.onBaseDeleted(simpleBase2);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void baseDeleted2() throws Exception {
        final UserState userState = new UserState();
        Base base = new Base(userState, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState).times(3);
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 2, null));
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void positionConditionTrigger() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestServerConditionServiceImpl.this.actor = actor;
                TestServerConditionServiceImpl.this.identifier = identifier;
            }
        });
        actor = null;
        identifier = null;
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null, false, null)), userService.getUserState(), 1);
        assertClearActorAndIdentifier();
        sendMoveCommand(builder, new Index(700, 700));
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);

        itemTypes = new HashMap<>();
        itemTypes.put(itemService.getItemType(TEST_FACTORY_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null, false, null)), userService.getUserState(), 1);
        sendBuildCommand(builder, new Index(900, 900), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);

        itemTypes = new HashMap<>();
        itemTypes.put(itemService.getItemType(TEST_ATTACK_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null, false, null)), userService.getUserState(), 1);
        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);

        itemTypes = new HashMap<>();
        itemTypes.put(itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null, false, null)), userService.getUserState(), 1);

        sendFactoryCommand(factory, TEST_CONTAINER_ITEM_ID);
        waitForActionServiceDone();
        Id container = getFirstSynItemId(TEST_CONTAINER_ITEM_ID);
        sendMoveCommand(container, new Index(400, 400)); // Prevent container move over other unit
        waitForActionServiceDone();
        sendMoveCommand(builder, new Index(300, 300));
        waitForActionServiceDone();
        sendContainerLoadCommand(builder, container);
        waitForActionServiceDone();

        sendMoveCommand(container, new Index(1100, 1100));
        waitForActionServiceDone();
        sendUnloadContainerCommand(container, new Index(1000, 1000));
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreDb() throws Exception {
        configureGameMultipleLevel();
        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_2_REAL_ID);
        SimpleBase simpleBase1 = getMyBase();

        // Verify the QuestsCms
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_1_2_REAL_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        userGuidanceService.deactivateLevelTaskCms(TEST_LEVEL_TASK_1_2_REAL_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_1_2_REAL_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        serverConditionService.onMoneyIncrease(simpleBase1, 2.0);

        serverConditionService.onIncreaseXp(userState1, 10);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete missions
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        simpleBase1 = getMyBase();
        serverConditionService.onMoneyIncrease(simpleBase1, 1.0);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify & Complete last task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        serverConditionService.onMoneyIncrease(simpleBase1, 1.0);
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        // activate last task
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_2_2_REAL_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_2_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
        }
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, (int) userGuidanceService.getDbLevel().getId());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreItemTypePositionDb() throws Exception {
        configureGameMultipleLevel();
        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);

        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_4_REAL_ID);
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_1_4_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(600, 600), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        Thread.sleep(500);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(500);
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        assertAndSetTimeRemaining();
        Thread.sleep(100);
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertAndSetTimeRemaining() throws Exception {
        // Manipulate the time inside the position comparison to avoid waiting a minute
        Map<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>> triggerMap = (Map<UserState, Collection<AbstractConditionTrigger<UserState, Integer>>>) getPrivateField(ServerConditionServiceImpl.class, serverConditionService, "triggerMap");
        Collection<AbstractConditionTrigger<UserState, Integer>> triggers = triggerMap.get(userService.getUserState());
        Assert.assertEquals(2, triggers.size());
        ItemTypePositionComparison itemTypePositionComparison = null;
        for (AbstractConditionTrigger<UserState, Integer> trigger : triggers) {
            if (trigger.getIdentifier() != null && trigger.getIdentifier() == TEST_LEVEL_TASK_1_4_REAL_ID) {
                itemTypePositionComparison = (ItemTypePositionComparison) trigger.getAbstractComparison();
            }
        }
        Assert.assertNotNull(itemTypePositionComparison);
        long fulfilledTimeStamp = (Long) getPrivateField(ItemTypePositionComparison.class, itemTypePositionComparison, "fulfilledTimeStamp");
        long fulfilledTime = (System.currentTimeMillis() - fulfilledTimeStamp);
        System.out.println("fulfilledTime: " + fulfilledTime);
        Assert.assertTrue("fulfilledTime invalid: " + fulfilledTime, fulfilledTime > 500 && fulfilledTime < 800);
        setPrivateField(ItemTypePositionComparison.class, itemTypePositionComparison, "fulfilledTimeStamp", System.currentTimeMillis() - 60000);
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreSyncItemTypeComparisonDb() throws Exception {
        configureGameMultipleLevel();

        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_4_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Proceed task but do not activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Proceed task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete task but inactive
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            // Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_2_4_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Proceed task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isActive());
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID != levelQuest.getDbLevelTask().getId(), levelQuest.isBlocked());
        }
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID == levelQuest.getDbLevelTask().getId(), levelQuest.isDone());
            Assert.assertFalse(levelQuest.isActive());
            Assert.assertFalse(levelQuest.isBlocked());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}
