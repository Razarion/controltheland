package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.ScatterSurfaceImageInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbScatterSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * User: beat
 * Date: 03.09.13
 * Time: 15:07
 */
public class TestMovableServiceTerrain extends AbstractServiceTest {
    @Autowired
    private TerrainImageService terrainService;
    @Autowired
    private ClipService clipService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    public void testRealGame() throws Exception {
        // Setup clip
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap1.setFrameCount(1);
        dbImageSpriteMap1.setFrameWidth(2);
        dbImageSpriteMap1.setFrameHeight(3);
        dbImageSpriteMap1.setFrameTime(4);
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup terrain images
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImageGroup dbTerrainImageGroup1 = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup1.setHtmlBackgroundColorLand("#111111");
        dbTerrainImageGroup1.setHtmlBackgroundColorCoast("#222222");
        dbTerrainImageGroup1.setHtmlBackgroundColorNone("#333333");
        dbTerrainImageGroup1.setHtmlBackgroundColorWater("#444444");
        dbTerrainImageGroup1.setImageSpriteMap(clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()));
        DbTerrainImage dbTerrainImage11 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        dbTerrainImage11.setTiles(1, 2);
        dbTerrainImage11.setSurfaceType(0, 0, SurfaceType.LAND);
        dbTerrainImage11.setSurfaceType(0, 1, SurfaceType.COAST);
        DbTerrainImage dbTerrainImage12 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        dbTerrainImage12.setTiles(2, 1);
        dbTerrainImage12.setSurfaceType(0, 0, SurfaceType.COAST);
        dbTerrainImage12.setSurfaceType(1, 0, SurfaceType.WATER);
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImageGroup dbTerrainImageGroup2 = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup2.setHtmlBackgroundColorLand("#A11111");
        dbTerrainImageGroup2.setHtmlBackgroundColorCoast("#A22222");
        dbTerrainImageGroup2.setHtmlBackgroundColorNone("#A33333");
        dbTerrainImageGroup2.setHtmlBackgroundColorWater("#A44444");
        DbTerrainImage dbTerrainImage21 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        dbTerrainImage21.setTiles(3, 2);
        DbTerrainImage dbTerrainImage22 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        dbTerrainImage22.setTiles(4, 3);
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup surface images
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSurfaceImage dbSurfaceImage1 = terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage1.setSurfaceType(SurfaceType.LAND);
        dbSurfaceImage1.setHtmlBackgroundColor("back1");
        dbSurfaceImage1.setImageSpriteMap(clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()));
        terrainService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage1);
        DbSurfaceImage dbSurfaceImage2 = terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage2.setHtmlBackgroundColor("back2");
        dbSurfaceImage2.setSurfaceType(SurfaceType.WATER);
        terrainService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage2);
        DbSurfaceImage dbSurfaceImage3 = terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage3.setHtmlBackgroundColor("back3");
        dbSurfaceImage3.setSurfaceType(SurfaceType.LAND);
        dbSurfaceImage3.setUncommon(0.3);
        dbSurfaceImage3.setRare(0.1);
        DbScatterSurfaceImage dbScatterSurfaceImage = dbSurfaceImage3.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbScatterSurfaceImage.setContentType("png");
        dbScatterSurfaceImage = dbSurfaceImage3.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0002.png").openStream()));
        dbScatterSurfaceImage.setContentType("png");
        dbScatterSurfaceImage = dbSurfaceImage3.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.RARE);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0003.png").openStream()));
        dbScatterSurfaceImage.setContentType("png");
        terrainService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage3);
        terrainService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup planet with terrain & level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbTerrainSetting dbTerrainSetting = dbPlanet.getDbTerrainSetting();
        dbTerrainSetting.setTileXCount(90);
        dbTerrainSetting.setTileYCount(80);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 50), terrainService.getDbSurfaceImageCrudServiceHelper().readDbChild(dbSurfaceImage1.getId()));
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 50, 100, 50), terrainService.getDbSurfaceImageCrudServiceHelper().readDbChild(dbSurfaceImage2.getId()));
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(new Index(0, 0),
                terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChild(dbTerrainImageGroup1.getId()).getTerrainImageCrud().readDbChild(dbTerrainImage11.getId()), TerrainImagePosition.ZIndex.LAYER_1),
                null);
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(new Index(10, 0),
                terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChild(dbTerrainImageGroup1.getId()).getTerrainImageCrud().readDbChild(dbTerrainImage12.getId()), TerrainImagePosition.ZIndex.LAYER_2),
                null);
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(new Index(20, 0),
                terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChild(dbTerrainImageGroup2.getId()).getTerrainImageCrud().readDbChild(dbTerrainImage21.getId()), TerrainImagePosition.ZIndex.LAYER_1),
                null);
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(new Index(30, 0),
                terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChild(dbTerrainImageGroup2.getId()).getTerrainImageCrud().readDbChild(dbTerrainImage22.getId()), TerrainImagePosition.ZIndex.LAYER_2),
                null);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 5000, 5000)));
        dbPlanet.setStartItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.activate();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel.setXp(10);
        dbLevel.setDbPlanet(dbPlanet);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        // Test terrain settings
        Assert.assertEquals(9000, realGameInfo.getTerrainSettings().getPlayFieldXSize());
        Assert.assertEquals(8000, realGameInfo.getTerrainSettings().getPlayFieldYSize());
        // Test surface images
        Assert.assertEquals(3, realGameInfo.getSurfaceImages().size());
        assertSurfaceImage(realGameInfo.getSurfaceImages(), dbSurfaceImage1.getId(), dbImageSpriteMap1.getId(), null, "back1", SurfaceType.LAND);
        assertSurfaceImage(realGameInfo.getSurfaceImages(), dbSurfaceImage2.getId(), null, null, "back2", SurfaceType.WATER);
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.3);
        scatterSurfaceImageInfo.setRare(0.1);
        scatterSurfaceImageInfo.setCommonImageCount(1);
        scatterSurfaceImageInfo.setUncommonImageCount(1);
        scatterSurfaceImageInfo.setRareImageCount(1);
        assertSurfaceImage(realGameInfo.getSurfaceImages(), dbSurfaceImage3.getId(), null, scatterSurfaceImageInfo, "back3", SurfaceType.LAND);
        // Test terrain images
        Assert.assertEquals(4, realGameInfo.getTerrainImages().size());
        assertTerrainImage(realGameInfo.getTerrainImages(), dbTerrainImage11.getId(), dbImageSpriteMap1.getId(), 1, 2, new SurfaceType[][]{{SurfaceType.LAND, SurfaceType.COAST}});
        assertTerrainImage(realGameInfo.getTerrainImages(), dbTerrainImage12.getId(), dbImageSpriteMap1.getId(), 2, 1, new SurfaceType[][]{{SurfaceType.COAST}, {SurfaceType.WATER}});
        assertTerrainImage(realGameInfo.getTerrainImages(), dbTerrainImage21.getId(), null, 3, 2, new SurfaceType[][]{{SurfaceType.NONE, SurfaceType.NONE}, {SurfaceType.NONE, SurfaceType.NONE}, {SurfaceType.NONE, SurfaceType.NONE}});
        assertTerrainImage(realGameInfo.getTerrainImages(), dbTerrainImage22.getId(), null, 4, 3, new SurfaceType[][]{{SurfaceType.NONE, SurfaceType.NONE, SurfaceType.NONE}, {SurfaceType.NONE, SurfaceType.NONE, SurfaceType.NONE}, {SurfaceType.NONE, SurfaceType.NONE, SurfaceType.NONE}, {SurfaceType.NONE, SurfaceType.NONE, SurfaceType.NONE}});
        // Test terrain images background. Only short tests. Is fully tested in TestTerrainService
        Assert.assertEquals("#111111", realGameInfo.getTerrainImageBackground().get(dbTerrainImage11.getId(), SurfaceType.LAND));
        Assert.assertEquals("#222222", realGameInfo.getTerrainImageBackground().get(dbTerrainImage12.getId(), SurfaceType.COAST));
        Assert.assertEquals("#444444", realGameInfo.getTerrainImageBackground().get(dbTerrainImage12.getId(), SurfaceType.WATER));
        Assert.assertEquals("#A33333", realGameInfo.getTerrainImageBackground().get(dbTerrainImage21.getId(), SurfaceType.NONE));
        Assert.assertEquals("#A33333", realGameInfo.getTerrainImageBackground().get(dbTerrainImage22.getId(), SurfaceType.NONE));
        // Surface positions
        Assert.assertEquals(2, realGameInfo.getSurfaceRects().size());
        assertSurfaceRect(realGameInfo.getSurfaceRects(), new Rectangle(0, 0, 100, 50), dbSurfaceImage1.getId());
        assertSurfaceRect(realGameInfo.getSurfaceRects(), new Rectangle(0, 50, 100, 50), dbSurfaceImage2.getId());
        // Terrain images positions
        Assert.assertEquals(4, realGameInfo.getTerrainImagePositions().size());
        assertTerrainImagePosition(realGameInfo.getTerrainImagePositions(), new Index(0, 0), dbTerrainImage11.getId(), TerrainImagePosition.ZIndex.LAYER_1);
        assertTerrainImagePosition(realGameInfo.getTerrainImagePositions(), new Index(10, 0), dbTerrainImage12.getId(), TerrainImagePosition.ZIndex.LAYER_2);
        assertTerrainImagePosition(realGameInfo.getTerrainImagePositions(), new Index(20, 0), dbTerrainImage21.getId(), TerrainImagePosition.ZIndex.LAYER_1);
        assertTerrainImagePosition(realGameInfo.getTerrainImagePositions(), new Index(30, 0), dbTerrainImage22.getId(), TerrainImagePosition.ZIndex.LAYER_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertTerrainImagePosition(Collection<TerrainImagePosition> terrainImagePositions, Index tileIndex, int imageId, TerrainImagePosition.ZIndex zIndex) {
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (terrainImagePosition.getTileIndex().equals(tileIndex)) {
                Assert.assertEquals(imageId, terrainImagePosition.getImageId());
                Assert.assertEquals(zIndex, terrainImagePosition.getzIndex());
                Assert.assertEquals(zIndex, terrainImagePosition.getzIndex());
                return;
            }
        }
        Assert.fail("No SurfaceRect for tileIndex: " + tileIndex);
    }

    private void assertSurfaceRect(Collection<SurfaceRect> surfaceRects, Rectangle rectangle, int surfaceImageId) {
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileRectangle().equals(rectangle)) {
                Assert.assertEquals(surfaceImageId, surfaceRect.getSurfaceImageId());
                return;
            }
        }
        Assert.fail("No SurfaceRect for rectangle: " + rectangle);
    }

    private void assertTerrainImage(Collection<TerrainImage> terrainImages, int id, Integer imageSpriteMapId, int tileWidth, int tileHeight, SurfaceType[][] surfaceTypes) {
        for (TerrainImage terrainImage : terrainImages) {
            if (terrainImage.getId() == id) {
                if (imageSpriteMapId != null) {
                    Assert.assertEquals((int) imageSpriteMapId, terrainImage.getImageSpriteMapInfo().getId());
                } else {
                    Assert.assertNull(terrainImage.getImageSpriteMapInfo());
                }
                Assert.assertEquals(tileWidth, terrainImage.getTileWidth());
                Assert.assertEquals(tileHeight, terrainImage.getTileHeight());
                Assert.assertArrayEquals(surfaceTypes, terrainImage.getSurfaceTypes());
                return;
            }
        }
        Assert.fail("No SurfaceImage for id: " + id);
    }

    private void assertSurfaceImage(Collection<SurfaceImage> surfaceImages, int id, Integer imageSpriteMapId, ScatterSurfaceImageInfo scatterSurfaceImageInfo, String htmlBackgroundColor, SurfaceType surfaceType) throws Exception {
        for (SurfaceImage surfaceImage : surfaceImages) {
            if (surfaceImage.getImageId() == id) {
                if (imageSpriteMapId != null) {
                    Assert.assertEquals((int) imageSpriteMapId, surfaceImage.getImageSpriteMapInfo().getId());
                    Assert.assertNull(surfaceImage.getScatterSurfaceImageInfo());
                } else if (scatterSurfaceImageInfo != null) {
                    Assert.assertNull(surfaceImage.getImageSpriteMapInfo());
                    Assert.assertNotNull(surfaceImage.getScatterSurfaceImageInfo());
                    ScatterSurfaceImageInfo received = surfaceImage.getScatterSurfaceImageInfo();
                    assertPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, received, "uncommon");
                    assertPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, received, "rare");
                    assertPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, received, "commonImageCount");
                    assertPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, received, "uncommonImageCount");
                    assertPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, received, "rareImageCount");
                } else {
                    Assert.assertNull(surfaceImage.getImageSpriteMapInfo());
                    Assert.assertNull(surfaceImage.getScatterSurfaceImageInfo());
                }
                Assert.assertEquals(htmlBackgroundColor, surfaceImage.getHtmlBackgroundColor());
                Assert.assertEquals(surfaceType, surfaceImage.getSurfaceType());
                return;
            }
        }
        Assert.fail("No SurfaceImage for id: " + id);
    }
}
