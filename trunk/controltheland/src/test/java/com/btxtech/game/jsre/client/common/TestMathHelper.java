package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.MathHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 24.05.2011
 * Time: 00:46:09
 */
public class TestMathHelper {

    @Test
    public void testCloserToAngel() {
        Assert.assertEquals(0.1, MathHelper.closerToAngel(0, 0.1, 0.2), 0.001);
        Assert.assertEquals(0.2, MathHelper.closerToAngel(0, 0.3, 0.2), 0.001);
        Assert.assertEquals(2.95, MathHelper.closerToAngel(3, 2.95, 3.1), 0.001);
        Assert.assertEquals(-0.2, MathHelper.closerToAngel(0.1, 1, -0.2), 0.001);
        Assert.assertEquals(0.2, MathHelper.closerToAngel(0.1, 0.2, -0.2), 0.001);
        Assert.assertEquals(0.2, MathHelper.closerToAngel(0.3, 0.2, -0.2), 0.001);
        Assert.assertEquals(Math.PI - 0.1, MathHelper.closerToAngel(Math.PI, Math.PI + 0.2, Math.PI - 0.1), 0.001);
        Assert.assertEquals(Math.PI + 0.2, MathHelper.closerToAngel(Math.PI, Math.PI + 0.2, Math.PI - 0.3), 0.001);
        Assert.assertEquals(3.394272908731871, MathHelper.closerToAngel(Math.PI, 3.3942729087318715, -2.4188584057763776), 0.001);
    }

