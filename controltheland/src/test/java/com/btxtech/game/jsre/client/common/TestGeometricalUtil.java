package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.GeometricalUtil;
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * User: beat
 * Date: 22.04.2011
 * Time: 21:06:58
 */
public class TestGeometricalUtil {
    private AbstractTerrainServiceImpl terrainService;

    @Before
    public void mockTerrainService() {
        TerrainSettings terrainSettings = new TerrainSettings(100, 100, 100, 100);
        terrainService = new AbstractTerrainServiceImpl() {
        };
        terrainService.setTerrainSettings(terrainSettings);
    }

    @Test
    public void setup1Rectangle() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(1, rectangles.size());
    }

    @Test
    public void setup2Rectangle() {
        boolean[][] field = new boolean[1][10];
        addTileIndex(field, 0, true);
        addTileIndex(field, 1, false);
        addTileIndex(field, 2, true);
        addTileIndex(field, 3, true);
        addTileIndex(field, 4, true);
        addTileIndex(field, 5, true);
        addTileIndex(field, 6, true);
        addTileIndex(field, 7, true);
        addTileIndex(field, 8, true);
        addTileIndex(field, 9, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(2, rectangles.size());
    }

    @Test
    public void setup3Rectangle() {
        boolean[][] field = new boolean[2][10];
        addTileIndex(field, 0, true, true);
        addTileIndex(field, 1, false, true);
        addTileIndex(field, 2, true, true);
        addTileIndex(field, 3, true, true);
        addTileIndex(field, 4, true, true);
        addTileIndex(field, 5, true, true);
        addTileIndex(field, 6, true, true);
        addTileIndex(field, 7, true, true);
        addTileIndex(field, 8, true, true);
        addTileIndex(field, 9, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(3, rectangles.size());
    }

    @Test
    public void setup3Rectangle2() {
        boolean[][] field = new boolean[3][10];
        addTileIndex(field, 0, true, true, true);
        addTileIndex(field, 1, false, true, true);
        addTileIndex(field, 2, true, true, true);
        addTileIndex(field, 3, true, true, true);
        addTileIndex(field, 4, true, true, true);
        addTileIndex(field, 5, true, true, true);
        addTileIndex(field, 6, true, true, true);
        addTileIndex(field, 7, true, true, true);
        addTileIndex(field, 8, true, true, true);
        addTileIndex(field, 9, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(3, rectangles.size());
    }

    @Test
    public void setup3Rectangle3() {
        boolean[][] field = new boolean[2][10];
        addTileIndex(field, 0, true, true);
        addTileIndex(field, 1, true, true);
        addTileIndex(field, 2, true, true);
        addTileIndex(field, 3, true, false);
        addTileIndex(field, 4, true, true);
        addTileIndex(field, 5, true, true);
        addTileIndex(field, 6, true, true);
        addTileIndex(field, 7, true, true);
        addTileIndex(field, 8, true, true);
        addTileIndex(field, 9, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(3, rectangles.size());
    }

    @Test
    public void setupDiagonalRectangle1() {
        boolean[][] field = new boolean[2][2];
        addTileIndex(field, 0, false, true);
        addTileIndex(field, 1, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(2, rectangles.size());
    }

    @Test
    public void setup3Rectangle2Corner() {
        boolean[][] field = new boolean[3][3];
        addTileIndex(field, 0, false, true, true);
        addTileIndex(field, 1, true, true, true);
        addTileIndex(field, 2, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        printRectangle(field, rectangles);
        Assert.assertEquals(3, rectangles.size());
    }

    @Test
    public void setup3RectangleCorner() {
        boolean[][] field = new boolean[3][3];
        addTileIndex(field, 0, true, true, true);
        addTileIndex(field, 1, true, false, false);
        addTileIndex(field, 2, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(3, rectangles.size());
    }

    @Test
    public void setupDiagonalRectangle2() {
        boolean[][] field = new boolean[3][3];
        addTileIndex(field, 0, false, true, true);
        addTileIndex(field, 1, true, false, true);
        addTileIndex(field, 2, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(4, rectangles.size());
    }

    @Test
    public void setup3RectangleGap() {
        boolean[][] field = new boolean[3][3];
        addTileIndex(field, 0, true, true, true);
        addTileIndex(field, 1, true, true, true);
        addTileIndex(field, 2, false, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(2, rectangles.size());
    }

    @Test
    public void setup3Rectangle1() {
        boolean[][] field = new boolean[4][3];
        addTileIndex(field, 0, false, false, false, true);
        addTileIndex(field, 1, true, false, false, true);
        addTileIndex(field, 2, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(3, rectangles.size());
        assertField(field, rectangles);
    }


    @Test
    public void setup4Rectangle() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(4, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle1() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, false, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(2, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle2() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, false, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(3, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle3() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 5, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, false, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(1, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle4() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 5, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(2, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle5() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(1, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle6() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, false, false, false, false, false, false, false, false, false, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(1, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle7() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 1, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 2, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 3, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 4, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 5, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 6, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 7, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 8, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 9, false, false, false, false, false, false, false, false, false, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(0, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle8() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, false, false, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, false, false, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(4, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle9() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, false, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, false, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, true, false, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, false, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, false, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, false, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(18, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle10() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, false, false, false, false, false, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 4, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 5, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 6, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 7, true, true, true, false, false, false, false, false, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(5, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle11() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, false, true, false, false, false, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 4, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 5, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 6, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 7, true, true, true, false, false, false, false, false, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(6, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle12() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 2, true, false, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, false, false, false, true, true, true, true, true, true);
        addTileIndex(field, 4, true, false, false, false, true, true, true, true, true, true);
        addTileIndex(field, 5, true, true, false, false, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        Assert.assertEquals(6, rectangles.size());
        assertField(field, rectangles);
    }

    @Test
    public void setupRectangle13() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, true, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, true, true, false, true, true, true);
        addTileIndex(field, 2, true, false, true, true, true, true, true, true, true, true);
        addTileIndex(field, 3, true, false, false, false, true, true, true, true, true, true);
        addTileIndex(field, 4, true, false, false, false, true, true, true, false, true, true);
        addTileIndex(field, 5, true, true, false, false, true, true, true, true, true, true);
        addTileIndex(field, 6, true, true, false, true, true, true, false, true, true, true);
        addTileIndex(field, 7, true, true, false, true, true, true, true, true, true, true);
        addTileIndex(field, 8, true, true, false, true, true, true, true, true, true, false);
        addTileIndex(field, 9, true, true, false, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(14, rectangles.size());
    }

    @Test
    public void setupRectangle14() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, true, true, true, true, true, true, true, true, false);
        addTileIndex(field, 1, true, false, true, true, true, true, true, true, false, true);
        addTileIndex(field, 2, true, true, false, true, true, true, true, false, true, true);
        addTileIndex(field, 3, true, true, true, false, true, true, false, true, true, true);
        addTileIndex(field, 4, true, true, true, true, false, false, true, true, true, true);
        addTileIndex(field, 5, true, true, true, true, false, false, true, true, true, true);
        addTileIndex(field, 6, true, true, true, false, true, true, false, true, true, true);
        addTileIndex(field, 7, true, true, false, true, true, true, true, false, true, true);
        addTileIndex(field, 8, true, false, true, true, true, true, true, true, false, true);
        addTileIndex(field, 9, false, true, true, true, true, true, true, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(19, rectangles.size());
    }

    @Test
    public void setupRectangle15() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 1, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 2, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 3, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 4, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 5, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 6, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 7, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 8, true, true, true, true, false, true, true, true, true, true);
        addTileIndex(field, 9, true, true, true, true, false, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(4, rectangles.size());
    }

    @Test
    public void setupRectangle16() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, false, false, true, true, true, true, true, true, true);
        addTileIndex(field, 1, false, false, false, false, false, false, true, true, true, true);
        addTileIndex(field, 2, true, true, false, true, true, true, false, true, true, true);
        addTileIndex(field, 3, true, false, true, true, true, true, true, false, true, true);
        addTileIndex(field, 4, true, false, true, true, false, false, true, false, true, true);
        addTileIndex(field, 5, true, false, true, true, false, false, true, false, true, true);
        addTileIndex(field, 6, true, false, false, true, true, true, true, false, true, true);
        addTileIndex(field, 7, true, true, true, false, true, true, true, false, true, true);
        addTileIndex(field, 8, true, true, true, true, false, false, false, true, true, true);
        addTileIndex(field, 9, true, true, true, true, true, true, true, true, true, true);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(16, rectangles.size());
    }

    @Test
    public void setupRectangle17() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 1, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 2, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 3, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 4, false, false, false, true, false, false, false, false, false, false);
        addTileIndex(field, 5, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 6, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 7, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 8, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 9, false, false, false, false, false, false, false, false, false, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(1, rectangles.size());
    }

    @Test
    public void setupRectangle18() {
        boolean[][] field = new boolean[10][10];
        addTileIndex(field, 0, false, false, false, false, false, false, false, false, true, true);
        addTileIndex(field, 1, false, false, false, false, false, false, false, false, true, true);
        addTileIndex(field, 2, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 3, false, false, false, false, true, false, false, false, false, false);
        addTileIndex(field, 4, false, false, false, true, true, true, false, false, false, false);
        addTileIndex(field, 5, false, false, false, true, true, true, false, false, false, false);
        addTileIndex(field, 6, false, false, false, false, true, false, false, false, false, false);
        addTileIndex(field, 7, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 8, false, false, false, false, false, false, false, false, false, false);
        addTileIndex(field, 9, false, false, false, false, false, false, false, false, false, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(4, rectangles.size());
    }


    @Test
    public void setupRectangle19() {
        boolean[][] field = new boolean[3][3];
        addTileIndex(field, 0, false, false, true);
        addTileIndex(field, 1, true, true, true);
        addTileIndex(field, 2, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        printRectangle(field, rectangles);
        assertField(field, rectangles);
        Assert.assertEquals(2, rectangles.size());
    }

    @Test
    public void setupRectangle20() {
        boolean[][] field = new boolean[3][4];
        addTileIndex(field, 0, false, false, true);
        addTileIndex(field, 1, false, false, true);
        addTileIndex(field, 2, true, true, true);
        addTileIndex(field, 3, true, true, false);

        Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
        assertField(field, rectangles);
        Assert.assertEquals(2, rectangles.size());
    }

    @Test
    public void setupRectangleDynamic() {
        final int x = 4;
        final int y = 5;
        boolean[][] field = new boolean[x][y];
        long count = (long) Math.pow(2, (x * y));

        for (int i = 0; i < count; i++) {
            if (i % 1000 == 0) {
                System.out.println("Test: " + i + "/" + count);
            }
            Collection<PassableRectangle> rectangles = GeometricalUtil.setupPassableRectangle(field);
            assertField(field, rectangles);
            increaseField(field);
        }
    }

    private void increaseField(boolean[][] field) {
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                if (field[x][y]) {
                    field[x][y] = false;
                } else if (!field[x][y]) {
                    field[x][y] = true;
                    return;
                }
            }
        }

    }

    private void assertField(boolean[][] field, Collection<PassableRectangle> rectangles) {
        try {
            GeometricalUtil.checkField(field, rectangles, terrainService);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }

        /*  Set<Index> blockedIndexes = new HashSet<Index>();
       Set<Index> freeIndexes = new HashSet<Index>();

       for (int x = 0; x < field.length; x++) {
           for (int y = 0; y < field[x].length; y++) {
               if (field[x][y]) {
                   freeIndexes.add(new Index(x, y));
               } else {
                   blockedIndexes.add(new Index(x, y));
               }
           }
       }

       for (PassableRectangle rectangle : rectangles) {
           Assert.assertTrue("Rectangle width is 0: " + rectangle, rectangle.getRectangle().getWidth() > 0);
           Assert.assertTrue("Rectangle height is 0: " + rectangle, rectangle.getRectangle().getHeight() > 0);
           Collection<Rectangle> tileRectangles = rectangle.getRectangle().split(1, 1);
           for (Rectangle tileRect : tileRectangles) {
               Index index = new Index(tileRect.getX(), tileRect.getY());
               if (!freeIndexes.remove(index)) {
                   Assert.fail("The index '" + index + "' could not be found in the filed for rectangle: " + rectangle);
               }
               if (blockedIndexes.contains(index)) {
                   Assert.fail("The index '" + index + "' should not be contained in the rectangle: " + rectangle);
               }
           }
       }

       if (!freeIndexes.isEmpty()) {
           StringBuffer stringBuffer = new StringBuffer();
           stringBuffer.append("This indexes are not contained in a rectangle:\n");
           for (Index freeIndex : freeIndexes) {
               stringBuffer.append(freeIndex);
               stringBuffer.append("\n");
           }
           Assert.fail(stringBuffer.toString());
       }

       // Check neighbors
       for (PassableRectangle rectangle : rectangles) {
           Map<PassableRectangle, PassableRectangle.Neighbor> neighbors = new HashMap<PassableRectangle, PassableRectangle.Neighbor>(rectangle.getNeighbors(terrainService));
           for (PassableRectangle possibleNeighbor : rectangles) {
               if (possibleNeighbor.equals(rectangle)) {
                   continue;
               }
               if (rectangle.getRectangle().adjoins(possibleNeighbor.getRectangle()) &&
                       !rectangle.getRectangle().getCrossSection(possibleNeighbor.getRectangle()).isEmpty()) {
                   Assert.assertNotNull("'" + rectangle + "' does not know neighbor '" + possibleNeighbor + "'", neighbors.remove(possibleNeighbor));
                   // Check size of port
                   Rectangle absRectangle = terrainService.convertToAbsolutePosition(rectangle.getRectangle());
                   Rectangle absNeighborRectangle = terrainService.convertToAbsolutePosition(possibleNeighbor.getRectangle());
                   Rectangle crossSection = absRectangle.getCrossSection(absNeighborRectangle);
                   int min = Math.min(crossSection.getWidth(), crossSection.getHeight());
                   int length = Math.max(crossSection.getWidth(), crossSection.getHeight());
                   Assert.assertEquals(0, min);
                   PassableRectangle.Neighbor neighbor = rectangle.getNeighbors(terrainService).get(possibleNeighbor);
                   Assert.assertEquals(length - 1, neighbor.getPort().getCurrentCrossLine().getLength());
               }
           }
           Assert.assertTrue("Passable rectangle does bot know all neighbors", neighbors.isEmpty());
       } */

    }


    private void printRectangle(boolean[][] field, Collection<PassableRectangle> rectangles) {
        char[][] charFiled = new char[field.length][];
        for (int x = 0; x < field.length; x++) {
            charFiled[x] = new char[field[x].length];
            for (int y = 0; y < field[x].length; y++) {
                if (!field[x][y]) {
                    charFiled[x][y] = '#';
                } else {
                    charFiled[x][y] = '?';
                }
            }
        }

        char rectChar = 'a';
        for (PassableRectangle rectangle : rectangles) {
            Collection<Rectangle> tileRectangles = rectangle.getRectangle().split(1, 1);
            for (Rectangle tileRect : tileRectangles) {
                if (charFiled[tileRect.getX()][tileRect.getY()] != '?') {
                    System.out.println("(" + charFiled[tileRect.getX()][tileRect.getY()] + rectChar + ")");
                    charFiled[tileRect.getX()][tileRect.getY()] = '!';
                } else {
                    charFiled[tileRect.getX()][tileRect.getY()] = rectChar;
                }
            }
            rectChar++;
        }

        int x = 0;
        for (int y = 0; y < charFiled[x].length; y++) {
            for (; x < field.length; x++) {
                System.out.print(charFiled[x][y]);
            }
            System.out.println();
            x = 0;
        }
        System.out.println("---------------------------------");
        printField(field);
        System.out.println("---------------------------------");
        for (PassableRectangle rectangle : rectangles) {
            System.out.println(rectangle.getRectangle() + " '" + rectangle.getRectangle().testString() + "'");
        }
    }

    private void addTileIndex(boolean[][] field, int y, boolean... value) {
        for (int x = 0; x < value.length; x++) {
            field[x][y] = value[x];
        }
    }

    private void printField(boolean[][] field) {
        int x = 0;
        for (int y = 0; y < field[x].length; y++) {
            for (; x < field.length; x++) {
                System.out.print(field[x][y] ? "." : "#");
            }
            System.out.println();
            x = 0;
        }
    }
}
