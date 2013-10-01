package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelItemTypeLimitation;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 18.10.12
 * Time: 15:45
 */
public class TestSyncWeapon extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void projectileAttack() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attackerType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = attackerType.getDbWeaponType();
        dbWeaponType.setProjectileSpeed(100);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackerType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(1000, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createBase(new Index(2000, 2000));
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 29.07.2013
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), target);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testReady() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup defense tower
        DbBaseItemType tower = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(tower, 24);
        tower.setName("Tower");
        tower.setTerrainType(TerrainType.LAND);
        tower.setBounding(new BoundingBox(80, ANGELS_24));
        tower.setHealth(10);
        tower.setBuildup(10);
        tower.setPrice(3);
        tower.setImageWidth(80);
        tower.setImageHeight(100);
        tower.setConsumingHouseSpace(1);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(1000);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbWeaponType.setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID), true);
        tower.setDbWeaponType(dbWeaponType);
        serverItemTypeService.saveDbItemType(tower);
        // Make builder build the defense tower
        DbBaseItemType builder = serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID);
        builder.getDbBuilderType().getAbleToBuild().add(tower);
        serverItemTypeService.saveDbItemType(builder);
        serverItemTypeService.activate();
        final int TOWER_ID = tower.getId();
        // Setup level
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelItemTypeLimitation dbLevelItemTypeLimitation = dbLevel.getItemTypeLimitationCrud().createDbChild();
        dbLevelItemTypeLimitation.setCount(1);
        dbLevelItemTypeLimitation.setDbBaseItemType(tower);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        // Setup planet
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbPlanetItemTypeLimitation dbPlanetItemTypeLimitation = dbPlanet.getItemLimitationCrud().createDbChild();
        dbPlanetItemTypeLimitation.setCount(1);
        dbPlanetItemTypeLimitation.setDbBaseItemType(tower);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Tower
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(8000, 8000), TOWER_ID);
        waitForActionServiceDone();
        SyncBaseItem towerItem = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TOWER_ID));
        towerItem.setBuildup(0.5);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SyncBaseItem target = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        sendMoveCommand(target.getId(), new Index(7830, 8000));
        waitForActionServiceDone();
        Assert.assertTrue(target.isAlive());
        // Finish buildup
        towerItem.setBuildup(1.0);
        waitForActionServiceDone();
        // TODO failed on 14.04.2013
        Assert.assertFalse(target.isAlive());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
