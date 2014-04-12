package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.AStar;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionTileContainer;
import org.junit.Test;

/**
 * User: beat
 * Date: 23.03.14
 * Time: 16:00
 */
public class TestAStar {

    @Test
    public void empty() throws Exception {
        AStar.findTilePath(createEmptyTiles(), new Index(2, 2), new Index(8, 8), 10);
    }

    @Test
    public void simpleTerrain() throws Exception {
        AStar.findTilePath(createSimpleTerrainTiles(), new Index(1, 1), new Index(8, 8), 10);
    }

    private CollisionTileContainer createEmptyTiles() {
        return new CollisionTileContainer(10, 10);
    }

    private CollisionTileContainer createSimpleTerrainTiles() {
        CollisionTileContainer collisionTileContainer = new CollisionTileContainer(10, 10);
        collisionTileContainer.setBlocked(4, 3);
        collisionTileContainer.setBlocked(4, 4);
        collisionTileContainer.setBlocked(4, 5);
        collisionTileContainer.setBlocked(4, 6);
        collisionTileContainer.setBlocked(4, 7);
        collisionTileContainer.setBlocked(5, 3);
        collisionTileContainer.setBlocked(5, 4);
        collisionTileContainer.setBlocked(5, 5);
        collisionTileContainer.setBlocked(5, 6);
        collisionTileContainer.setBlocked(5, 7);
        return collisionTileContainer;
    }
}
