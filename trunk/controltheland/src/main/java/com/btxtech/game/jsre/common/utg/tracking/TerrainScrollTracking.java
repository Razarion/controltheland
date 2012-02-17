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

package com.btxtech.game.jsre.common.utg.tracking;

import java.io.Serializable;

/**
 * User: beat
 * Date: 24.12.2010
 * Time: 12:30:41
 */
public class TerrainScrollTracking implements Serializable {
    private int left;
    private int top;
    @Deprecated
    private int width;
    @Deprecated
    private int height;
    private long clientTimeStamp;

    /**
     * Used by GWT
     */
    public TerrainScrollTracking() {
    }

    public TerrainScrollTracking(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        clientTimeStamp = System.currentTimeMillis();
    }

    public TerrainScrollTracking(int left, int top, int width, int height, long clientTimeStamp) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.clientTimeStamp = clientTimeStamp;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    @Deprecated
    public int getWidth() {
        return width;
    }

    @Deprecated
    public int getHeight() {
        return height;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }
}