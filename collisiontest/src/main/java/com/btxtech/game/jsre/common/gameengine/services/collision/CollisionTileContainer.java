package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Index;

import java.util.Collection;

/**
 * User: beat
 * Date: 23.03.14
 * Time: 16:31
 */
public class CollisionTileContainer {
    private int xTiles;
    private int yTiles;
    private CollisionTile[][] collisionTiles;

    public CollisionTileContainer(int xTiles, int yTiles) {
        this.xTiles = xTiles;
        this.yTiles = yTiles;

        collisionTiles = new CollisionTile[xTiles][yTiles];
        for (int x = 0; x < xTiles; x++) {
            for (int y = 0; y < yTiles; y++) {
                collisionTiles[x][y] = new CollisionTile();
            }
        }
    }

    public int getXTiles() {
        return xTiles;
    }

    public int getYTiles() {
        return yTiles;
    }

    public boolean isBlocked(Index tilePosition, int absoluteRadius) {
        Collection<Index> tilesToBeChecked = CollisionUtil.getCoveringTiles(tilePosition, absoluteRadius);
        for (Index index : tilesToBeChecked) {
            if (isBlocked(index.getX(), index.getY())) {
                return true;
            }
        }
        return false;
    }


    public boolean isBlocked(int xTile, int yTile) {
        return xTile < 0 || yTile < 0 || xTile >= xTiles || yTile >= yTiles || collisionTiles[xTile][yTile].isBlocked();
    }


    public void clearBlocked(Collection<Index> blockingTiles) {
        for (Index coveringTile : blockingTiles) {
            clearBlocked(coveringTile.getX(), coveringTile.getY());
        }
    }

    public void setBlocked(Collection<Index> coveringTiles) {
        // Check blocked
        for (Index coveringTile : coveringTiles) {
            if (isBlocked(coveringTile.getX(), coveringTile.getY())) {
                throw new BlockingStateException("CollisionTile already blocked: " + coveringTile.getX() + ":" + coveringTile.getY());
            }
        }
        for (Index coveringTile : coveringTiles) {
            setBlocked(coveringTile.getX(), coveringTile.getY());
        }
    }

    public void setBlocked(int xTile, int yTile) {
        collisionTiles[xTile][yTile].setBlocked();
    }

    public void clearBlocked(int xTile, int yTile) {
        collisionTiles[xTile][yTile].clearBlocked();
    }
}
