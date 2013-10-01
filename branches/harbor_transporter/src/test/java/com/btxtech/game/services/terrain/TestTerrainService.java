package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerTerrainService;
import com.btxtech.game.services.planet.db.DbPlanet;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerrainService extends AbstractServiceTest {
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ItemTypeService itemTypeService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testIsFreeSimple() throws Exception {
        configureOneLevelOnePlaneComplexTerrain();
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Assert.assertFalse(terrainService.isFree(new Index(49, 49), 50, allowedSurfaces, null));
        Assert.assertTrue(terrainService.isFree(new Index(50, 50), 50, allowedSurfaces, null));
        Assert.assertFalse(terrainService.isFree(new Index(1050, 50), 50, allowedSurfaces, null));

        Assert.assertTrue(terrainService.isFree(new Index(9950, 9950), 50, allowedSurfaces, null));
        Assert.assertFalse(terrainService.isFree(new Index(9951, 9950), 50, allowedSurfaces, null));
        Assert.assertFalse(terrainService.isFree(new Index(9950, 9951), 50, allowedSurfaces, null));
    }

    @Test
    @DirtiesContext
    public void testIsFreeXCount() throws Exception {
        configureOneLevelOnePlaneComplexTerrain();
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage1 = new Rectangle(1000, 0, 400, 1000);

        for (int i = 100; i < 1500; i++) {
            Index index = new Index(i, 100);
            Assert.assertEquals(!terrainImage1.adjoinsEclusive(new Rectangle(i - 50, 50, 100, 100)), terrainService.isFree(index, 50, allowedSurfaces, null));
        }
    }

    @Test
    @DirtiesContext
    public void testIsFreeYCount() throws Exception {
        configureOneLevelOnePlaneComplexTerrain();
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage2 = new Rectangle(0, 1300, 1000, 400);

        for (int i = 100; i < 1500; i++) {
            Index index = new Index(100, i);
            Assert.assertEquals(!terrainImage2.adjoinsEclusive(new Rectangle(50, i - 50, 100, 100)), terrainService.isFree(index, 50, allowedSurfaces, null));
        }

    }

    @Test
    @DirtiesContext
    public void testTerrainImageBackground() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.setTileXCount(10);
        dbTerrainSetting.setTileYCount(10);

        DbTerrainImageGroup dbTerrainImageGroup1 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup1.setHtmlBackgroundColorNone("#000000");
        dbTerrainImageGroup1.setHtmlBackgroundColorWater("#000001");
        dbTerrainImageGroup1.setHtmlBackgroundColorLand("#000002");
        dbTerrainImageGroup1.setHtmlBackgroundColorWaterCoast("#000003");
        dbTerrainImageGroup1.setHtmlBackgroundColorLandCoast("#000004");
        DbTerrainImage dbTerrainImage11 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage12 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage13 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage14 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage15 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup1);

        DbTerrainImageGroup dbTerrainImageGroup2 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup2.setHtmlBackgroundColorNone("#000010");
        dbTerrainImageGroup2.setHtmlBackgroundColorWater("#000011");
        dbTerrainImageGroup2.setHtmlBackgroundColorLand("#000012");
        dbTerrainImageGroup2.setHtmlBackgroundColorWaterCoast("#000013");
        dbTerrainImageGroup2.setHtmlBackgroundColorLandCoast("#000014");
        DbTerrainImage dbTerrainImage21 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage22 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage23 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup2);

        DbTerrainImageGroup dbTerrainImageGroup3 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup3.setHtmlBackgroundColorNone("#000020");
        dbTerrainImageGroup3.setHtmlBackgroundColorWater("#000021");
        dbTerrainImageGroup3.setHtmlBackgroundColorLand("#000022");
        dbTerrainImageGroup3.setHtmlBackgroundColorWaterCoast("#000023");
        DbTerrainImage dbTerrainImage31 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage32 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage33 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup3);

        terrainImageService.activate();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        TerrainImageBackground backgrounds = terrainImageService.getTerrainImageBackground();

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.LAND_COAST));
    }

    @Test
    @DirtiesContext
    public void testIsFreePlayFieldBorder() throws Exception {
        configureSimplePlanetNoResources();
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();
        ItemType radius80ItemType = itemTypeService.getItemType(TEST_ATTACK_ITEM_ID);

        Assert.assertFalse(terrainService.isFree(new Index(0, 0), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(0, 9999), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 0), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 9999), radius80ItemType));

        Assert.assertFalse(terrainService.isFree(new Index(5000, 0), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(0, 5000), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(5000, 9999), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 5000), radius80ItemType));

        Assert.assertFalse(terrainService.isFree(new Index(79, 79), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(80, 79), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(79, 80), radius80ItemType));
        Assert.assertTrue(terrainService.isFree(new Index(80, 80), radius80ItemType));

        Assert.assertFalse(terrainService.isFree(new Index(79, 9921), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(80, 9921), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(79, 9920), radius80ItemType));
        Assert.assertTrue(terrainService.isFree(new Index(80, 9920), radius80ItemType));

        Assert.assertFalse(terrainService.isFree(new Index(9921, 79), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9920, 79), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9921, 80), radius80ItemType));
        Assert.assertTrue(terrainService.isFree(new Index(9920, 80), radius80ItemType));

        Assert.assertFalse(terrainService.isFree(new Index(9921, 9921), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9920, 9921), radius80ItemType));
        Assert.assertFalse(terrainService.isFree(new Index(9921, 9920), radius80ItemType));
        Assert.assertTrue(terrainService.isFree(new Index(9920, 9920), radius80ItemType));

        Assert.assertTrue(terrainService.isFree(new Index(5000, 5000), radius80ItemType));
    }

    // TODO test harbor builder
    // TODO test Adjoin SurfaceType -> NONE

    @Test
    @DirtiesContext
    public void testIsFreeAdjoin() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup surface images ------------------
        DbSurfaceImage dbLandSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbLandSurfaceImage.setSurfaceType(SurfaceType.LAND);
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbLandSurfaceImage);
        DbSurfaceImage dbWaterSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbWaterSurfaceImage.setSurfaceType(SurfaceType.WATER);
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbWaterSurfaceImage);
        DbSurfaceImage dbCostSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbCostSurfaceImage.setSurfaceType(SurfaceType.LAND_COAST);
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbCostSurfaceImage);
        terrainImageService.activate();
        // Setup surface ------------------
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        dbPlanet.getDbTerrainSetting().setTileXCount(10);
        dbPlanet.getDbTerrainSetting().setTileYCount(11);
        dbPlanet.getDbTerrainSetting().getDbTerrainImagePositionCrudServiceHelper().deleteAllChildren();
        dbPlanet.getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().deleteAllChildren();
        dbPlanet.getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 0, 4, 11), dbLandSurfaceImage), null);
        dbPlanet.getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(4, 0, 1, 11), dbCostSurfaceImage), null);
        dbPlanet.getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(5, 0, 5, 11), dbWaterSurfaceImage), null);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(dbPlanet.getId());
        planetSystemService.activatePlanet(dbPlanet.getId());
        // Setup Harbor
        DbBaseItemType dbHarborItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbHarborItemType, 1);
        dbHarborItemType.setName("Harbor");
        dbHarborItemType.setTerrainType(TerrainType.WATER);
        dbHarborItemType.setAdjoinSurfaceType(SurfaceType.LAND_COAST);
        dbHarborItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbHarborItemType.setHealth(10);
        dbHarborItemType.setBuildup(10);
        dbHarborItemType.setPrice(2);
        dbHarborItemType.setXpOnKilling(2);
        dbHarborItemType.setConsumingHouseSpace(2);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbHarborItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemType harborItemType = itemTypeService.getItemType(dbHarborItemType.getId());
        ServerTerrainService serverTerrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();
        // OK
        Assert.assertTrue(serverTerrainService.isFree(new Index(590, 130), harborItemType));
        Assert.assertTrue(serverTerrainService.isFree(new Index(654, 154), harborItemType));
        Assert.assertTrue(serverTerrainService.isFree(new Index(586, 366), harborItemType));
        // Out of play field
        Assert.assertFalse(serverTerrainService.isFree(new Index(594, 46), harborItemType));
        Assert.assertFalse(serverTerrainService.isFree(new Index(612, 1076), harborItemType));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 500), harborItemType));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 1000), harborItemType));
        // No adjoin
        Assert.assertFalse(serverTerrainService.isFree(new Index(726, 268), harborItemType));
        Assert.assertFalse(serverTerrainService.isFree(new Index(848, 460), harborItemType));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}