    @Test
    public void testIsInSection() {
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(5), MathHelper.gradToRad(10)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(15), MathHelper.gradToRad(5), MathHelper.gradToRad(10)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(33), MathHelper.gradToRad(33), MathHelper.gradToRad(20)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(33), MathHelper.gradToRad(35), MathHelper.gradToRad(20)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(350), MathHelper.gradToRad(30)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(350), MathHelper.gradToRad(350), MathHelper.gradToRad(30)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(20), MathHelper.gradToRad(350), MathHelper.gradToRad(30)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(100), MathHelper.gradToRad(200), MathHelper.gradToRad(340)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(190), MathHelper.gradToRad(200), MathHelper.gradToRad(180)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(100), MathHelper.gradToRad(220), MathHelper.gradToRad(340)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(210), MathHelper.gradToRad(220), MathHelper.gradToRad(340)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(20), MathHelper.gradToRad(10), MathHelper.gradToRad(10)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(10), MathHelper.gradToRad(10)));

        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(20), MathHelper.gradToRad(-20)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(20), MathHelper.gradToRad(20)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(243), MathHelper.gradToRad(264), MathHelper.gradToRad(-180)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(243), MathHelper.gradToRad(264), MathHelper.gradToRad(180)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(20), MathHelper.gradToRad(-40)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(20), MathHelper.gradToRad(40)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(350), MathHelper.gradToRad(20), MathHelper.gradToRad(-40)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(350), MathHelper.gradToRad(20), MathHelper.gradToRad(40)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(30), MathHelper.gradToRad(50), MathHelper.gradToRad(-30)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(342), MathHelper.gradToRad(0), MathHelper.gradToRad(-180)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(322), MathHelper.gradToRad(313), MathHelper.gradToRad(-180)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(322), MathHelper.gradToRad(313), MathHelper.gradToRad(180)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(20), MathHelper.gradToRad(30), MathHelper.gradToRad(-10)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(30), MathHelper.gradToRad(30), MathHelper.gradToRad(-10)));

    }

    @Test
    public void testGetAngel() {
        Assert.assertEquals(MathHelper.gradToRad(5), MathHelper.getAngel(MathHelper.gradToRad(5), MathHelper.gradToRad(10), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(30), MathHelper.getAngel(MathHelper.gradToRad(30), MathHelper.gradToRad(60), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(90), MathHelper.getAngel(MathHelper.gradToRad(0), MathHelper.gradToRad(90), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(10), MathHelper.getAngel(MathHelper.gradToRad(350), MathHelper.gradToRad(0), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(20), MathHelper.getAngel(MathHelper.gradToRad(350), MathHelper.gradToRad(10), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(350), MathHelper.getAngel(MathHelper.gradToRad(-10), MathHelper.gradToRad(-20), true), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(350), MathHelper.getAngel(MathHelper.gradToRad(20), MathHelper.gradToRad(10), true), 0.0001);

        Assert.assertEquals(MathHelper.gradToRad(10), MathHelper.getAngel(MathHelper.gradToRad(20), MathHelper.gradToRad(10), false), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(20), MathHelper.getAngel(MathHelper.gradToRad(0), MathHelper.gradToRad(340), false), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(30), MathHelper.getAngel(MathHelper.gradToRad(30), MathHelper.gradToRad(0), false), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(10), MathHelper.getAngel(MathHelper.gradToRad(-10), MathHelper.gradToRad(-20), false), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(350), MathHelper.getAngel(MathHelper.gradToRad(10), MathHelper.gradToRad(20), false), 0.0001);
    }

    @Test
    public void testCompareWithPrecision() {
        Assert.assertFalse(MathHelper.compareWithPrecision(1.0, 2.0));
        Assert.assertFalse(MathHelper.compareWithPrecision(-1.0, -2.0));
        Assert.assertFalse(MathHelper.compareWithPrecision(2.0, 1.0));
        Assert.assertFalse(MathHelper.compareWithPrecision(-1.0, 1.0));
        Assert.assertFalse(MathHelper.compareWithPrecision(1.0, -1.0));

        Assert.assertTrue(MathHelper.compareWithPrecision(0.0, 0.0));
        Assert.assertTrue(MathHelper.compareWithPrecision(-0.0, 0.0));
        Assert.assertTrue(MathHelper.compareWithPrecision(0.0, -0.0));

        Assert.assertTrue(MathHelper.compareWithPrecision(1.0, 1.0));
        Assert.assertTrue(MathHelper.compareWithPrecision(10000.0, 10000.0));
        Assert.assertTrue(MathHelper.compareWithPrecision(-42.0, -42.0));
        Assert.assertTrue(MathHelper.compareWithPrecision(1.0, 1.000009));
        Assert.assertTrue(MathHelper.compareWithPrecision(1.0, 0.99999));
        Assert.assertFalse(MathHelper.compareWithPrecision(1.0, 0.9));
        Assert.assertFalse(MathHelper.compareWithPrecision(1.0, 1.1));

        Assert.assertTrue(MathHelper.compareWithPrecision(-1.0, -1.000009));
        Assert.assertTrue(MathHelper.compareWithPrecision(-1.0, -0.99999));
        Assert.assertFalse(MathHelper.compareWithPrecision(-1.0, -0.9));
        Assert.assertFalse(MathHelper.compareWithPrecision(-1.0, -1.1));

        Assert.assertTrue(MathHelper.compareWithPrecision(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        Assert.assertTrue(MathHelper.compareWithPrecision(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.POSITIVE_INFINITY, 0));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.NEGATIVE_INFINITY, 0));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.POSITIVE_INFINITY, 100));
        Assert.assertFalse(MathHelper.compareWithPrecision(Double.NEGATIVE_INFINITY, 100));
    }

    @Test
    public void isRandomPossibility() {
        testIsRandomPossibility(0.0, 0.0);
        testIsRandomPossibility(0.1, 0.1);
        testIsRandomPossibility(0.2, 0.2);
        testIsRandomPossibility(0.3, 0.3);
        testIsRandomPossibility(0.4, 0.4);
        testIsRandomPossibility(0.5, 0.5);
        testIsRandomPossibility(0.6, 0.6);
        testIsRandomPossibility(0.7, 0.7);
        testIsRandomPossibility(0.8, 0.8);
        testIsRandomPossibility(0.9, 0.9);
        testIsRandomPossibility(1.0, 1.0);

        testIsRandomPossibility(2.0, 1.0);
        testIsRandomPossibility(1.1, 1.0);
        testIsRandomPossibility(-2.0, 0.0);
        testIsRandomPossibility(-5.0, 0.0);
    }

    private void testIsRandomPossibility(double possibilityParameter, double expectedPossibility) {
        int yesCount = 0;
        int count = 100000;
        for (int i = 0; i < count; i++) {
            if (MathHelper.isRandomPossibility(possibilityParameter)) {
                yesCount++;
            }
        }
        System.out.println("Possibility: " + ((double) yesCount / (double) count));
        System.out.println("Diff: " + Math.abs(((double) yesCount / (double) count) - expectedPossibility));
        Assert.assertEquals(expectedPossibility, ((double) yesCount / (double) count), 0.01);
    }
}
