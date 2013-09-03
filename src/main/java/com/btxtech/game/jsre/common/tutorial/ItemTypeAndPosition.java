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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:47:19
 */
public class ItemTypeAndPosition implements Serializable {
    private int itemTypeId;
    private Index position;
    private double angel;

    /**
     * Used by GWT
     */
    ItemTypeAndPosition() {
    }

    public ItemTypeAndPosition(int itemTypeId, Index position, double angel) {
        this.itemTypeId = itemTypeId;
        this.position = position;
        this.angel = angel;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public Index getPosition() {
        return position;
    }

    public double getAngel() {
        return angel;
    }
}
