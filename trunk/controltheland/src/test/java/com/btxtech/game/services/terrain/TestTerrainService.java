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

        Assert.assertFalse(terrainService.isFree(new Index(49, 49), 50, allowedSurfaces, null, null, null));
        Assert.assertTrue(terrainService.isFree(new Index(50, 50), 50, allowedSurfaces, null, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(1050, 50), 50, allowedSurfaces, null, null, null));

        Assert.assertTrue(terrainService.isFree(new Index(9950, 9950), 50, allowedSurfaces, null, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9951, 9950), 50, allowedSurfaces, null, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9950, 9951), 50, allowedSurfaces, null, null, null));
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
            Assert.assertEquals(!terrainImage1.adjoinsEclusive(new Rectangle(i - 50, 50, 100, 100)), terrainService.isFree(index, 50, allowedSurfaces, null, null, null));
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
            Assert.assertEquals(!terrainImage2.adjoinsEclusive(new Rectangle(50, i - 50, 100, 100)), terrainService.isFree(index, 50, allowedSurfaces, null, null, null));
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
        dbTerrainImageGroup1.setHtmlBackgroundColorCoast("#000003");
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
        dbTerrainImageGroup2.setHtmlBackgroundColorCoast("#000013");
        DbTerrainImage dbTerrainImage21 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage22 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage23 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup2);

        DbTerrainImageGroup dbTerrainImageGroup3 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup3.setHtmlBackgroundColorNone("#000020");
        dbTerrainImageGroup3.setHtmlBackgroundColorWater("#000021");
        dbTerrainImageGroup3.setHtmlBackgroundColorLand("#000022");
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
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.LAND));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.LAND));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.LAND));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.COAST));
    }

    @Test
    @DirtiesContext
    public void testIsFreePlayFieldBorder() throws Exception {
        configureSimplePlanetNoResources();
        ServerTerrainService terrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();
        ItemType radius80ItemType = itemTypeService.getItemType(TEST_ATTACK_ITEM_ID);

        Assert.assertFalse(terrainService.isFree(new Index(0, 0), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(0, 9999), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 0), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 9999), radius80ItemType, null, null));

        Assert.assertFalse(terrainService.isFree(new Index(5000, 0), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(0, 5000), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(5000, 9999), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9999, 5000), radius80ItemType, null, null));

        Assert.assertFalse(terrainService.isFree(new Index(79, 79), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(80, 79), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(79, 80), radius80ItemType, null, null));
        Assert.assertTrue(terrainService.isFree(new Index(80, 80), radius80ItemType, null, null));

        Assert.assertFalse(terrainService.isFree(new Index(79, 9921), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(80, 9921), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(79, 9920), radius80ItemType, null, null));
        Assert.assertTrue(terrainService.isFree(new Index(80, 9920), radius80ItemType, null, null));

        Assert.assertFalse(terrainService.isFree(new Index(9921, 79), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9920, 79), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9921, 80), radius80ItemType, null, null));
        Assert.assertTrue(terrainService.isFree(new Index(9920, 80), radius80ItemType, null, null));

        Assert.assertFalse(terrainService.isFree(new Index(9921, 9921), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9920, 9921), radius80ItemType, null, null));
        Assert.assertFalse(terrainService.isFree(new Index(9921, 9920), radius80ItemType, null, null));
        Assert.assertTrue(terrainService.isFree(new Index(9920, 9920), radius80ItemType, null, null));

        Assert.assertTrue(terrainService.isFree(new Index(5000, 5000), radius80ItemType, null, null));
    }

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
        dbCostSurfaceImage.setSurfaceType(SurfaceType.COAST);
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
        // Setup Harbor with adjoin surface type
        DbBaseItemType dbHarborItemTypeAdjoin = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbHarborItemTypeAdjoin, 1);
        dbHarborItemTypeAdjoin.setName("Harbor Adjoin");
        dbHarborItemTypeAdjoin.setTerrainType(TerrainType.WATER);
        dbHarborItemTypeAdjoin.setAdjoinSurfaceType(SurfaceType.COAST);
        dbHarborItemTypeAdjoin.setBounding(new BoundingBox(80, ANGELS_1));
        dbHarborItemTypeAdjoin.setHealth(10);
        dbHarborItemTypeAdjoin.setBuildup(10);
        dbHarborItemTypeAdjoin.setPrice(2);
        dbHarborItemTypeAdjoin.setXpOnKilling(2);
        dbHarborItemTypeAdjoin.setConsumingHouseSpace(2);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbHarborItemTypeAdjoin);
        DbBaseItemType dbHarborItemTypeEmpty = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbHarborItemTypeEmpty, 1);
        dbHarborItemTypeEmpty.setName("Harbor Adjoin");
        dbHarborItemTypeEmpty.setTerrainType(TerrainType.WATER);
        dbHarborItemTypeEmpty.setBounding(new BoundingBox(80, ANGELS_1));
        dbHarborItemTypeEmpty.setHealth(10);
        dbHarborItemTypeEmpty.setBuildup(10);
        dbHarborItemTypeEmpty.setPrice(2);
        dbHarborItemTypeEmpty.setXpOnKilling(2);
        dbHarborItemTypeEmpty.setConsumingHouseSpace(2);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbHarborItemTypeEmpty);
        DbBaseItemType dbHarborItemTypeNone = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbHarborItemTypeNone, 1);
        dbHarborItemTypeNone.setName("Harbor Adjoin");
        dbHarborItemTypeNone.setTerrainType(TerrainType.WATER);
        dbHarborItemTypeNone.setAdjoinSurfaceType(SurfaceType.NONE);
        dbHarborItemTypeNone.setBounding(new BoundingBox(80, ANGELS_1));
        dbHarborItemTypeNone.setHealth(10);
        dbHarborItemTypeNone.setBuildup(10);
        dbHarborItemTypeNone.setPrice(2);
        dbHarborItemTypeNone.setXpOnKilling(2);
        dbHarborItemTypeNone.setConsumingHouseSpace(2);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbHarborItemTypeNone);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemType harborItemTypeAdjoin = itemTypeService.getItemType(dbHarborItemTypeAdjoin.getId());
        ItemType harborItemTypeEmpty = itemTypeService.getItemType(dbHarborItemTypeEmpty.getId());
        ItemType harborItemTypeNone = itemTypeService.getItemType(dbHarborItemTypeNone.getId());
        ServerTerrainService serverTerrainService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getTerrainService();
        // builderMaxAdjoinDistance
        Assert.assertTrue(serverTerrainService.isFree(new Index(608, 500), harborItemTypeAdjoin, TerrainType.LAND_COAST, 30));
        Assert.assertTrue(serverTerrainService.isFree(new Index(609, 500), harborItemTypeAdjoin, TerrainType.LAND_COAST, 30));
        Assert.assertFalse(serverTerrainService.isFree(new Index(610, 500), harborItemTypeAdjoin, TerrainType.LAND_COAST, 30));
        Assert.assertTrue(serverTerrainService.isFree(new Index(608, 500), harborItemTypeEmpty, TerrainType.LAND_COAST, 30));
        Assert.assertTrue(serverTerrainService.isFree(new Index(609, 500), harborItemTypeEmpty, TerrainType.LAND_COAST, 30));
        // no difference Assert.assertFalse(serverTerrainService.isFree(new Index(610, 500), harborItemTypeEmpty, TerrainType.LAND_COAST, 30));
        // OK
        Assert.assertTrue(serverTerrainService.isFree(new Index(590, 130), harborItemTypeAdjoin, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(654, 154), harborItemTypeAdjoin, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(586, 366), harborItemTypeAdjoin, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(590, 130), harborItemTypeEmpty, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(654, 154), harborItemTypeEmpty, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(586, 366), harborItemTypeEmpty, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(590, 130), harborItemTypeNone, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(654, 154), harborItemTypeNone, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(586, 366), harborItemTypeNone, null, null));
        // Out of play field
        Assert.assertFalse(serverTerrainService.isFree(new Index(594, 46), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(612, 1076), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 500), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 1000), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(594, 46), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(612, 1076), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 500), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 1000), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(594, 46), harborItemTypeNone, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(612, 1076), harborItemTypeNone, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 500), harborItemTypeNone, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(1000, 1000), harborItemTypeNone, null, null));
        // No adjoin
        Assert.assertFalse(serverTerrainService.isFree(new Index(726, 268), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(848, 460), harborItemTypeAdjoin, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(726, 268), harborItemTypeEmpty, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(848, 460), harborItemTypeEmpty, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(726, 268), harborItemTypeNone, null, null));
        Assert.assertTrue(serverTerrainService.isFree(new Index(848, 460), harborItemTypeNone, null, null));
        // Cover coast
        Assert.assertFalse(serverTerrainService.isFree(new Index(531, 560), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(531, 560), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(531, 560), harborItemTypeNone, null, null));
        // On cost
        Assert.assertFalse(serverTerrainService.isFree(new Index(446, 755), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(446, 755), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(446, 755), harborItemTypeNone, null, null));
        // On land
        Assert.assertFalse(serverTerrainService.isFree(new Index(248, 765), harborItemTypeAdjoin, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(248, 765), harborItemTypeEmpty, null, null));
        Assert.assertFalse(serverTerrainService.isFree(new Index(248, 765), harborItemTypeNone, null, null));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}