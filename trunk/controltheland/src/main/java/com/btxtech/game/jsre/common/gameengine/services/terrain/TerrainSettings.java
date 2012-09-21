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

import com.btxtech.game.jsre.client.common.Constants;

import java.io.Serializable;

/**
 * User: beat
 * Date: 09.01.2010
 * Time: 12:40:33
 */
public class TerrainSettings implements Serializable {
    private int tileXCount;
    private int tileYCount;

    /**
     * Used gy BWT
     */
    TerrainSettings() {
    }

    public TerrainSettings(int tileXCount, int tileYCount) {
        this.tileXCount = tileXCount;
        this.tileYCount = tileYCount;
    }

    public int getTileXCount() {
        return tileXCount;
    }

    public int getTileYCount() {
        return tileYCount;
    }

    public int getPlayFieldXSize() {
        return tileXCount * Constants.TERRAIN_TILE_WIDTH;
    }

    public int getPlayFieldYSize() {
        return tileYCount * Constants.TERRAIN_TILE_HEIGHT;
    }
}
