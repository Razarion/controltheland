package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 23.08.2011
 * Time: 17:18:42
 */
public class TestSyncItemArea extends AbstractServiceTest {

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

        // TODO range should be 10 -> rounding problem
        Assert.assertTrue(syncItemArea.isInRange(11, new Index(5000, 5290), baseItemType));
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
    public void testInRangeSyncItem() throws Exception {
        configureMinimalGame();

        BoundingBox boundingBox = new BoundingBox(400, 400, 200, 200, 1);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));

        Assert.assertTrue(syncItemArea.isInRange(100, createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 400), new Id(1, 1, 1))));
        // TODO why 101
        Assert.assertTrue(syncItemArea.isInRange(101, createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(400, 640), new Id(1, 1, 1))));
        // TODO test angel != 0
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

}
