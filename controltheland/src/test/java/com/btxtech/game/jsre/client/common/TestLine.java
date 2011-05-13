package com.btxtech.game.jsre.client.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 08.05.2011
 * Time: 18:15:38
 */
public class TestLine {
    @Test
    public void testM_C() {
        Line line = new Line(new Index(0, 0), new Index(10, 10));
        Assert.assertEquals(1.0, line.getM(), 0.001);
        Assert.assertEquals(0.0, line.getC(), 0.001);

        line = new Line(new Index(0, 0), new Index(10, 0));
        Assert.assertEquals(0, line.getM(), 0.001);
        Assert.assertEquals(0.0, line.getC(), 0.001);

        line = new Line(new Index(0, 0), new Index(0, 10));
        Assert.assertTrue(Double.isInfinite(line.getM()));
        Assert.assertTrue(Double.isNaN(line.getC()));
    }

    @Test
    public void testProjectOnInfiniteLine_M1() {
        Line line = new Line(new Index(0, 0), new Index(10, 10));
        Assert.assertEquals(new Index(5, 5), line.projectOnInfiniteLine(new Index(7, 3)));
        Assert.assertEquals(new Index(20, 20), line.projectOnInfiniteLine(new Index(20, 20)));
        Assert.assertEquals(new Index(5, 5), line.projectOnInfiniteLine(new Index(2, 7)));
        Assert.assertEquals(new Index(2, 2), line.projectOnInfiniteLine(new Index(3, 1)));
    }

    @Test
    public void testProjectOnInfiniteLine_M2() {
        Line line = new Line(new Index(0, 0), new Index(2, 4));
        Assert.assertEquals(new Index(3, 5), line.projectOnInfiniteLine(new Index(7, 3)));
        Assert.assertEquals(new Index(6, 12), line.projectOnInfiniteLine(new Index(10, 10)));
        Assert.assertEquals(new Index(3, 6), line.projectOnInfiniteLine(new Index(2, 7)));
        Assert.assertEquals(new Index(1, 2), line.projectOnInfiniteLine(new Index(3, 1)));
    }

    @Test
    public void testProjectOnInfiniteLine_M05() {
        Line line = new Line(new Index(0, 0), new Index(4, 2));
        Assert.assertEquals(new Index(7, 3), line.projectOnInfiniteLine(new Index(7, 3)));
        Assert.assertEquals(new Index(12, 6), line.projectOnInfiniteLine(new Index(10, 10)));
        Assert.assertEquals(new Index(4, 2), line.projectOnInfiniteLine(new Index(2, 7)));
        Assert.assertEquals(new Index(3, 1), line.projectOnInfiniteLine(new Index(3, 1)));
    }

    @Test
    public void testProjectOnInfiniteLine_M0() {
        Line line = new Line(new Index(0, 5), new Index(10, 5));
        Assert.assertEquals(new Index(7, 5), line.projectOnInfiniteLine(new Index(7, 3)));
        Assert.assertEquals(new Index(10, 5), line.projectOnInfiniteLine(new Index(10, 10)));
        Assert.assertEquals(new Index(2, 5), line.projectOnInfiniteLine(new Index(2, 7)));
        Assert.assertEquals(new Index(3, 5), line.projectOnInfiniteLine(new Index(3, 1)));
    }

    @Test
    public void testProjectOnInfiniteLine_MInfinite() {
        Line line = new Line(new Index(5, 0), new Index(5, 10));
        Assert.assertEquals(new Index(5, 3), line.projectOnInfiniteLine(new Index(7, 3)));
        Assert.assertEquals(new Index(5, 10), line.projectOnInfiniteLine(new Index(10, 10)));
        Assert.assertEquals(new Index(5, 7), line.projectOnInfiniteLine(new Index(2, 7)));
        Assert.assertEquals(new Index(5, 1), line.projectOnInfiniteLine(new Index(3, 1)));
    }

    @Test
    public void testProjectOnInfiniteLineNull() {
        Line line = new Line(new Index(33, 1), new Index(38, 4));
        Assert.assertNull(line.projectOnInfiniteLine(new Index(27, 2)));
    }

    @Test
    public void testGetShortestDistance1() {
        Line line = new Line(new Index(5, 5), new Index(10, 10));
        Assert.assertEquals(2, line.getShortestDistance(new Index(7, 3)));
        Assert.assertEquals(1, line.getShortestDistance(new Index(4, 4)));
        Assert.assertEquals(0, line.getShortestDistance(new Index(5, 5)));
        Assert.assertEquals(7, line.getShortestDistance(new Index(0, 0)));
        Assert.assertEquals(5, line.getShortestDistance(new Index(0, 4)));
        Assert.assertEquals(14, line.getShortestDistance(new Index(20, 20)));
        Assert.assertEquals(11, line.getShortestDistance(new Index(20, 15)));
        Assert.assertEquals(1, line.getShortestDistance(new Index(4, 4)));
        Assert.assertEquals(0, line.getShortestDistance(new Index(10, 10)));
        Assert.assertEquals(1, line.getShortestDistance(new Index(11, 11)));
        Assert.assertEquals(5, line.getShortestDistance(new Index(10, 3)));
        Assert.assertEquals(6, line.getShortestDistance(new Index(13, 16)));
        Assert.assertEquals(8, line.getShortestDistance(new Index(8, 18)));
        Assert.assertEquals(7, line.getShortestDistance(new Index(0, 0)));
    }

    @Test
    public void testGetShortestDistance2() {
        Line line = new Line(new Index(0, 5), new Index(10, 5));
        Assert.assertEquals(2, line.getShortestDistance(new Index(7, 3)));
        Assert.assertEquals(1, line.getShortestDistance(new Index(4, 4)));
    }

    @Test
    public void testGetShortestDistance3() {
        Line line = new Line(new Index(20, 10), new Index(20, 20));
        Assert.assertEquals(15, line.getShortestDistance(new Index(5, 20)));
        Assert.assertEquals(7, line.getShortestDistance(new Index(13, 16)));
        Assert.assertEquals(8, line.getShortestDistance(new Index(28, 24)));
    }

    @Test
    public void testGetShortestDistance4() {
        Line line = new Line(new Index(33, 2), new Index(39, 4));
        Assert.assertEquals(6, line.getShortestDistance(new Index(27, 2)));
        Assert.assertEquals(3, line.getShortestDistance(new Index(37, 0)));
        Assert.assertEquals(8, line.getShortestDistance(new Index(25, 0)));
    }

    @Test
    public void testGetShortestDistance5() {
        Line line = new Line(new Index(3, 9), new Index(10, 2));
        Assert.assertEquals(4, line.getShortestDistance(new Index(4, 2)));
        Assert.assertEquals(3, line.getShortestDistance(new Index(13, 2)));
        Assert.assertEquals(2, line.getShortestDistance(new Index(9, 7)));
        Assert.assertEquals(2, line.getShortestDistance(new Index(1, 9)));
    }

}