package com.btxtech.game.services.terrain;

import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerrainServiceManipulation extends AbstractServiceTest {
    @Autowired
    private TerrainImageService terrainService;

    @Test
    @DirtiesContext
    public void testSaveEmptyMap() {
        Assert.fail();
        // TODO
        /*
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUniqueImagePosition() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImage dbTerrainImage = createDbTerrainImage(2, 2);
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLayer1_2() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImage dbTerrainImage = createDbTerrainImage(2, 2);
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_2));
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_2));
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));

        Collection<DbTerrainImagePosition> dbTerrainImagePositions = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChild(dbTerrainSetting.getId()).getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        List<DbTerrainImagePosition> list = new ArrayList<>(dbTerrainImagePositions);
        if (list.get(0).getzIndex() == TerrainImagePosition.ZIndex.LAYER_1) {
            Assert.assertEquals(TerrainImagePosition.ZIndex.LAYER_2, list.get(1).getzIndex());
        } else {
            Assert.assertEquals(TerrainImagePosition.ZIndex.LAYER_1, list.get(1).getzIndex());
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUniqueSurfaceRect() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSurfaceImage dbSurfaceImage = createDbSurfaceImage(SurfaceType.LAND);
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage.getId()));
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage.getId()));
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveNewMap() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImage dbTerrainImage = createDbTerrainImage(1, 1);
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), 1));
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveImagePositionMulti() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImage dbTerrainImage = createDbTerrainImage(1, 1);
        // Fill with 1600 tiles. Override the original first tile
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
            }
        }
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));
        verifyImagePositions(0, 40, 0, 40);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Override all 1600 first tile
        terrainImagePositions = new ArrayList<>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
            }
        }
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));
        verifyImagePositions(0, 40, 0, 40);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Override 1600
        terrainImagePositions = new ArrayList<>();
        for (int x = 20; x < 60; x++) {
            for (int y = 20; y < 60; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
            }
        }
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(0, getSurfaceRectCount(dbTerrainSetting));
        verifyImagePositions(20, 60, 20, 60);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveSurfaceRectMulti() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1, getSurfaceRectCount(dbTerrainSetting));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Fill with 1600 tiles. Override the original first tile
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        Collection<TerrainImagePosition> imagePositions = new ArrayList<>();
        terrainService.saveTerrain(imagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTerrainSetting));
        verifySurfaceRects(0, 40, 0, 40, 1, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Override all 1600 first tile
        surfaceRects = new ArrayList<>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        terrainService.saveTerrain(imagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTerrainSetting));
        verifySurfaceRects(0, 40, 0, 40, 1, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Override 1600
        surfaceRects = new ArrayList<>();
        for (int x = 20; x < 60; x++) {
            for (int y = 20; y < 60; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        terrainService.saveTerrain(imagePositions, surfaceRects, dbTerrainSetting.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTerrainSetting));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTerrainSetting));
        verifySurfaceRects(20, 60, 20, 60, 1, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private int getSurfaceRectCount(DbTerrainSetting dbTerrainSetting) {
        return terrainService.getDbTerrainSettingCrudServiceHelper().readDbChild(dbTerrainSetting.getId()).getDbSurfaceRectCrudServiceHelper().readDbChildren().size();
    }

    private int getTerrainTileCount(DbTerrainSetting dbTerrainSetting) {
        return terrainService.getDbTerrainSettingCrudServiceHelper().readDbChild(dbTerrainSetting.getId()).getDbTerrainImagePositionCrudServiceHelper().readDbChildren().size();
    }


    private void verifyImagePositions(int xFrom, int xTo, int yFrom, int yTo) {
        Collection<DbTerrainImagePosition> terrainImagePositions = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next().getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        for (DbTerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (terrainImagePosition.getTileX() < xFrom
                    || terrainImagePosition.getTileX() > xTo
                    || terrainImagePosition.getTileY() < yFrom
                    || terrainImagePosition.getTileY() > yTo) {
                Assert.fail("ImagePosition is out of band");
            }
        }
    }

    private void verifySurfaceRects(int xFrom, int xTo, int yFrom, int yTo, int width, int height) {
        Collection<DbSurfaceRect> dbSurfaceRects = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next().getDbSurfaceRectCrudServiceHelper().readDbChildren();
        for (DbSurfaceRect dbSurfaceRect : dbSurfaceRects) {
            if (dbSurfaceRect.getRectangle().getX() < xFrom
                    || dbSurfaceRect.getRectangle().getEndX() > xTo
                    || dbSurfaceRect.getRectangle().getY() < yFrom
                    || dbSurfaceRect.getRectangle().getEndY() > yTo
                    || dbSurfaceRect.getRectangle().getWidth() > width
                    || dbSurfaceRect.getRectangle().getHeight() > height) {
                Assert.fail("SurfaceRect is out of band");
            }
        }*/
    }


}