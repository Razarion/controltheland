package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.Index;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 01.09.13
 * Time: 14:58
 */
public class TestImageSpriteMapInfo {
    @Test
    public void getFrame() {
        ImageSpriteMapInfo imageSpriteMapInfo = new ImageSpriteMapInfo(1);
        imageSpriteMapInfo.setFrameCount(5);
        imageSpriteMapInfo.setFrameTime(20);
        imageSpriteMapInfo.setFrameWidth(150);
        imageSpriteMapInfo.setFrameHeight(120);
        Assert.assertEquals(0, imageSpriteMapInfo.getFrame(0));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrame(1));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrame(19));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrame(20));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrame(21));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrame(39));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrame(40));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrame(41));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrame(59));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrame(60));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrame(61));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrame(79));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrame(80));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrame(81));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrame(99));
        Assert.assertEquals(-1, imageSpriteMapInfo.getFrame(100));
        Assert.assertEquals(-1, imageSpriteMapInfo.getFrame(101));
        Assert.assertEquals(-1, imageSpriteMapInfo.getFrame(119));
        Assert.assertEquals(-1, imageSpriteMapInfo.getFrame(500));
    }

    @Test
    public void getFrameInfinite() {
        ImageSpriteMapInfo imageSpriteMapInfo = new ImageSpriteMapInfo(1);
        imageSpriteMapInfo.setFrameCount(5);
        imageSpriteMapInfo.setFrameTime(20);
        imageSpriteMapInfo.setFrameWidth(150);
        imageSpriteMapInfo.setFrameHeight(120);
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(0));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(1));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(19));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(20));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(21));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(39));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(40));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(41));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(59));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(60));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(61));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(79));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(80));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(81));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(99));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(100));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(101));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(119));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(120));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(121));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(139));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(140));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(141));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(159));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(160));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(161));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(179));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(180));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(181));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(199));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(200));
        Assert.assertEquals(1, imageSpriteMapInfo.getFrameInfinite(220));
        Assert.assertEquals(2, imageSpriteMapInfo.getFrameInfinite(240));
        Assert.assertEquals(3, imageSpriteMapInfo.getFrameInfinite(260));
        Assert.assertEquals(4, imageSpriteMapInfo.getFrameInfinite(280));
        Assert.assertEquals(0, imageSpriteMapInfo.getFrameInfinite(300));
    }

    @Test
    public void getSpriteMapOffset() {
        ImageSpriteMapInfo imageSpriteMapInfo = new ImageSpriteMapInfo(1);
        imageSpriteMapInfo.setFrameCount(5);
        imageSpriteMapInfo.setFrameTime(20);
        imageSpriteMapInfo.setFrameWidth(150);
        imageSpriteMapInfo.setFrameHeight(120);
        Assert.assertEquals(new Index(0, 0), imageSpriteMapInfo.getSpriteMapOffset(0));
        Assert.assertEquals(new Index(150, 0), imageSpriteMapInfo.getSpriteMapOffset(1));
        Assert.assertEquals(new Index(300, 0), imageSpriteMapInfo.getSpriteMapOffset(2));
        Assert.assertEquals(new Index(450, 0), imageSpriteMapInfo.getSpriteMapOffset(3));
        Assert.assertEquals(new Index(600, 0), imageSpriteMapInfo.getSpriteMapOffset(4));
        try {
            imageSpriteMapInfo.getSpriteMapOffset(5);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            imageSpriteMapInfo.getSpriteMapOffset(6);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            imageSpriteMapInfo.getSpriteMapOffset(500);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            imageSpriteMapInfo.getSpriteMapOffset(-1);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}
