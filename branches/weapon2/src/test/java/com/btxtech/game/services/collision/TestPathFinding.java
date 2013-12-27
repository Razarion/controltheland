package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.PlanetSystemService;
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
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testTest() throws Exception {
        List<Index> path = new ArrayList<>();
        path.add(new Index(0, 0));
        path.add(new Index(1000, 1200));
        path.add(new Index(1000, 1800));
        path.add(new Index(0, 1900));
        assertPathCanBeReduced(path);

        path = new ArrayList<>();
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

        path = new ArrayList<>();
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
        configureOneLevelOnePlaneComplexTerrain();
        CollisionService collisionService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService();
        Path path = collisionService.setupPathToDestination(new Index(800, 3400), new Index(2000, 2700), TerrainType.LAND, new BoundingBox(0, ANGELS_24));
        assertPathNotInTerrainImage(path);
        // assertPathCanBeReduced(path); Do this may later
    }

    @Test
    @DirtiesContext
    public void testPathSameStartAndDest() throws Exception {
        configureOneLevelOnePlaneComplexTerrain();
        CollisionService collisionService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService();
        Path path = collisionService.setupPathToDestination(new Index(800, 3400), new Index(800, 3400), TerrainType.LAND, new BoundingBox(0, ANGELS_24));
        assertPathNotInTerrainImage(path);
        Assert.assertEquals(1, path.getPath().size());
        Assert.assertEquals(new Index(800, 3400), path.getPath().get(0));
    }

    @Test
    @DirtiesContext
    public void testPathDifferentTerrains() throws Exception {
        configureOneLevelOnePlanetComplexTerrain2();
        CollisionService collisionService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService();
        SyncItemArea target = new BoundingBox(100, ANGELS_24).createSyntheticSyncItemArea(new Index(2750, 350));
        List<AttackFormationItem> attacker = new ArrayList<>();
        attacker.add(new AttackFormationItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1450, 350), new Id(0, 0)), 250));
        attacker = collisionService.setupDestinationHints(target, attacker);
        Assert.assertEquals(new Index(2325, 289), attacker.get(0).getDestinationHint());
        Assert.assertEquals(4.569833164347493, attacker.get(0).getDestinationAngel(), 0.001);
        Assert.assertTrue(attacker.get(0).isInRange());
    }


    private void assertPathNotInTerrainImage(Path path) {
        Index previous = null;
        for (Index index : path.getPath()) {
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
