package com.btxtech.game.jsre.client.common;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Assert.assertEquals(2.0, rectangle.getShortestDistanceToLine(new Index(18, 36), new Index(21, 36)), 0.001);
        Assert.assertEquals(1.0, rectangle.getShortestDistanceToLine(new Index(33, 28), new Index(38, 33)), 0.001);
        Assert.assertEquals(2.0, rectangle.getShortestDistanceToLine(new Index(26, 45), new Index(29, 45)), 0.001);

        rectangle = new Rectangle(300, 300, 200, 200);
        Assert.assertEquals(100.0, rectangle.getShortestDistanceToLine(new Index(360, 600), new Index(360, 680)), 0.001);
    }

    @Test
    public void testAdjoinsExclusive1() {
        Collection<Rectangle> rectangles = new ArrayList<>();
        rectangles.add(new Rectangle(0, 0, 5, 5));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    public void testAdjoinsExclusive2() {
        Collection<Rectangle> rectangles = new ArrayList<>();
        rectangles.add(new Rectangle(0, 0, 15, 15));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertTrue(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    public void testAdjoinsExclusive3() {
        Collection<Rectangle> rectangles = new ArrayList<>();
        rectangles.add(new Rectangle(0, 0, 10, 10));
        rectangles.add(new Rectangle(10, 10, 5, 5));
        Assert.assertFalse(Rectangle.adjoinsExclusive(rectangles));
    }

    @Test
    public void testGetCrossPointsExclusive1() {
        Rectangle rectangle = new Rectangle(523, 313, 247, 248);
        Line line = new Line(new Index(646, 437), new Index(803, 594));
        // Line is on the corner
        List<Index> crossSections = rectangle.getCrossPointsExclusive(line);
        Assert.assertEquals(1, crossSections.size());
        Assert.assertEquals(new Index(769, 560), crossSections.get(0));
    }

    @Test
    public void testIterateOverPerimeterInclusive1() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 4))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 4))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 6))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 6))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 7))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 7))).andReturn(true);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(2, 3, 5, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }

    @Test
    public void testIterateOverPerimeterInclusive2() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 4))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 6))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 7))).andReturn(true);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(2, 3, 1, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }

    @Test
    public void testIterateOverPerimeterInclusive3() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 5))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(7, 5))).andReturn(true);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(4, 5, 4, 1);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }

    @Test
    public void testIterateOverPerimeterInclusive4() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 8))).andReturn(true);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(3, 8, 1, 1);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }

    @Test
    public void testIterateOverPerimeterInclusive5() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(3, 8, 0, 1);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        rectangle = new Rectangle(3, 8, 1, 0);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        rectangle = new Rectangle(3, 8, 0, 0);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }

    @Test
    public void testIterateOverPerimeterInclusiveBreak() {
        IndexCallback indexCallbackMock = EasyMock.createStrictMock(IndexCallback.class);
        // Break north
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(false);
        // Break south
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 8))).andReturn(false);
        // Break east
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 4))).andReturn(false);
        // Break west
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(3, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(4, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(5, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 3))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 8))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(2, 4))).andReturn(true);
        EasyMock.expect(indexCallbackMock.onIndex(new Index(6, 4))).andReturn(false);
        EasyMock.replay(indexCallbackMock);
        Rectangle rectangle = new Rectangle(2, 3, 5, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        rectangle = new Rectangle(2, 3, 5, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        rectangle = new Rectangle(2, 3, 5, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        rectangle = new Rectangle(2, 3, 5, 6);
        rectangle.iterateOverPerimeterInclusive(indexCallbackMock);
        EasyMock.verify(indexCallbackMock);
    }
}
