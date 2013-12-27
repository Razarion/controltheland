package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.AStar;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Collections;

import static com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType.LAND;
import static com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType.NONE;

/**
 * User: beat
 * Date: 04.06.12
 * Time: 01:58
 */
public class TestAStar {

    private void addLine(TerrainTile[][] terrainTiles, int y, SurfaceType... surfaceType) {
        for (int x = 0; x < surfaceType.length; x++) {
            terrainTiles[x][y] = new TerrainTile(surfaceType[x], false, 0, null, 0, 0, 0);
        }
    }

    @Test
    public void simple() {
        TerrainTile[][] terrainTiles = new TerrainTile[10][10];
        //                       0     1     2     3     4     5     6     7      8    9
        addLine(terrainTiles, 0, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 1, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 2, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 3, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 4, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 5, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 6, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 7, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 8, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 9, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(1, 1), new Index(8, 8), Collections.singleton(LAND));
        assertPath(aStar, new Index(1, 1), new Index(8, 8));

        aStar = AStar.findTilePath(terrainTiles, new Index(8, 8), new Index(1, 1), Collections.singleton(LAND));
        assertPath(aStar, new Index(8, 8), new Index(1, 1));

        aStar = AStar.findTilePath(terrainTiles, new Index(8, 1), new Index(1, 8), Collections.singleton(LAND));
        assertPath(aStar, new Index(8, 1), new Index(1, 8));

        aStar = AStar.findTilePath(terrainTiles, new Index(1, 8), new Index(8, 1), Collections.singleton(LAND));
        assertPath(aStar, new Index(1, 8), new Index(8, 1));

    }

    private void assertPath(AStar aStar, Index start, Index destination) {
        Assert.assertTrue("Path not found", aStar.isPathFound());
        Assert.assertFalse("Start should not be the first entry", aStar.getTilePath().get(0).equals(start));
        Assert.assertFalse("Destination should not be the first entry", aStar.getTilePath().get(aStar.getTilePath().size() - 1).equals(destination));
        Index lastPosition = start;
        for (Index index : aStar.getTilePath()) {
            Assert.assertEquals(1.0, lastPosition.getDistanceDouble(index), 0.0001);
            lastPosition = index;
        }
        Assert.assertEquals(1.0, lastPosition.getDistanceDouble(destination), 0.0001);
    }

    @Test
    public void notFound() {
        TerrainTile[][] terrainTiles = new TerrainTile[10][10];
        //                       0     1     2     3     4     5     6     7      8    9
        addLine(terrainTiles, 0, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 1, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 2, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 3, LAND, LAND, LAND, NONE, NONE, NONE, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 4, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 5, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 6, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 7, LAND, LAND, LAND, NONE, NONE, NONE, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 8, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 9, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(0, 0), new Index(4, 4), Collections.singleton(LAND));
        Assert.assertFalse(aStar.isPathFound());
        Assert.assertEquals(new Index(4, 2), aStar.getBestFitTile());
        Assert.assertEquals(5, aStar.getTilePath().size());
        Assert.assertEquals(new Index(1, 0), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(1, 1), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(1, 2), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(3, 2), aStar.getTilePath().get(4));

        aStar = AStar.findTilePath(terrainTiles, new Index(0, 0), new Index(5, 5), Collections.singleton(LAND));
        Assert.assertFalse(aStar.isPathFound());
        Assert.assertEquals(new Index(7, 5), aStar.getBestFitTile());
        Assert.assertEquals(11, aStar.getTilePath().size());
        Assert.assertEquals(new Index(1, 0), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(1, 1), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(1, 2), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(3, 2), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(4, 2), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(5, 2), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(6, 2), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(7, 2), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(7, 3), aStar.getTilePath().get(9));
        Assert.assertEquals(new Index(7, 4), aStar.getTilePath().get(10));

        aStar = AStar.findTilePath(terrainTiles, new Index(0, 0), new Index(5, 5), Collections.singleton(LAND));
        Assert.assertFalse(aStar.isPathFound());
        Assert.assertEquals(new Index(7, 5), aStar.getBestFitTile());

    }

