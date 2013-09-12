package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractUpdatingComparison;
import com.btxtech.game.jsre.common.utg.config.ArtifactItemIdComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.inventory.impl.GlobalInventoryServiceImpl;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.DbArtifactItemIdComparisonConfig;
import com.btxtech.game.services.utg.condition.DbComparisonArtifactItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 10.09.13
 * Time: 12:48
 */
public class TestConditionServiceArtifactItemAdded extends AbstractServiceTest {
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private UserService userService;
    private boolean passed;
    private UserState userState;

    @Test
    @DirtiesContext
    public void checkCallBoxPicket() throws Exception {
        configureSimplePlanetNoResources();

        // Setup box
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        DbInventoryArtifact dbInventoryArtifact3 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact3.setName("dbInventoryArtifact3");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact3);

        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(5000);
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(1.0);
        dbBoxItemTypePossibility1.setDbInventoryArtifact(dbInventoryArtifact1);
        DbBoxItemTypePossibility dbBoxItemTypePossibility2 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility2.setPossibility(1.0);
        dbBoxItemTypePossibility2.setDbInventoryArtifact(dbInventoryArtifact2);
        DbBoxItemTypePossibility dbBoxItemTypePossibility3 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility3.setPossibility(1.0);
        dbBoxItemTypePossibility3.setDbInventoryArtifact(dbInventoryArtifact3);
        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onArtifactItemAdded(createUserStateMatcher("qaywsx"), EasyMock.eq(true), EasyMock.eq((int) dbInventoryArtifact1.getId()));
        serverConditionServiceMock.onArtifactItemAdded(createUserStateMatcher("qaywsx"), EasyMock.eq(true), EasyMock.eq((int) dbInventoryArtifact2.getId()));
        serverConditionServiceMock.onArtifactItemAdded(createUserStateMatcher("qaywsx"), EasyMock.eq(true), EasyMock.eq((int) dbInventoryArtifact3.getId()));
        EasyMock.replay(serverConditionServiceMock);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "serverConditionService", serverConditionServiceMock);

        ServerItemService serverItemService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("qaywsx");
        createBase(new Index(1000, 1000));
        SyncBaseItem bulldozer = (SyncBaseItem) serverItemService.getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        SyncBoxItem syncBoxItem = (SyncBoxItem) serverItemService.createSyncObject(serverItemTypeService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId()).createItemType(), new Index(2000, 2000), null, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        globalInventoryService.onSyncBoxItemPicked(syncBoxItem, bulldozer);

        EasyMock.verify(serverConditionServiceMock);
    }

    @Test
    @DirtiesContext
    public void checkCallBuyInventoryArtifact() throws Exception {
        configureSimplePlanetNoResources();

        // Setup artifacts
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        dbInventoryArtifact1.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        dbInventoryArtifact2.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onArtifactItemAdded(createUserStateMatcher("qaywsx"), EasyMock.eq(false), EasyMock.eq((int) dbInventoryArtifact1.getId()));
        serverConditionServiceMock.onArtifactItemAdded(createUserStateMatcher("qaywsx"), EasyMock.eq(false), EasyMock.eq((int) dbInventoryArtifact2.getId()));
        EasyMock.replay(serverConditionServiceMock);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "serverConditionService", serverConditionServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("qaywsx");
        getUserState().addRazarion(100);
        globalInventoryService.buyInventoryArtifact(dbInventoryArtifact1.getId());
        globalInventoryService.buyInventoryArtifact(dbInventoryArtifact2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConditionServiceMock);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceNoPlanetInteraction() throws Exception {
        configureSimplePlanetNoResources();
        Map<Integer, Integer> artifactItemIdCount = new HashMap<>();
        artifactItemIdCount.put(1, 2);
        artifactItemIdCount.put(2, 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.ARTIFACT_ITEM_ADDED, new ArtifactItemIdComparisonConfig(artifactItemIdCount), null, null, false);
        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        serverConditionService.activateCondition(conditionConfig, userState, 1);
        passed = false;

        Assert.assertFalse(passed);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Test inside a web request
        serverConditionService.onArtifactItemAdded(userState, false, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, 2);
        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, 1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServicePlanetInteraction() throws Exception {
        configureSimplePlanetNoResources();
        Map<Integer, Integer> artifactItemIdCount = new HashMap<>();
        artifactItemIdCount.put(1, 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.ARTIFACT_ITEM_ADDED, new ArtifactItemIdComparisonConfig(artifactItemIdCount), null, null, false);
        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        serverConditionService.activateCondition(conditionConfig, userState, 1);
        passed = false;

        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, true, 1);
        Assert.assertFalse(passed);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        serverConditionService.onArtifactItemAdded(userState, true, 1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceLevelTask() throws Exception {
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        dbInventoryArtifact1.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        dbInventoryArtifact2.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig = new DbArtifactItemIdComparisonConfig();
        DbComparisonArtifactItemCount itemCount1 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount1.setCount(1);
        itemCount1.setDbInventoryArtifact(dbInventoryArtifact1);
        DbComparisonArtifactItemCount itemCount2 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount2.setCount(2);
        itemCount2.setDbInventoryArtifact(dbInventoryArtifact2);
        dbConditionConfig.setDbAbstractComparisonConfig(dbArtifactItemIdComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        userState.addRazarion(100);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact1.getId());
        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceBackup() throws Exception {
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        dbInventoryArtifact1.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        dbInventoryArtifact2.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);

        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig = new DbArtifactItemIdComparisonConfig();
        DbComparisonArtifactItemCount itemCount1 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount1.setCount(1);
        itemCount1.setDbInventoryArtifact(dbInventoryArtifact1);
        DbComparisonArtifactItemCount itemCount2 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount2.setCount(2);
        itemCount2.setDbInventoryArtifact(dbInventoryArtifact2);
        dbConditionConfig.setDbAbstractComparisonConfig(dbArtifactItemIdComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals("U1", userService.getUserName(actor));
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        userState.addRazarion(100);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        Assert.assertFalse(passed);
        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        assertBackupSummery(1, 0, 0, 1);
        backupService.restore(backupSummaries.get(0).getDate());
        UserState newUserState = userService.getUserState(userService.getUser("U1"));
        Assert.assertNotSame(userState, newUserState);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        serverConditionService.onArtifactItemAdded(newUserState, false, dbInventoryArtifact1.getId());
        Assert.assertFalse(passed);
        serverConditionService.onArtifactItemAdded(newUserState, false, dbInventoryArtifact2.getId());
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testUpdateSending() throws Exception {
        setPrivateStaticField(AbstractUpdatingComparison.class, "MIN_SEND_DELAY", 0);
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        dbInventoryArtifact1.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        dbInventoryArtifact2.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        InventoryArtifactInfo artifactInfo1 = dbInventoryArtifact1.generateInventoryArtifactInfo();
        InventoryArtifactInfo artifactInfo2 = dbInventoryArtifact2.generateInventoryArtifactInfo();

        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig = new DbArtifactItemIdComparisonConfig();
        DbComparisonArtifactItemCount itemCount1 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount1.setCount(1);
        itemCount1.setDbInventoryArtifact(dbInventoryArtifact1);
        DbComparisonArtifactItemCount itemCount2 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount2.setCount(2);
        itemCount2.setDbInventoryArtifact(dbInventoryArtifact2);
        dbConditionConfig.setDbAbstractComparisonConfig(dbArtifactItemIdComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        createBase(new Index(1000, 1000));
        userState = getUserState();
        userState.addRazarion(100);
        createConnection();
        clearPackets();
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        //
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        QuestProgressInfo questProgressInfo = new QuestProgressInfo(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        levelTaskPacket.setQuestProgressInfo(questProgressInfo);
        Map<InventoryArtifactInfo, QuestProgressInfo.Amount> inventoryArtifactInfoAmount = new HashMap<>();
        inventoryArtifactInfoAmount.put(artifactInfo1, new QuestProgressInfo.Amount(0, 1));
        inventoryArtifactInfoAmount.put(artifactInfo2, new QuestProgressInfo.Amount(1, 2));
        questProgressInfo.setInventoryArtifactInfoAmount(inventoryArtifactInfoAmount);
        assertPackagesIgnoreSyncItemInfoAndClear(true, levelTaskPacket);
        //
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact1.getId());
        //
        levelTaskPacket = new LevelTaskPacket();
        questProgressInfo = new QuestProgressInfo(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        levelTaskPacket.setQuestProgressInfo(questProgressInfo);
        inventoryArtifactInfoAmount = new HashMap<>();
        inventoryArtifactInfoAmount.put(artifactInfo1, new QuestProgressInfo.Amount(1, 1));
        inventoryArtifactInfoAmount.put(artifactInfo2, new QuestProgressInfo.Amount(1, 2));
        questProgressInfo.setInventoryArtifactInfoAmount(inventoryArtifactInfoAmount);
        assertPackagesIgnoreSyncItemInfoAndClear(true, levelTaskPacket);
        //
        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        //
        levelTaskPacket = new LevelTaskPacket();
        questProgressInfo = new QuestProgressInfo(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        levelTaskPacket.setQuestProgressInfo(questProgressInfo);
        inventoryArtifactInfoAmount = new HashMap<>();
        inventoryArtifactInfoAmount.put(artifactInfo1, new QuestProgressInfo.Amount(1, 1));
        inventoryArtifactInfoAmount.put(artifactInfo2, new QuestProgressInfo.Amount(2, 2));
        questProgressInfo.setInventoryArtifactInfoAmount(inventoryArtifactInfoAmount);
        LevelTaskPacket levelTaskPacketComplete = new LevelTaskPacket();
        levelTaskPacketComplete.setCompleted();
        assertPackagesIgnoreSyncItemInfoAndClear(true, levelTaskPacket, levelTaskPacketComplete);
        //
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGetRealGameInfo() throws Exception {
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact1 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact1.setName("dbInventoryArtifact1");
        dbInventoryArtifact1.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact1);
        DbInventoryArtifact dbInventoryArtifact2 = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact2.setName("dbInventoryArtifact2");
        dbInventoryArtifact2.setRazarionCoast(1);
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact2);
        InventoryArtifactInfo artifactInfo1 = dbInventoryArtifact1.generateInventoryArtifactInfo();
        InventoryArtifactInfo artifactInfo2 = dbInventoryArtifact2.generateInventoryArtifactInfo();

        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.ARTIFACT_ITEM_ADDED);
        DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig = new DbArtifactItemIdComparisonConfig();
        DbComparisonArtifactItemCount itemCount1 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount1.setCount(1);
        itemCount1.setDbInventoryArtifact(dbInventoryArtifact1);
        DbComparisonArtifactItemCount itemCount2 = dbArtifactItemIdComparisonConfig.getArtifactItemCountCrud().createDbChild();
        itemCount2.setCount(2);
        itemCount2.setDbInventoryArtifact(dbInventoryArtifact2);
        dbConditionConfig.setDbAbstractComparisonConfig(dbArtifactItemIdComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        createBase(new Index(1000, 1000));
        userState = getUserState();
        userState.addRazarion(100);
        LevelTaskPacket levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertFalse(levelTaskPacket.isCompleted());
        Assert.assertEquals(ConditionTrigger.ARTIFACT_ITEM_ADDED, levelTaskPacket.getQuestProgressInfo().getConditionTrigger());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getAmount());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getItemIdAmounts());
        Assert.assertEquals(2, levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().size());
        Assert.assertEquals(new QuestProgressInfo.Amount(0, 1), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo1));
        Assert.assertEquals(new QuestProgressInfo.Amount(0, 2), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo2));

        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact2.getId());
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertFalse(levelTaskPacket.isCompleted());
        Assert.assertEquals(ConditionTrigger.ARTIFACT_ITEM_ADDED, levelTaskPacket.getQuestProgressInfo().getConditionTrigger());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getAmount());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getItemIdAmounts());
        Assert.assertEquals(2, levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().size());
        Assert.assertEquals(new QuestProgressInfo.Amount(0, 1), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo1));
        Assert.assertEquals(new QuestProgressInfo.Amount(1, 2), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo2));

        serverConditionService.onArtifactItemAdded(userState, false, dbInventoryArtifact1.getId());
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertFalse(levelTaskPacket.isCompleted());
        Assert.assertEquals(ConditionTrigger.ARTIFACT_ITEM_ADDED, levelTaskPacket.getQuestProgressInfo().getConditionTrigger());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getAmount());
        Assert.assertNull(levelTaskPacket.getQuestProgressInfo().getItemIdAmounts());
        Assert.assertEquals(2, levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().size());
        Assert.assertEquals(new QuestProgressInfo.Amount(1, 1), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo1));
        Assert.assertEquals(new QuestProgressInfo.Amount(1, 2), levelTaskPacket.getQuestProgressInfo().getInventoryArtifactInfoAmount().get(artifactInfo2));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
