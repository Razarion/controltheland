package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.Port;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.GumPath;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.PathFinderUtilities;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.TestPathFinding;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.terrain.TerrainService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: beat
 * Date: 02.05.2011
 * Time: 13:10:21
 */
public class TestPathFindingImpl extends AbstractServiceTest {
    private static final Rectangle RECT_22_13 = new Rectangle(22, 13, 13, 17);
    private static final Rectangle RECT_35_13 = new Rectangle(35, 13, 5, 7);
    private static final Rectangle RECT_35_07 = new Rectangle(35, 7, 5, 6);
    private static final Rectangle RECT_28_04 = new Rectangle(28, 4, 10, 3);
    private static final Rectangle RECT_24_05 = new Rectangle(24, 5, 4, 7);
    private static final Rectangle RECT_25_12 = new Rectangle(25, 12, 2, 1);
    private static final Rectangle RECT_13_13 = new Rectangle(13, 13, 3, 3);
    private static final Rectangle RECT_16_13 = new Rectangle(16, 13, 3, 3);
    private static final Rectangle RECT_19_13 = new Rectangle(19, 13, 3, 3);
    private static final Rectangle RECT_13_16 = new Rectangle(13, 16, 3, 3);
    private static final Rectangle RECT_16_16 = new Rectangle(16, 16, 3, 3);
    private static final Rectangle RECT_19_16 = new Rectangle(19, 16, 3, 3);
    private static final Rectangle RECT_13_19 = new Rectangle(13, 19, 3, 3);
    private static final Rectangle RECT_16_19 = new Rectangle(16, 19, 3, 3);
    private static final Rectangle RECT_19_19 = new Rectangle(19, 19, 3, 3);
    private static final Rectangle RECT_13_22 = new Rectangle(13, 22, 3, 3);
    private static final Rectangle RECT_16_22 = new Rectangle(16, 22, 3, 3);
    private static final Rectangle RECT_19_22 = new Rectangle(19, 22, 3, 3);
    private static final Rectangle RECT_13_25 = new Rectangle(13, 25, 3, 3);
    private static final Rectangle RECT_16_25 = new Rectangle(16, 25, 3, 3);
    private static final Rectangle RECT_19_25 = new Rectangle(19, 25, 3, 3);
    private static final Rectangle RECT_20_28 = new Rectangle(20, 28, 2, 11);
    private static final Rectangle RECT_22_30 = new Rectangle(22, 30, 4, 2);
    private static final Rectangle RECT_31_30 = new Rectangle(31, 30, 4, 2);
    private static final Rectangle RECT_24_32 = new Rectangle(24, 32, 5, 2);
    private static final Rectangle RECT_30_32 = new Rectangle(30, 32, 4, 3);
    private static final Rectangle RECT_26_34 = new Rectangle(26, 34, 4, 3);
    private static final Rectangle RECT_22_37 = new Rectangle(22, 37, 13, 2);
    private static final Rectangle RECT_20_39 = new Rectangle(20, 39, 6, 2);
    private static final Rectangle RECT_29_39 = new Rectangle(29, 39, 11, 2);
    private static final Rectangle RECT_20_41 = new Rectangle(20, 41, 15, 2);
    private static final Rectangle RECT_09_30 = new Rectangle(9, 30, 11, 2);
    private static final Rectangle RECT_07_22 = new Rectangle(7, 22, 2, 10);
    private static final Rectangle RECT_40_39 = new Rectangle(40, 39, 2, 22);
    private static final Rectangle RECT_29_61 = new Rectangle(29, 61, 13, 2);
    private static final Rectangle RECT_29_57 = new Rectangle(29, 57, 2, 4);
    private static final Rectangle RECT_26_53 = new Rectangle(26, 53, 11, 4);
    private static final Rectangle RECT_14_53 = new Rectangle(14, 53, 12, 1);
    private static final Rectangle RECT_14_50 = new Rectangle(14, 50, 6, 3);
    private static final Rectangle RECT_16_46 = new Rectangle(16, 46, 4, 4);
    private static final Rectangle RECT_20_46 = new Rectangle(20, 46, 9, 7);
    private static final Rectangle RECT_29_46 = new Rectangle(29, 46, 3, 4);
    private static final Rectangle RECT_29_50 = new Rectangle(29, 50, 5, 3);
    private static final Rectangle RECT_32_46 = new Rectangle(32, 46, 2, 4);
    private static final Rectangle RECT_34_46 = new Rectangle(34, 46, 3, 7);

