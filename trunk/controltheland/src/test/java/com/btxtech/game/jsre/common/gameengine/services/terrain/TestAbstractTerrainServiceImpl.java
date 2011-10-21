package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<TerrainType, boolean[][]> terrainTypes = abstractTerrainService.createSurfaceTypeField();
        Assert.assertEquals(TerrainType.values().length, terrainTypes.size());
        for (boolean[][] boolX : terrainTypes.values()) {
            Assert.assertEquals(100, boolX.length);
            for (boolean[] boolY : boolX) {
                Assert.assertEquals(200, boolY.length);
                for (boolean b : boolY) {
                    Assert.assertFalse(b);
                }
            }
        }
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

        Map<TerrainType, boolean[][]> terrainTypes = abstractTerrainService.createSurfaceTypeField();
        assertLand(terrainTypes.get(TerrainType.LAND));
        assertWater(terrainTypes.get(TerrainType.WATER));
    }

    private void assertWater(boolean[][] field) {
        Assert.assertEquals(100, field.length);
        Assert.assertEquals(100, field[0].length);

        Assert.assertFalse(field[0][0]);
        Assert.assertTrue(field[0][1]);
        Assert.assertFalse(field[1][0]);
        Assert.assertFalse(field[1][1]);
        Assert.assertFalse(field[2][2]);
        Assert.assertFalse(field[1][9]);
        Assert.assertFalse(field[9][1]);
        Assert.assertFalse(field[10][1]);
        Assert.assertFalse(field[1][10]);
        Assert.assertFalse(field[10][10]);

        Assert.assertFalse(field[20][20]);
        Assert.assertTrue(field[20][21]);
        Assert.assertFalse(field[21][20]);
        Assert.assertFalse(field[21][21]);
        Assert.assertFalse(field[19][19]);
        Assert.assertFalse(field[20][19]);
        Assert.assertFalse(field[19][20]);
        Assert.assertFalse(field[22][22]);
        Assert.assertFalse(field[22][21]);
        Assert.assertFalse(field[21][22]);

        Assert.assertFalse(field[30][40]);
        Assert.assertFalse(field[30][89]);
        Assert.assertFalse(field[39][40]);
        Assert.assertFalse(field[39][89]);
        Assert.assertFalse(field[29][39]);
        Assert.assertFalse(field[29][90]);
        Assert.assertFalse(field[90][39]);

        Assert.assertFalse(field[90][90]);
    }

    private void assertLand(boolean[][] field) {
        Assert.assertEquals(100, field.length);
        Assert.assertEquals(100, field[0].length);

        Assert.assertTrue(field[0][0]);
        Assert.assertFalse(field[0][1]);
        Assert.assertFalse(field[1][0]);
        Assert.assertTrue(field[1][1]);
        Assert.assertTrue(field[2][2]);
        Assert.assertTrue(field[1][9]);
        Assert.assertTrue(field[9][1]);

        Assert.assertFalse(field[10][1]);
        Assert.assertFalse(field[1][10]);
        Assert.assertFalse(field[10][10]);

        Assert.assertTrue(field[20][20]);
        Assert.assertFalse(field[20][21]);
        Assert.assertFalse(field[21][20]);

        Assert.assertTrue(field[21][21]);
        Assert.assertFalse(field[19][19]);
        Assert.assertFalse(field[20][19]);
        Assert.assertFalse(field[19][20]);
        Assert.assertFalse(field[22][22]);
        Assert.assertFalse(field[22][21]);
        Assert.assertFalse(field[21][22]);

        Assert.assertTrue(field[30][40]);
        Assert.assertTrue(field[30][89]);
        Assert.assertTrue(field[39][40]);
        Assert.assertTrue(field[39][89]);
        Assert.assertFalse(field[29][39]);
        Assert.assertFalse(field[29][90]);
        Assert.assertFalse(field[90][39]);

        Assert.assertTrue(field[90][90]);
    }

}
