package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.terrain.TerrainService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 02.05.2011
 * Time: 13:10:21
 */
public class TestPathFinding extends AbstractServiceTest {
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private DebugService debugService;

    @Test
    @DirtiesContext
    public void testTest() throws Exception {
        List<Index> path = new ArrayList<Index>();
        path.add(new Index(0, 0));
        path.add(new Index(1000, 1200));
        path.add(new Index(1000, 1800));
        path.add(new Index(0, 1900));
        assertPathCanBeReduced(path);

        path = new ArrayList<Index>();
        path.add(new Index(0, 0));
        path.add(new Index(700, 0));
        path.add(new Index(700, 600));
        path.add(new Index(0, 600));
        try {
            assertPathCanBeReduced(path);
            Assert.fail("AssertionError expected");
        } catch (AssertionError assertionError) {
            // OK
        }

        path = new ArrayList<Index>();
        path.add(new Index(0, 0));
        path.add(new Index(1000, 1200));
        path.add(new Index(1000, 1800));
        path.add(new Index(500, 1900));
        path.add(new Index(0, 1900));
        try {
            assertPathCanBeReduced(path);
            Assert.fail("AssertionError expected");
        } catch (AssertionError assertionError) {
            // OK
        }

    }

    @Test
    @DirtiesContext
    public void testPath1() throws Exception {
        configureComplexGame();
        List<Index> path = collisionService.setupPathToDestination(new Index(800, 3400), new Index(2000, 2700), TerrainType.LAND, new BoundingBox(0, 0, 0, 0, ANGELS_24));
        assertPathNotInTerrainImage(path);
        // assertPathCanBeReduced(path); Do this may later
    }

    private void assertPathNotInTerrainImage(List<Index> path) {
        Index previous = null;
        for (Index index : path) {
            if (previous != null) {
                assertLineNotInTerrainImage(previous, index);
            }
            previous = index;
        }
    }

    private void assertPathCanBeReduced(List<Index> path) {
        if (path.size() < 3) {
            return;
        }

        for (int i = 0; i < path.size(); i++) {
            Index index1 = path.get(i);
            for (int j = i + 2; j < path.size(); j++) {
                Index index2 = path.get(j);
                if (!lineInTerrainImage(index1, index2)) {
                    System.out.println("Original path: " + path);
                    Assert.fail("Points can be directly connected -> index1(" + i + ")[" + index1 + "] index2(" + j + ")[" + index2 + "]");
                }
            }
        }
    }

    private void assertLineNotInTerrainImage(Index point1, Index point2) {
        for (Rectangle complexTerrainRect : COMPLEX_TERRAIN_RECTS) {
            if (complexTerrainRect.doesLineCut(point1, point2)) {
                Assert.fail("Line dose cut terrain image: point1[" + point1 + "] point2[" + point2 + "] TerrainImage[" + complexTerrainRect + "]");
            }
        }
    }

    private boolean lineInTerrainImage(Index point1, Index point2) {
        for (Rectangle complexTerrainRect : COMPLEX_TERRAIN_RECTS) {
            if (complexTerrainRect.doesLineCut(point1, point2)) {
                return true;
            }
        }
        return false;
    }

    public static void assertRectangleNotInTerrainImage(Rectangle rectangle) {
        for (Rectangle complexTerrainRect : COMPLEX_TERRAIN_RECTS) {
            if (complexTerrainRect.adjoinsEclusive(rectangle)) {
                Assert.fail("Rectangle overlap terrain image: rectangle[" + rectangle + "] TerrainImage[" + complexTerrainRect + "]");
            }
        }

    }

}
