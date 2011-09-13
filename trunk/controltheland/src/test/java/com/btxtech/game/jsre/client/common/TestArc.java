package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.MathHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 29.08.2011
 * Time: 23:28:14
 */
public class TestArc {

    @Test
    public void getConstructor() {
        Assert.assertNotNull(new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300)));

        try {
            new Arc(new Index(300, 101), new Index(100, 300), new Index(300, 300));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            new Arc(new Index(300, 100), new Index(100, 301), new Index(300, 300));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            new Arc(new Index(300, 101), new Index(100, 300), new Index(301, 300));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void getPerpendicular() {
        Arc arc = new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300));
        Index cross = new Index(159, 159);
        Index perpendicular = arc.getPerpendicular(cross, 100, null);
        Assert.assertEquals(new Index(87, 87), perpendicular);

        cross = new Index(180, 140);
        perpendicular = arc.getPerpendicular(cross, 100, null);
        Assert.assertEquals(new Index(120, 60), perpendicular);

        cross = new Index(129, 197);
        perpendicular = arc.getPerpendicular(cross, 100, null);
        Assert.assertEquals(new Index(44, 146), perpendicular);

        cross = new Index(100, 300);
        perpendicular = arc.getPerpendicular(cross, 50, null);
        Assert.assertEquals(new Index(50, 300), perpendicular);

        arc = new Arc(new Index(100, 300), new Index(300, 500), new Index(300, 300));
        cross = new Index(121, 389);
        perpendicular = arc.getPerpendicular(cross, 50, null);
        Assert.assertEquals(new Index(76, 411), perpendicular);

        arc = new Arc(new Index(300, 500), new Index(500, 300), new Index(300, 300));
        cross = new Index(389, 479);
        perpendicular = arc.getPerpendicular(cross, 50, null);
        Assert.assertEquals(new Index(411, 524), perpendicular);

        arc = new Arc(new Index(500, 300), new Index(300, 100), new Index(300, 300));
        cross = new Index(389, 121);
        perpendicular = arc.getPerpendicular(cross, 50, null);
        Assert.assertEquals(new Index(411, 76), perpendicular);
    }

    @Test
    public void getGetCross() {
        Arc arc = new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300));
        Index reference = new Index(400, 300);
        Assert.assertNull(arc.getCross(0, reference));
        Assert.assertEquals(new Index(218, 118), arc.getCross(MathHelper.gradToRad(45), reference));
        Assert.assertEquals(new Index(100, 295), arc.getCross(MathHelper.gradToRad(89), reference));

        arc = new Arc(new Index(100, 300), new Index(300, 500), new Index(300, 300));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(89), reference));
        Assert.assertEquals(new Index(102, 326), arc.getCross(MathHelper.gradToRad(95), reference));
        Assert.assertEquals(new Index(218, 482), arc.getCross(MathHelper.gradToRad(135), reference));
        Assert.assertEquals(new Index(196, 471), arc.getCross(MathHelper.gradToRad(130), reference));
        Assert.assertEquals(new Index(285, 499), arc.getCross(MathHelper.gradToRad(150), reference));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(180), reference));

        arc = new Arc(new Index(300, 500), new Index(500, 300), new Index(300, 300));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(150), reference));
        Assert.assertEquals(new Index(400, 473), arc.getCross(MathHelper.gradToRad(180), reference));
        Assert.assertEquals(new Index(449, 434), arc.getCross(MathHelper.gradToRad(200), reference));
        Assert.assertEquals(new Index(493, 354), arc.getCross(MathHelper.gradToRad(240), reference));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(271), reference));

        arc = new Arc(new Index(500, 300), new Index(300, 100), new Index(300, 300));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(260), reference));
        Assert.assertEquals(new Index(500, 298), arc.getCross(MathHelper.gradToRad(271), reference));
        Assert.assertEquals(new Index(493, 246), arc.getCross(MathHelper.gradToRad(300), reference));
        Assert.assertEquals(new Index(427, 146), arc.getCross(MathHelper.gradToRad(350), reference));
        Assert.assertNull(arc.getCross(MathHelper.gradToRad(45), reference));

        arc = new Arc(new Index(606, 323), new Index(464, 323), new Index(535, 394));
        reference = new Index(500, 500);
        Assert.assertEquals(new Index(500, 300), arc.getCross(MathHelper.gradToRad(0), reference));

        arc = new Arc(new Index(1500, 1240), new Index(1239, 1347), new Index(1423, 1424));
        reference = new Index(1500, 1500);
        Assert.assertEquals(new Index(1500, 1240), arc.getCross(0.0, reference));

        arc = new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300));
        reference = new Index(300, 300);
        Assert.assertEquals(new Index(300, 100), arc.getCross(0.0, reference));
        Assert.assertEquals(new Index(100, 300), arc.getCross(MathHelper.QUARTER_RADIANT, reference));
        arc = new Arc(new Index(400, 200), new Index(200, 200), new Index(300, 300));
        Assert.assertEquals(new Index(300, 159), arc.getCross(0.0, reference));

        arc = new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300));
        reference = new Index(300, 300);
        Assert.assertEquals(new Index(300, 100), arc.getCross(MathHelper.ONE_RADIANT, reference));
        arc = new Arc(new Index(400, 200), new Index(200, 200), new Index(300, 300));
        Assert.assertEquals(new Index(300, 159), arc.getCross(MathHelper.ONE_RADIANT, reference));

        arc = new Arc(new Index(300, 100), new Index(100, 300), new Index(300, 300));
        reference = new Index(300, 300);
        Assert.assertEquals(new Index(100, 300), arc.getCross(MathHelper.QUARTER_RADIANT, reference));
        arc = new Arc(new Index(200, 200), new Index(200, 400), new Index(300, 300));
        Assert.assertEquals(new Index(159, 300), arc.getCross(MathHelper.QUARTER_RADIANT, reference));

        arc = new Arc(new Index(100, 300), new Index(300, 500), new Index(300, 300));
        reference = new Index(300, 300);
        Assert.assertEquals(new Index(300, 500), arc.getCross(MathHelper.HALF_RADIANT, reference));
        arc = new Arc(new Index(200, 400), new Index(400, 400), new Index(300, 300));
        Assert.assertEquals(new Index(300, 441), arc.getCross(MathHelper.HALF_RADIANT, reference));

        arc = new Arc(new Index(500, 300), new Index(300, 100), new Index(300, 300));
        reference = new Index(300, 300);
        Assert.assertEquals(new Index(500, 300), arc.getCross(MathHelper.THREE_QUARTER_RADIANT, reference));
        arc = new Arc(new Index(400, 400), new Index(400, 200), new Index(300, 300));
        Assert.assertEquals(new Index(441, 300), arc.getCross(MathHelper.THREE_QUARTER_RADIANT, reference));
    }
}
