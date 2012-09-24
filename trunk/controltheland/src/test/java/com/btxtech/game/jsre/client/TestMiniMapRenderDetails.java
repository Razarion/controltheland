package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.radar.MiniMapRenderDetails;
import com.btxtech.game.jsre.client.common.Rectangle;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 24.09.12
 * Time: 13:52
 */
public class TestMiniMapRenderDetails {

    @Test
    public void simple() {
        MiniMapRenderDetails miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(0, 0, 30, 30));
        Assert.assertTrue(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(1, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(0, 0, 40, 40));
        Assert.assertTrue(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(1, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(0, 0, 70, 70));
        Assert.assertFalse(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(1, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(0, 0, 130, 130));
        Assert.assertFalse(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(2, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(100, 100, 130, 130));
        Assert.assertFalse(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(2, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(0, 0, 10, 1690));
        Assert.assertFalse(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(2, miniMapRenderDetails.getTileIncrease());

        miniMapRenderDetails = new MiniMapRenderDetails(new Rectangle(100, 100, 10, 1690));
        Assert.assertFalse(miniMapRenderDetails.isDrawImages());
        Assert.assertEquals(2, miniMapRenderDetails.getTileIncrease());

    }
}
