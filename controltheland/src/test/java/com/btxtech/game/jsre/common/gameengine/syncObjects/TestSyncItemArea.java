package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 23.08.2011
 * Time: 17:18:42
 */
public class TestSyncItemArea {

    @Test
    public void testContains() {
        BoundingBox boundingBox = new BoundingBox(200, AbstractServiceTest.ANGELS_24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(400, 400));
        Assert.assertTrue(syncItemArea.contains(new Index(400, 300)));
        Assert.assertTrue(syncItemArea.contains(new Index(400, 201)));
        Assert.assertTrue(syncItemArea.contains(new Index(400, 200)));
        Assert.assertFalse(syncItemArea.contains(new Index(400, 100)));
        Assert.assertTrue(syncItemArea.contains(new Index(280, 300)));
        Assert.assertTrue(syncItemArea.contains(new Index(232, 295)));
        Assert.assertTrue(syncItemArea.contains(new Index(272, 246)));
        Assert.assertTrue(syncItemArea.contains(new Index(272, 246)));
        Assert.assertFalse(syncItemArea.contains(new Index(316, 217)));
        Assert.assertTrue(syncItemArea.contains(new Index(438, 586)));
        Assert.assertFalse(syncItemArea.contains(new Index(549, 535)));
    }

    @Test
    public void testContainsRectangle() {
        Rectangle rectangle = new Rectangle(1000, 1000, 500, 400);
        BoundingBox boundingBox = new BoundingBox(100, AbstractServiceTest.ANGELS_24);
        //Inside
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1200, 1200)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1300, 1300)).contains(rectangle));
        // Edge
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(900, 1000)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(899, 1000)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1000, 900)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(1000, 899)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1510, 1000)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(1601, 1000)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1000, 1410)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(1000, 1501)).contains(rectangle));
        // Corners
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1000, 1000)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(930, 930)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(929, 929)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1000, 1399)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(930, 1469)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(929, 1470)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1499, 1000)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1569, 930)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(1570, 929)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1499, 1399)).contains(rectangle));
        Assert.assertTrue(new SyncItemArea(boundingBox, new Index(1569, 1469)).contains(rectangle));
        Assert.assertFalse(new SyncItemArea(boundingBox, new Index(1570, 1470)).contains(rectangle));
    }

    @Test
    public void testInRange() {
        BoundingBox boundingBox = new BoundingBox(100, AbstractServiceTest.ANGELS_24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(1000, 1000));

        BaseItemType baseItemType = new BaseItemType();
        baseItemType.setBoundingBox(new BoundingBox(200, AbstractServiceTest.ANGELS_24));
        Assert.assertTrue(syncItemArea.isInRange(700, new Index(1000, 1000), baseItemType));
        Assert.assertTrue(syncItemArea.isInRange(700, new Index(1000, 2000), baseItemType));
        Assert.assertTrue(syncItemArea.isInRange(701, new Index(1000, 2000), baseItemType));
        Assert.assertFalse(syncItemArea.isInRange(699, new Index(1000, 2000), baseItemType));
    }

    @Test
    public void testInRangePosition() {
        BoundingBox boundingBox = new BoundingBox(200, AbstractServiceTest.ANGELS_24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(1000, 1000));

        Assert.assertTrue(syncItemArea.isInRange(100, new Index(1000, 1000)));
        Assert.assertTrue(syncItemArea.isInRange(0, new Index(1200, 1000)));
        Assert.assertTrue(syncItemArea.isInRange(100, new Index(1300, 1000)));
        Assert.assertTrue(syncItemArea.isInRange(101, new Index(1300, 1000)));
        Assert.assertFalse(syncItemArea.isInRange(99, new Index(1300, 1000)));
    }

    @Test
    public void getDistanceToPoint() throws Exception {
        BoundingBox boundingBox = new BoundingBox(200, AbstractServiceTest.ANGELS_24);
        SyncItemArea syncItemArea = new SyncItemArea(boundingBox, new Index(1000, 1000));
        Assert.assertEquals(300, syncItemArea.getDistance(new Index(1000, 500)), 0.001);
        Assert.assertEquals(200, syncItemArea.getDistance(new Index(1000, 600)), 0.001);
        Assert.assertEquals(0, syncItemArea.getDistance(new Index(1000, 1000)), 0.001);
    }
}
