package com.btxtech.game.jsre.client.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * User: beat
 * Date: 06.05.2011
 * Time: 15:36:31
 */
public class TestIndex {
    @Test
    public void testMiddlePoint() {
        Index middle = new Index(0, 0).getMiddlePoint(new Index(5, 5));
        Assert.assertEquals(2, middle.getX());
        Assert.assertEquals(2, middle.getY());
        middle = new Index(5, 5).getMiddlePoint(new Index(0, 0));
        Assert.assertEquals(2, middle.getX());
        Assert.assertEquals(2, middle.getY());

        middle = new Index(9, 0).getMiddlePoint(new Index(15, 0));
        Assert.assertEquals(12, middle.getX());
        Assert.assertEquals(0, middle.getY());
        middle = new Index(15, 0).getMiddlePoint(new Index(9, 0));
        Assert.assertEquals(12, middle.getX());
        Assert.assertEquals(0, middle.getY());

        middle = new Index(0, 10).getMiddlePoint(new Index(0, 16));
        Assert.assertEquals(0, middle.getX());
        Assert.assertEquals(13, middle.getY());
        middle = new Index(0, 16).getMiddlePoint(new Index(0, 10));
        Assert.assertEquals(0, middle.getX());
        Assert.assertEquals(13, middle.getY());

        middle = new Index(17, 10).getMiddlePoint(new Index(21, 20));
        Assert.assertEquals(19, middle.getX());
        Assert.assertEquals(15, middle.getY());
        middle = new Index(21, 20).getMiddlePoint(new Index(17, 10));
        Assert.assertEquals(19, middle.getX());
        Assert.assertEquals(15, middle.getY());

        middle = new Index(9, 7).getMiddlePoint(new Index(17, 5));
        Assert.assertEquals(13, middle.getX());
        Assert.assertEquals(6, middle.getY());
        middle = new Index(17, 5).getMiddlePoint(new Index(9, 7));
        Assert.assertEquals(13, middle.getX());
        Assert.assertEquals(6, middle.getY());
    }

    @Test
    public void testGetPointWithDistanceAllowOverrun() {
        Index point = new Index(399, 350);
        Index directionTo = new Index(400, 350);
        Index newPoint = point.getPointWithDistance(100, directionTo, true);
        Assert.assertEquals(new Index(499, 350), newPoint);
    }

    @Test
    public void testCalculateMiddle() {
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(5, 5))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 0), new Index(10, 10))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 0), new Index(0, 10), new Index(10, 0), new Index(10, 10))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 10), new Index(10, 0), new Index(10, 10))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 0), new Index(10, 0), new Index(10, 10))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 0), new Index(0, 10), new Index(10, 10))));
        Assert.assertEquals(new Index(5, 5), Index.calculateMiddle(Arrays.asList(new Index(0, 0), new Index(0, 10), new Index(10, 0))));
    }

}
