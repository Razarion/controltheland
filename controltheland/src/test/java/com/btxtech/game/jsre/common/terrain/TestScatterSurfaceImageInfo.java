package com.btxtech.game.jsre.common.terrain;

import com.btxtech.game.jsre.common.gameengine.services.terrain.ScatterSurfaceImageInfo;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 24.09.13
 * Time: 23:06
 */
public class TestScatterSurfaceImageInfo {
    @Test
    public void getImageOffset_ImageOffset() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.1);
        scatterSurfaceImageInfo.setRare(0.01);
        scatterSurfaceImageInfo.setCommonImageCount(5);
        scatterSurfaceImageInfo.setUncommonImageCount(4);
        scatterSurfaceImageInfo.setRareImageCount(3);
        // Test Common
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.05));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.1));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.199999));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.1, 0.2));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.1, 0.3));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.1, 0.39999999));
        Assert.assertEquals(2, scatterSurfaceImageInfo.getImageOffset(0.1, 0.4));
        Assert.assertEquals(2, scatterSurfaceImageInfo.getImageOffset(0.1, 0.5));
        Assert.assertEquals(2, scatterSurfaceImageInfo.getImageOffset(0.1, 0.5999999));
        Assert.assertEquals(3, scatterSurfaceImageInfo.getImageOffset(0.1, 0.6));
        Assert.assertEquals(3, scatterSurfaceImageInfo.getImageOffset(0.1, 0.7));
        Assert.assertEquals(3, scatterSurfaceImageInfo.getImageOffset(0.1, 0.79999999));
        Assert.assertEquals(4, scatterSurfaceImageInfo.getImageOffset(0.1, 0.8));
        Assert.assertEquals(4, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9));
        Assert.assertEquals(4, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9999999));
        // Test uncommon
        Assert.assertEquals(5, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(5, scatterSurfaceImageInfo.getImageOffset(0.9, 0.1));
        Assert.assertEquals(5, scatterSurfaceImageInfo.getImageOffset(0.9, 0.249999999));
        Assert.assertEquals(6, scatterSurfaceImageInfo.getImageOffset(0.9, 0.25));
        Assert.assertEquals(6, scatterSurfaceImageInfo.getImageOffset(0.9, 0.26));
        Assert.assertEquals(6, scatterSurfaceImageInfo.getImageOffset(0.9, 0.499999));
        Assert.assertEquals(7, scatterSurfaceImageInfo.getImageOffset(0.9, 0.5));
        Assert.assertEquals(7, scatterSurfaceImageInfo.getImageOffset(0.9, 0.6));
        Assert.assertEquals(7, scatterSurfaceImageInfo.getImageOffset(0.9, 0.7499999));
        Assert.assertEquals(8, scatterSurfaceImageInfo.getImageOffset(0.9, 0.75));
        Assert.assertEquals(8, scatterSurfaceImageInfo.getImageOffset(0.9, 0.76));
        Assert.assertEquals(8, scatterSurfaceImageInfo.getImageOffset(0.9, 0.9999999999));
        // Test rare
        Assert.assertEquals(9, scatterSurfaceImageInfo.getImageOffset(0.999, 0.0));
        Assert.assertEquals(9, scatterSurfaceImageInfo.getImageOffset(0.999, 0.1));
        Assert.assertEquals(9, scatterSurfaceImageInfo.getImageOffset(0.999, 0.33333333));
        Assert.assertEquals(10, scatterSurfaceImageInfo.getImageOffset(0.999, 0.34));
        Assert.assertEquals(10, scatterSurfaceImageInfo.getImageOffset(0.999, 0.4));
        Assert.assertEquals(10, scatterSurfaceImageInfo.getImageOffset(0.999, 0.66666666));
        Assert.assertEquals(11, scatterSurfaceImageInfo.getImageOffset(0.999, 0.67));
        Assert.assertEquals(11, scatterSurfaceImageInfo.getImageOffset(0.999, 0.7));
        Assert.assertEquals(11, scatterSurfaceImageInfo.getImageOffset(0.999, 0.999999999));
    }

    @Test
    public void getImageOffset_frequency() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.3);
        scatterSurfaceImageInfo.setRare(0.1);
        scatterSurfaceImageInfo.setCommonImageCount(1);
        scatterSurfaceImageInfo.setUncommonImageCount(1);
        scatterSurfaceImageInfo.setRareImageCount(1);
        // Test Common
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.0, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.599999999, 0.0));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.6, 0.0));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.61, 0.0));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.89999999999, 0.0));
        Assert.assertEquals(2, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(2, scatterSurfaceImageInfo.getImageOffset(0.999999999, 0.0));
    }

    @Test
    public void getImageOffsetOnlyOneCommon() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.0);
        scatterSurfaceImageInfo.setRare(0.0);
        scatterSurfaceImageInfo.setCommonImageCount(1);
        scatterSurfaceImageInfo.setUncommonImageCount(0);
        scatterSurfaceImageInfo.setRareImageCount(0);
        // Test
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.9));
    }

    @Test
    public void getImageOffsetTwoCommon() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.0);
        scatterSurfaceImageInfo.setRare(0.0);
        scatterSurfaceImageInfo.setCommonImageCount(2);
        scatterSurfaceImageInfo.setUncommonImageCount(0);
        scatterSurfaceImageInfo.setRareImageCount(0);
        // Test
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.3));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.1, 0.7));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.3, 0.7));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.6, 0.7));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.9, 0.7));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.3, 0.9));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.6, 0.9));
        Assert.assertEquals(1, scatterSurfaceImageInfo.getImageOffset(0.9, 0.9));
    }

    @Test
    public void getImageOffsetOnlyOneUnCommon() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(1.0);
        scatterSurfaceImageInfo.setRare(0.0);
        scatterSurfaceImageInfo.setCommonImageCount(0);
        scatterSurfaceImageInfo.setUncommonImageCount(1);
        scatterSurfaceImageInfo.setRareImageCount(0);
        // Test
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.9));
    }

    @Test
    public void getImageOffsetOnlyOneRare() {
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(0.0);
        scatterSurfaceImageInfo.setRare(1.0);
        scatterSurfaceImageInfo.setCommonImageCount(0);
        scatterSurfaceImageInfo.setUncommonImageCount(0);
        scatterSurfaceImageInfo.setRareImageCount(1);
        // Test
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.0));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.3));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.7));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.1, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.3, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.6, 0.9));
        Assert.assertEquals(0, scatterSurfaceImageInfo.getImageOffset(0.9, 0.9));
    }
}
