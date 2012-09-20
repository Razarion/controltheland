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
 * Date: 02.02.2010
 * Time: 22:53:15
 */
public class TerrainImage implements Serializable {
    private int id;
    private int tileWidth;
    private int tileHeight;
    private SurfaceType[][] surfaceTypes;

    /**
     * Used by GWT
     */
    public TerrainImage() {
    }

    public TerrainImage(int id, int tileWidth, int tileHeight, SurfaceType[][] surfaceTypes) {
        this.id = id;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.surfaceTypes = surfaceTypes;
    }

    public int getId() {
        return id;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public SurfaceType getSurfaceType(int tileX, int tileY) {
        return surfaceTypes[tileX][tileY];
    }

    public SurfaceType[][] getSurfaceTypes() {
        return surfaceTypes;
    }
}
