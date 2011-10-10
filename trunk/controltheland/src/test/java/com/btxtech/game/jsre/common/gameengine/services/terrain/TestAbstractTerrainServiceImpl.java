package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 20:07:23
 */
public class TestAbstractTerrainServiceImpl {

    @Test
    public void createSurfaceTypeField1() {
        AbstractTerrainServiceImpl abstractTerrainService = new AbstractTerrainServiceImpl() {
        };
        abstractTerrainService.setTerrainSettings(new TerrainSettings(100, 200, 100, 100));
        SurfaceType[][] surfaceTypes = abstractTerrainService.createSurfaceTypeField();
        Assert.assertEquals(100, surfaceTypes.length);
        Assert.assertEquals(200, surfaceTypes[0].length);
    }

    @Test
    public void createSurfaceTypeField2() {
        AbstractTerrainServiceImpl abstractTerrainService = new AbstractTerrainServiceImpl() {
        };
        abstractTerrainService.setTerrainSettings(new TerrainSettings(100, 100, 100, 100));

        SurfaceType[][] tileSurfaceTypes = new SurfaceType[2][2];
        tileSurfaceTypes[0][0] = SurfaceType.LAND;
        tileSurfaceTypes[0][1] = SurfaceType.WATER;
        tileSurfaceTypes[1][0] = SurfaceType.LAND_COAST;
        tileSurfaceTypes[1][1] = SurfaceType.LAND;
        abstractTerrainService.putTerrainImage(new TerrainImage(0, 2, 2, tileSurfaceTypes));

        abstractTerrainService.putSurfaceImage(new SurfaceImage(SurfaceType.LAND, 0));

        List<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), 0));
        terrainImagePositions.add(new TerrainImagePosition(new Index(20, 20), 0));
        terrainImagePositions.add(new TerrainImagePosition(new Index(99, 99), 0));
        abstractTerrainService.setTerrainImagePositions(terrainImagePositions);

        List<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 10, 10), 0));
        surfaceRects.add(new SurfaceRect(new Rectangle(30, 40, 10, 50), 0));
        surfaceRects.add(new SurfaceRect(new Rectangle(90, 90, 20, 20), 0)); // Overbooked
        abstractTerrainService.setSurfaceRects(surfaceRects);

        SurfaceType[][] surfaceTypes = abstractTerrainService.createSurfaceTypeField();
        Assert.assertEquals(100, surfaceTypes.length);
        Assert.assertEquals(100, surfaceTypes[0].length);

        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[0][0]);
        Assert.assertEquals(SurfaceType.WATER, surfaceTypes[0][1]);
        Assert.assertEquals(SurfaceType.LAND_COAST, surfaceTypes[1][0]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[1][1]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[2][2]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[1][9]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[9][1]);
        Assert.assertNull(surfaceTypes[10][1]);
        Assert.assertNull(surfaceTypes[1][10]);
        Assert.assertNull(surfaceTypes[10][10]);

        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[20][20]);
        Assert.assertEquals(SurfaceType.WATER, surfaceTypes[20][21]);
        Assert.assertEquals(SurfaceType.LAND_COAST, surfaceTypes[21][20]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[21][21]);
        Assert.assertNull(surfaceTypes[19][19]);
        Assert.assertNull(surfaceTypes[20][19]);
        Assert.assertNull(surfaceTypes[19][20]);
        Assert.assertNull(surfaceTypes[22][22]);
        Assert.assertNull(surfaceTypes[22][21]);
        Assert.assertNull(surfaceTypes[21][22]);

        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[30][40]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[30][89]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[39][40]);
        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[39][89]);
        Assert.assertNull(surfaceTypes[29][39]);
        Assert.assertNull(surfaceTypes[29][90]);
        Assert.assertNull(surfaceTypes[90][39]);

        Assert.assertEquals(SurfaceType.LAND, surfaceTypes[90][90]);
    }

}
