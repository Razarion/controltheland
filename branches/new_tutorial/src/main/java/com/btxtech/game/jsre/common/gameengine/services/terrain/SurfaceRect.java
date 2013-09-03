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
import com.btxtech.game.jsre.client.common.Rectangle;

import java.io.Serializable;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 12:06:16
 */
public class SurfaceRect implements Serializable {
    private Rectangle tileRectangle;
    private int surfaceImageId;

    /**
     * Used by GWT
     */
    public SurfaceRect() {
    }

    public SurfaceRect(Rectangle tileRectangle, int surfaceImageId) {
        this.tileRectangle = tileRectangle;
        this.surfaceImageId = surfaceImageId;
    }

    public Rectangle getTileRectangle() {
        return tileRectangle;
    }

    public int getSurfaceImageId() {
        return surfaceImageId;
    }

    public Index getTileIndex() {
        return tileRectangle.getStart();
    }

    public int getTileWidth() {
        return tileRectangle.getWidth();
    }

    public int getTileHeight() {
        return tileRectangle.getHeight();
    }

    public void setTileRectangle(Rectangle tileRect) {
        tileRectangle = tileRect;
    }
}
