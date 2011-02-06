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

package com.btxtech.game.jsre.common.gameengine.services.terrain;

import java.io.Serializable;

/**
 * User: beat
 * Date: 09.01.2010
 * Time: 12:40:33
 */
public class TerrainSettings implements Serializable {
    private int tileXCount;
    private int tileYCount;
    private int tileHeight;
    private int tileWidth;

    /**
     * Used gy BWT
     */
    public TerrainSettings() {
    }

    public TerrainSettings(int tileXCount, int tileYCount, int tileHeight, int tileWidth) {
        this.tileXCount = tileXCount;
        this.tileYCount = tileYCount;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
    }

    public int getTileXCount() {
        return tileXCount;
    }

    public int getTileYCount() {
        return tileYCount;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getPlayFieldXSize() {
        return tileXCount * tileWidth;
    }

    public int getPlayFieldYSize() {
        return tileYCount * tileHeight;
    }
}
