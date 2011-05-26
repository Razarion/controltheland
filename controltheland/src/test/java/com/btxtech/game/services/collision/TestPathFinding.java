package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.terrain.TerrainService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;
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

        List<Index> path = collisionService.setupPathToDestination(new Index(800, 3400), new Index(2000, 2700), TerrainType.LAND);
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

    @Test
    @DirtiesContext
    public void testCircleFormationNoBlockingObject() throws Exception {
        configureMinimalGame();

        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<CircleFormation.CircleFormationItem> items = new ArrayList<CircleFormation.CircleFormationItem>();
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 300));
        CircleFormation circleFormation = new CircleFormation(target.getRectangle(), MathHelper.EAST - 0.1, items);

        List<Rectangle> expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(848, 490, 100, 100));
        expected.add(new Rectangle(825, 590, 100, 100));
        expected.add(new Rectangle(845, 390, 100, 100));
        expected.add(new Rectangle(770, 690, 100, 100));
        expected.add(new Rectangle(817, 290, 100, 100));
        expected.add(new Rectangle(670, 784, 100, 100));
        expected.add(new Rectangle(754, 190, 100, 100));
        expected.add(new Rectangle(570, 832, 100, 100));
        expected.add(new Rectangle(654, 106, 100, 100));
        expected.add(new Rectangle(470, 849, 100, 100));
        expected.add(new Rectangle(554, 64, 100, 100));
        expected.add(new Rectangle(370, 842, 100, 100));
        expected.add(new Rectangle(454, 50, 100, 100));
        expected.add(new Rectangle(270, 807, 100, 100));
        expected.add(new Rectangle(354, 62, 100, 100));
        expected.add(new Rectangle(170, 736, 100, 100));
        expected.add(new Rectangle(254, 101, 100, 100));
        expected.add(new Rectangle(96, 636, 100, 100));
        expected.add(new Rectangle(154, 181, 100, 100));
        expected.add(new Rectangle(59, 536, 100, 100));
        expected.add(new Rectangle(87, 281, 100, 100));
        expected.add(new Rectangle(50, 436, 100, 100));

        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        while (circleFormation.hasNext()) {
            CircleFormation.CircleFormationItem circleFormationItem = circleFormation.calculateNextEntry();
            Assert.assertTrue(circleFormationItem.isInRange());
            rectangles.add(circleFormationItem.getRectangle());
            // ***************************************************************************************************
            /*System.out.println("expected.add(new Rectangle(" + circleFormationItem.getRectangle().getX()
                    + " ," + circleFormationItem.getRectangle().getY()
                    + " ," + circleFormationItem.getRectangle().getWidth()
                    + " ," + circleFormationItem.getRectangle().getHeight() + "));");  */
            // ***************************************************************************************************
            circleFormation.lastAccepted();
        }
        for (int i = 0, rectanglesSize = rectangles.size(); i < rectanglesSize; i++) {
            Assert.assertEquals(expected.get(i), rectangles.get(i));
        }
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    @DirtiesContext
    public void testCircleFormationBlocking() throws Exception {
        configureMinimalGame();

        Rectangle rectangle1 = new Rectangle(0, 0, 1000, 300);
        Rectangle rectangle2 = new Rectangle(0, 300, 300, 1000);

        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<CircleFormation.CircleFormationItem> items = new ArrayList<CircleFormation.CircleFormationItem>();
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        CircleFormation circleFormation = new CircleFormation(target.getRectangle(), MathHelper.SOUTH_EAST, items);

        List<Rectangle> expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(491, 491, 100, 100));
        expected.add(new Rectangle(391, 546, 100, 100));
        expected.add(new Rectangle(546, 391, 100, 100));
        expected.add(new Rectangle(450, 676, 100, 100));
        expected.add(new Rectangle(550, 626, 100, 100));
        expected.add(new Rectangle(642, 526, 100, 100));
        expected.add(new Rectangle(682, 426, 100, 100));
        expected.add(new Rectangle(690, 326, 100, 100));
        expected.add(new Rectangle(450, 822, 100, 100));
        expected.add(new Rectangle(550, 789, 100, 100));
        expected.add(new Rectangle(650, 727, 100, 100));
        expected.add(new Rectangle(744, 627, 100, 100));
        expected.add(new Rectangle(798, 527, 100, 100));
        expected.add(new Rectangle(826, 427, 100, 100));
        expected.add(new Rectangle(831, 327, 100, 100));
        expected.add(new Rectangle(450, 965, 100, 100));
        expected.add(new Rectangle(550, 940, 100, 100));
        expected.add(new Rectangle(650, 896, 100, 100));
        expected.add(new Rectangle(750, 828, 100, 100));
        expected.add(new Rectangle(846, 728, 100, 100));
        expected.add(new Rectangle(908, 628, 100, 100));
        expected.add(new Rectangle(947, 528, 100, 100));
        expected.add(new Rectangle(968, 428, 100, 100));
        expected.add(new Rectangle(973, 328, 100, 100));
        expected.add(new Rectangle(450, 1107, 100, 100));
        expected.add(new Rectangle(550, 1087, 100, 100));
        expected.add(new Rectangle(650, 1053, 100, 100));
        expected.add(new Rectangle(750, 1001, 100, 100));
        expected.add(new Rectangle(850, 928, 100, 100));
        expected.add(new Rectangle(946, 828, 100, 100));
        expected.add(new Rectangle(1014, 728, 100, 100));
        expected.add(new Rectangle(1062, 628, 100, 100));
        expected.add(new Rectangle(1093, 528, 100, 100));
        expected.add(new Rectangle(1110, 428, 100, 100));
        expected.add(new Rectangle(1114, 328, 100, 100));
        expected.add(new Rectangle(450, 1249, 100, 100));
        expected.add(new Rectangle(550, 1233, 100, 100));
        expected.add(new Rectangle(650, 1204, 100, 100));
        expected.add(new Rectangle(750, 1162, 100, 100));
        expected.add(new Rectangle(850, 1104, 100, 100));
        expected.add(new Rectangle(950, 1028, 100, 100));
        expected.add(new Rectangle(1047, 928, 100, 100));

        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        int count = 0;
        while (circleFormation.hasNext()) {
            CircleFormation.CircleFormationItem circleFormationItem = circleFormation.calculateNextEntry();
            Assert.assertNotNull(circleFormationItem.getDestinationHint());
            if (rectangle1.adjoinsEclusive(circleFormationItem.getRectangle()) || rectangle2.adjoinsEclusive(circleFormationItem.getRectangle())) {
                continue;
            }
            if (count < 3) {
                Assert.assertTrue(circleFormationItem.isInRange());
            } else {
                Assert.assertFalse(circleFormationItem.isInRange());
            }
            count++;
            circleFormation.lastAccepted();
            rectangles.add(Rectangle.generateRectangleFromMiddlePoint(circleFormationItem.getDestinationHint(), 100, 100));
        }
        for (int i = 0, rectanglesSize = rectangles.size(); i < rectanglesSize; i++) {
            Assert.assertEquals(expected.get(i), rectangles.get(i));
        }
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    @DirtiesContext
    public void testCircleFormationBlockingChannel() throws Exception {
        configureMinimalGame();

        Rectangle rectangle1 = new Rectangle(0, 0, 1000, 300);
        Rectangle rectangle2 = new Rectangle(0, 300, 300, 500);
        Rectangle rectangle3 = new Rectangle(500, 300, 300, 500);

        SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, -100, -100));

        List<CircleFormation.CircleFormationItem> items = new ArrayList<CircleFormation.CircleFormationItem>();
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 60));
        CircleFormation circleFormation = new CircleFormation(target.getRectangle(), MathHelper.SOUTH - 0.35, items);

        List<Rectangle> expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(395, 504, 100, 100));
        expected.add(new Rectangle(352, 651, 100, 100));
        expected.add(new Rectangle(313, 790, 100, 100));
        expected.add(new Rectangle(74, 864, 100, 100));
        expected.add(new Rectangle(174, 906, 100, 100));
        expected.add(new Rectangle(274, 928, 100, 100));
        expected.add(new Rectangle(374, 932, 100, 100));

        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        int count = 0;
        while (circleFormation.hasNext()) {
            CircleFormation.CircleFormationItem circleFormationItem = circleFormation.calculateNextEntry();
            Assert.assertNotNull(circleFormationItem.getDestinationHint());
            if (rectangle1.adjoinsEclusive(circleFormationItem.getRectangle())
                    || rectangle2.adjoinsEclusive(circleFormationItem.getRectangle())
                    || rectangle3.adjoinsEclusive(circleFormationItem.getRectangle())) {
                continue;
            }
            if (count < 1) {
                Assert.assertTrue(circleFormationItem.isInRange());
            } else {
                Assert.assertFalse(circleFormationItem.isInRange());
            }
            count++;

            circleFormation.lastAccepted();
            rectangles.add(Rectangle.generateRectangleFromMiddlePoint(circleFormationItem.getDestinationHint(), 100, 100));
        }
        for (int i = 0, rectanglesSize = rectangles.size(); i < rectanglesSize; i++) {
            Assert.assertEquals(expected.get(i), rectangles.get(i));
        }
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    @DirtiesContext
    public void testSetupDestinationHints1() throws Exception {
        configureComplexGame();

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, -100, -100));

        List<CircleFormation.CircleFormationItem> items = new ArrayList<CircleFormation.CircleFormationItem>();
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));
        items.add(new CircleFormation.CircleFormationItem(syncBaseItem, 100));

        SyncItem target = createSyncResourceItem(TEST_RESOURCE_ITEM_ID, new Index(1000, 1000), new Id(1, -100, -100));

        collisionService.setupDestinationHints(target, items);

        for (CircleFormation.CircleFormationItem item : items) {
            System.out.println(item.getDestinationHint());
        }
    }


}
