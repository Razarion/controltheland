package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ItemTypePositionComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.ServerConnectionServiceTestHelper;
import com.btxtech.game.services.TestPlanetHelper;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private UserState actor;
    private Integer identifier;
    private boolean passed = false;


    @Test
    @DirtiesContext
    public void multiplePlayers() throws Exception {
        configureSimplePlanetNoResources();

        UserState userState1 = new UserState();
        userState1.setUser(1);
        userState1.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        Base base1 = new Base(userState1, planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);

        UserState userState2 = new UserState();
        userState2.setUser(2);
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        Base base2 = new Base(userState2, planetSystemService.getPlanet(TEST_PLANET_1_ID), 2);

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.expect(baseServiceMock.getUserState(base2.getSimpleBase())).andReturn(userState2).anyTimes();
        EasyMock.replay(baseServiceMock);

        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setBaseService(baseServiceMock);

        ConditionConfig conditionTutorial = new ConditionConfig(ConditionTrigger.TUTORIAL, null, null, null, false);
        ConditionConfig conditionXp = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(30), null, null, false);
        ConditionConfig conditionMoney = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(80), null, null, false);

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
        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(0, null, null);
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        serverPlanetServices.setPlanetInfo(planetInfo);
        TestPlanetHelper testPlanetHelper = new TestPlanetHelper();
        testPlanetHelper.setServerPlanetServices(serverPlanetServices);
        LevelScope levelScope = new LevelScope(null, 1, 1, null, 1);

        final UserState userState = new UserState();
        //userState.setDbLevelId();
        Base base = new Base(userState, testPlanetHelper, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();
        SimpleBase simpleBase2 = new SimpleBase(2, 1);

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase2)).andReturn(null);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.replay(baseServiceMock);
        serverPlanetServices.setBaseService(baseServiceMock);

        // Mock ConnectionService
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(EasyMock.eq(simpleBase1), EasyMock.<LevelTaskPacket>anyObject());
        EasyMock.replay(serverConnectionService);
        serverPlanetServices.setServerConnectionService(serverConnectionService);

        // Mock PlanetSystemService
        PlanetSystemService planetSystemService = EasyMock.createStrictMock(PlanetSystemService.class);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices);
        EasyMock.expect(planetSystemService.isUserOnCorrectPlanet(userState)).andReturn(true);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase2)).andReturn(serverPlanetServices);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices);
        EasyMock.expect(planetSystemService.isUserOnCorrectPlanet(userState)).andReturn(true);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices);
        EasyMock.replay(planetSystemService);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "planetSystemService", planetSystemService);

        // Mock UserGuidanceService
        UserGuidanceService userGuidanceService = EasyMock.createStrictMock(UserGuidanceService.class);
        EasyMock.expect(userGuidanceService.getLevelScope(userState)).andReturn(levelScope);
        EasyMock.replay(userGuidanceService);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "userGuidanceService", userGuidanceService);

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
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(1), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        serverConditionService.onBaseDeleted(simpleBase2);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
        EasyMock.verify(serverConnectionService, planetSystemService, userGuidanceService);
    }

    @Test
    @DirtiesContext
    public void baseDeleted2() throws Exception {
        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(0, null, null);
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        serverPlanetServices.setPlanetInfo(planetInfo);
        TestPlanetHelper testPlanetHelper = new TestPlanetHelper();
        testPlanetHelper.setServerPlanetServices(serverPlanetServices);
        LevelScope levelScope = new LevelScope(null, 1, 1, null, 1);

        final UserState userState = new UserState();
        //userState.setDbLevelId();
        Base base = new Base(userState, testPlanetHelper, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState).times(3);
        EasyMock.replay(baseServiceMock);
        serverPlanetServices.setBaseService(baseServiceMock);

        // Mock ConnectionService
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(EasyMock.eq(simpleBase1), EasyMock.<LevelTaskPacket>anyObject());
        EasyMock.replay(serverConnectionService);
        serverPlanetServices.setServerConnectionService(serverConnectionService);

        // Mock PlanetSystemService
        PlanetSystemService planetSystemService = EasyMock.createStrictMock(PlanetSystemService.class);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices);
        EasyMock.expect(planetSystemService.isUserOnCorrectPlanet(userState)).andReturn(true);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices);
        EasyMock.expect(planetSystemService.isUserOnCorrectPlanet(userState)).andReturn(true);
        EasyMock.expect(planetSystemService.getServerPlanetServices(simpleBase1)).andReturn(serverPlanetServices).times(2);
        EasyMock.expect(planetSystemService.isUserOnCorrectPlanet(userState)).andReturn(true);
        EasyMock.replay(planetSystemService);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "planetSystemService", planetSystemService);

        // Mock UserGuidanceService
        UserGuidanceService userGuidanceService = EasyMock.createStrictMock(UserGuidanceService.class);
        EasyMock.expect(userGuidanceService.getLevelScope(userState)).andReturn(levelScope);
        EasyMock.replay(userGuidanceService);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "userGuidanceService", userGuidanceService);

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
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(2), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        // TODO test failed (Unexpected method call getServerPlanetServices) 31.12.2012, 02.01.2013, 09.01.2013 05.07.2013
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
        EasyMock.verify(serverConnectionService, planetSystemService, userGuidanceService);
    }

    @Test
    @DirtiesContext
    public void positionConditionTrigger() throws Exception {
        configureSimplePlanetNoResources();

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
        itemTypes.put(serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null, false), null, null, false), userService.getUserState(), 1);
        assertClearActorAndIdentifier();
        sendMoveCommand(builder, new Index(700, 700));
        waitForActionServiceDone();
        // TODO test failed (expected:<UserState: user=null> but was:<null>) 24.01.2013
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);
        itemTypes = new HashMap<>();
        itemTypes.put(serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null, false), null, null, false), userService.getUserState(), 1);
        sendBuildCommand(builder, new Index(900, 900), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);

        itemTypes = new HashMap<>();
        itemTypes.put(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null, false), null, null, false), userService.getUserState(), 1);
        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);

        itemTypes = new HashMap<>();
        itemTypes.put(serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1);
        serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null, false), null, null, false), userService.getUserState(), 1);

        sendFactoryCommand(factory, TEST_CONTAINER_ITEM_ID);
        waitForActionServiceDone();
        Id container = getFirstSynItemId(TEST_CONTAINER_ITEM_ID);
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(2000, 2000)); // Prevent container move over other unit
        sendMoveCommand(container, new Index(400, 400)); // Prevent container move over other unit
        waitForActionServiceDone();
        sendMoveCommand(builder, new Index(220, 400));
        waitForActionServiceDone();
        sendContainerLoadCommand(builder, container);
        waitForActionServiceDone();

        sendMoveCommand(container, new Index(1100, 1100));
        waitForActionServiceDone();
        sendUnloadContainerCommand(container, new Index(1000, 1000));
        waitForActionServiceDone(); // TODO crashed almost always here FIX THIS!!!: 02.20.2012, 02.20.2012, 05.10.2012, 12.10.2012, 29.10.2012, 30.10.2012, 03.12.2012, 04.12.2012, 05.12.2012, 05.12.2012
        assertActorAndIdentifierAndClear(userService.getUserState(), 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreDb() throws Exception {
        configureMultiplePlanetsAndLevels();
        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_2_REAL_ID);
        SimpleBase simpleBase1 = getOrCreateBase();

        serverConditionService.onMoneyIncrease(simpleBase1, 2.0);
        serverConditionService.onIncreaseXp(userState1, 10);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(TEST_LEVEL_TASK_1_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getMissionsDone());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalMissions());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestsDone());
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalQuests());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete missions
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        simpleBase1 = getOrCreateBase();
        serverConditionService.onMoneyIncrease(simpleBase1, 1.0);
        Assert.assertEquals(TEST_LEVEL_TASK_2_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getMissionsDone());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalMissions());
        Assert.assertEquals(1, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestsDone());
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalQuests());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify & Complete last task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        serverConditionService.onMoneyIncrease(simpleBase1, 1.0);
        Assert.assertEquals(TEST_LEVEL_TASK_2_2_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getMissionsDone());
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalMissions());
        Assert.assertEquals(1, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestsDone());
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getTotalQuests());
        // fulfill next task level
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, (int) userGuidanceService.getDbLevel().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreItemTypePositionDb() throws Exception {
        configureMultiplePlanetsAndLevels();
        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);

        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_4_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(600, 600), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        Thread.sleep(5000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(5000);
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // TODO failed 05.10.2012, 25.10.2012
        assertAndSetTimeRemaining();
        Thread.sleep(100);
        Assert.assertEquals(1, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
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
        Assert.assertTrue("fulfilledTime invalid: " + fulfilledTime, fulfilledTime > 5000 && fulfilledTime < 10000);
        setPrivateField(ItemTypePositionComparison.class, itemTypePositionComparison, "fulfilledTimeStamp", System.currentTimeMillis() - 60000);
    }

    @Test
    @DirtiesContext
    public void testBackupRestoreSyncItemTypeComparisonDb() throws Exception {
        configureMultiplePlanetsAndLevels();

        //Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState1 = userService.getUserState();
        userGuidanceService.promote(userState1, TEST_LEVEL_2_REAL_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.promote(userState1, TEST_LEVEL_4_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_4_REAL_ID, Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Proceed task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_4_REAL_ID, Locale.ENGLISH);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_4_REAL_ID, Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Complete task
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_4_REAL_ID, Locale.ENGLISH);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_1_4_REAL_ID, Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDeferredUpdate() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        SimpleBase simpleBase = getOrCreateBase();

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 10);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userService.getUserState(), 1);

        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(simpleBase);
        ServerConnectionServiceTestHelper serverConnectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        serverPlanetServices.setServerConnectionService(serverConnectionServiceTestHelper);

        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1500, 1500), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        List<SyncItem> factories = new ArrayList<>(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItems(serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), simpleBase));

        sendFactoryCommand(factories.get(0).getId(), TEST_ATTACK_ITEM_ID);
        sendFactoryCommand(factories.get(1).getId(), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();

        Thread.sleep(3100);

        List<ServerConnectionServiceTestHelper.PacketEntry> packetEntries = serverConnectionServiceTestHelper.getPacketEntries(simpleBase, LevelTaskPacket.class);
        // TODO test failed 31.12.2012, 02.01.2013, 09.01.2013 05.07.2013
        Assert.assertEquals(2, packetEntries.size());
        QuestProgressInfo questProgressInfo = ((LevelTaskPacket) packetEntries.get(0).getPacket()).getQuestProgressInfo();
        Assert.assertEquals(1, questProgressInfo.getItemIdAmounts().get(TEST_ATTACK_ITEM_ID).getAmount());
        questProgressInfo = ((LevelTaskPacket) packetEntries.get(1).getPacket()).getQuestProgressInfo();
        Assert.assertEquals(2, questProgressInfo.getItemIdAmounts().get(TEST_ATTACK_ITEM_ID).getAmount());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
