package com.btxtech.game.jsre.client.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 22.04.2011
 * Time: 13:34:47
 */
public class TestRectangle {

    @Test
    public void testRectangleContainsSmaller() {
        Rectangle outer = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Rectangle inner = new Rectangle(2, 2, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(2, 5, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(5, 5, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(5, 2, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        // 4 Edges
        inner = new Rectangle(2, 4, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(4, 5, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(5, 4, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(4, 2, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        // Middle
        inner = new Rectangle(3, 3, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));
    }

    @Test
    public void testRectangleContainsBigger() {
        Rectangle outer = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Rectangle inner = new Rectangle(2, 2, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(2, 5, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(5, 5, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(5, 2, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        // 4 Edges
        inner = new Rectangle(2, 4, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(4, 5, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(5, 4, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        inner = new Rectangle(4, 2, 2, 2);
        Assert.assertTrue(inner.adjoins(outer));
        Assert.assertTrue(inner.adjoinsEclusive(outer));

        // Middle
        inner = new Rectangle(3, 3, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));
    }

    @Test
    public void testRectangleOverlap() {
        Rectangle outer = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Rectangle inner = new Rectangle(1, 1, 3, 3);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(1, 6, 3, 3);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(6, 6, 3, 3);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(6, 1, 3, 3);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        // 4 Edges
        inner = new Rectangle(1, 3, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(3, 6, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(6, 3, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));

        inner = new Rectangle(3, 1, 2, 2);
        Assert.assertTrue(outer.adjoins(inner));
        Assert.assertTrue(outer.adjoinsEclusive(inner));
    }

    @Test
    public void testIndexAdjoins() {
        Rectangle rectangle = new Rectangle(2, 2, 5, 5);

        // 4 Corners
        Rectangle outer = new Rectangle(0, 0, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(0, 7, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(7, 7, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(7, 0, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        // 4 Edges
        outer = new Rectangle(4, 0, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(7, 4, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(4, 7, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(0, 4, 2, 2);
        Assert.assertTrue(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));
    }

    @Test
    public void testIndexOutside() {
        Rectangle rectangle = new Rectangle(3, 3, 5, 5);

        // 4 Corners
        Rectangle outer = new Rectangle(0, 0, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(9, 9, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(0, 9, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(9, 0, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        // 4 Edges
        outer = new Rectangle(0, 4, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(9, 4, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(4, 0, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));

        outer = new Rectangle(4, 9, 2, 2);
        Assert.assertFalse(rectangle.adjoins(outer));
        Assert.assertFalse(rectangle.adjoinsEclusive(outer));
    }

    @Test
    public void testSplit() {
        Rectangle rectangle = new Rectangle(0, 0, 10, 12);
        Collection<Rectangle> rectangles = rectangle.split(5, 6);
        Assert.assertEquals(4, rectangles.size());
        Set<Rectangle> expected = new HashSet<Rectangle>();
        expected.add(new Rectangle(0, 0, 5, 6));
        expected.add(new Rectangle(0, 6, 5, 6));
        expected.add(new Rectangle(5, 0, 5, 6));
        expected.add(new Rectangle(5, 6, 5, 6));
        for (Rectangle rectangle1 : rectangles) {
            Assert.assertTrue(expected.remove(rectangle1));
        }
        Assert.assertEquals(0, expected.size());
    }

    @Test
    public void testSplitOdd() {
        Rectangle rectangle = new Rectangle(0, 0, 5, 7);
        Collection<Rectangle> rectangles = rectangle.split(4, 6);
        Assert.assertEquals(4, rectangles.size());
        Set<Rectangle> expected = new HashSet<Rectangle>();
        expected.add(new Rectangle(0, 0, 4, 6));
        expected.add(new Rectangle(0, 6, 4, 6));
        expected.add(new Rectangle(4, 0, 4, 6));
        expected.add(new Rectangle(4, 6, 4, 6));
        for (Rectangle rectangle1 : rectangles) {
            Assert.assertTrue(expected.remove(rectangle1));
        }
        Assert.assertEquals(0, expected.size());
    }

    @Test
    public void testSplitMulti() {
        Rectangle rectangle = new Rectangle(0, 0, 100, 200);
        Collection<Rectangle> rectangles = rectangle.split(10, 10);
        Assert.assertEquals(200, rectangles.size());
        Set<Rectangle> uniqueCheck = new HashSet<Rectangle>();
        for (Rectangle rectangle1 : rectangles) {
            Assert.assertTrue(rectangle1.getEndX() <= 100);
            Assert.assertTrue(rectangle1.getEndY() <= 200);
            Assert.assertEquals(10, rectangle1.getWidth());
            Assert.assertEquals(10, rectangle1.getHeight());
            for (Rectangle rectangle2 : rectangles) {
                if (!rectangle1.equals(rectangle2)) {
                    Assert.assertFalse(rectangle1.adjoinsEclusive(rectangle2));
                }
            }
            Assert.assertFalse(uniqueCheck.contains(rectangle1));
            uniqueCheck.add(rectangle1);
        }
        Assert.assertEquals(200, uniqueCheck.size());

    }
}
