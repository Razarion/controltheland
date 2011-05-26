package com.btxtech.game.jsre.client.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 03.05.2011
 * Time: 13:34:47
 */
public class TestRectangle2 {

    @Test
    public void testLineInside() {
        Rectangle rectangle = new Rectangle(2, 2, 5, 5);
        // Test complete inside
        Assert.assertTrue(rectangle.doesLineCut(new Index(3, 3), new Index(5, 3)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(5, 3), new Index(3, 3)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 3), new Index(4, 5)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 5), new Index(4, 3)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(3, 3), new Index(5, 5)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(5, 5), new Index(35, 3)));
        // Test as big as rectangle inside
        Assert.assertTrue(rectangle.doesLineCut(new Index(2, 3), new Index(6, 3)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(6, 3), new Index(2, 3)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 2), new Index(4, 6)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 6), new Index(4, 2)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(2, 2), new Index(6, 6)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(6, 6), new Index(2, 2)));
        // Test line goes from outside to inside of the rectangle
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 0), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 4), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(8, 4), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 8), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 0), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(8, 0), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(8, 0), new Index(4, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 8), new Index(4, 4)));
        // Line starts and ends outside rectangle but cuts rectangle
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 0), new Index(8, 8)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(8, 8), new Index(0, 0)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(8, 0), new Index(0, 8)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 8), new Index(8, 0)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 4), new Index(8, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 0), new Index(4, 8)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(4, 0), new Index(8, 4)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 5), new Index(5, 7)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(2, 0), new Index(2, 8)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 2), new Index(8, 2)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(6, 0), new Index(6, 8)));
        Assert.assertTrue(rectangle.doesLineCut(new Index(0, 6), new Index(6, 8)));
        // Line does not cut rectangle
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 0), new Index(9, 0)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 0), new Index(0, 9)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(1, 1), new Index(9, 1)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(1, 1), new Index(1, 9)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(7, 1), new Index(7, 7)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(1, 7), new Index(7, 7)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 3), new Index(0, 4)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(5, 9), new Index(11, 5)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 6), new Index(2, 9)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(5, 0), new Index(15, 2)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 2), new Index(3, 0)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(5, 9), new Index(10, 4)));
        // absolutely precise diagonally which does not cut edge
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 3), new Index(3, 0)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 5), new Index(4, 9)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(5, 0), new Index(9, 4)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(4, 9), new Index(9, 4)));
        // Line outside but would cut rectangle if line would be longer
        Assert.assertFalse(rectangle.doesLineCut(new Index(4, 0), new Index(4, 1)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(7, 4), new Index(9, 4)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(4, 7), new Index(4, 9)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 4), new Index(1, 4)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(0, 0), new Index(1, 1)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(7, 1), new Index(8, 0)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(7, 7), new Index(8, 8)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(1, 7), new Index(0, 8)));
        Assert.assertFalse(rectangle.doesLineCut(new Index(8, 4), new Index(18, 9)));
    }

    @Test
    public void testDistanceToPoint() {
        Rectangle rectangle = new Rectangle(23, 32, 13, 11);
        Assert.assertEquals(2, rectangle.getShortestDistanceToLine(new Index(18, 36), new Index(21, 36)));
        Assert.assertEquals(1, rectangle.getShortestDistanceToLine(new Index(33, 28), new Index(38, 33)));
        Assert.assertEquals(3, rectangle.getShortestDistanceToLine(new Index(26, 45), new Index(29, 45)));
    }

    @Test
    public void testAdjoinsExclusive1() {
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(new Rectangle(0, 0, 5, 5));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    public void testAdjoinsExclusive2() {
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(new Rectangle(0, 0, 15, 15));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertTrue(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    public void testAdjoinsExclusive3() {
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>();
        rectangles.add(new Rectangle(0, 0, 10, 10));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

}
