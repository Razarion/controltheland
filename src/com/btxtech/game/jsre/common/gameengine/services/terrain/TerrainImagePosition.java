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

import com.btxtech.game.jsre.client.common.Index;
import java.io.Serializable;

/**
 * User: beat
 * Date: 07.01.2010
 * Time: 22:25:25
 */
public class TerrainImagePosition implements Serializable{
    private Index tile;
    private int imageId;

    /**
     * Used by GWT
     */
    public TerrainImagePosition() {
    }

    public TerrainImagePosition(Index tile, int imageId) {
        this.tile = tile;
        this.imageId = imageId;
    }

    public Index getTile() {
        return tile;
    }

    public int getImageId() {
        return imageId;
    }
}
