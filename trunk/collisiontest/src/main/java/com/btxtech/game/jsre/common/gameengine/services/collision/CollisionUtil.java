package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 17:26
 */
public class CollisionUtil {
    public static int getCollisionTileIndexForAbsXPosition(int x) {
        return x / Constants.COLLISION_TILE_WIDTH;
    }

    public static int getCollisionTileIndexForAbsYPosition(int y) {
        return y / Constants.COLLISION_TILE_HEIGHT;
    }

    public static int getCollisionTileIndexForAbsXPositionRoundUp(int x) {
        return (int) Math.ceil((double) x / (double) Constants.COLLISION_TILE_WIDTH);
    }

    public static int getCollisionTileIndexForAbsYPositionRoundUp(int y) {
        return (int) Math.ceil((double) y / (double) Constants.COLLISION_TILE_HEIGHT);
    }

    public static Index getCollisionTileIndexForAbsPosition(Index absolutePos) {
        return new Index(getCollisionTileIndexForAbsXPosition(absolutePos.getX()), getCollisionTileIndexForAbsYPosition(absolutePos.getY()));
    }

    public static Index getCollisionTileIndexForAbsPositionRoundUp(Index absolutePos) {
        return new Index(getCollisionTileIndexForAbsXPositionRoundUp(absolutePos.getX()), getCollisionTileIndexForAbsYPositionRoundUp(absolutePos.getY()));
    }

    public static Index getAbsoluteIndexForCollisionTileIndex(int xTile, int yTile) {
        return new Index(xTile * Constants.COLLISION_TILE_WIDTH + Constants.COLLISION_TILE_WIDTH / 2,
                yTile * Constants.COLLISION_TILE_HEIGHT + Constants.COLLISION_TILE_HEIGHT / 2);
    }

    public static Index getAbsoluteIndexForCollisionTileIndex(Index tileIndex) {
        return getAbsoluteIndexForCollisionTileIndex(tileIndex.getX(), tileIndex.getY());
    }

    public static Collection<Index> getCoveringTilesTemplate(int absoluteRadius) {
        // Put to cache?
        int minXTile = getCollisionTileIndexForAbsXPosition(-absoluteRadius);
        int maxXTile = getCollisionTileIndexForAbsXPositionRoundUp(absoluteRadius);
        int minYTile = getCollisionTileIndexForAbsYPosition(-absoluteRadius);
        int maxYTile = getCollisionTileIndexForAbsYPositionRoundUp(absoluteRadius);

        Collection<Index> result = new ArrayList<Index>();

        Index middle = getAbsoluteIndexForCollisionTileIndex(0, 0);
        for (int xTile = minXTile; xTile <= maxXTile; xTile++) {
            for (int yTile = minYTile; yTile <= maxYTile; yTile++) {
                Index absoluteCollisionRaster = getAbsoluteIndexForCollisionTileIndex(xTile, yTile);
                if (absoluteCollisionRaster.getDistanceDouble(middle) <= absoluteRadius) {
                    result.add(new Index(xTile, yTile));
                }
            }
        }
        return result;
    }

    public static Collection<Index> getCoveringTilesAbsolute(Index absolutePosition, int absoluteRadius) {
        Collection<Index> template = getCoveringTilesTemplate(absoluteRadius);
        Collection<Index> result = new ArrayList<Index>();
        for (Index templateTile : template) {
            Index absolute = getAbsoluteIndexForCollisionTileIndex(templateTile).add(absolutePosition).sub(new Index(5, 5)); // TODO why -Index(5, 5) ???????
            result.add(getCollisionTileIndexForAbsPosition(absolute));
        }
        return result;
    }

    public static Collection<Index> getCoveringTiles(Index tilePosition, int absoluteRadius) {
        Collection<Index> template = getCoveringTilesTemplate(absoluteRadius);
        Collection<Index> result = new ArrayList<Index>();
        for (Index templateTile : template) {
            result.add(templateTile.add(tilePosition));
        }
        return result;
    }

    public static List<Index> toAbsolutePath(List<Index> tilePath) {
        List<Index> absolutePath = new ArrayList<Index>();
        for (Index tileIndex : tilePath) {
            absolutePath.add(getAbsoluteIndexForCollisionTileIndex(tileIndex));
        }
        return absolutePath;
    }
}
