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
public class TerrainImagePosition implements Serializable {
    public enum ZIndex {
        LAYER_1,
        LAYER_2
    }

    private Index tileIndex;
    private int imageId;
    private ZIndex zIndex;

    /**
     * Used by GWT
     */
    public TerrainImagePosition() {
    }

    public TerrainImagePosition(Index tileIndex, int imageId, ZIndex zIndex) {
        this.tileIndex = tileIndex;
        this.imageId = imageId;
        this.zIndex = zIndex;
    }

    public Index getTileIndex() {
        return tileIndex;
    }

    public void setTileIndex(Index tileIndex) {
        this.tileIndex = tileIndex;
    }

    public int getImageId() {
        return imageId;
    }

    public ZIndex getzIndex() {
        return zIndex;
    }
}
