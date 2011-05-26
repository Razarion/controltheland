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
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(5), MathHelper.gradToRad(15)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(15), MathHelper.gradToRad(5), MathHelper.gradToRad(15)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(33), MathHelper.gradToRad(33), MathHelper.gradToRad(60)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(33), MathHelper.gradToRad(35), MathHelper.gradToRad(60)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(10), MathHelper.gradToRad(350), MathHelper.gradToRad(20)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(350), MathHelper.gradToRad(350), MathHelper.gradToRad(20)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(20), MathHelper.gradToRad(350), MathHelper.gradToRad(20)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(-30), MathHelper.gradToRad(-20), MathHelper.gradToRad(-40)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(-30), MathHelper.gradToRad(20), MathHelper.gradToRad(-40)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(100), MathHelper.gradToRad(200), MathHelper.gradToRad(180)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(190), MathHelper.gradToRad(200), MathHelper.gradToRad(180)));
        Assert.assertTrue(MathHelper.isInSection(MathHelper.gradToRad(100), MathHelper.gradToRad(220), MathHelper.gradToRad(200)));
        Assert.assertFalse(MathHelper.isInSection(MathHelper.gradToRad(210), MathHelper.gradToRad(220), MathHelper.gradToRad(200)));

    }
}
