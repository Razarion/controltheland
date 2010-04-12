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
 * Date: 03.12.2009
 * Time: 21:37:49
 */
public class TurnableType implements Serializable {
    private int imageCount;

    /**
     * Used by GWT
     */
    TurnableType() {
    }

    public TurnableType(int imageCount) {
        this.imageCount = imageCount;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void changeTo(TurnableType turnableType) {
        imageCount = turnableType.getImageCount();
    }
}
