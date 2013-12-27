package com.btxtech.game.jsre.client.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 22.04.2011
 * Time: 13:34:47
 */
public class TestIndexRectangle {

    @Test
    public void testIndexContains() {
        Rectangle rectangle = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Index index = new Index(2, 2);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(2, 6);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(6, 6);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(6, 2);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        // 4 Edges
        index = new Index(2, 4);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(4, 6);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(6, 4);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        index = new Index(4, 2);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));

        // Middle
        index = new Index(4, 4);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertTrue(rectangle.containsExclusive(index));
    }

    @Test
    public void testIndexAdjoins() {
        Rectangle rectangle = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Index index = new Index(1, 1);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(1, 7);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(7, 7);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(7, 1);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        // 4 Edges
        index = new Index(4, 1);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(7, 4);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(4, 7);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(1, 4);
        Assert.assertTrue(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));
    }

    @Test
    public void testIndexOutside() {
        Rectangle rectangle = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Index index = new Index(0, 0);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(8, 8);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(0, 8);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(8, 0);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        // 4 Edges
        index = new Index(0, 4);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(8, 4);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(4, 0);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));

        index = new Index(4, 8);
        Assert.assertFalse(rectangle.contains(index));
        Assert.assertFalse(rectangle.containsExclusive(index));
    }

}
