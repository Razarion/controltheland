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

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 23:23:38
 */
public class HarvesterType implements Serializable {
    private int range;
    private double progress;

    /**
     * Used by GWT
     */
    HarvesterType() {
    }

    public HarvesterType(int range, double progress) {
        this.range = range;
        this.progress = progress;
    }

    public int getRange() {
        return range;
    }

    public double getProgress() {
        return progress;
    }

    public void changeTo(HarvesterType harvesterType) {
        range = harvesterType.range;
        progress = harvesterType.progress;
    }
}
