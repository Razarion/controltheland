package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelItemTypeLimitation;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 29.08.12
 * Time: 00:59
 */
public class TestPlanetSystem extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void testStartup() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // ---------- Global ----------
        setupItemTypes();
        // Terrain Image
        DbSurfaceImage dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setSurfaceType(SurfaceType.LAND);
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        terrainImageService.activate();
        // Levels
        DbLevel realGameLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        realGameLevel.setXp(1);
        DbLevelItemTypeLimitation levelItemTypeLimitation = realGameLevel.getItemTypeLimitationCrud().createDbChild();
        levelItemTypeLimitation.setCount(1);
        levelItemTypeLimitation.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        userGuidanceService.getDbLevelCrud().updateDbChild(realGameLevel);
        userGuidanceService.activateLevels();
        // ---------- Planet ----------
        // Setup planets
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet.setStartMoney(1000);
        dbPlanet.setStartItemFreeRange(100);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 1000, 1000)));
        dbPlanet.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        DbPlanetItemTypeLimitation planetItemTypeLimitation = dbPlanet.getItemLimitationCrud().createDbChild();
        planetItemTypeLimitation.setCount(1);
        planetItemTypeLimitation.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbPlanet.setHouseSpace(1);
        // Terrain
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbPlanet.setDbTerrainSetting(dbTerrainSetting);
        //
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        // ---------- post config ----------
        realGameLevel = userGuidanceService.getDbLevelCrud().readDbChild(realGameLevel.getId());
        realGameLevel.setDbPlanet(dbPlanet);
        userGuidanceService.getDbLevelCrud().updateDbChild(realGameLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals(1, planetSystemService.getRunningPlanets().size());
        Planet planet = CommonJava.getFirst(planetSystemService.getRunningPlanets());

        // Simulate User
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState = userService.getUserState();
        planetSystemService.createBase(userState);
        Assert.assertEquals(1, planet.getPlanetServices().getBaseService().getBases().size());
        Assert.assertEquals(1, getAllSynItemId(TEST_START_BUILDER_ITEM_ID).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void isUserOnCorrectPlanet() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState = userService.getUserState();
        Assert.assertFalse(planetSystemService.isUserOnCorrectPlanet(userState));
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        getMyBase();  // Build base
        Assert.assertTrue(planetSystemService.isUserOnCorrectPlanet(userState));
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        Assert.assertFalse(planetSystemService.isUserOnCorrectPlanet(userState));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
