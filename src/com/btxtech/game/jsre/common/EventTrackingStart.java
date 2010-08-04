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

import java.io.Serializable;

/**
 * User: beat
 * Date: 03.08.2010
 * Time: 22:09:15
 */
public class EventTrackingStart implements Serializable {
    private int xResolution;
    private int yResolution;
    private long clientTimeStamp;

    /**
     * Used by GWT
     */
    public EventTrackingStart() {
    }

    public EventTrackingStart(int xResolution, int yResolution) {
        this.xResolution = xResolution;
        this.yResolution = yResolution;
        clientTimeStamp = System.currentTimeMillis();
    }

    public EventTrackingStart(int xResolution, int yResolution, long clientTimeStamp) {
        this.xResolution = xResolution;
        this.yResolution = yResolution;
        this.clientTimeStamp = clientTimeStamp;
    }

    public int getXResolution() {
        return xResolution;
    }

    public int getYResolution() {
        return yResolution;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }
}
