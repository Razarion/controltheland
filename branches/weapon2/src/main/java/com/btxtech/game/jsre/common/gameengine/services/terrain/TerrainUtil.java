package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 17:26
 */
public class TerrainUtil {
    public static Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / Constants.TERRAIN_TILE_WIDTH, y / Constants.TERRAIN_TILE_HEIGHT);
    }

    public static int getTerrainTileIndexForAbsXPosition(int x) {
        return x / Constants.TERRAIN_TILE_WIDTH;
    }

    public static int getTerrainTileIndexForAbsYPosition(int y) {
        return y / Constants.TERRAIN_TILE_HEIGHT;
    }

    public static Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / Constants.TERRAIN_TILE_WIDTH, absolutePos.getY() / Constants.TERRAIN_TILE_HEIGHT);
    }

    public static Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * Constants.TERRAIN_TILE_WIDTH, tileIndex.getY() * Constants.TERRAIN_TILE_HEIGHT);
    }

    public static Index getTerrainTileIndexForAbsPositionRoundUp(Index absolutePos) {
        return new Index((int) Math.ceil((double) absolutePos.getX() / (double) Constants.TERRAIN_TILE_WIDTH),
                (int) Math.ceil((double) absolutePos.getY() / (double) Constants.TERRAIN_TILE_HEIGHT));
    }

    public static Index getTerrainTileIndexForAbsPositionRound(Index absolutePos) {
        return new Index((int) Math.round((double) absolutePos.getX() / (double) Constants.TERRAIN_TILE_WIDTH),
                (int) Math.round((double) absolutePos.getY() / (double) Constants.TERRAIN_TILE_HEIGHT));
    }

    public static Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * Constants.TERRAIN_TILE_WIDTH, yTile * Constants.TERRAIN_TILE_HEIGHT);
    }

    public static int getAbsolutXForTerrainTile(int xTile) {
        return xTile * Constants.TERRAIN_TILE_WIDTH;
    }

    public static int getAbsolutYForTerrainTile(int yTile) {
        return yTile * Constants.TERRAIN_TILE_HEIGHT;
    }

    public static Rectangle convertToTilePosition(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPosition(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    public static Rectangle convertToTilePositionRoundUp(Rectangle rectangle) {
        Index start = getTerrainTileIndexForAbsPosition(rectangle.getStart());
        Index end = getTerrainTileIndexForAbsPositionRoundUp(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    public static Rectangle convertToAbsolutePosition(Rectangle rectangle) {
        Index start = getAbsolutIndexForTerrainTileIndex(rectangle.getStart());
        Index end = getAbsolutIndexForTerrainTileIndex(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    public static Index moveAbsoluteToGrid(Index absolute) {
        return getAbsolutIndexForTerrainTileIndex(getTerrainTileIndexForAbsPositionRound(absolute));
    }

}
