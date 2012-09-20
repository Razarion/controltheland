package com.btxtech.game.jsre.common.algorithm;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 12:13
 */
public class TestRegionQuadTree {
    @Test
    public void testSimple1() {
        Assert.fail();
        /*
        RegionQuadTree regionQuadTree = new RegionQuadTree(new Rectangle(0, 0, 8, 8));
        regionQuadTree.insert(new Index(0, 0));
        regionQuadTree.insert(new Index(0, 1));
        regionQuadTree.insert(new Index(1, 0));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(7, 7));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(0, 0)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(0, 1)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(1, 0)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(1, 1)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(0, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(1, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 0)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 1)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(7, 7)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(8, 8)));
    }

    @Test
    public void testSimple2() {
        RegionQuadTree regionQuadTree = new RegionQuadTree(new Rectangle(0, 0, 9, 9));
        regionQuadTree.insert(new Index(0, 0));
        regionQuadTree.insert(new Index(0, 1));
        regionQuadTree.insert(new Index(1, 0));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(4, 4));
        regionQuadTree.insert(new Index(5, 5));
        regionQuadTree.insert(new Index(8, 8));

        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(0, 0)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(0, 1)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(1, 0)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(1, 1)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(0, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(1, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 0)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 1)));

        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(4, 4)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(5, 5)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(8, 8)));

    }

    @Test
    public void testFail() {
        RegionQuadTree regionQuadTree = new RegionQuadTree(new Rectangle(0, 0, 10, 10));
        try {
            regionQuadTree.insert(new Index(12, 4));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            regionQuadTree.insert(new Index(11, 4));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            regionQuadTree.insert(new Index(10, 4));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            regionQuadTree.insert(new Index(5, 10));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            regionQuadTree.insert(new Index(5, 11));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            regionQuadTree.insert(new Index(5, 12));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        regionQuadTree.insert(new Index(5, 9));
        regionQuadTree.insert(new Index(9, 3));
    }

    @Test
    public void multipleSameInsert() {
        RegionQuadTree regionQuadTree = new RegionQuadTree(new Rectangle(0, 0, 10, 10));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));
        regionQuadTree.insert(new Index(1, 1));

        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(0, 0)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(1, 0)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 0)));

        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(0, 1)));
        Assert.assertTrue(regionQuadTree.queryPointInside(new Index(1, 1)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 1)));

        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(0, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(1, 2)));
        Assert.assertFalse(regionQuadTree.queryPointInside(new Index(2, 2)));
       */
    }
}
