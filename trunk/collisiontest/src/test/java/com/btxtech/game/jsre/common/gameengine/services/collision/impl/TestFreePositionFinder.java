package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionTileContainer;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 06.04.14
 * Time: 11:15
 */
public class TestFreePositionFinder {

    @Test
    public void testEmpty() {
        CollisionTileContainer collisionTileContainer = new CollisionTileContainer(10, 10);
        Index pos = FreePositionFinder.findFreePosition(collisionTileContainer, new Index(7, 7), 20, new Index(2, 2), 20);
        Assert.assertEquals(new Index(7, 7), pos);
    }

}