    @Test
    public void whole() {
        TerrainTile[][] terrainTiles = new TerrainTile[10][10];
        //                       0     1     2     3     4     5     6     7      8    9
        addLine(terrainTiles, 0, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 1, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 2, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 3, LAND, LAND, LAND, NONE, NONE, NONE, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 4, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 5, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 6, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 7, LAND, LAND, LAND, NONE, LAND, NONE, NONE, LAND, LAND, LAND);
        addLine(terrainTiles, 8, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 9, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(4, 4), new Index(4, 2), Collections.singleton(LAND));
        assertPath(aStar, new Index(4, 4), new Index(4, 2));
        Assert.assertEquals(13, aStar.getTilePath().size());
        Assert.assertEquals(new Index(4, 5), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(4, 6), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(4, 7), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(4, 8), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(3, 8), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(2, 8), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(2, 7), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(2, 6), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(2, 5), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(2, 4), aStar.getTilePath().get(9));
        Assert.assertEquals(new Index(2, 3), aStar.getTilePath().get(10));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(11));
        Assert.assertEquals(new Index(3, 2), aStar.getTilePath().get(12));
    }

    @Test
    public void test1() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(1, 1), new Index(8, 8), Collections.singleton(LAND));
        assertPath(aStar, new Index(1, 1), new Index(8, 8));
        Assert.assertEquals(13, aStar.getTilePath().size());
        Assert.assertEquals(new Index(2, 1), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(3, 2), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(3, 3), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(4, 3), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(4, 4), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(5, 4), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(6, 4), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(6, 5), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(7, 5), aStar.getTilePath().get(9));
        Assert.assertEquals(new Index(7, 6), aStar.getTilePath().get(10));
        Assert.assertEquals(new Index(7, 7), aStar.getTilePath().get(11));
        Assert.assertEquals(new Index(7, 8), aStar.getTilePath().get(12));
    }

    @Test
    public void test2() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(1, 0), new Index(0, 9), Collections.singleton(LAND));
        assertPath(aStar, new Index(1, 0), new Index(0, 9));
        Assert.assertEquals(11, aStar.getTilePath().size());
        Assert.assertEquals(new Index(1, 1), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(2, 1), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(2, 3), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(2, 4), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(2, 5), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(1, 5), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(1, 6), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(1, 7), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(1, 8), aStar.getTilePath().get(9));
        Assert.assertEquals(new Index(1, 9), aStar.getTilePath().get(10));
    }

    @Test
    public void test3() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(5, 1), new Index(7, 7), Collections.singleton(LAND));
        assertPath(aStar, new Index(5, 1), new Index(7, 7));
        Assert.assertEquals(7, aStar.getTilePath().size());
        Assert.assertEquals(new Index(5, 2), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(5, 3), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(5, 4), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(6, 4), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(6, 5), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(7, 5), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(7, 6), aStar.getTilePath().get(6));
    }

    @Test
    public void test4() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(9, 9), new Index(0, 0), Collections.singleton(LAND));
        assertPath(aStar, new Index(9, 9), new Index(0, 0));
        Assert.assertEquals(17, aStar.getTilePath().size());
        Assert.assertEquals(new Index(9, 8), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(8, 8), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(7, 8), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(7, 7), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(7, 6), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(7, 5), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(6, 5), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(6, 4), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(5, 4), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(4, 4), aStar.getTilePath().get(9));
        Assert.assertEquals(new Index(4, 3), aStar.getTilePath().get(10));
        Assert.assertEquals(new Index(3, 3), aStar.getTilePath().get(11));
        Assert.assertEquals(new Index(3, 2), aStar.getTilePath().get(12));
        Assert.assertEquals(new Index(2, 2), aStar.getTilePath().get(13));
        Assert.assertEquals(new Index(2, 1), aStar.getTilePath().get(14));
        Assert.assertEquals(new Index(1, 1), aStar.getTilePath().get(15));
        Assert.assertEquals(new Index(0, 1), aStar.getTilePath().get(16));
    }

    @Test
    public void test5() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(0, 0), new Index(0, 9), Collections.singleton(LAND));
        assertPath(aStar, new Index(0, 0), new Index(0, 9));
        Assert.assertEquals(10, aStar.getTilePath().size());
        Assert.assertEquals(new Index(0, 1), aStar.getTilePath().get(0));
        Assert.assertEquals(new Index(0, 2), aStar.getTilePath().get(1));
        Assert.assertEquals(new Index(0, 3), aStar.getTilePath().get(2));
        Assert.assertEquals(new Index(1, 3), aStar.getTilePath().get(3));
        Assert.assertEquals(new Index(1, 4), aStar.getTilePath().get(4));
        Assert.assertEquals(new Index(1, 5), aStar.getTilePath().get(5));
        Assert.assertEquals(new Index(1, 6), aStar.getTilePath().get(6));
        Assert.assertEquals(new Index(1, 7), aStar.getTilePath().get(7));
        Assert.assertEquals(new Index(1, 8), aStar.getTilePath().get(8));
        Assert.assertEquals(new Index(1, 9), aStar.getTilePath().get(9));
    }

    @Test
    public void testAdjoin() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(4, 4), new Index(4, 5), Collections.singleton(LAND));
        Assert.assertEquals(0, aStar.getTilePath().size());
    }

    @Test
    public void testAdjoinDiagonal() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(2, 1), new Index(3, 2), Collections.singleton(LAND));
        assertPath(aStar, new Index(2, 1), new Index(3, 2));
        Assert.assertEquals(1, aStar.getTilePath().size());
        Assert.assertEquals(new Index(3, 1), aStar.getTilePath().get(0));
    }

    @Test
    public void testSameTile() {
        TerrainTile[][] terrainTiles = generateField();

        AStar aStar = AStar.findTilePath(terrainTiles, new Index(2, 2), new Index(2, 2), Collections.singleton(LAND));
        Assert.assertEquals(0, aStar.getTilePath().size());
    }

    private TerrainTile[][] generateField() {
        TerrainTile[][] terrainTiles = new TerrainTile[10][10];
        //                       0     1     2     3     4     5     6     7      8    9
        addLine(terrainTiles, 0, LAND, LAND, LAND, LAND, NONE, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 1, LAND, LAND, LAND, LAND, LAND, NONE, LAND, LAND, NONE, NONE);
        addLine(terrainTiles, 2, LAND, NONE, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 3, LAND, LAND, LAND, LAND, LAND, LAND, NONE, LAND, NONE, NONE);
        addLine(terrainTiles, 4, NONE, LAND, LAND, NONE, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 5, LAND, LAND, LAND, LAND, LAND, NONE, LAND, LAND, NONE, LAND);
        addLine(terrainTiles, 6, LAND, LAND, NONE, LAND, LAND, LAND, NONE, LAND, LAND, NONE);
        addLine(terrainTiles, 7, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 8, NONE, LAND, LAND, LAND, NONE, NONE, LAND, LAND, LAND, LAND);
        addLine(terrainTiles, 9, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, NONE, LAND);
        return terrainTiles;
    }

    private void generateAssertCode(AStar aStar) {
        System.out.println("        Assert.assertEquals(" + aStar.getTilePath().size() + ", aStar.getTilePath().size());");
        int index = 0;
        for (Index tile : aStar.getTilePath()) {
            System.out.println("        Assert.assertEquals(" + tile.testString() + ", aStar.getTilePath().get(" + index + "));");
            index++;
        }
    }

}
