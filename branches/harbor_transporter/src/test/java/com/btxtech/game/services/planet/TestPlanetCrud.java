package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 05.09.12
 * Time: 18:34
 */
public class TestPlanetCrud extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void testCreateModifyDelete() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel level1 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevel level2 = userGuidanceService.getDbLevelCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        DbPlanet dbPlanet1 = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet1.setName("Planet 1");
        dbPlanet1.setHtml("HTML 1");
        dbPlanet1.setMinLevel(userGuidanceService.getDbLevelCrud().readDbChild(level1.getId()));
        dbPlanet1.setMaxMoney(1);
        dbPlanet1.setHouseSpace(2);
        dbPlanet1.setRadarMode(RadarMode.MAP);
        dbPlanet1.setStartItemFreeRange(3);
        dbPlanet1.setStarMapImageContentType("img");
        dbPlanet1.setStarMapImageData(new byte[]{1, 2, 3, 4});
        dbPlanet1.setStarMapImagePosition(new Index(101, 99));
        dbPlanet1.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        DbPlanetItemTypeLimitation dbLimit11 = dbPlanet1.getItemLimitationCrud().createDbChild();
        dbLimit11.setCount(1);
        dbLimit11.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        DbPlanetItemTypeLimitation dbLimit12 = dbPlanet1.getItemLimitationCrud().createDbChild();
        dbLimit12.setCount(2);
        dbLimit12.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        DbPlanetItemTypeLimitation dbLimit13 = dbPlanet1.getItemLimitationCrud().createDbChild();
        dbLimit13.setCount(3);
        dbLimit13.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, planetSystemService.getDbPlanetCrud().readDbChildren().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        Assert.assertEquals("Planet 1", dbPlanet1.getName());
        Assert.assertEquals("HTML 1", dbPlanet1.getHtml());
        Assert.assertEquals(level1.getId(), dbPlanet1.getMinLevel().getId());
        Assert.assertEquals(1, dbPlanet1.getMaxMoney());
        Assert.assertEquals(2, dbPlanet1.getHouseSpace());
        Assert.assertEquals(RadarMode.MAP, dbPlanet1.getRadarMode());
        Assert.assertEquals(3, dbPlanet1.getStartItemFreeRange());
        Assert.assertEquals("img", dbPlanet1.getStarMapImageContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, dbPlanet1.getStarMapImageData());
        Assert.assertEquals(new Index(101, 99), dbPlanet1.getStarMapImagePosition());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbPlanet1.getStartItemType().getId());
        Assert.assertEquals(3, dbPlanet1.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit11.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(1, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit11.getId()).getCount());
        Assert.assertEquals(TEST_SIMPLE_BUILDING_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(2, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getCount());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(3, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create new planet
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet2.setName("Planet 2");
        dbPlanet2.setHtml("HTML 2");
        dbPlanet2.setMaxMoney(5);
        dbPlanet2.setHouseSpace(6);
        dbPlanet2.setRadarMode(RadarMode.MAP_AND_UNITS);
        dbPlanet2.setStartItemFreeRange(7);
        dbPlanet2.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        DbPlanetItemTypeLimitation dbLimit21 = dbPlanet2.getItemLimitationCrud().createDbChild();
        dbLimit21.setCount(2);
        dbLimit21.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        DbPlanetItemTypeLimitation dbLimit22 = dbPlanet2.getItemLimitationCrud().createDbChild();
        dbLimit22.setCount(4);
        dbLimit22.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID_2));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, planetSystemService.getDbPlanetCrud().readDbChildren().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        Assert.assertEquals("Planet 1", dbPlanet1.getName());
        Assert.assertEquals("HTML 1", dbPlanet1.getHtml());
        Assert.assertEquals(level1.getId(), dbPlanet1.getMinLevel().getId());
        Assert.assertEquals(1, dbPlanet1.getMaxMoney());
        Assert.assertEquals(2, dbPlanet1.getHouseSpace());
        Assert.assertEquals(RadarMode.MAP, dbPlanet1.getRadarMode());
        Assert.assertEquals("img", dbPlanet1.getStarMapImageContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, dbPlanet1.getStarMapImageData());
        Assert.assertEquals(new Index(101, 99), dbPlanet1.getStarMapImagePosition());
        Assert.assertEquals(3, dbPlanet1.getStartItemFreeRange());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, (int) dbPlanet1.getStartItemType().getId());
        Assert.assertEquals(3, dbPlanet1.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit11.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(1, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit11.getId()).getCount());
        Assert.assertEquals(TEST_SIMPLE_BUILDING_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(2, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getCount());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(3, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getCount());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertEquals("Planet 2", dbPlanet2.getName());
        Assert.assertEquals("HTML 2", dbPlanet2.getHtml());
        Assert.assertEquals(5, dbPlanet2.getMaxMoney());
        Assert.assertEquals(6, dbPlanet2.getHouseSpace());
        Assert.assertEquals(RadarMode.MAP_AND_UNITS, dbPlanet2.getRadarMode());
        Assert.assertEquals(7, dbPlanet2.getStartItemFreeRange());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getStartItemType().getId());
        Assert.assertEquals(2, dbPlanet2.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(2, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID_2, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(4, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        dbPlanet1.setName("Planet 3");
        dbPlanet1.setHtml("HTML 3");
        dbPlanet1.setMinLevel(userGuidanceService.getDbLevelCrud().readDbChild(level2.getId()));
        dbPlanet1.setMaxMoney(10);
        dbPlanet1.setHouseSpace(20);
        dbPlanet1.setRadarMode(RadarMode.NONE);
        dbPlanet1.setStartItemFreeRange(40);
        dbPlanet1.setStarMapImageContentType("img2");
        dbPlanet1.setStarMapImagePosition(new Index(202, 88));
        dbPlanet1.setStarMapImageData(new byte[]{3, 2, 3});
        dbPlanet1.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbPlanet1.getItemLimitationCrud().deleteDbChild(dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit11.getId()));
        dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).setCount(10);
        dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        dbPlanet2.setName("Planet 4");
        DbPlanetItemTypeLimitation dbLimit23 = dbPlanet2.getItemLimitationCrud().createDbChild();
        dbLimit23.setCount(20);
        dbLimit23.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, planetSystemService.getDbPlanetCrud().readDbChildren().size());
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        Assert.assertEquals("Planet 3", dbPlanet1.getName());
        Assert.assertEquals("HTML 3", dbPlanet1.getHtml());
        Assert.assertEquals(level2.getId(), dbPlanet1.getMinLevel().getId());
        Assert.assertEquals(10, dbPlanet1.getMaxMoney());
        Assert.assertEquals(20, dbPlanet1.getHouseSpace());
        Assert.assertEquals(RadarMode.NONE, dbPlanet1.getRadarMode());
        Assert.assertEquals(40, dbPlanet1.getStartItemFreeRange());
        Assert.assertEquals("img2", dbPlanet1.getStarMapImageContentType());
        Assert.assertEquals(new Index(202, 88), dbPlanet1.getStarMapImagePosition());
        Assert.assertArrayEquals(new byte[]{3, 2, 3}, dbPlanet1.getStarMapImageData());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, (int) dbPlanet1.getStartItemType().getId());
        Assert.assertEquals(2, dbPlanet1.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(10, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit12.getId()).getCount());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, (int) dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(3, dbPlanet1.getItemLimitationCrud().readDbChild(dbLimit13.getId()).getCount());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertEquals("Planet 4", dbPlanet2.getName());
        Assert.assertEquals("HTML 2", dbPlanet2.getHtml());
        Assert.assertEquals(5, dbPlanet2.getMaxMoney());
        Assert.assertEquals(6, dbPlanet2.getHouseSpace());
        Assert.assertEquals(RadarMode.MAP_AND_UNITS, dbPlanet2.getRadarMode());
        Assert.assertEquals(7, dbPlanet2.getStartItemFreeRange());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getStartItemType().getId());
        Assert.assertEquals(3, dbPlanet2.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(2, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID_2, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(4, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getCount());
        Assert.assertEquals(TEST_SIMPLE_BUILDING_ID, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit23.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(20, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit23.getId()).getCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Delete planet
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet1 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet1.getId());
        planetSystemService.getDbPlanetCrud().deleteDbChild(dbPlanet1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, planetSystemService.getDbPlanetCrud().readDbChildren().size());
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        Assert.assertEquals("Planet 4", dbPlanet2.getName());
        Assert.assertEquals("HTML 2", dbPlanet2.getHtml());
        Assert.assertEquals(5, dbPlanet2.getMaxMoney());
        Assert.assertEquals(6, dbPlanet2.getHouseSpace());
        Assert.assertEquals(RadarMode.MAP_AND_UNITS, dbPlanet2.getRadarMode());
        Assert.assertEquals(7, dbPlanet2.getStartItemFreeRange());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getStartItemType().getId());
        Assert.assertEquals(3, dbPlanet2.getItemLimitationCrud().readDbChildren().size());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(2, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit21.getId()).getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID_2, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(4, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit22.getId()).getCount());
        Assert.assertEquals(TEST_SIMPLE_BUILDING_ID, (int) dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit23.getId()).getDbBaseItemType().getId());
        Assert.assertEquals(20, dbPlanet2.getItemLimitationCrud().readDbChild(dbLimit23.getId()).getCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Delete planet
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId());
        planetSystemService.getDbPlanetCrud().deleteDbChild(dbPlanet2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, planetSystemService.getDbPlanetCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
