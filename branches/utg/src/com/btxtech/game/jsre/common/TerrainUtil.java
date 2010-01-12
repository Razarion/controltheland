/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

public class TerrainUtil {

    public static Index getTerrainTileIndexForAbsPosition(int x, int y) {
        return new Index(x / Constants.TILE_WIDTH, y / Constants.TILE_HEIGHT);
    }

    public static Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
        return new Index(absolutePos.getX() / Constants.TILE_WIDTH, absolutePos.getY() / Constants.TILE_HEIGHT);
    }

    public static Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
        return new Index(tileIndex.getX() * Constants.TILE_WIDTH, tileIndex.getY() * Constants.TILE_HEIGHT);
    }

    public static Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
        return new Index(xTile * Constants.TILE_WIDTH, yTile * Constants.TILE_HEIGHT);
    }

    public static Rectangle convertToAbsolutePosition(Rectangle rectangle) {
        Index start = getAbsolutIndexForTerrainTileIndex(rectangle.getStart());
        Index end = getAbsolutIndexForTerrainTileIndex(rectangle.getEnd());
        return new Rectangle(start, end);
    }

    public static int getTerrainTileCount4PixelWidth(int width) {
        return (int) Math.ceil((double)width / (double) Constants.TILE_WIDTH);
    }

    public static int getTerrainTileCount4PixelHeight(int height) {
        return (int) Math.ceil((double)height / (double) Constants.TILE_HEIGHT);
    }

}