    @Autowired
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private DebugService debugService;
    private BoundingBox boundingBox;

    @Test
    @DirtiesContext
    public void testPassableRectangles() throws Exception {
        configureComplexGame();

        Map<TerrainType, Collection<PassableRectangle>> passableRectanglesPerTerrainType = collisionService.getPassableRectangles();
        Assert.assertNotNull(passableRectanglesPerTerrainType.get(TerrainType.LAND));
        Collection<PassableRectangle> passableRectangles = passableRectanglesPerTerrainType.get(TerrainType.LAND);
        for (PassableRectangle passableRectangle : passableRectangles) {
            TestPathFinding.assertRectangleNotInTerrainImage(terrainService.convertToAbsolutePosition(passableRectangle.getRectangle()));
        }
    }

    @Test
    @DirtiesContext
    public void testPassableRectanglesSynthetic1() throws Exception {
        configureComplexGame();
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(RECT_13_16);
        rectangles.add(RECT_16_16);
        rectangles.add(RECT_13_19);
        rectangles.add(RECT_16_19);

        List<PassableRectangle> passableRectangles = PathFinderUtilities.buildPassableRectangleList(rectangles);
        Assert.assertEquals(4, passableRectangles.size());
        assertNeighbor(passableRectangles, RECT_13_16, RECT_16_16, RECT_13_19);
        assertNeighbor(passableRectangles, RECT_16_16, RECT_13_16, RECT_16_19);
        assertNeighbor(passableRectangles, RECT_16_19, RECT_16_16, RECT_13_19);
        assertNeighbor(passableRectangles, RECT_13_19, RECT_16_19, RECT_13_16);
    }

    @Test
    @DirtiesContext
    public void testPassableRectanglesSynthetic2() throws Exception {
        configureComplexGame();
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(RECT_22_13);
        rectangles.add(RECT_19_16);
        rectangles.add(RECT_19_22);
        rectangles.add(RECT_35_13);

        List<PassableRectangle> passableRectangles = PathFinderUtilities.buildPassableRectangleList(rectangles);
        Assert.assertEquals(4, passableRectangles.size());
        assertNeighbor(passableRectangles, RECT_22_13, RECT_19_16, RECT_19_22, RECT_35_13);
        assertNeighbor(passableRectangles, RECT_19_16, RECT_22_13);
        assertNeighbor(passableRectangles, RECT_19_22, RECT_22_13);
        assertNeighbor(passableRectangles, RECT_35_13, RECT_22_13);
    }

    private void assertNeighbor(List<PassableRectangle> passableRectangles, Rectangle rectangle, Rectangle... neighbors) {
        for (PassableRectangle passableRectangle : passableRectangles) {
            Map<PassableRectangle, PassableRectangle.Neighbor> neighborHashMap = passableRectangle.getNeighbors(terrainService);
            if (passableRectangle.getRectangle().equals(rectangle)) {
                Assert.assertEquals(neighbors.length, neighborHashMap.size());
                assertContains(neighborHashMap, neighbors);
            }
        }
    }

    private void assertContains(Map<PassableRectangle, PassableRectangle.Neighbor> neighborHashMap, Rectangle... neighbors) {
        for (PassableRectangle passableRectangle : neighborHashMap.keySet()) {
            boolean found = false;
            Rectangle rectangle = passableRectangle.getRectangle();
            for (Rectangle rect : neighbors) {
                if (rectangle.equals(rect)) {
                    found = true;
                }
            }
            if (!found) {
                Assert.fail("Rectangle can not be found: " + rectangle);
            }
        }
    }

