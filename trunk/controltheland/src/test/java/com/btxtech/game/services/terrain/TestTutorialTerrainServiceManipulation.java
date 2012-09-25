package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTutorialTerrainServiceManipulation extends AbstractServiceTest {
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void testSaveEmptyMap() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), createDbSurfaceImage(SurfaceType.LAND));
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1, getSurfaceRectCount(dbTutorialConfig));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        Collection<SurfaceRect> surfaceRects = new ArrayList<>();
        tutorialService.saveTerrain(terrainImagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(0, getSurfaceRectCount(dbTutorialConfig));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveImagePositionMulti() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), createDbSurfaceImage(SurfaceType.LAND));
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1, getSurfaceRectCount(dbTutorialConfig));
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
        tutorialService.saveTerrain(terrainImagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(0, getSurfaceRectCount(dbTutorialConfig));
        verifyImagePositions(dbTutorialConfig, 0, 40, 0, 40);
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
        tutorialService.saveTerrain(terrainImagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(0, getSurfaceRectCount(dbTutorialConfig));
        verifyImagePositions(dbTutorialConfig, 0, 40, 0, 40);
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
        tutorialService.saveTerrain(terrainImagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1600, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(0, getSurfaceRectCount(dbTutorialConfig));
        verifyImagePositions(dbTutorialConfig, 20, 60, 20, 60);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveSurfaceRectMulti() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), createDbSurfaceImage(SurfaceType.LAND));
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1, getSurfaceRectCount(dbTutorialConfig));
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
        tutorialService.saveTerrain(imagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTutorialConfig));
        verifySurfaceRects(dbTutorialConfig, 0, 40, 0, 40, 1, 1);
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
        tutorialService.saveTerrain(imagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTutorialConfig));
        verifySurfaceRects(dbTutorialConfig, 0, 40, 0, 40, 1, 1);
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
        tutorialService.saveTerrain(imagePositions, surfaceRects, dbTutorialConfig.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, getTerrainTileCount(dbTutorialConfig));
        Assert.assertEquals(1600, getSurfaceRectCount(dbTutorialConfig));
        verifySurfaceRects(dbTutorialConfig, 20, 60, 20, 60, 1, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private int getSurfaceRectCount(DbTutorialConfig dbTutorialConfig) {
        return tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().readDbChildren().size();
    }

    private int getTerrainTileCount(DbTutorialConfig dbTutorialConfig) {
        return tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTerrainSetting().getDbTerrainImagePositionCrudServiceHelper().readDbChildren().size();
    }

    private void verifyImagePositions(DbTutorialConfig dbTutorialConfig, int xFrom, int xTo, int yFrom, int yTo) {
        Collection<DbTerrainImagePosition> terrainImagePositions = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTerrainSetting().getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        for (DbTerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (terrainImagePosition.getTileX() < xFrom
                    || terrainImagePosition.getTileX() > xTo
                    || terrainImagePosition.getTileY() < yFrom
                    || terrainImagePosition.getTileY() > yTo) {
                Assert.fail("ImagePosition is out of band");
            }
        }
    }

    private void verifySurfaceRects(DbTutorialConfig dbTutorialConfig, int xFrom, int xTo, int yFrom, int yTo, int width, int height) {
        Collection<DbSurfaceRect> dbSurfaceRects = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(dbTutorialConfig.getId()).getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().readDbChildren();
        for (DbSurfaceRect dbSurfaceRect : dbSurfaceRects) {
            if (dbSurfaceRect.getRectangle().getX() < xFrom
                    || dbSurfaceRect.getRectangle().getEndX() > xTo
                    || dbSurfaceRect.getRectangle().getY() < yFrom
                    || dbSurfaceRect.getRectangle().getEndY() > yTo
                    || dbSurfaceRect.getRectangle().getWidth() > width
                    || dbSurfaceRect.getRectangle().getHeight() > height) {
                Assert.fail("SurfaceRect is out of band");
            }
        }
    }

}