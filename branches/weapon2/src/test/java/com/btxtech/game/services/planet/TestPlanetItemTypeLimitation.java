package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelItemTypeLimitation;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 07.09.12
 * Time: 21:48
 */
public class TestPlanetItemTypeLimitation extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void itemLimitation() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        // Planet
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        setupMinimalTerrain(dbPlanet);
        dbPlanet.setName("Planet 1");
        dbPlanet.setHouseSpace(50);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 10, 10)));
        dbPlanet.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        DbPlanetItemTypeLimitation planetLimit1 = dbPlanet.getItemLimitationCrud().createDbChild();
        planetLimit1.setCount(1);
        planetLimit1.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        DbPlanetItemTypeLimitation planetLimit2 = dbPlanet.getItemLimitationCrud().createDbChild();
        planetLimit2.setCount(2);
        planetLimit2.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        DbPlanetItemTypeLimitation planetLimit3 = dbPlanet.getItemLimitationCrud().createDbChild();
        planetLimit3.setCount(3);
        planetLimit3.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID_2));
        DbPlanetItemTypeLimitation planetLimitS = dbPlanet.getItemLimitationCrud().createDbChild();
        planetLimitS.setCount(1);
        planetLimitS.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        // Level
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel.setXp(1);
        DbLevelItemTypeLimitation levelLimit1 = dbLevel.getItemTypeLimitationCrud().createDbChild();
        levelLimit1.setCount(2);
        levelLimit1.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        DbLevelItemTypeLimitation levelLimit2 = dbLevel.getItemTypeLimitationCrud().createDbChild();
        levelLimit2.setCount(1);
        levelLimit2.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        DbLevelItemTypeLimitation dbPlanetLimit3 = dbLevel.getItemTypeLimitationCrud().createDbChild();
        dbPlanetLimit3.setCount(3);
        dbPlanetLimit3.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        DbLevelItemTypeLimitation dbPlanetLimitS = dbLevel.getItemTypeLimitationCrud().createDbChild();
        dbPlanetLimitS.setCount(1);
        dbPlanetLimitS.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbLevel.setDbPlanet(dbPlanet);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        // Post init
        dbPlanet.setMinLevel(dbLevel);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setDbLevelId(dbLevel.getId());
        BaseService baseService = planetSystemService.getServerPlanetServices(dbPlanet.getId()).getBaseService();
        SimpleBase simpleBase = createBase(new Index(1000, 1000));

        Assert.assertFalse(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_CONTAINER_ITEM_ID), 1, simpleBase));
        Assert.assertTrue(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_CONTAINER_ITEM_ID), 2, simpleBase));
        Assert.assertFalse(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 1, simpleBase));
        Assert.assertTrue(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, simpleBase));
        Assert.assertTrue(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID_2), 1, simpleBase));
        Assert.assertTrue(baseService.isLevelLimitation4ItemTypeExceeded((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
