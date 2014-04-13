package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionTileContainer;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 23.03.14
 * Time: 16:00
 */
public class TestCollisionTileContainer {

    @Test
    public void simple() throws Exception {
        CollisionTileContainer collisionTileContainer = new CollisionTileContainer(5, 7);
        Assert.assertEquals(5, collisionTileContainer.getXTiles());
        Assert.assertEquals(7, collisionTileContainer.getYTiles());

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 7; y++) {
                Assert.assertFalse(collisionTileContainer.isBlocked(x, y));
            }
        }
    }

    @Test
    public void blocked() throws Exception {
        CollisionTileContainer collisionTileContainer = new CollisionTileContainer(11, 13);
        Assert.assertEquals(11, collisionTileContainer.getXTiles());
        Assert.assertEquals(13, collisionTileContainer.getYTiles());

        collisionTileContainer.setBlocked(3, 4);
        collisionTileContainer.setBlocked(6, 8);
        collisionTileContainer.setBlocked(10, 12);
        collisionTileContainer.setBlocked(4, 6);

        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 13; y++) {
                boolean blocked = collisionTileContainer.isBlocked(x, y);
                if (x == 3 && y == 4) {
                    Assert.assertTrue(blocked);
                } else if (x == 6 && y == 8) {
                    Assert.assertTrue(blocked);
                } else if (x == 10 && y == 12) {
                    Assert.assertTrue(blocked);
                } else if (x == 4 && y == 6) {
                    Assert.assertTrue(blocked);
                } else {
                    Assert.assertFalse(blocked);
                }
            }
        }

        collisionTileContainer.clearBlocked(3, 4);
        collisionTileContainer.clearBlocked(6, 8);
        collisionTileContainer.clearBlocked(10, 12);
        collisionTileContainer.clearBlocked(4, 6);

        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 13; y++) {
                Assert.assertFalse(collisionTileContainer.isBlocked(x, y));
            }
        }

        collisionTileContainer.setBlocked(3, 4);
        collisionTileContainer.setBlocked(6, 8);
        collisionTileContainer.setBlocked(10, 12);
        collisionTileContainer.setBlocked(4, 6);

        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 13; y++) {
                boolean blocked = collisionTileContainer.isBlocked(x, y);
                if (x == 3 && y == 4) {
                    Assert.assertTrue(blocked);
                } else if (x == 6 && y == 8) {
                    Assert.assertTrue(blocked);
                } else if (x == 10 && y == 12) {
                    Assert.assertTrue(blocked);
                } else if (x == 4 && y == 6) {
                    Assert.assertTrue(blocked);
                } else {
                    Assert.assertFalse(blocked);
                }
            }
        }
    }
}
