package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 11.02.13
 * Time: 18:45
 */
public class TestServerUnlockService extends AbstractServiceTest {
    @Autowired
    private ServerUnlockService unlockService;
    @Autowired
    private ServerItemTypeService itemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testRealGameInfo() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup items
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockCrystals(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockCrystals(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        // Setup Quests
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setUnlockCrystals(1);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setUnlockCrystals(1);
        setupCondition(dbLevelTask2);
        DbLevelTask dbLevelTask3 = dbLevel.getLevelTaskCrud().createDbChild();
        setupCondition(dbLevelTask3);
        dbLevelTask3.setUnlockCrystals(1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        // Setup planets
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        getUserState().setCrystals(100);
        getOrCreateBase(); // Create Base
        // Setup
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        unlockService.unlockQuest(dbLevelTask3.getId());
        unlockService.unlockPlanet(TEST_PLANET_2_ID);
        unlockService.unlockPlanet(TEST_PLANET_3_ID);
        // Verify
        UnlockContainer unlockContainer = getMovableService().getRealGameInfo(START_UID_1, null).getUnlockContainer();
        Assert.assertNotNull(unlockContainer);
        // Verify items
        Assert.assertEquals(2, unlockContainer.getItemTypes().size());
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_ATTACK_ITEM_ID));
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_FACTORY_ITEM_ID));
        // Verify quests
        Assert.assertEquals(3, unlockContainer.getQuests().size());
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask1.getId()));
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask2.getId()));
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask3.getId()));
        // Verify planets
        Assert.assertEquals(2, unlockContainer.getPlanets().size());
        Assert.assertTrue(unlockContainer.getPlanets().contains(TEST_PLANET_2_ID));
        Assert.assertTrue(unlockContainer.getPlanets().contains(TEST_PLANET_3_ID));
        // Verify planet
        Assert.assertNull(getMovableService().getRealGameInfo(START_UID_1, null).getLevelScope().getPlanetLiteInfo().getUnlockCrystals());
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
    public void testRealGameInfoLoginLogoff() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup items
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockCrystals(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockCrystals(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        // Setup Quests
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setUnlockCrystals(1);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setUnlockCrystals(1);
        setupCondition(dbLevelTask2);
        DbLevelTask dbLevelTask3 = dbLevel.getLevelTaskCrud().createDbChild();
        setupCondition(dbLevelTask3);
        dbLevelTask3.setUnlockCrystals(1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        // Setup planets
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        getUserState().setCrystals(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        unlockService.unlockQuest(dbLevelTask3.getId());
        unlockService.unlockPlanet(TEST_PLANET_2_ID);
        unlockService.unlockPlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        // Verify
        UnlockContainer unlockContainer = getMovableService().getRealGameInfo(START_UID_1, null).getUnlockContainer();
        Assert.assertNotNull(unlockContainer);
        // Verify items
        Assert.assertEquals(2, unlockContainer.getItemTypes().size());
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_ATTACK_ITEM_ID));
        Assert.assertTrue(unlockContainer.getItemTypes().contains(TEST_FACTORY_ITEM_ID));
        // Verify quests
        Assert.assertEquals(3, unlockContainer.getQuests().size());
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask1.getId()));
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask2.getId()));
        Assert.assertTrue(unlockContainer.getQuests().contains(dbLevelTask3.getId()));
        // Verify planets
        Assert.assertEquals(2, unlockContainer.getPlanets().size());
        Assert.assertTrue(unlockContainer.getPlanets().contains(TEST_PLANET_2_ID));
        Assert.assertTrue(unlockContainer.getPlanets().contains(TEST_PLANET_3_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
