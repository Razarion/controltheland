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

package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import java.io.Serializable;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 23:12:43
 */
public class MovableType implements Serializable {
    /**
     * Pixel per second
     */
    private int speed;
    private SurfaceType surfaceType;

    /**
     * Used by GWT
     */
    MovableType() {
    }

    public MovableType(int speed, SurfaceType surfaceType) {
        this.speed = speed;
        this.surfaceType = surfaceType;
    }

    public int getSpeed() {
        return speed;
    }

    public SurfaceType getTerrainType() {
        return surfaceType;
    }

    public void changeTo(MovableType movableType) {
        speed = movableType.speed;
        surfaceType = movableType.surfaceType;
    }
}
