package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.MathHelper;
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
        Assert.assertEquals(3, line.getShortestDistance(new Index(7, 3)));
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
        Assert.assertEquals(7, line.getShortestDistance(new Index(13, 16)));
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
        Assert.assertEquals(9, line.getShortestDistance(new Index(28, 24)));
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
        Assert.assertEquals(3, line.getShortestDistance(new Index(9, 7)));
        Assert.assertEquals(2, line.getShortestDistance(new Index(1, 9)));
    }

    @Test
    public void testTranslate() {
        Line line1 = new Line(new Index(10, 500), new Index(100, 500));
        Line movedLine1 = line1.translate(0, 100);
        Assert.assertEquals(new Line(new Index(10, 400), new Index(100, 400)), movedLine1);

        Line line2 = new Line(new Index(100, 100), new Index(100, 300));
        Line movedLine2 = line2.translate(MathHelper.gradToRad(-45), 100);
        Assert.assertEquals(new Line(new Index(171, 29), new Index(171, 229)), movedLine2);

        Line line3 = new Line(new Index(100, 100), new Index(300, 200));
        Line movedLine3 = line3.translate(MathHelper.gradToRad(-135), 50);
        Assert.assertEquals(new Line(new Index(135, 135), new Index(335, 235)), movedLine3);
    }

    @Test
    public void testConstructor2() {
        Line line1 = new Line(new Index(100, 100), MathHelper.gradToRad(135), 141);
        Assert.assertEquals(new Line(new Index(100, 100), new Index(0, 200)), line1);
        Line line2 = new Line(new Index(200, 200), 0, 100);
        Assert.assertEquals(new Line(new Index(200, 200), new Index(200, 100)), line2);
    }

    @Test
    public void testCross1() {
        // Cross 1
        Line line1 = new Line(new Index(100, 100), new Index(300, 300));
        Line line2 = new Line(new Index(300, 100), new Index(100, 300));
        Assert.assertEquals(new Index(200, 200), line1.getCross(line2));
        Assert.assertEquals(line2.getCross(line1), line1.getCross(line2));

        // Cross 2
        Line line3 = new Line(new Index(200, 500), new Index(300, 300));
        Line line4 = new Line(new Index(200, 400), new Index(750, 500));
        Assert.assertEquals(new Index(246, 408), line3.getCross(line4));
        Assert.assertEquals(line4.getCross(line3), line3.getCross(line4));

        Line line5 = new Line(new Index(600, 350), new Index(400, 350));
        Line line6 = new Line(new Index(500, 500), new Index(499, 188));
        Assert.assertEquals(new Index(500, 350), line5.getCross(line6));
        Assert.assertEquals(new Index(500, 350), line6.getCross(line5));

        // Overlapping
        Line line7 = new Line(new Index(200, 500), new Index(300, 300));
        Line line8 = new Line(new Index(200, 500), new Index(300, 300));
        Assert.assertNull(line7.getCross(line8));
        Assert.assertNull(line8.getCross(line7));

        // Not crossing
        Line line9 = new Line(new Index(100, 400), new Index(200, 200));
        Line line10 = new Line(new Index(200, 500), new Index(300, 300));
        Assert.assertNull(line9.getCross(line10));
        Assert.assertNull(line10.getCross(line9));
    }

    @Test
    public void testCross2() {
        // m = infinite & m = 0
        Line line1 = new Line(new Index(100, 200), new Index(400, 200));
        Line line2 = new Line(new Index(200, 100), new Index(200, 300));
        Assert.assertEquals(new Index(200, 200), line1.getCross(line2));
        Assert.assertEquals(new Index(200, 200), line2.getCross(line1));

        // m = 0
        Line line3 = new Line(new Index(100, 200), new Index(400, 200));
        Line line4 = new Line(new Index(100, 100), new Index(300, 300));
        Assert.assertEquals(new Index(200, 200), line3.getCross(line4));
        Assert.assertEquals(new Index(200, 200), line4.getCross(line3));

        // m = infinite
        Line line5 = new Line(new Index(200, 100), new Index(200, 300));
        Line line6 = new Line(new Index(100, 100), new Index(300, 300));
        Assert.assertEquals(new Index(200, 200), line5.getCross(line6));
        Assert.assertEquals(new Index(200, 200), line6.getCross(line5));
    }

    @Test
    public void testCross3() {
        Line line = new Line(new Index(1810, 2628), new Index(1810, 2532));
        Assert.assertEquals(new Index(1810, 2533), line.getCross(4.884017048765569, new Index(1538, 2580)));

        line = new Line(new Index(800, 540), new Index(800, 460));
        Assert.assertEquals(new Index(800, 530), line.getCross(4.61238898038469, new Index(500, 500)));

        line = new Line(new Index(1407, 1742), new Index(1607, 1736));
        Assert.assertEquals(new Index(1407, 1742), line.getCross(2.774999999999963, new Index(1500, 1500)));
    }

    @Test
    public void testPerpendicular1() {
        Index cross = new Index(200, 200);
        Index otherSide = new Index(200, 100);
        Line line = new Line(new Index(100, 300), new Index(300, 100));
        Index result = line.getPerpendicular(cross, 141, otherSide);
        Assert.assertEquals(new Index(300, 300), result);

        otherSide = new Index(200, 300);
        result = line.getPerpendicular(cross, 141, otherSide);
        Assert.assertEquals(new Index(100, 100), result);

        line = new Line(new Index(599, 261), new Index(399, 262));
        result = line.getPerpendicular(new Index(500, 261), 40, new Index(500, 500));
        Assert.assertEquals(new Index(500, 221), result);
    }

    @Test
    public void testPerpendicular2() {
        // m = 0
        Index cross = new Index(200, 200);
        Index otherSide = new Index(200, 100);
        Line line = new Line(new Index(100, 200), new Index(400, 200));
        Index result = line.getPerpendicular(cross, 50, otherSide);
        Assert.assertEquals(new Index(200, 250), result);

        otherSide = new Index(200, 300);
        line = new Line(new Index(100, 200), new Index(400, 200));
        result = line.getPerpendicular(cross, 50, otherSide);
        Assert.assertEquals(new Index(200, 150), result);

        otherSide = new Index(500, 500);
        cross = new Index(500, 350);
        line = new Line(new Index(600, 350), new Index(400, 350));
        result = line.getPerpendicular(cross, 100, otherSide);
        Assert.assertEquals(new Index(500, 250), result);

        // m = infinite
        otherSide = new Index(50, 50);
        cross = new Index(200, 200);
        line = new Line(new Index(200, 100), new Index(200, 400));
        result = line.getPerpendicular(cross, 100, otherSide);
        Assert.assertEquals(new Index(300, 200), result);

        otherSide = new Index(300, 50);
        line = new Line(new Index(200, 100), new Index(200, 400));
        result = line.getPerpendicular(cross, 100, otherSide);
        Assert.assertEquals(new Index(100, 200), result);
    }

    @Test
    public void testGetPoint() {
        Index reference = new Index(200, 300);
        Line line = new Line(new Index(100, 100), new Index(400, 400));
        Assert.assertEquals(new Index(100, 100), line.getEndPoint(reference, true));
        Assert.assertEquals(new Index(400, 400), line.getEndPoint(reference, false));

        reference = new Index(200, 300);
        line = new Line(new Index(100, 100), new Index(100, 400));
        Assert.assertEquals(new Index(100, 400), line.getEndPoint(reference, true));
        Assert.assertEquals(new Index(100, 100), line.getEndPoint(reference, false));

        reference = new Index(400, 200);
        line = new Line(new Index(100, 100), new Index(400, 100));
        Assert.assertEquals(new Index(100, 100), line.getEndPoint(reference, true));
        Assert.assertEquals(new Index(400, 100), line.getEndPoint(reference, false));

        reference = new Index(300, 300);
        line = new Line(new Index(600, 500), new Index(100, 400));
        Assert.assertEquals(new Index(600, 500), line.getEndPoint(reference, true));
        Assert.assertEquals(new Index(100, 400), line.getEndPoint(reference, false));
    }

    @Test
    public void testIsNextPointOnSegment() {
        Index cross = new Index(200, 200);
        Index reference = new Index(200, 300);
        Line line = new Line(new Index(100, 100), new Index(400, 400));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, false));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 200, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 200, reference, false));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 400, reference, true));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 400, reference, false));

        cross = new Index(100, 300);
        reference = new Index(400, 200);
        line = new Line(new Index(100, 100), new Index(100, 400));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 99, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 99, reference, false));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, false));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 200, reference, true));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 201, reference, false));

        cross = new Index(200, 100);
        reference = new Index(400, 400);
        line = new Line(new Index(100, 100), new Index(400, 100));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 99, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 99, reference, false));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 100, reference, false));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 101, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 101, reference, false));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 201, reference, true));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 201, reference, false));

        cross = new Index(1500, 1261);
        reference = new Index(1500, 1500);
        line = new Line(new Index(1599, 1261), new Index(1399, 1262));
        System.out.println(line.isNextPointOnSegment(cross, 82, reference, true));
        Assert.assertTrue(line.isNextPointOnSegment(cross, 99, reference, true));
    }

    @Test
    public void testIsNextPointOnSegment2() {
        Index cross = new Index(400, 350);
        Index reference = new Index(500, 500);
        Line line = new Line(new Index(600, 350), new Index(400, 350));
        Assert.assertFalse(line.isNextPointOnSegment(cross, 100, reference, true));
    }

    @Test
    public void testGetNextPoint() {
        Index cross = new Index(200, 200);
        Index reference = new Index(200, 300);
        Line line = new Line(new Index(100, 100), new Index(400, 400));
        Assert.assertEquals(new Index(129, 129), line.getNextPoint(cross, 100, reference, true));
        Assert.assertEquals(new Index(271, 271), line.getNextPoint(cross, 100, reference, false));
        try {
            line.getNextPoint(cross, 200, reference, true);
            Assert.fail("IllegalArgumentException expected. Point not on line");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
        Assert.assertEquals(new Index(341, 341), line.getNextPoint(cross, 200, reference, false));

        cross = new Index(100, 300);
        reference = new Index(400, 200);
        line = new Line(new Index(100, 100), new Index(100, 400));
        Assert.assertEquals(new Index(100, 399), line.getNextPoint(cross, 99, reference, true));
        Assert.assertEquals(new Index(100, 150), line.getNextPoint(cross, 150, reference, false));

        cross = new Index(200, 100);
        reference = new Index(400, 400);
        line = new Line(new Index(100, 100), new Index(400, 100));
        Assert.assertEquals(new Index(150, 100), line.getNextPoint(cross, 50, reference, true));
        Assert.assertEquals(new Index(250, 100), line.getNextPoint(cross, 50, reference, false));
    }

    @Test
    public void testGetDistanceToEnd() {
        Index cross = new Index(200, 200);
        Index reference = new Index(200, 300);
        Line line = new Line(new Index(100, 100), new Index(400, 400));
        Assert.assertEquals(141, line.getDistanceToEnd(cross, reference, true, 666));
        Assert.assertEquals(283, line.getDistanceToEnd(cross, reference, false, 666));

        cross = new Index(100, 300);
        reference = new Index(400, 200);
        line = new Line(new Index(100, 100), new Index(100, 400));
        Assert.assertEquals(100, line.getDistanceToEnd(cross, reference, true, 666));
        Assert.assertEquals(200, line.getDistanceToEnd(cross, reference, false, 666));

        cross = new Index(200, 100);
        reference = new Index(400, 400);
        line = new Line(new Index(100, 100), new Index(400, 100));
        Assert.assertEquals(100, line.getDistanceToEnd(cross, reference, true, 666));
        Assert.assertEquals(200, line.getDistanceToEnd(cross, reference, false, 666));

        cross = new Index(1500, 1261);
        reference = new Index(1500, 1500);
        line = new Line(new Index(1599, 1261), new Index(1399, 1262));
        Assert.assertEquals(101, line.getDistanceToEnd(cross, reference, true, 666));
        Assert.assertEquals(99, line.getDistanceToEnd(cross, reference, false, 666));

    }

}