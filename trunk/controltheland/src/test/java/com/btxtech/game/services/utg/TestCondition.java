package com.btxtech.game.services.utg;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 14:17:33
 */
@Ignore
public class TestCondition extends AbstractServiceTest {
    private static final String LAST_LEVEL = "LastLevel";
    private static final String TERRITORY_LEVEL = "TerritoryLevel";

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    //@Test

    @Ignore
    @DirtiesContext
    public void testCreateOnAllowedTerrain() throws Exception {
        Assert.fail();
//
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevel(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(LAST_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
    }

//    @Test
//    @DirtiesContext
//    public void testCreateOnExcludedTerrain() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevel(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(500, 500), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        getFirstSynItemId(TEST_FACTORY_ITEM_ID); // Verify id build
//
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateOnAllowedComplexTerrain() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevel(new Rectangle(0, 0, 10, 10), new Rectangle(20, 20, 40, 40), new Rectangle(0, 40, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(100, 3000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(LAST_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrain() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2200, 2200), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2400, 2400), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        id = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(LAST_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrain_Fail1() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        id = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrain_Fail2() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        id = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrain_Fail3() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        id = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrain_Fail_wrongTerritory() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id id = proceedToTerritoryLevel(dbRealGameLevel);
//
//        sendBuildCommand(id, new Index(200, 200), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2200, 2200), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(id, new Index(2400, 2400), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        id = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        sendFactoryCommand(id, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    @Test
//    @DirtiesContext
//    public void testCreateMultipleTerrainDifferentTerrain() throws Exception {
//        configureMinimalGame();
//
//        DbRealGameLevel dbRealGameLevel = setupTerritoryLevelMultiple(new Rectangle(0, 0, 10, 10));
//
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        Id bulldozer = proceedToTerritoryLevel(dbRealGameLevel);
//        sendBuildCommand(bulldozer, new Index(200, 200), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(bulldozer, new Index(400, 400), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(bulldozer, new Index(600, 600), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        Id factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
//        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        sendBuildCommand(bulldozer, new Index(3000, 3000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(bulldozer, new Index(4000, 3000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        sendBuildCommand(bulldozer, new Index(5000, 3000), TEST_FACTORY_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID, new Rectangle(4900, 2900, 200,200));
//        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        sendFactoryCommand(factory, TEST_ATTACK_ITEM_ID);
//        waitForActionServiceDone();
//        Assert.assertEquals(LAST_LEVEL, movableService.getGameInfo().getLevel().getName());
//
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//    }
//
//    private Id proceedToTerritoryLevel(DbRealGameLevel dbRealGameLevel) {
//        // 1. Level
//        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
//        // 2. Level
//        UserState userState = userService.getUserState();
//        userGuidanceService.promote(userState, dbRealGameLevel.getId());
//        // Destination Level
//        Assert.assertEquals(TERRITORY_LEVEL, movableService.getGameInfo().getLevel().getName());
//        return getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
//    }
//
//    private DbRealGameLevel setupTerritoryLevel(Rectangle... regions) throws LevelActivationException {
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        // Territory
//        DbTerritory dbTerritory = setupTerritory("noob", new int[]{TEST_ATTACK_ITEM_ID, TEST_START_BUILDER_ITEM_ID, TEST_FACTORY_ITEM_ID}, regions);
//        // Testing Level
//        DbConditionConfig dbConditionConfig = new DbConditionConfig();
//        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
//        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig = new DbSyncItemTypeComparisonConfig();
//        dbSyncItemTypeComparisonConfig.setExcludedDbTerritory(dbTerritory);
//        DbComparisonItemCount dbComparisonItemCount = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
//        dbComparisonItemCount.setItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
//        dbComparisonItemCount.setCount(1);
//        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
//        DbRealGameLevel dbRealGameLevel = setupGameLevel(TERRITORY_LEVEL, dbConditionConfig);
//        // Last Level
//        setupItemTypePositionSimulationLevel(LAST_LEVEL);
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//        return dbRealGameLevel;
//    }
//
//    private DbRealGameLevel setupTerritoryLevelMultiple(Rectangle... regions) throws LevelActivationException {
//        beginHttpSession();
//        beginHttpRequestAndOpenSessionInViewFilter();
//        // Territory
//        DbTerritory dbTerritory = setupTerritory("noob", new int[]{TEST_ATTACK_ITEM_ID, TEST_START_BUILDER_ITEM_ID, TEST_FACTORY_ITEM_ID}, regions);
//        // Testing Level
//        DbConditionConfig dbConditionConfig = new DbConditionConfig();
//        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
//        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig = new DbSyncItemTypeComparisonConfig();
//        dbSyncItemTypeComparisonConfig.setExcludedDbTerritory(dbTerritory);
//        DbComparisonItemCount factory = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
//        factory.setItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
//        factory.setCount(3);
//        DbComparisonItemCount attacker = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
//        attacker.setItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
//        attacker.setCount(2);
//        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
//        DbRealGameLevel dbRealGameLevel = setupGameLevel(TERRITORY_LEVEL, dbConditionConfig);
//        // Last Level
//        setupItemTypePositionSimulationLevel(LAST_LEVEL);
//        endHttpRequestAndOpenSessionInViewFilter();
//        endHttpSession();
//        return dbRealGameLevel;
//    }

}
