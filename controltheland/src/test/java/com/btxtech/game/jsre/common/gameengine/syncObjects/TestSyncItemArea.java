package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.item.ItemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;

/**
 * User: beat
 * Date: 23.08.2011
 * Time: 17:18:42
 */
public class TestSyncItemArea extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private DebugService debugService;

    @Test
    @DirtiesContext
    public void testStrait() {
        BoundingBox boundingBox = new BoundingBox(100, 100, 100, 100, 24);
        SyncItemArea syncItemArea1 = new SyncItemArea(boundingBox, new Index(300, 300));
        Assert.assertEquals(new Index(250, 250), syncItemArea1.getCorner1());
        Assert.assertEquals(new Index(250, 350), syncItemArea1.getCorner2());
        Assert.assertEquals(new Index(350, 350), syncItemArea1.getCorner3());
        Assert.assertEquals(new Index(350, 250), syncItemArea1.getCorner4());
    }

    @Test
    @DirtiesContext
    public void testRotated() {
        BoundingBox boundingBox = new BoundingBox(200, 200, 100, 150, 24);
        SyncItemArea syncItemArea1 = new SyncItemArea(boundingBox, new Index(300, 300));
        syncItemArea1.setAngel(MathHelper.gradToRad(10));
        Assert.assertEquals(new Index(238, 235), syncItemArea1.getCorner1());
        Assert.assertEquals(new Index(264, 383), syncItemArea1.getCorner2());
        Assert.assertEquals(new Index(362, 365), syncItemArea1.getCorner3());
        Assert.assertEquals(new Index(336, 217), syncItemArea1.getCorner4());
    }

    @Test
    @DirtiesContext
    public void testInRangePosition() {
        BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
        Assert.assertTrue(syncItemArea.isInRange(100, new Index(200, 400)));
        Assert.assertTrue(syncItemArea.isInRange(101, new Index(200, 400)));
        Assert.assertTrue(syncItemArea.isInRange(1000, new Index(200, 400)));
        Assert.assertFalse(syncItemArea.isInRange(10, new Index(200, 400)));
        Assert.assertFalse(syncItemArea.isInRange(99, new Index(200, 400)));
        // TODO test angel != 0
    }

    @Test
    @DirtiesContext
    public void testInRange() {
        BoundingBox boundingBox = new BoundingBox(100, 100, 80, 80, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(5000, 5200));

        BaseItemType baseItemType = new BaseItemType();
        baseItemType.setBoundingBox(new BoundingBox(100, 100, 80, 80, 1));
        Assert.assertTrue(syncItemArea.isInRange(10, new Index(5000, 5290), baseItemType));
        // TODO test angel != 0
    }

    @Test
    @DirtiesContext
    public void testInRangePositionInside() {
        BoundingBox boundingBox = new BoundingBox(200, 200, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(300, 300));
        Assert.assertTrue(syncItemArea.isInRange(100, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(1, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(0, new Index(300, 300)));

        syncItemArea = new SyncItemArea(boundingBox, new Index(201, 201));
        Assert.assertTrue(syncItemArea.isInRange(100, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(1, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(0, new Index(300, 300)));

        syncItemArea = new SyncItemArea(boundingBox, new Index(399, 399));
        Assert.assertTrue(syncItemArea.isInRange(100, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(1, new Index(300, 300)));
        Assert.assertTrue(syncItemArea.isInRange(0, new Index(300, 300)));
        // TODO test angel != 0
    }

    @Test
    @DirtiesContext
    public void testInRangeSyncItem__TMP() throws Exception {
        configureMinimalGame();
        // TODO test angel != 0

        BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
        double max = 0;
        double min = Double.POSITIVE_INFINITY;
        for (double angel = 0; angel <= 360.0; angel += 0.5) {
            syncItemArea.turnTo(MathHelper.gradToRad(angel));
            double distance = syncItemArea.getDistance(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 640), new Id(1, 1, 1)));
            System.out.println(angel + ":" + distance);
            if (distance > max) {
                max = distance;
            }
            if (min > distance) {
                min = distance;
            }
        }
    }

    @Test
    @DirtiesContext
    public void testInRangeSyncItem() throws Exception {
        configureMinimalGame();

        assertSameDistance(45, 102);
        assertSameDistance(0, 160);
        // TODO more angel test

//
//        Assert.assertTrue(syncItemArea.isInRange(100, createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, 1, 1))));
//        Assert.assertTrue(syncItemArea.isInRange(100, createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 640), new Id(1, 1, 1))));
//
//        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
//        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
//        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));
//        target.getSyncItemArea().turnTo(MathHelper.gradToRad(0));
//
//        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1500, 1220), new Id(2, -100, -100));
//        syncBaseItem.getSyncItemArea().turnTo(MathHelper.gradToRad(0));
//        Assert.assertTrue(syncBaseItem.getSyncItemArea().isInRange(200, target.getSyncItemArea()));
//
//        targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
//        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
//        target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));
//        target.getSyncItemArea().turnTo(MathHelper.gradToRad(14.32394487827058));
/////////////////
//        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1572, 1771), new Id(2, -100, -100));
//        syncBaseItem.getSyncItemArea().turnTo(MathHelper.gradToRad(194.80565034447966));
//
//        ItemType targetItemType = itemService.getItemType(TEST_SIMPLE_BUILDING_ID);
//        targetItemType.setBoundingBox(new BoundingBox(100, 100, 200, 80, 1));
//        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(1, -100, -100));
//        target.getSyncItemArea().turnTo(MathHelper.gradToRad(14.32394487827058));
//
//        System.out.println(syncBaseItem.getSyncItemArea().getDistance(target.getSyncItemArea()));
//        debugService.drawSyncItemArea(syncBaseItem.getSyncItemArea(), Color.BLACK);
//        debugService.drawSyncItemArea(target.getSyncItemArea(), Color.BLACK);
//        debugService.waitForClose();
        //Assert.assertTrue(syncBaseItem.getSyncItemArea().isInRange(200, target.getSyncItemArea()));
    }

    private void assertSameDistance(double startAngel, int distance) throws Exception {
        for (double attackerAngel = startAngel; attackerAngel <= 360.0; attackerAngel += 90) {
            for (double targetAngel = startAngel; targetAngel <= 360.0; targetAngel += 90) {
                BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
                SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
                syncItemArea.turnTo(MathHelper.gradToRad(attackerAngel));

                SyncBaseItem target = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 700), new Id(1, 1, 1));
                target.getSyncItemArea().turnTo(MathHelper.gradToRad(targetAngel));

                Assert.assertEquals(distance, syncItemArea.getDistanceRounded(target));
            }
        }
    }

    @Test
    @DirtiesContext
    public void testInRangeSyncItemArea() throws Exception {
        configureMinimalGame();

        BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
        BoundingBox boundingBoxTarget = new BoundingBox(400, 400, 100, 100, 1);
        Assert.assertTrue(syncItemArea.isInRange(100, new SyncItemArea(boundingBoxTarget, new Index(400, 400))));
        Assert.assertTrue(syncItemArea.isInRange(100, new SyncItemArea(boundingBoxTarget, new Index(150, 400))));
        Assert.assertFalse(syncItemArea.isInRange(99, new SyncItemArea(boundingBoxTarget, new Index(150, 400))));
        // TODO test angel != 0
    }

    @Test
    @DirtiesContext
    public void getDistanceToPoint() throws Exception {
        BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(500, 500));
        Assert.assertEquals(300, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(90));
        Assert.assertEquals(300, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(180));
        Assert.assertEquals(300, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(270));
        Assert.assertEquals(300, syncItemArea.getDistance(new Index(100, 500)), 0.001);

        syncItemArea.setAngel(MathHelper.gradToRad(45));
        Assert.assertEquals(258.801, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(135));
        Assert.assertEquals(258.801, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(225));
        Assert.assertEquals(258.801, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(315));
        Assert.assertEquals(258.801, syncItemArea.getDistance(new Index(100, 500)), 0.001);

        syncItemArea.setAngel(MathHelper.gradToRad(60));
        Assert.assertEquals(265.54848, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(150));
        Assert.assertEquals(265.54848, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(240));
        Assert.assertEquals(265.54848, syncItemArea.getDistance(new Index(100, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(330));
        Assert.assertEquals(265.54848, syncItemArea.getDistance(new Index(100, 500)), 0.001);

        boundingBox = new BoundingBox(400, 400, 300, 100, 1);
        syncItemArea = new SyncItemArea(boundingBox, new Index(300, 200));
        Assert.assertEquals(250, syncItemArea.getDistance(new Index(400, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(10));
        Assert.assertEquals(263, syncItemArea.getDistance(new Index(400, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(35));
        Assert.assertEquals(253, syncItemArea.getDistance(new Index(400, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(111));
        Assert.assertEquals(166, syncItemArea.getDistance(new Index(400, 500)), 0.001);

        boundingBox = new BoundingBox(400, 400, 300, 300, 1);
        syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
        Assert.assertEquals(0, syncItemArea.getDistance(new Index(400, 500)), 0.001);
        syncItemArea.setAngel(MathHelper.gradToRad(359));
        Assert.assertEquals(0, syncItemArea.getDistance(new Index(400, 500)), 0.001);
    }

}
