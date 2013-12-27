package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 04.12.2011
 * Time: 14:18:08
 */
public class TestBoundingBox extends AbstractServiceTest {

    @Test
    @DirtiesContext
    public void testTheTest() {
        final double[] ANGELS_1 = new double[]{MathHelper.gradToRad(88), MathHelper.gradToRad(92)};
        final double[] ANGELS_2 = new double[]{MathHelper.gradToRad(90), MathHelper.gradToRad(270)};
        final double[] ANGELS_3 = new double[]{MathHelper.gradToRad(93), MathHelper.gradToRad(270)};
        final double[] ANGELS_4 = new double[]{MathHelper.gradToRad(10), MathHelper.gradToRad(20)};
        final double[] ANGELS_5 = new double[]{MathHelper.gradToRad(0), MathHelper.gradToRad(10), MathHelper.gradToRad(20), MathHelper.gradToRad(88), MathHelper.gradToRad(90), MathHelper.gradToRad(92), MathHelper.gradToRad(93), MathHelper.gradToRad(270)};

        assertAllowedAngels(MathHelper.gradToRad(90), MathHelper.gradToRad(88), MathHelper.gradToRad(92), ANGELS_1);
        assertAllowedAngels(MathHelper.gradToRad(90), MathHelper.gradToRad(92), MathHelper.gradToRad(88), ANGELS_1);
        try {
            assertAllowedAngels(MathHelper.gradToRad(93), MathHelper.gradToRad(92), MathHelper.gradToRad(88), ANGELS_1);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }
        try {
            assertAllowedAngels(MathHelper.gradToRad(93), MathHelper.gradToRad(88), MathHelper.gradToRad(92), ANGELS_1);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }
        assertAllowedAngels(MathHelper.gradToRad(0), MathHelper.gradToRad(90), MathHelper.gradToRad(270), ANGELS_2);
        assertAllowedAngels(MathHelper.gradToRad(0), MathHelper.gradToRad(270), MathHelper.gradToRad(90), ANGELS_2);

        try {
            assertAllowedAngels(MathHelper.gradToRad(90), MathHelper.gradToRad(93), MathHelper.gradToRad(270), ANGELS_3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }
        assertAllowedAngels(MathHelper.gradToRad(94), MathHelper.gradToRad(93), MathHelper.gradToRad(270), ANGELS_3);
        try {
            assertAllowedAngels(MathHelper.gradToRad(0), MathHelper.gradToRad(93), MathHelper.gradToRad(270), ANGELS_3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }

        assertAllowedAngels(MathHelper.gradToRad(10), MathHelper.gradToRad(10), MathHelper.gradToRad(20), ANGELS_4);

        try {
            assertAllowedAngels(MathHelper.gradToRad(0), MathHelper.gradToRad(90), MathHelper.gradToRad(90), ANGELS_5);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }

        try {
            assertAllowedAngels(MathHelper.gradToRad(15), MathHelper.gradToRad(11), MathHelper.gradToRad(20), ANGELS_4);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }

        try {
            assertAllowedAngels(MathHelper.gradToRad(15), MathHelper.gradToRad(10), MathHelper.gradToRad(21), ANGELS_4);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }

        try {
            assertAllowedAngels(MathHelper.gradToRad(89), MathHelper.gradToRad(88), MathHelper.gradToRad(92), ANGELS_5);
            Assert.fail("AssertionError expected");
        } catch (AssertionError ignore) {
            // ignore
        }

    }

    @Test
    @DirtiesContext
    public void getAllowedAngel() throws Throwable {
        final double[] ANGELS = new double[]{MathHelper.gradToRad(0.0), MathHelper.gradToRad(10), MathHelper.gradToRad(11), MathHelper.gradToRad(12), MathHelper.gradToRad(90), MathHelper.gradToRad(180)};
        BoundingBox boundingBox = new BoundingBox(100, ANGELS);
        double angel = 0.0;
        try {
            for (angel = 0.0; angel <= MathHelper.ONE_RADIANT; angel += 0.0001) {
                double allowedAngel1 = boundingBox.getAllowedAngel(angel);
                double allowedAngel2 = boundingBox.getAllowedAngel(angel, allowedAngel1);
                System.out.println("angel: " + MathHelper.radToGrad(angel) + " allowedAngel1: " + MathHelper.radToGrad(allowedAngel1) + " allowedAngel2: " + MathHelper.radToGrad(allowedAngel2));
                assertAllowedAngels(angel, allowedAngel1, allowedAngel2, ANGELS);
            }
        } catch (Throwable t) {
            System.out.println("angel: " + MathHelper.radToGrad(angel) + " " + t.getMessage());
            throw t;
        }
    }

    private void assertAllowedAngels(double angel, double allowedAngel1, double allowedAngel2, double[] angels) {
        int index = -1;
        for (int i = 0; i < angels.length; i++) {
            double allowedAngel = angels[i];
            if (MathHelper.compareWithPrecision(allowedAngel, allowedAngel1)) {
                index = i;
                break;
            }
            if (MathHelper.compareWithPrecision(allowedAngel, allowedAngel2)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            Assert.fail("allowedAngel1 allowedAngel2 are not known angels: " + MathHelper.radToGrad(allowedAngel1) + " " + MathHelper.radToGrad(allowedAngel2));
        }
        int secondIndex = index == angels.length - 1 ? 0 : index + 1;

        if (!MathHelper.compareWithPrecision(allowedAngel1, angels[secondIndex]) && !MathHelper.compareWithPrecision(allowedAngel2, angels[secondIndex])) {
            if (index == 0) {
                // 1=180 2=0.0
                secondIndex = angels.length - 1;
                if (!MathHelper.compareWithPrecision(allowedAngel1, angels[secondIndex]) && !MathHelper.compareWithPrecision(allowedAngel2, angels[secondIndex])) {
                    Assert.fail("allowedAngel1 allowedAngel2 is wrong: " + MathHelper.radToGrad(allowedAngel1) + " " + MathHelper.radToGrad(allowedAngel2));
                }
            } else {
                Assert.fail("allowedAngel1 allowedAngel2 is wrong: " + MathHelper.radToGrad(allowedAngel1) + " " + MathHelper.radToGrad(allowedAngel2));
            }
        }

        int index1 = -1;
        int index2 = -1;
        for (int i = 0, angelsLength = angels.length; i < angelsLength; i++) {
            double allowedAngel = angels[i];
            if (MathHelper.compareWithPrecision(allowedAngel, allowedAngel1)) {
                index1 = i;
            }
            if (MathHelper.compareWithPrecision(allowedAngel, allowedAngel2)) {
                index2 = i;
            }
        }

        if (index1 < 0 || index2 < 0) {
            Assert.fail("allowedAngel1 allowedAngel2 are not known angels: " + allowedAngel1 + " " + allowedAngel2);
        }

        int minIndex = Math.min(index1, index2);
        int maxIndex = Math.max(index1, index2);

        if (maxIndex - minIndex > 1 && (minIndex != 0 && maxIndex != angels.length - 1)) {
            Assert.fail("Angels are not proceeding: " + minIndex + "<->" + maxIndex);
        }

        double counterClockAngel = MathHelper.getAngel(allowedAngel1, allowedAngel2, true);
        double normalAngel = MathHelper.getAngel(allowedAngel1, allowedAngel2, false);
        double delta = MathHelper.getAngel(allowedAngel1, allowedAngel2);

        if (MathHelper.compareWithPrecision(allowedAngel1, allowedAngel2)) {
            Assert.fail("allowedAngel1 allowedAngel2 are the same: " + allowedAngel1 + " " + allowedAngel2);
        }

        if (MathHelper.compareWithPrecision(counterClockAngel, normalAngel)) {
            return;
        }

        if (MathHelper.compareWithPrecision(angel, allowedAngel1)) {
            return;
        }

        if (MathHelper.compareWithPrecision(angel, allowedAngel2)) {
            return;
        }

        boolean isCounterClock = counterClockAngel < normalAngel;

        if (isCounterClock) {
            Assert.assertTrue("The angel:" + MathHelper.radToGrad(angel) + " is not inside allowedAngel1:" + MathHelper.radToGrad(allowedAngel1) + " and allowedAngel2:" + MathHelper.radToGrad(allowedAngel2), MathHelper.isInSection(angel, allowedAngel1, delta));
        } else {
            Assert.assertTrue("The angel:" + MathHelper.radToGrad(angel) + " is not inside allowedAngel1:" + MathHelper.radToGrad(allowedAngel1) + " and allowedAngel2:" + MathHelper.radToGrad(allowedAngel2), MathHelper.isInSection(angel, allowedAngel2, delta));
        }
    }
}