    private Map<TerrainType, Collection<PassableRectangle>> setupPassableRectangle() throws Exception {
        configureComplexGame();

        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(RECT_22_13);
        rectangles.add(RECT_35_13);
        rectangles.add(RECT_35_07);
        rectangles.add(RECT_28_04);
        rectangles.add(RECT_24_05);
        rectangles.add(RECT_25_12);
        rectangles.add(RECT_13_13);
        rectangles.add(RECT_16_13);
        rectangles.add(RECT_19_13);
        rectangles.add(RECT_13_16);
        rectangles.add(RECT_16_16);
        rectangles.add(RECT_19_16);
        rectangles.add(RECT_13_19);
        rectangles.add(RECT_16_19);
        rectangles.add(RECT_19_19);
        rectangles.add(RECT_13_22);
        rectangles.add(RECT_16_22);
        rectangles.add(RECT_19_22);
        rectangles.add(RECT_13_25);
        rectangles.add(RECT_16_25);
        rectangles.add(RECT_19_25);
        rectangles.add(RECT_20_28);
        rectangles.add(RECT_22_30);
        rectangles.add(RECT_31_30);
        rectangles.add(RECT_24_32);
        rectangles.add(RECT_30_32);
        rectangles.add(RECT_26_34);
        rectangles.add(RECT_22_37);
        rectangles.add(RECT_20_39);
        rectangles.add(RECT_29_39);
        rectangles.add(RECT_20_41);
        rectangles.add(RECT_09_30);
        rectangles.add(RECT_07_22);
        rectangles.add(RECT_40_39);
        rectangles.add(RECT_29_61);
        rectangles.add(RECT_29_57);
        rectangles.add(RECT_26_53);
        rectangles.add(RECT_14_53);
        rectangles.add(RECT_14_50);
        rectangles.add(RECT_16_46);
        rectangles.add(RECT_20_46);
        rectangles.add(RECT_29_46);
        rectangles.add(RECT_29_50);
        rectangles.add(RECT_32_46);
        rectangles.add(RECT_34_46);


        List<PassableRectangle> passableRectangles = PathFinderUtilities.buildPassableRectangleList(rectangles);
        Assert.assertEquals(rectangles.size(), passableRectangles.size());

        Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, Collection<PassableRectangle>>();
        passableRectangles4TerrainType.put(TerrainType.LAND, passableRectangles);
        return passableRectangles4TerrainType;
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths1() throws Exception {
        Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(2900, 1600);
        Index destination = new Index(2500, 800);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);
        Path path = startRect.findPossiblePassableRectanglePaths(terrainService, start, end, destination);
        Assert.assertEquals(3, path.getPathElements().size());
        Assert.assertEquals(RECT_22_13, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_25_12, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_24_05, path.getPathElements().get(2).getPassableRectangle().getRectangle());
        // Swap end and start
        start = new Index(2500, 800);
        destination = new Index(2900, 1600);
        startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);
        path = startRect.findPossiblePassableRectanglePaths(terrainService, start, end, destination);
        Assert.assertEquals(3, path.getPathElements().size());
        Assert.assertEquals(RECT_24_05, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_25_12, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_22_13, path.getPathElements().get(2).getPassableRectangle().getRectangle());
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths_Checkerboard1() throws Exception {
        Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(1300, 1300);
        Index destination = new Index(1300, 2500);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);
        Path path = startRect.findPossiblePassableRectanglePaths(terrainService, start, end, destination);
        Assert.assertEquals(5, path.getPathElements().size());
        Assert.assertEquals(RECT_13_13, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_13_16, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_13_19, path.getPathElements().get(2).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_13_22, path.getPathElements().get(3).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_13_25, path.getPathElements().get(4).getPassableRectangle().getRectangle());
    }
    /*
    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths_Checkerboard2() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(2000, 1300);
        Index destination = new Index(2000, 2600);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);
        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(5, path.getPathElements().size());
        Assert.assertEquals(RECT_19_13, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_19_16, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_19_19, path.getPathElements().get(2).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_19_22, path.getPathElements().get(3).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_19_25, path.getPathElements().get(4).getPassableRectangle().getRectangle());
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths2() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(2900, 1600);
        Index destination = new Index(2500, 4200);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);
        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(7, path.getPathElements().size());
        Assert.assertEquals(RECT_22_13, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_22_30, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_24_32, path.getPathElements().get(2).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_26_34, path.getPathElements().get(3).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_22_37, path.getPathElements().get(4).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_20_39, path.getPathElements().get(5).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_20_41, path.getPathElements().get(6).getPassableRectangle().getRectangle());
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths3() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(1700, 1400);
        Index destination = new Index(3300, 4200);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);

        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(13, path.getPathElements().size());
        Assert.assertEquals(RECT_16_13, path.getPathElements().get(0).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_16_16, path.getPathElements().get(1).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_16_19, path.getPathElements().get(2).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_16_22, path.getPathElements().get(3).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_16_25, path.getPathElements().get(4).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_19_25, path.getPathElements().get(5).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_22_13, path.getPathElements().get(6).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_31_30, path.getPathElements().get(7).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_30_32, path.getPathElements().get(8).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_26_34, path.getPathElements().get(9).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_22_37, path.getPathElements().get(10).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_29_39, path.getPathElements().get(11).getPassableRectangle().getRectangle());
        Assert.assertEquals(RECT_20_41, path.getPathElements().get(12).getPassableRectangle().getRectangle());
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePathsBacktracking1() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(1600, 1900);
        Index destination = new Index(700, 2200);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);

        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(0, path.getPathElements().size());
        // TODO
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePathsBacktracking2() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

//        Index start = new Index(3200, 4600);
//        Index destination = new Index(3700, 400);
        Index start = new Index(3700, 400);
        Index destination = new Index(3200, 4600);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);

        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(0, path.getPathElements().size());
        // TODO
    }

    @Test
    @DirtiesContext
    public void testFindPossiblePassableRectanglePathsBacktracking3() throws Exception {
        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();

        Index start = new Index(4193, 6148);
        Index destination = new Index(3484, 5114);
        PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(startRect);
        PassableRectangle end = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
        Assert.assertNotNull(end);

        Path path = startRect.findPossiblePassableRectanglePaths(start, end, destination);
        Assert.assertEquals(0, path.getPathElements().size());
        // TODO
        // 1786-3045 890-2592 tries: 412401
    }
    */
    //@Test

    @DirtiesContext
    public void testFindPossiblePassableRectanglePaths_Random() throws Exception {
        Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = setupPassableRectangle();
        Collection<PassableRectangle> passableRectangles = passableRectangles4TerrainType.get(TerrainType.LAND);

        while (true) {
            Index start = getRandomPosition(passableRectangles);
            Index destination = getRandomPosition(passableRectangles);
            System.out.println("start: " + start + " destination:" + destination);
            PassableRectangle startRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(start, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
            Assert.assertNotNull(startRect);
            PassableRectangle endRect = PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(destination, TerrainType.LAND, passableRectangles4TerrainType, terrainService);
            Assert.assertNotNull(endRect);
            if (startRect.equals(endRect)) {
                continue;
            }
            startRect.findPossiblePassableRectanglePaths(terrainService, start, endRect, destination);

        }
    }

    private Index getRandomPosition(Collection<PassableRectangle> passableRectangles) {
        Random random = new Random();
        List<PassableRectangle> passableRectanglesList = new ArrayList<PassableRectangle>(passableRectangles);
        PassableRectangle passableRectangle = passableRectanglesList.get(random.nextInt(passableRectangles.size()));
        Rectangle absRectangle = terrainService.convertToAbsolutePosition(passableRectangle.getRectangle());
        int x = absRectangle.getX() + random.nextInt(absRectangle.getWidth());
        int y = absRectangle.getY() + random.nextInt(absRectangle.getHeight());
        return new Index(x, y);
    }


    @Test
    @DirtiesContext
    public void testTestGumPath1() throws Exception {
        configureComplexGame();

        GumPath gumPathMock = EasyMock.createNiceMock(GumPath.class);
        EasyMock.expect(gumPathMock.getOptimizedPath()).andReturn(Arrays.asList(new Index(1700, 200), new Index(3200, 200)));
        EasyMock.replay(gumPathMock);

        List<Rectangle> borders = new ArrayList<Rectangle>();
        borders.add(new Rectangle(20, 0, 0, 4));
        borders.add(new Rectangle(25, 0, 0, 4));
        borders.add(new Rectangle(29, 0, 0, 4));

        assertPathInAllBorders(borders, gumPathMock);
    }

    @Test
    @DirtiesContext
    public void testTestGumPath2() throws Exception {
        configureComplexGame();

        GumPath gumPathMock = EasyMock.createNiceMock(GumPath.class);
        EasyMock.expect(gumPathMock.getOptimizedPath()).andReturn(Arrays.asList(
                new Index(700, 1300),
                new Index(1200, 1300),
                new Index(1800, 1500),
                new Index(2500, 2000),
                new Index(2500, 2500),
                new Index(2500, 3000)));
        EasyMock.replay(gumPathMock);

        List<Rectangle> borders = new ArrayList<Rectangle>();
        borders.add(new Rectangle(12, 10, 0, 7));
        borders.add(new Rectangle(18, 12, 0, 7));
        borders.add(new Rectangle(21, 20, 10, 0));
        borders.add(new Rectangle(21, 25, 10, 0));

        assertPathInAllBorders(borders, gumPathMock);
    }

    @Test
    @DirtiesContext
    public void testTestGumPathFail() throws Exception {
        configureComplexGame();

        GumPath gumPathMock = EasyMock.createNiceMock(GumPath.class);
        EasyMock.expect(gumPathMock.getOptimizedPath()).andReturn(Arrays.asList(
                new Index(700, 1300),
                new Index(1200, 1300),
                new Index(2500, 3000)));
        EasyMock.replay(gumPathMock);

        List<Rectangle> borders = new ArrayList<Rectangle>();
        borders.add(new Rectangle(12, 10, 0, 7));
        borders.add(new Rectangle(18, 12, 0, 7));
        borders.add(new Rectangle(21, 20, 10, 0));
        borders.add(new Rectangle(21, 25, 10, 0));

        try {
            assertPathInAllBorders(borders, gumPathMock);
            Assert.fail("AssertionError expected");
        } catch (AssertionError assertionError) {
            // OK
        }
    }

    @Test
    @DirtiesContext
    public void toItemAngelSameAtomLoop1() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, ANGELS_24);
        Index start = new Index(1000, 1000);
        Index destination = new Index(3000, 3000);
        Index middle = new Index(2000, 2000);

        try {
            for (double angel = 0.0; angel < MathHelper.ONE_RADIANT; angel += 0.0001) {
                System.out.println("angel: " + MathHelper.radToGrad(angel));
                List<Index> path = GumPath.toItemAngelSameAtom(start, destination, boundingBox);
                assertPathAngels(path, boundingBox);
                start = start.rotateCounterClock(middle, angel);
                destination = destination.rotateCounterClock(middle, angel);
            }
        } catch (Throwable t) {
            System.out.println("start: " + start + " destination: " + destination);
            throw t;
        }
    }

    @Test
    @DirtiesContext
    public void toItemAngelSameAtomLoop2() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, ANGELS_24_2);
        Index start = new Index(1000, 1000);
        Index destination = new Index(3000, 3000);
        Index middle = new Index(2000, 2000);

        try {
            for (double angel = 0.0; angel <= MathHelper.ONE_RADIANT; angel += 0.0001) {
                System.out.println("angel: " + MathHelper.radToGrad(angel));
                List<Index> path = GumPath.toItemAngelSameAtom(start, destination, boundingBox);
                assertPathAngels(path, boundingBox);
                start = start.rotateCounterClock(middle, angel);
                destination = destination.rotateCounterClock(middle, angel);
            }
        } catch (Throwable t) {
            System.out.println("start: " + start + " destination: " + destination);
            throw t;
        }
    }

    @Test
    @DirtiesContext
    public void toItemAngelSameAtom1() throws Throwable {
        BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, ANGELS_24_2);
        Index start = new Index(2538, 1311);
        Index destination = new Index(841, 1348);

        List<Index> path = GumPath.toItemAngelSameAtom(start, destination, boundingBox);

        Index point1 = null;
        for (Index index : path) {
            if (point1 != null) {
                debugService.drawLine(new Line(point1, index), Color.RED);
            }
            point1 = index;
        }
        debugService.drawPosition(start, Color.BLUE);
        debugService.drawPosition(destination, Color.BLUE);
        debugService.waitForClose();
        assertPathAngels(path, boundingBox);
    }

    private void assertPathAngels(List<Index> path, BoundingBox boundingBox) {
        Index start = path.get(0);
        Index destination = path.get(path.size() - 1);
        Index point1 = null;
        Line line = new Line(start, destination);
        for (Index index : path) {
            if (point1 != null) {
                Assert.assertFalse("Points are equals:" + point1, point1.equals(index));
                double angel = point1.getAngleToNord(index);
                double allowedAngel = boundingBox.getAllowedAngel(angel);
                double delta = MathHelper.getAngel(angel, allowedAngel);
                double distance = point1.getDistanceDouble(index);
                if (delta > MathHelper.gradToRad(0.1)) {
                    Index allowedPoint = point1.getPointFromAngelToNord(allowedAngel, distance);
                    if (allowedPoint.getDistanceDouble(index) > 1.5) {
                        System.out.println("distance: " + distance);
                        System.out.println("Allowed Point:" + allowedPoint + " actual:" + index);
                        Assert.fail("Delta too big: " + MathHelper.radToGrad(delta));
                    }
                }

                if (line.getShortestDistance(index) > 11.5) {
                    Assert.fail("Distance too big:" + line.getShortestDistance(index));
                }
            }
            point1 = index;
        }
    }

    @Test
    @DirtiesContext
    public void testGumPath1() throws Exception {
        configureComplexGame();

        //BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, new double[]{MathHelper.NORTH, MathHelper.EAST, MathHelper.SOUTH, MathHelper.WEST});
        //BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, new double[]{MathHelper.NORTH_EAST, MathHelper.NORTH_WEST, MathHelper.SOUTH_EAST, MathHelper.SOUTH_WEST});
        BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, ANGELS_24);

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(1400, 0), new Index(2000, 300)), new Rectangle(new Index(2000, 0), new Index(2500, 300))));
        ports.add(new Port(new Rectangle(new Index(2000, 0), new Index(2500, 300)), new Rectangle(new Index(2500, 0), new Index(2900, 300))));
        ports.add(new Port(new Rectangle(new Index(2500, 0), new Index(2900, 300)), new Rectangle(new Index(2900, 0), new Index(3500, 300))));

        GumPath gumPath = new GumPath(new Index(1700, 200), new Index(3200, 190), ports, boundingBox);
        displayGumPath(gumPath);

        Assert.assertEquals(2, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(1700, 200), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(3200, 200), gumPath.getOptimizedPath().get(1));
    }

    private void displayGumPath(GumPath gumPath) {
        for (Port port : gumPath.getPorts()) {
            debugService.drawRectangle(port.getAbsoluteCurrent(), Color.DARK_GRAY);
            debugService.drawRectangle(port.getAbsoluteDestination(), Color.DARK_GRAY);
            debugService.drawLine(port.getCurrentCrossLine(), Color.RED);
            debugService.drawLine(port.getDestinationCrossLine(), Color.RED);
        }
        Index point1 = null;
        for (Index index : gumPath.getOptimizedPath()) {
            if (point1 != null) {
                debugService.drawLine(new Line(point1, index), new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            }
            point1 = index;
        }
        //debugService.drawLine(new Line(gumPath.getStart(), gumPath.getDestination()), Color.BLUE);
        debugService.drawPosition(gumPath.getStart(), Color.BLUE);
        debugService.drawPosition(gumPath.getDestination(), Color.BLUE);
        debugService.waitForClose();
    }

    @Test
    @DirtiesContext
    public void testGumPath2() throws Exception {
        configureComplexGame();

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(400, 1000), new Index(1200, 1600)), new Rectangle(new Index(1200, 1000), new Index(1800, 1800))));
        ports.add(new Port(new Rectangle(new Index(1200, 1000), new Index(1800, 1800)), new Rectangle(new Index(1200, 1800), new Index(3000, 3000))));
        ports.add(new Port(new Rectangle(new Index(1200, 1800), new Index(3000, 2000)), new Rectangle(new Index(2100, 2000), new Index(3000, 2500))));
        ports.add(new Port(new Rectangle(new Index(2100, 2000), new Index(3000, 2500)), new Rectangle(new Index(2100, 2500), new Index(3000, 3100))));

        GumPath gumPath = new GumPath(new Index(700, 1300), new Index(2500, 3000), ports, boundingBox);

        Assert.assertEquals(3, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(700, 1300), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(2100, 1999), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(2500, 3000), gumPath.getOptimizedPath().get(2));
    }

    @Test
    @DirtiesContext
    public void testGumPath3() throws Exception {
        configureComplexGame();

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(4200, 700), new Index(4700, 1400)), new Rectangle(new Index(4700, 700), new Index(5200, 1500))));
        ports.add(new Port(new Rectangle(new Index(4700, 700), new Index(5200, 1500)), new Rectangle(new Index(5200, 1000), new Index(5900, 1600))));
        ports.add(new Port(new Rectangle(new Index(5200, 1000), new Index(5900, 1600)), new Rectangle(new Index(5500, 1600), new Index(5900, 2200))));
        ports.add(new Port(new Rectangle(new Index(5200, 1800), new Index(5900, 2200)), new Rectangle(new Index(4800, 1800), new Index(5200, 2200))));
        ports.add(new Port(new Rectangle(new Index(4800, 1800), new Index(5200, 2200)), new Rectangle(new Index(4200, 1800), new Index(4800, 2200))));


        GumPath gumPath = new GumPath(new Index(4400, 1300), new Index(4400, 2000), ports, boundingBox);

        Assert.assertEquals(6, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(4400, 1300), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(5200, 1499), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(5500, 1599), gumPath.getOptimizedPath().get(2));
        Assert.assertEquals(new Index(5500, 1600), gumPath.getOptimizedPath().get(3));
        Assert.assertEquals(new Index(5200, 1800), gumPath.getOptimizedPath().get(4));
        Assert.assertEquals(new Index(4400, 2000), gumPath.getOptimizedPath().get(5));
    }

    @Test
    @DirtiesContext
    public void testGumPath4() throws Exception {
        configureComplexGame();

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(0, 4000), new Index(600, 4500)), new Rectangle(new Index(600, 3800), new Index(1000, 4500))));
        ports.add(new Port(new Rectangle(new Index(600, 3800), new Index(1000, 4500)), new Rectangle(new Index(1000, 3800), new Index(2000, 4200))));
        ports.add(new Port(new Rectangle(new Index(1000, 3800), new Index(2000, 4200)), new Rectangle(new Index(1500, 4200), new Index(2300, 4600))));
        ports.add(new Port(new Rectangle(new Index(1500, 4200), new Index(2300, 4600)), new Rectangle(new Index(1800, 4600), new Index(2500, 5300))));
        ports.add(new Port(new Rectangle(new Index(1900, 4900), new Index(2500, 5300)), new Rectangle(new Index(2500, 4900), new Index(3100, 5100))));
        ports.add(new Port(new Rectangle(new Index(2500, 4600), new Index(3100, 5100)), new Rectangle(new Index(3100, 4600), new Index(4600, 5100))));
        ports.add(new Port(new Rectangle(new Index(3300, 4400), new Index(3800, 4800)), new Rectangle(new Index(3300, 4100), new Index(3900, 4400))));
        ports.add(new Port(new Rectangle(new Index(3400, 4100), new Index(3900, 4300)), new Rectangle(new Index(3400, 3500), new Index(3900, 4100))));
        ports.add(new Port(new Rectangle(new Index(3400, 3500), new Index(4100, 3900)), new Rectangle(new Index(4100, 3500), new Index(4600, 3900))));
        ports.add(new Port(new Rectangle(new Index(4200, 3300), new Index(4700, 3600)), new Rectangle(new Index(4700, 3300), new Index(5400, 3600))));


        GumPath gumPath = new GumPath(new Index(200, 4200), new Index(5100, 3400), ports, boundingBox);

        Assert.assertEquals(7, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(200, 4200), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(1000, 3800), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(2499, 4900), gumPath.getOptimizedPath().get(2));
        Assert.assertEquals(new Index(3100, 4600), gumPath.getOptimizedPath().get(3));
        Assert.assertEquals(new Index(3300, 4400), gumPath.getOptimizedPath().get(4));
        Assert.assertEquals(new Index(4100, 3800), gumPath.getOptimizedPath().get(5));
        Assert.assertEquals(new Index(5100, 3400), gumPath.getOptimizedPath().get(6));
    }

    @Test
    @DirtiesContext
    public void testGumPath5() throws Exception {
        configureComplexGame();

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(100, 6000), new Index(600, 6400)), new Rectangle(new Index(600, 6000), new Index(900, 6700))));
        ports.add(new Port(new Rectangle(new Index(600, 6000), new Index(900, 6700)), new Rectangle(new Index(900, 6100), new Index(1200, 6700))));
        ports.add(new Port(new Rectangle(new Index(1300, 6200), new Index(1900, 6600)), new Rectangle(new Index(1300, 6600), new Index(2200, 7200))));
        ports.add(new Port(new Rectangle(new Index(1800, 6700), new Index(2200, 7200)), new Rectangle(new Index(2200, 6600), new Index(2800, 7200))));
        ports.add(new Port(new Rectangle(new Index(2200, 6600), new Index(2800, 7200)), new Rectangle(new Index(2800, 6600), new Index(3800, 7000))));

        GumPath gumPath = new GumPath(new Index(300, 6200), new Index(3200, 6800), ports, boundingBox);

        Assert.assertEquals(3, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(300, 6200), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(2200, 6700), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(3200, 6800), gumPath.getOptimizedPath().get(2));
    }

    @Test
    @DirtiesContext
    public void testGumPath6() throws Exception {
        configureComplexGame();

        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(5100, 7500), new Index(6000, 7800)), new Rectangle(new Index(6000, 7500), new Index(6400, 7800))));
        ports.add(new Port(new Rectangle(new Index(6700, 6200), new Index(7000, 6700)), new Rectangle(new Index(7000, 6200), new Index(7400, 6700))));
        ports.add(new Port(new Rectangle(new Index(7500, 7500), new Index(7800, 7800)), new Rectangle(new Index(7800, 7500), new Index(8700, 7800))));

        GumPath gumPath = new GumPath(new Index(5500, 7500), new Index(8400, 7500), ports, boundingBox);

        Assert.assertEquals(6, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(5500, 7500), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(6000, 7500), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(6999, 6699), gumPath.getOptimizedPath().get(2));
        Assert.assertEquals(new Index(7000, 6699), gumPath.getOptimizedPath().get(3));
        Assert.assertEquals(new Index(7799, 7500), gumPath.getOptimizedPath().get(4));
        Assert.assertEquals(new Index(8400, 7500), gumPath.getOptimizedPath().get(5));
    }

    @Test
    @DirtiesContext
    public void testGumPath7() throws Exception {
        List<Port> ports = new ArrayList<Port>();
        ports.add(new Port(new Rectangle(new Index(1400, 1500), new Index(2300, 1600)), new Rectangle(new Index(1100, 1400), new Index(2200, 1500))));
        ports.add(new Port(new Rectangle(new Index(1100, 1400), new Index(2200, 1500)), new Rectangle(new Index(900, 1300), new Index(1100, 1500))));
        ports.add(new Port(new Rectangle(new Index(900, 1300), new Index(1100, 1500)), new Rectangle(new Index(300, 1500), new Index(1100, 1600))));

        GumPath gumPath = new GumPath(new Index(1471, 1538), new Index(960, 1551), ports, boundingBox);

        Assert.assertEquals(4, gumPath.getOptimizedPath().size());
        Assert.assertEquals(new Index(1471, 1538), gumPath.getOptimizedPath().get(0));
        Assert.assertEquals(new Index(1400, 1499), gumPath.getOptimizedPath().get(1));
        Assert.assertEquals(new Index(1099, 1499), gumPath.getOptimizedPath().get(2));
        Assert.assertEquals(new Index(960, 1551), gumPath.getOptimizedPath().get(3));
    }


    private void assertPathInAllBorders(List<Rectangle> borders, GumPath gumPath) {
        Index previousPoint = null;
        int borderIndex = 0;
        for (Index point : gumPath.getOptimizedPath()) {
            if (previousPoint != null) {
                while (borderIndex < borders.size()) {
                    Rectangle border = borders.get(borderIndex);
                    Rectangle absBorder = terrainService.convertToAbsolutePosition(border);
                    if (absBorder.doesLineCut(previousPoint, point)) {
                        borderIndex++;
                    } else {
                        break;
                    }
                }
            }
            previousPoint = point;
        }
        if (borderIndex < borders.size()) {
            Assert.fail("Border has not been crossed: [" + borderIndex + "] " + borders.get(borderIndex));
        }
    }

    @Test
    @DirtiesContext
    public void testOptimizePath1() throws Exception {
        //configureComplexGame();

        //Path path = new Path();

        //PathFinderUtilities.optimizePathOnePass()
    }


}
