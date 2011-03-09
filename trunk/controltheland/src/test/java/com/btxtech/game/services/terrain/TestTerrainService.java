package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.BaseTestService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerrainService extends BaseTestService {
    @Autowired
    private TerrainService terrainService;

    @Test
    @DirtiesContext
    public void testSaveEmptyMap() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);

        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());
    }

    @Test
    @DirtiesContext
    public void testUniqueImagePosition() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), 1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), 1));
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);

        Assert.assertEquals(1, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());
    }

    @Test
    @DirtiesContext
    public void testUniqueSurfaceRect() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), 1));
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), 1));
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);

        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(1, getSurfaceRectCount());
    }

    @Test
    @DirtiesContext
    public void testSaveNewMap() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), 1));
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), 1));
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);

        Assert.assertEquals(1, getTerrainTileCount());
        Assert.assertEquals(1, getSurfaceRectCount());
    }

    @Test
    @DirtiesContext
    public void testSaveImagePositionMulti() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        // Fill with 1600 tiles. Override the original first tile
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), 1));
            }
        }
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);
        Assert.assertEquals(1600, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());
        verifyImagePositions(0, 40, 0, 40);

        // Override all 1600 first tile
        terrainImagePositions = new ArrayList<TerrainImagePosition>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), 1));
            }
        }
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);
        Assert.assertEquals(1600, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());
        verifyImagePositions(0, 40, 0, 40);

        // Override 1600
        terrainImagePositions = new ArrayList<TerrainImagePosition>();
        for (int x = 20; x < 60; x++) {
            for (int y = 20; y < 60; y++) {
                terrainImagePositions.add(new TerrainImagePosition(new Index(x, y), 1));
            }
        }
        terrainService.saveTerrain(terrainImagePositions, surfaceRects, 1);
        Assert.assertEquals(1600, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());
        verifyImagePositions(20, 60, 20, 60);
    }

    @Test
    @DirtiesContext
    public void testSaveSurfaceRectMulti() {
        setupMinimalTerrain();
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(0, getSurfaceRectCount());

        // Fill with 1600 tiles. Override the original first tile
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        Collection<TerrainImagePosition> imagePositions = new ArrayList<TerrainImagePosition>();
        terrainService.saveTerrain(imagePositions, surfaceRects, 1);
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(1600, getSurfaceRectCount());
        verifySurfaceRects(0, 40, 0, 40, 1, 1);

        // Override all 1600 first tile
        surfaceRects = new ArrayList<SurfaceRect>();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        terrainService.saveTerrain(imagePositions, surfaceRects, 1);
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(1600, getSurfaceRectCount());
        verifySurfaceRects(0, 40, 0, 40, 1, 1);

        // Override 1600
        surfaceRects = new ArrayList<SurfaceRect>();
        for (int x = 20; x < 60; x++) {
            for (int y = 20; y < 60; y++) {
                surfaceRects.add(new SurfaceRect(new Rectangle(x, y, 1, 1), 1));
            }
        }
        terrainService.saveTerrain(imagePositions, surfaceRects, 1);
        Assert.assertEquals(0, getTerrainTileCount());
        Assert.assertEquals(1600, getSurfaceRectCount());
        verifySurfaceRects(20, 60, 20, 60, 1, 1);
    }

    private int getTerrainTileCount() {
        SessionFactoryUtils.initDeferredClose(getHibernateTemplate().getSessionFactory());
        int count = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next().getDbTerrainImagePositionCrudServiceHelper().readDbChildren().size();
        SessionFactoryUtils.processDeferredClose(getHibernateTemplate().getSessionFactory());
        return count;
    }

    private int getSurfaceRectCount() {
        SessionFactoryUtils.initDeferredClose(getHibernateTemplate().getSessionFactory());
        int count = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next().getDbSurfaceRectCrudServiceHelper().readDbChildren().size();
        SessionFactoryUtils.processDeferredClose(getHibernateTemplate().getSessionFactory());
        return count;
    }


    private void verifyImagePositions(int xFrom, int xTo, int yFrom, int yTo) {
        SessionFactoryUtils.initDeferredClose(getHibernateTemplate().getSessionFactory());
        Collection<DbTerrainImagePosition> terrainImagePositions = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next().getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        for (DbTerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (terrainImagePosition.getTileX() < xFrom
                    || terrainImagePosition.getTileX() > xTo
                    || terrainImagePosition.getTileY() < yFrom
                    || terrainImagePosition.getTileY() > yTo) {
                Assert.fail("ImagePosition is out of band");
            }
        }
        SessionFactoryUtils.processDeferredClose(getHibernateTemplate().getSessionFactory());
    }

    private void verifySurfaceRects(int xFrom, int xTo, int yFrom, int yTo, int width, int height) {
        SessionFactoryUtils.initDeferredClose(getHibernateTemplate().getSessionFactory());
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
        }
        SessionFactoryUtils.processDeferredClose(getHibernateTemplate().getSessionFactory());
    }


}