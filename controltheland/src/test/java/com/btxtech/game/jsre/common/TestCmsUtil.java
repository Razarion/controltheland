package com.btxtech.game.jsre.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 14.02.2012
 * Time: 12:43:50
 */
public class TestCmsUtil {
    @Test
    public void testGameUrl() {
        Assert.assertEquals("/game_run", CmsUtil.getUrl4Game(null));
        Assert.assertEquals("/game_run/taskId/233", CmsUtil.getUrl4Game(233));
    }
}
